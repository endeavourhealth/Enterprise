package org.endeavourhealth.enterprise.engine;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.compiled.CompiledLibrary;
import org.endeavourhealth.enterprise.engine.compiled.CompiledQuery;
import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;
import org.endeavourhealth.enterprise.engine.compiler.CompilerApi;
import org.endeavourhealth.enterprise.engine.execution.Request;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EngineApi {

    private final EntityMapWrapper.EntityMap entityMap;
    private final List<LibraryItem> requiredLibraryItems;
    private final List<DbJobReport> jobReports;

    private List<Request> executionRequests = new ArrayList<>();
    private CompiledLibrary compiledLibrary;

    public EngineApi(
            EntityMapWrapper.EntityMap entityMap,
            List<DbJobReport> jobReports,
            List<LibraryItem> requiredLibraryItems) {

        this.entityMap = entityMap;
        this.jobReports = jobReports;
        this.requiredLibraryItems = requiredLibraryItems;
    }

    public void initialise() throws Exception {

        if (CollectionUtils.isEmpty(jobReports))
            throw new Exception("Job Reports empty");

        CompilerApi compilerApi = new CompilerApi(entityMap);
        compilerApi.compiledAllLibraryItems(requiredLibraryItems);
        compiledLibrary = compilerApi.getCompiledLibrary();

        for (DbJobReport jobReport: jobReports) {

            RequestParameters parameters = RequestParametersSerializer.readFromJobReport(jobReport);

            if (parameters.getBaselineDate() == null)
                throw new Exception("Request parameters do not have a baseline date.  JobReportUUID: " + jobReport.getJobReportUuid());

            CompiledReport compiledReport = compilerApi.compile(jobReport, parameters);

            Request request = new Request(jobReport.getJobReportUuid(), compiledReport, parameters);
            executionRequests.add(request);
        }
    }

    public ResultCounts getResults() {
        ResultCounts resultCounts = new ResultCounts();

        for (Request request: executionRequests) {
            CompiledReport compiledReport = request.getCompiledReport();

            JobReportType jobReportType = new JobReportType();
            jobReportType.setJobReportUuid(request.getJobReportUuid().toString());

            jobReportType.setOrganisationResults(new JobReportType.OrganisationResults());
            populateOrganisationResults(jobReportType.getOrganisationResults().getOrganisationResult(), compiledReport.getReportLevelResults());

            jobReportType.setJobReportItemResults(new JobReportType.JobReportItemResults());
            populateJobItemResults(jobReportType.getJobReportItemResults().getJobReportItemResult(), compiledReport.getQueryResults());

            resultCounts.setJobReport(jobReportType);
        }

        return resultCounts;
    }

    private void populateJobItemResults(List<JobReportItemResultType> jobReportItemResult, Map<UUID, ResultCounter> queryResults) {

        for (Map.Entry<UUID, ResultCounter> item: queryResults.entrySet()) {

            JobReportItemResultType result = new JobReportItemResultType();
            result.setJobReportItemResultUuid(item.getKey().toString());
            populateOrganisationResults(result.getOrganisationResult(), item.getValue());
            jobReportItemResult.add(result);
        }
    }

    private void populateOrganisationResults(List<OrganisationResult> organisationResult, ResultCounter reportLevelResults) {

        Map<String, AtomicInteger> results = reportLevelResults.getResults();

        for (Map.Entry<String, AtomicInteger> item: results.entrySet()) {

            if (item.getValue().get() > 0) {
                OrganisationResult result = new OrganisationResult();
                result.setOdsCode(item.getKey());
                result.setResultCount(item.getValue().get());
                organisationResult.add(result);
            }
        }
    }

    public Processor createProcessor() {
        return new Processor(executionRequests, compiledLibrary);
    }
}
