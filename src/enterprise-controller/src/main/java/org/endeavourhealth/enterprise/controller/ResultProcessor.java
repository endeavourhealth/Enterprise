package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.controller.jobinventory.JobReportInfo;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.TableSaveMode;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentHelper;
import org.endeavourhealth.enterprise.enginecore.resultcounts.ResultCountMerger;
import org.endeavourhealth.enterprise.enginecore.resultcounts.ResultCountsHelper;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.JobReportItemResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.JobReportResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.OrganisationResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ResultCounts;

import java.util.*;

class ResultProcessor {

    private final UUID jobUuid;
    private final Map<UUID, Set<String>> jobReportUuidToOrganisations = new HashMap<>();
    private final Map<UUID, ResultCounts> processorToResult = new HashMap<>();
    private ResultCounts finalResultCounts;
    private List<Object> toSave;

    public ResultProcessor(UUID jobUuid, List<JobReportInfo> jobReportInfoList) {
        this.jobUuid = jobUuid;

        for (JobReportInfo jobReportInfo : jobReportInfoList) {
            jobReportUuidToOrganisations.put(jobReportInfo.getJobReportUuid(), jobReportInfo.getOrganisations());
        }
    }

    public void complete(Set<UUID> expectedProcessorNodes) throws Exception {
        populateProcessorToResult();
        validateExpectedProcessors(expectedProcessorNodes);
        mergeProcessorResults();

        toSave = new ArrayList<>();

        List<JobreportEntity> jobReports = JobreportEntity.retrieveForJob(jobUuid);

        for (JobreportEntity jobReport: jobReports) {
            processJobReport(jobReport);
        }

        //DatabaseManager.db().writeEntities(toSave); TODO
        toSave = null;
    }

    private void mergeProcessorResults() throws Exception {

        ResultCountMerger merger = new ResultCountMerger(jobReportUuidToOrganisations);

        for (ResultCounts resultCounts: processorToResult.values()) {
            merger.merge(resultCounts);
        }

        finalResultCounts = merger.getResult();
    }

    private void processJobReport(JobreportEntity jobReport) throws Exception {

        JobReportResult jobReportResult = getResultForJobReport(jobReport.getJobreportuuid());

        jobReport.setPopulationcount(ResultCountsHelper.calculateTotal(jobReportResult.getOrganisationResult()));
        jobReport.setStatusid((short)ExecutionStatus.Succeeded.getValue());
        toSave.add(jobReport);

        addOrganisationBreakdownToJobReport(jobReport.getJobreportuuid(), jobReportResult.getOrganisationResult());

        List<JobreportitemEntity> JobreportEntityItems = JobreportitemEntity.retrieveForJobReport(jobReport.getJobreportuuid());

        for (JobreportitemEntity JobreportEntityItem: JobreportEntityItems) {
            JobReportItemResult jobReportItemResult = getJobReportItemResult(JobreportEntityItem.getJobreportitemuuid(), jobReportResult.getJobReportItemResult());

            if (jobReportItemResult != null)
                processJobReportItem(JobreportEntityItem, jobReportItemResult);
        }
    }

    private void processJobReportItem(JobreportitemEntity JobreportEntityItem, JobReportItemResult jobReportItemResult) {

        JobreportEntityItem.setResultcount(ResultCountsHelper.calculateTotal(jobReportItemResult.getOrganisationResult()));
        toSave.add(JobreportEntityItem);

        addOrganisationBreakdownToJobReportItem(JobreportEntityItem.getJobreportitemuuid(), jobReportItemResult.getOrganisationResult());
    }

    private void addOrganisationBreakdownToJobReportItem(UUID jobReportItemUuid, List<OrganisationResult> organisationResultList) {

        for (OrganisationResult organisationResult: organisationResultList) {

            JobreportitemorganisationEntity jobReportItemOrganisation = new JobreportitemorganisationEntity();
            jobReportItemOrganisation.setJobreportitemuuid(jobReportItemUuid);
            jobReportItemOrganisation.setOrganisationodscode(organisationResult.getOdsCode());
            jobReportItemOrganisation.setResultcount(organisationResult.getResultCount());
            toSave.add(jobReportItemOrganisation);
        }
    }

    private void addOrganisationBreakdownToJobReport(UUID jobReportUuid, List<OrganisationResult> organisationResultList) {

        for (OrganisationResult organisationResult: organisationResultList) {

            JobreportorganisationEntity jobReportOrganisation = new JobreportorganisationEntity();
            jobReportOrganisation.setJobreportuuid(jobReportUuid);
            jobReportOrganisation.setOrganisationodscode(organisationResult.getOdsCode());
            jobReportOrganisation.setPopulationcount(organisationResult.getResultCount());
            toSave.add(jobReportOrganisation);
        }
    }

    private JobReportResult getResultForJobReport(UUID jobReportUuid) throws Exception {
        for (JobReportResult jobReportResult: finalResultCounts.getJobReport()) {
            UUID tempUuid = QueryDocumentHelper.parseMandatoryUuid(jobReportResult.getJobReportUuid());

            if (tempUuid.equals(jobReportUuid))
                return jobReportResult;
        }

        throw new Exception("Missing JobReportUuid: " + jobReportUuid);
    }

    private JobReportItemResult getJobReportItemResult(UUID jobReportItemUuid, List<JobReportItemResult> jobReportItemResultList) throws Exception {
        for (JobReportItemResult jobReportItemResult: jobReportItemResultList) {
            UUID tempUuid = QueryDocumentHelper.parseMandatoryUuid(jobReportItemResult.getJobReportItemUuid());

            if (tempUuid.equals(jobReportItemUuid))
                return jobReportItemResult;
        }

        return null;
        //If it's a listrepor then this won't be in there.
        //throw new Exception("Missing JobReportItemUuid: " + jobReportItemUuid);
    }

    private void validateExpectedProcessors(Set<UUID> expectedProcessorNodes) throws Exception {
        Set<UUID> actualProcessors = processorToResult.keySet();

        if (!expectedProcessorNodes.equals(actualProcessors))
            throw new Exception("Actual process nodes do no match expected processor nodes");
    }

    private void populateProcessorToResult() throws Exception {
        List<JobprocessorresultEntity> processorResults = JobprocessorresultEntity.retrieveForJob(jobUuid);

        for (JobprocessorresultEntity processorResult: processorResults) {

            String xml = processorResult.getResultxml();
            ResultCounts count = ResultCountsHelper.deserializeFromString(xml);
            processorToResult.put(processorResult.getProcessoruuid(), count);
        }
    }
}
