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
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.JobReportItemResult;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ResultCounts;

import java.util.*;

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

            Map<UUID, Integer> requestResults = compiledReport.getQueryResults();

            for (Map.Entry<UUID, Integer> entry: requestResults.entrySet()) {

                JobReportItemResult result = new JobReportItemResult();
                result.setUuid(entry.getKey().toString());
                result.setResultCount(entry.getValue());

                resultCounts.getJobReportItemResult().add(result);
            }
        }

        return resultCounts;
    }

    public Processor createProcessor() {
        return new Processor(executionRequests, compiledLibrary);
    }
}
