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

        ResultCreator resultCreator = new ResultCreator(executionRequests);
        return resultCreator.create();
    }

    public Processor createProcessor() {
        return new Processor(executionRequests, compiledLibrary);
    }
}
