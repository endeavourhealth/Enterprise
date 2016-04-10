package org.endeavourhealth.enterprise.enginecore.resultcounts;

import org.endeavourhealth.enterprise.enginecore.resultcounts.models.JobReportItemResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.JobReportResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.OrganisationResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ResultCounts;

import java.util.UUID;

public class ResultCountBuilder {

    private ResultCounts resultCounts = new ResultCounts();
    private JobReportResult currentJobReportResult;
    private JobReportItemResult currentJobReportItemResult;

    public ResultCountBuilder startNewJobReportResult(UUID jobReportUuid) {

        currentJobReportResult = new JobReportResult();
        currentJobReportResult.setJobReportUuid(jobReportUuid.toString());

        resultCounts.getJobReport().add(currentJobReportResult);

        return this;
    }

    public ResultCountBuilder addResultToJob(String organisationId, int count) {
        OrganisationResult result = new OrganisationResult();
        result.setOdsCode(organisationId);
        result.setResultCount(count);

        currentJobReportResult.getOrganisationResult().add(result);

        return this;
    }

    public ResultCountBuilder startNewReportItemResult(UUID jobReportItemUuid) {
        currentJobReportItemResult = new JobReportItemResult();
        currentJobReportItemResult.setJobReportItemUuid(jobReportItemUuid.toString());

        currentJobReportResult.getJobReportItemResult().add(currentJobReportItemResult);
        return this;
    }

    public ResultCountBuilder addResultToReportItem(String organisationId, int count ) {
        OrganisationResult result = new OrganisationResult();
        result.setOdsCode(organisationId);
        result.setResultCount(count);

        currentJobReportItemResult.getOrganisationResult().add(result);

        return this;
    }

    public ResultCounts build() {
        return resultCounts;
    }
}
