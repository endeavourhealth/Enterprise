package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReportItem;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ReportCompiler {
    private final CompilerContext compilerContext;

    public ReportCompiler(CompilerContext compilerContext) {
        this.compilerContext = compilerContext;
    }

    public CompiledReport compile(DbJobReport jobReport, RequestParameters parameters) throws UnableToCompileExpection {

        try {

            CompiledReport compiledReport = new CompiledReport();

            List<DbJobReportItem> jobReportItemList = DbJobReportItem.retrieveForJobReport(jobReport.getJobReportUuid());

            populateChildren(null, jobReportItemList, compiledReport.getChildQueries(), compiledReport.getChildListReports());

            compiledReport.initialise();

            return compiledReport;

        } catch (Exception e) {
            throw new UnableToCompileExpection(jobReport.getReportUuid(), e);
        }
    }

    private void populateChildren(
            UUID parentJobReportItemUuid,
            List<DbJobReportItem> jobReportItemList,
            List<CompiledReport.CompiledReportQuery> childQueries,
            List<CompiledReport.CompiledReportListReport> childListReports
            ) throws Exception {

        List<DbJobReportItem> rootItems = getChildren(jobReportItemList, parentJobReportItemUuid);

        for (DbJobReportItem dbJobReportItem: rootItems) {
            DefinitionItemType itemType = compilerContext.getRequiredLibraryItems().getType(dbJobReportItem.getItemUuid());

            if (itemType == DefinitionItemType.Query) {
                CompiledReport.CompiledReportQuery compiledReportQuery = new CompiledReport.CompiledReportQuery(dbJobReportItem.getItemUuid(), dbJobReportItem.getJobReportItemUuid());

                childQueries.add(compiledReportQuery);

                populateChildren(dbJobReportItem.getJobReportItemUuid(), jobReportItemList, compiledReportQuery.getChildQueries(), compiledReportQuery.getChildListReports());

            } else if (itemType == DefinitionItemType.ListOutput) {
                CompiledReport.CompiledReportListReport compiledReportListReport = new CompiledReport.CompiledReportListReport(dbJobReportItem.getItemUuid(), dbJobReportItem.getJobReportItemUuid());

                childListReports.add(compiledReportListReport);
            }
        }
    }

    private List<DbJobReportItem> getChildren(List<DbJobReportItem> jobReportItemList, UUID parentUuid) {
        List<DbJobReportItem> jobReportItems = new ArrayList<>();

        for (DbJobReportItem item: jobReportItemList) {
            if (item.getParentJobReportItemUuid() == null && parentUuid == null)
                jobReportItems.add(item);
            else if (item.getParentJobReportItemUuid() != null && parentUuid != null && item.getParentJobReportItemUuid().equals(parentUuid))
                jobReportItems.add(item);
        }

        return jobReportItems;
    }
}
