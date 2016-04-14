package org.endeavourhealth.enterprise.engine;

import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;
import org.endeavourhealth.enterprise.engine.execution.Request;
import org.endeavourhealth.enterprise.enginecore.resultcounts.ResultCountBuilder;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ResultCounts;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

class ResultCreator {

    private final List<Request> executionRequests;
    private final ResultCountBuilder resultCountBuilder = new ResultCountBuilder();

    public ResultCreator(List<Request> executionRequests) {
        this.executionRequests = executionRequests;
    }

    public ResultCounts create() {

        for (Request request: executionRequests) {
            CompiledReport compiledReport = request.getCompiledReport();

            resultCountBuilder.startNewJobReportResult(request.getJobReportUuid());

            populateJobResults(compiledReport.getReportLevelResults());
            populateJobItemResults(compiledReport.getJobReportItemResults());
        }

        return resultCountBuilder.build();
    }

    private void populateJobItemResults(Map<UUID, ResultCounter> queryResults) {

        for (Map.Entry<UUID, ResultCounter> item: queryResults.entrySet()) {

            resultCountBuilder.startNewReportItemResult(item.getKey());
            populateJobItemResults(item.getValue());
        }
    }

    private void populateJobResults(ResultCounter resultCounter) {

        for (Map.Entry<String, AtomicInteger> item: resultCounter.getResults().entrySet()) {
            resultCountBuilder.addResultToJob(item.getKey(), item.getValue().get());
        }
    }

    private void populateJobItemResults(ResultCounter resultCounter) {

        for (Map.Entry<String, AtomicInteger> item: resultCounter.getResults().entrySet()) {
            resultCountBuilder.addResultToReportItem(item.getKey(), item.getValue().get());
        }
    }
}
