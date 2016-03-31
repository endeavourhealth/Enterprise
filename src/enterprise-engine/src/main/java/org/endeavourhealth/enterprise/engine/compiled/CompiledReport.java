package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.engine.ExecutionException;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

import java.util.*;

public class CompiledReport {

    private final List<CompiledReportQuery> rootQueries = new ArrayList<>();
    private final List<CompiledReportListReport> rootListReports = new ArrayList<>();

    private final Map<UUID, Integer> queryResults = new HashMap<>();

    public void initialise() {
        initialiseQueryResults(rootQueries);
    }

    public static class CompiledReportQuery {
        private final UUID queryUuid;
        private final UUID jobReportItemUuid;
        private List<CompiledReportQuery> childQueries = new ArrayList<>();
        private List<CompiledReportListReport> childListReports = new ArrayList<>();

        public CompiledReportQuery(UUID queryUuid, UUID jobReportItemUuid) {
            this.queryUuid = queryUuid;
            this.jobReportItemUuid = jobReportItemUuid;
        }

        public boolean execute() {
            return true;
        }

        public UUID getQueryUuid() {
            return queryUuid;
        }

        public UUID getJobReportItemUuid() {
            return jobReportItemUuid;
        }

        public List<CompiledReportQuery> getChildQueries() { return childQueries; }
        public List<CompiledReportListReport> getChildListReports() { return childListReports; }
    }

    public static class CompiledReportListReport {
        private final UUID listReportUuid;
        private final UUID jobReportItemUuid;

        public CompiledReportListReport(UUID listReportUuid, UUID jobReportItemUuid) {
            this.listReportUuid = listReportUuid;
            this.jobReportItemUuid = jobReportItemUuid;
        }

        public void execute(ExecutionContext context) {

        }
    }

    public List<CompiledReportQuery> getChildQueries() { return rootQueries; }
    public List<CompiledReportListReport> getChildListReports() { return rootListReports; }

    public void execute(ExecutionContext context) throws ExecutionException {

        executeQueryList(rootQueries, context);
        executeReportList(rootListReports, context);
    }

    private void executeQueryList(List<CompiledReportQuery> queries, ExecutionContext context) throws ExecutionException {
        if (queries == null)
            return;

        for (CompiledReportQuery query: queries) {
            if (context.getQueryResult(query.getQueryUuid())) {
                incrementPatientResult(query.getJobReportItemUuid());
                executeQueryList(query.childQueries, context);
                executeReportList(query.childListReports, context);
            }
        }
    }

    private void executeReportList(List<CompiledReportListReport> listReports, ExecutionContext context) {
        for (CompiledReportListReport report: listReports) {
            report.execute(context);
        }
    }

    private synchronized void initialiseQueryResults(List<CompiledReportQuery> rootQueries) {
        for (CompiledReportQuery query: rootQueries) {
            queryResults.put(query.getJobReportItemUuid(), 0);
        }
    }

    private synchronized void incrementPatientResult(UUID reportItemUuid) {
        queryResults.put(reportItemUuid, queryResults.get(reportItemUuid) + 1);
    }

    public synchronized Map<UUID, Integer> getQueryResults() {
        return queryResults;
    }
}
