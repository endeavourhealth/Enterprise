package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReportItem;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;

import java.util.*;

class ReportCompiler {

    public CompiledReport compile(
            DbJobReport jobReport,
            RequestParameters parameters,
            CompilerContext compilerContext) throws UnableToCompileExpection {

        try {

            List<DbJobReportItem> jobReportItemList = DbJobReportItem.retrieveForJobReport(jobReport.getJobReportUuid());

            List<CompiledReport.CompiledReportQuery> rootQueries = new ArrayList<>();
            List<CompiledReport.CompiledReportListReport> rootListReports = new ArrayList<>();

            populateChildren(compilerContext, null, jobReportItemList, rootQueries, rootListReports);

            Set<String> allowedOrganisations = getAllowedOrganisations(parameters.getOrganisation());

            return new CompiledReport(allowedOrganisations, rootQueries, rootListReports);

        } catch (Exception e) {
            throw new UnableToCompileExpection("JobReportUuid: " + jobReport.getReportUuid(), e);
        }
    }

    private Set<String> getAllowedOrganisations(List<String> organisations) throws UnableToCompileExpection {
        if (CollectionUtils.isEmpty(organisations))
            throw new UnableToCompileExpection("No organisations specified");

        Set<String> allowedOrganisations = new HashSet<>();
        allowedOrganisations.addAll(organisations);
        return allowedOrganisations;
    }

    private void populateChildren(
            CompilerContext compilerContext,
            UUID parentJobReportItemUuid,
            List<DbJobReportItem> jobReportItemList,
            List<CompiledReport.CompiledReportQuery> childQueries,
            List<CompiledReport.CompiledReportListReport> childListReports
            ) throws Exception {

        List<DbJobReportItem> rootItems = getChildren(jobReportItemList, parentJobReportItemUuid);

        for (DbJobReportItem dbJobReportItem: rootItems) {
            UUID itemUuid = dbJobReportItem.getItemUuid();

            if (compilerContext.getCompiledLibrary().isItemOfTypeQuery(itemUuid)) {

                CompiledReport.CompiledReportQuery compiledReportQuery = new CompiledReport.CompiledReportQuery(itemUuid, dbJobReportItem.getJobReportItemUuid());

                childQueries.add(compiledReportQuery);

                populateChildren(compilerContext, dbJobReportItem.getJobReportItemUuid(), jobReportItemList, compiledReportQuery.getChildQueries(), compiledReportQuery.getChildListReports());

            } else if (compilerContext.getCompiledLibrary().isItemOfTypeListReport(itemUuid)) {
                CompiledReport.CompiledReportListReport compiledReportListReport = new CompiledReport.CompiledReportListReport(dbJobReportItem.getItemUuid(), dbJobReportItem.getJobReportItemUuid());

                childListReports.add(compiledReportListReport);
            } else {
                throw new UnableToCompileExpection("JobReportItem is trying to use an item that is either not loaded or not of the correct type: " + itemUuid);
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
