package org.endeavourhealth.enterprise.engine;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.compiled.CompiledQuery;
import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;
import org.endeavourhealth.enterprise.engine.compiler.CompilerApi;
import org.endeavourhealth.enterprise.engine.execution.Request;
import org.endeavourhealth.enterprise.enginecore.Library;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.time.LocalDate;
import java.util.*;

public class EngineApi {

    private final EntityMapWrapper.EntityMap entityMap;
    private final Library library;
    private final List<DbJobReport> jobReports;

    private List<Request> executionRequests = new ArrayList<>();
    private Map<UUID, CompiledQuery> compiledQueryMap;

    public EngineApi(
            EntityMapWrapper.EntityMap entityMap,
            Library library,
            List<DbJobReport> jobReports) {

        this.entityMap = entityMap;
        this.library = library;
        this.jobReports = jobReports;
    }

    public void initialise() throws Exception {

        if (CollectionUtils.isEmpty(jobReports))
            throw new Exception("Job Reports empty");

        CompilerApi compilerApi = new CompilerApi(entityMap, library);

        compiledQueryMap = compilerApi.compileAllQueries(library);

        for (DbJobReport jobReport: jobReports) {

            RequestParameters parameters = RequestParametersSerializer.readFromJobReport(jobReport);

            if (parameters.getBaselineDate() == null)
                throw new Exception("Request parameters do not have a baseline date.  JobReportUUID: " + jobReport.getJobReportUuid());

            CompiledReport compiledReport = compilerApi.compile(jobReport, parameters);

            Request request = new Request(compiledReport, parameters);
            executionRequests.add(request);
        }
    }

    public Map<UUID, Integer> getResults() {
        Map<UUID, Integer> results = new HashMap<>();

        for (Request request: executionRequests) {
            CompiledReport compiledReport = request.getCompiledReport();

            Map<UUID, Integer> requestResults = compiledReport.getQueryResults();

            for (Map.Entry<UUID, Integer> entry: requestResults.entrySet()) {
                results.put(entry.getKey(), entry.getValue());
            }
        }

        return results;
    }

    public Processor createProcessor() {
        return new Processor(executionRequests, compiledQueryMap);
    }
}
