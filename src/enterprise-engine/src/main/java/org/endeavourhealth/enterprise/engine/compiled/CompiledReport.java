package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.engine.ResultCounter;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

import java.util.*;

public class CompiledReport {

    private final List<CompiledReportQuery> rootQueries;
    private final List<CompiledReportListReport> rootListReports;
    private final Set<String> allowedOrganisations;

    private final ResultCounter reportLevelResults;
    private final Map<UUID, ResultCounter> jobReportItemResults = new HashMap<>();

    public CompiledReport(
            Set<String> allowedOrganisations,
            List<CompiledReportQuery> rootQueries,
            List<CompiledReportListReport> rootListReports) {

        this.allowedOrganisations = allowedOrganisations;
        this.rootQueries = rootQueries;
        this.rootListReports = rootListReports;

        reportLevelResults = new ResultCounter(allowedOrganisations);

        initialiseQueryResults(rootQueries);
        initialiseListReportResults(rootListReports);
    }

    private void initialiseQueryResults(List<CompiledReportQuery> queries) {

        for (CompiledReportQuery query: queries) {
            jobReportItemResults.put(query.getJobReportItemUuid(), new ResultCounter(allowedOrganisations));

            if (query.getChildQueries() != null) {
                initialiseQueryResults(query.getChildQueries());
                initialiseListReportResults(query.getChildListReports());
            }
        }
    }

    private void initialiseListReportResults(List<CompiledReportListReport> reports) {

        for (CompiledReportListReport report: reports) {
            jobReportItemResults.put(report.getJobReportItemUuid(), new ResultCounter(allowedOrganisations));
        }
    }

    public void execute(ExecutionContext context) throws Exception {

        if (!allowedOrganisations.contains(context.getDataContainer().getOrganisationId()))
            return;

        reportLevelResults.recordResult(context.getDataContainer().getOrganisationId());

        executeQueryList(rootQueries, context);
        executeReportList(rootListReports, context);
    }

    private void executeQueryList(List<CompiledReportQuery> queries, ExecutionContext context) throws Exception {
        if (queries == null)
            return;

        for (CompiledReportQuery query: queries) {
            if (context.getQueryResult(query.getQueryUuid())) {

                jobReportItemResults.get(query.getJobReportItemUuid()).recordResult(context.getDataContainer().getOrganisationId());

                executeQueryList(query.childQueries, context);
                executeReportList(query.childListReports, context);
            }
        }
    }

    private void executeReportList(List<CompiledReportListReport> listReports, ExecutionContext context) throws Exception {
        for (CompiledReportListReport report: listReports) {
            jobReportItemResults.get(report.getJobReportItemUuid()).recordResult(context.getDataContainer().getOrganisationId());
            report.execute(context);
        }
    }

    public Map<UUID, ResultCounter> getJobReportItemResults() {
        return jobReportItemResults;
    }

    public ResultCounter getReportLevelResults() { return reportLevelResults; }

    public static class CompiledReportQuery {
        private final UUID queryUuid;
        private final UUID jobReportItemUuid;
        private List<CompiledReportQuery> childQueries = new ArrayList<>();
        private List<CompiledReportListReport> childListReports = new ArrayList<>();

        public CompiledReportQuery(UUID queryUuid, UUID jobReportItemUuid) {
            this.queryUuid = queryUuid;
            this.jobReportItemUuid = jobReportItemUuid;
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

        public void execute(ExecutionContext context) throws Exception {
            context.executeListReport(listReportUuid, jobReportItemUuid);
        }

        public UUID getJobReportItemUuid() {
            return jobReportItemUuid;
        }
    }
}
