package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.database.models.JobreportEntity;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;

import java.util.*;

class ReportCompiler {

    public CompiledReport compile(
            JobreportEntity jobReport,
            RequestParameters parameters,
            CompilerContext compilerContext) throws UnableToCompileExpection {

        try {

            List<JobreportitemEntity> jobReportItemList = JobreportitemEntity.retrieveForJobReport(jobReport.getJobreportuuid());

            List<CompiledReport.CompiledReportQuery> rootQueries = new ArrayList<>();
            List<CompiledReport.CompiledReportListReport> rootListReports = new ArrayList<>();

            populateChildren(compilerContext, null, jobReportItemList, rootQueries, rootListReports);

            Set<String> allowedOrganisations = getAllowedOrganisations(parameters.getOrganisation());

            return new CompiledReport(allowedOrganisations, rootQueries, rootListReports);

        } catch (Exception e) {
            throw new UnableToCompileExpection("JobReportUuid: " + jobReport.getReportuuid(), e);
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
            List<JobreportitemEntity> jobReportItemList,
            List<CompiledReport.CompiledReportQuery> childQueries,
            List<CompiledReport.CompiledReportListReport> childListReports
            ) throws Exception {

        List<JobreportitemEntity> rootItems = getChildren(jobReportItemList, parentJobReportItemUuid);

        for (JobreportitemEntity dbJobReportItem: rootItems) {
            UUID itemUuid = dbJobReportItem.getItemuuid();

            if (compilerContext.getCompiledLibrary().isItemOfTypeQuery(itemUuid)) {

                CompiledReport.CompiledReportQuery compiledReportQuery = new CompiledReport.CompiledReportQuery(itemUuid, dbJobReportItem.getJobreportitemuuid());

                childQueries.add(compiledReportQuery);

                populateChildren(compilerContext, dbJobReportItem.getJobreportitemuuid(), jobReportItemList, compiledReportQuery.getChildQueries(), compiledReportQuery.getChildListReports());

            } else if (compilerContext.getCompiledLibrary().isItemOfTypeListReport(itemUuid)) {
                CompiledReport.CompiledReportListReport compiledReportListReport = new CompiledReport.CompiledReportListReport(dbJobReportItem.getItemuuid(), dbJobReportItem.getJobreportitemuuid());

                childListReports.add(compiledReportListReport);
            } else {
                throw new UnableToCompileExpection("JobReportItem is trying to use an item that is either not loaded or not of the correct type: " + itemUuid);
            }
        }
    }

    private List<JobreportitemEntity> getChildren(List<JobreportitemEntity> jobReportItemList, UUID parentUuid) {
        List<JobreportitemEntity> jobReportItems = new ArrayList<>();

        for (JobreportitemEntity item: jobReportItemList) {
            if (item.getParentjobreportitemuuid() == null && parentUuid == null)
                jobReportItems.add(item);
            else if (item.getParentjobreportitemuuid() != null && parentUuid != null && item.getParentjobreportitemuuid().equals(parentUuid))
                jobReportItems.add(item);
        }

        return jobReportItems;
    }
}
