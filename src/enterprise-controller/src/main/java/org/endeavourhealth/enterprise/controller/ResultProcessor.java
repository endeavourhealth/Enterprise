package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.TableSaveMode;
import org.endeavourhealth.enterprise.core.database.execution.*;
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
    private List<DbAbstractTable> toSave;

    public ResultProcessor(UUID jobUuid) {
        this.jobUuid = jobUuid;
    }

    public void registerReport(UUID jobReportUuid, Set<String> organisations) {
        jobReportUuidToOrganisations.put(jobReportUuid, organisations);
    }

    public void complete(Set<UUID> expectedProcessorNodes) throws Exception {
        populateProcessorToResult();
        validateExpectedProcessors(expectedProcessorNodes);
        mergeProcessorResults();

        toSave = new ArrayList<>();

        List<DbJobReport> jobReports = DbJobReport.retrieveForJob(jobUuid);

        for (DbJobReport jobReport: jobReports) {
            processJobReport(jobReport);
        }

        DatabaseManager.db().writeEntities(toSave);
        toSave = null;
    }

    private void mergeProcessorResults() throws Exception {

        ResultCountMerger merger = new ResultCountMerger(jobReportUuidToOrganisations);

        for (ResultCounts resultCounts: processorToResult.values()) {
            merger.merge(resultCounts);
        }

        finalResultCounts = merger.getResult();
    }

    private void processJobReport(DbJobReport jobReport) throws Exception {

        JobReportResult jobReportResult = getResultForJobReport(jobReport.getJobReportUuid());

        jobReport.setPopulationCount(ResultCountsHelper.calculateTotal(jobReportResult.getOrganisationResult()));
        jobReport.setStatusId(ExecutionStatus.Succeeded);
        jobReport.setSaveMode(TableSaveMode.UPDATE);
        toSave.add(jobReport);

        addOrganisationBreakdownToJobReport(jobReport.getJobReportUuid(), jobReportResult.getOrganisationResult());

        List<DbJobReportItem> dbJobReportItems = DbJobReportItem.retrieveForJobReport(jobReport.getJobReportUuid());

        for (DbJobReportItem dbJobReportItem: dbJobReportItems) {
            JobReportItemResult jobReportItemResult = getJobReportItemResult(dbJobReportItem.getJobReportItemUuid(), jobReportResult.getJobReportItemResult());
            processJobReportItem(dbJobReportItem, jobReportItemResult);
        }
    }

    private void processJobReportItem(DbJobReportItem dbJobReportItem, JobReportItemResult jobReportItemResult) {

        dbJobReportItem.setResultCount(ResultCountsHelper.calculateTotal(jobReportItemResult.getOrganisationResult()));
        dbJobReportItem.setSaveMode(TableSaveMode.UPDATE);
        toSave.add(dbJobReportItem);

        addOrganisationBreakdownToJobReportItem(dbJobReportItem.getJobReportItemUuid(), jobReportItemResult.getOrganisationResult());
    }

    private void addOrganisationBreakdownToJobReportItem(UUID jobReportItemUuid, List<OrganisationResult> organisationResultList) {

        for (OrganisationResult organisationResult: organisationResultList) {

            DbJobReportItemOrganisation jobReportItemOrganisation = new DbJobReportItemOrganisation();
            jobReportItemOrganisation.setJobReportItemUuid(jobReportItemUuid);
            jobReportItemOrganisation.setOrganisationOdsCode(organisationResult.getOdsCode());
            jobReportItemOrganisation.setResultCount(organisationResult.getResultCount());
            jobReportItemOrganisation.setSaveMode(TableSaveMode.INSERT);
            toSave.add(jobReportItemOrganisation);
        }
    }

    private void addOrganisationBreakdownToJobReport(UUID jobReportUuid, List<OrganisationResult> organisationResultList) {

        for (OrganisationResult organisationResult: organisationResultList) {

            DbJobReportOrganisation jobReportOrganisation = new DbJobReportOrganisation();
            jobReportOrganisation.setJobReportUuid(jobReportUuid);
            jobReportOrganisation.setOrganisationOdsCode(organisationResult.getOdsCode());
            jobReportOrganisation.setPopulationCount(organisationResult.getResultCount());
            jobReportOrganisation.setSaveMode(TableSaveMode.INSERT);
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

        throw new Exception("Missing JobReportItemUuid: " + jobReportItemUuid);
    }

    private void validateExpectedProcessors(Set<UUID> expectedProcessorNodes) throws Exception {
        Set<UUID> actualProcessors = processorToResult.keySet();

        if (!expectedProcessorNodes.equals(actualProcessors))
            throw new Exception("Actual process nodes do no match expected processor nodes");
    }

    private void populateProcessorToResult() throws Exception {
        List<DbJobProcessorResult> processorResults = DbJobProcessorResult.retrieveForJob(jobUuid);

        for (DbJobProcessorResult processorResult: processorResults) {

            String xml = processorResult.getResultXml();
            ResultCounts count = ResultCountsHelper.deserializeFromString(xml);
            processorToResult.put(processorResult.getProcessorUuid(), count);
        }
    }
}
