package org.endeavourhealth.enterprise.enginecore.resultcounts;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentHelper;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.JobReportItemResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.JobReportResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.OrganisationResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ResultCounts;

import java.util.*;

public class ResultCountMerger {
    private final Map<UUID, Set<String>> jobReportUuidToOrganisations;
    private ResultCounts finalResult;

    public ResultCountMerger(Map<UUID, Set<String>> jobReportUuidToOrganisations) throws Exception {

        if (jobReportUuidToOrganisations.size() == 0)
            throw new Exception("jobReportUuidToOrganisations is empty");

        for (Set<String> organisations: jobReportUuidToOrganisations.values()) {
            if (CollectionUtils.isEmpty(organisations)) {
                throw new Exception("Organisation list is empty");
            }
        }

        this.jobReportUuidToOrganisations = jobReportUuidToOrganisations;
    }

    public void merge(ResultCounts source) throws Exception {

        validateJobReportUuid(source);
        validateOrganisations(source);

        if (finalResult == null) {
            finalResult = ResultCountsHelper.clone(source);
        } else {

            for (JobReportResult sourceReport: source.getJobReport()) {
                mergeInternal(sourceReport);
            }
        }
    }

    private void validateOrganisations(ResultCounts source) throws Exception {

        for (JobReportResult jobReportResult: source.getJobReport()) {
            Set<String> expectedOrganisations = jobReportUuidToOrganisations.get(getJobReportUuid(jobReportResult));

            validateOrganisations(expectedOrganisations, jobReportResult.getOrganisationResult());

            for (JobReportItemResult itemResult: jobReportResult.getJobReportItemResult()) {
                validateOrganisations(expectedOrganisations, itemResult.getOrganisationResult());
            }
        }
    }

    private void validateOrganisations(Set<String> expectedOrganisations, List<OrganisationResult> organisationResults) throws Exception {
        Set<String> actual = new HashSet<>();

        for (OrganisationResult organisationResult: organisationResults) {
            actual.add(organisationResult.getOdsCode());
        }

        if (!expectedOrganisations.equals(actual))
            throw new Exception("Organisation list does not match expected");
    }

    private void validateJobReportUuid(ResultCounts source) throws Exception {
        Set<UUID> expectedKeys = jobReportUuidToOrganisations.keySet();
        Set<UUID> sourceKeys = new HashSet<>();

        for (JobReportResult jobReportResult: source.getJobReport()) {
            sourceKeys.add(getJobReportUuid(jobReportResult));
        }

        if (!expectedKeys.equals(sourceKeys))
            throw new Exception("JobReportResult does not contain expected JobResultUuids");
    }

    private UUID getJobReportUuid(JobReportResult jobReportResult) {
        return QueryDocumentHelper.parseMandatoryUuid(jobReportResult.getJobReportUuid());
    }

    private void mergeInternal(JobReportResult source) throws Exception {

        JobReportResult target = findMatchingJobReportResult(source);

        mergeResults(target.getOrganisationResult(), source.getOrganisationResult());

        for (JobReportItemResult sourceItem: source.getJobReportItemResult()) {
            JobReportItemResult targetItem = findMatchingJobReportItemResult(target, sourceItem);
            mergeResults(targetItem.getOrganisationResult(), sourceItem.getOrganisationResult());
        }
    }

    private void mergeResults(List<OrganisationResult> targetResults, List<OrganisationResult> sourceResults) throws Exception {
        for (OrganisationResult source: sourceResults) {
            OrganisationResult target = findOrganisationResult(targetResults, source.getOdsCode());

            target.setResultCount(target.getResultCount() + source.getResultCount());
        }
    }

    private OrganisationResult findOrganisationResult(List<OrganisationResult> targetResults, String odsCode) throws Exception {
        for (OrganisationResult result: targetResults) {
            if (result.getOdsCode().equals(odsCode))
                return result;
        }

        throw new Exception("Could not find organisation: " + odsCode);
    }

    private JobReportResult findMatchingJobReportResult(JobReportResult sought) throws Exception {

        UUID soughtUuid = QueryDocumentHelper.parseMandatoryUuid(sought.getJobReportUuid());

        for (JobReportResult result: finalResult.getJobReport()) {
            UUID tempUuid = QueryDocumentHelper.parseMandatoryUuid(result.getJobReportUuid());
            if (soughtUuid.equals(tempUuid))
                return result;
        }

        throw new Exception("Could not match JobReportResult");
    }

    private JobReportItemResult findMatchingJobReportItemResult(JobReportResult target, JobReportItemResult sought) throws Exception {

        UUID soughtUuid = QueryDocumentHelper.parseMandatoryUuid(sought.getJobReportItemUuid());

        for (JobReportItemResult result: target.getJobReportItemResult()) {
            UUID tempUuid = QueryDocumentHelper.parseMandatoryUuid(result.getJobReportItemUuid());
            if (soughtUuid.equals(tempUuid))
                return result;
        }

        throw new Exception("Could not match JobReportItemResult");
    }

    public ResultCounts getResult() {
        return finalResult;
    }
}
