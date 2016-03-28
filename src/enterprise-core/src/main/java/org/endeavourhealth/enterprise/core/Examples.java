package org.endeavourhealth.enterprise.core;

import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.definition.DbItemDependency;
import org.endeavourhealth.enterprise.core.database.execution.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.core.terminology.TerminologyService;

import java.time.Instant;
import java.util.*;

public abstract class Examples {


    public static void findingPendingRequestsAndCreateJob() throws Exception {

        //retrieve pending requests, and if none, return out
        List<DbRequest> pendingRequests = DbRequest.retrieveAllPending();
        if (pendingRequests.isEmpty()) {
            return;
        }

        List<DbAbstractTable> toSave = new ArrayList<>();

        int patientsInDb = 0; //get count of patients
        int maxAuditVersion = DbAudit.retrieveMaxAuditVersion();

        //create the job
        DbJob job = new DbJob();
        job.assignPrimaryUUid();
        job.setStartDateTime(Instant.now());
        job.setPatientsInDatabase(patientsInDb);
        job.setBaselineAuditVersion(maxAuditVersion);
        toSave.add(job);

        UUID jobUuid = job.getJobUuid();

        //retrieve pending requests
        for (DbRequest request: pendingRequests) {

            UUID reportUuid = request.getReportUuid();

            //find the current audit for the report
            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(reportUuid);
            UUID auditUuid = activeItem.getAuditUuid();

            UUID orgUuid = request.getOrganisationUuid();

            DbJobReport jobReport = new DbJobReport();
            jobReport.assignPrimaryUUid();
            UUID jobReportUuid = jobReport.getJobReportUuid();
            jobReport.setJobUuid(jobUuid);
            jobReport.setReportUuid(reportUuid);
            jobReport.setAuditUuid(auditUuid);
            jobReport.setOrganisationUuid(orgUuid);
            jobReport.setEndUserUuid(request.getEndUserUuid());
            jobReport.setParameters(request.getParameters());
            toSave.add(jobReport);

            //update the request to link back to the job
            request.setJobUuid(jobUuid);
            toSave.add(request);

            //then create the JobReportItem objects for each query and listOutput in the report being requested
            DbItem dbItem = DbItem.retrieveForUuidAndAudit(reportUuid, auditUuid);
            String itemXml = dbItem.getXmlContent();
            Report report = QueryDocumentSerializer.readReportFromXml(itemXml);
            List<ReportItem> reportItems = report.getReportItem();
            createReportItems(jobUuid, jobReportUuid, null, reportItems, toSave);
        }

        //commit all changes to the DB in one atomic batch
        DatabaseManager.db().writeEntities(toSave);
    }
    private static void createReportItems(UUID jobUuid, UUID jobReportUuid, UUID parentJobReportItemUuid, List<ReportItem> reportItems, List<DbAbstractTable> toSave) throws Exception {

        for (ReportItem reportItem: reportItems) {

            //report item may have a queryUuid OR listOutputUuid
            String queryUuidStr = reportItem.getQueryLibraryItemUuid();
            String listOutputUuidStr = reportItem.getListReportLibraryItemUuid();

            String uuidStr = queryUuidStr;
            if (uuidStr == null) {
                uuidStr = listOutputUuidStr;
            }
            UUID itemUuid = UUID.fromString(uuidStr);

            DbJobReportItem jobReportItem = new DbJobReportItem();
            jobReportItem.assignPrimaryUUid();
            jobReportItem.setJobReportUuid(jobReportUuid);
            jobReportItem.setItemUuid(itemUuid);
            jobReportItem.setParentJobReportItemUuid(parentJobReportItemUuid);
            toSave.add(jobReportItem);

            UUID jobReportItemUuid = jobReportItem.getJobReportItemUuid();

            //create the jobContent objects for ALL dependent items
            createReportContents(jobUuid, itemUuid, toSave);

            //then recurse for any child reportItems
            List<ReportItem> childReportItems = reportItem.getReportItem();
            createReportItems(jobUuid, jobReportUuid, jobReportItemUuid, childReportItems, toSave);
        }
    }
    private static void createReportContents(UUID jobUuid, UUID itemUuid, List<DbAbstractTable> toSave) throws Exception {

        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);
        UUID auditUuid = activeItem.getAuditUuid();

        //if the same query is in a report more than once, we don't want to create duplicate jobContents for it
        for (DbAbstractTable entity: toSave) {
            if (entity instanceof DbJobContent) {
                DbJobContent existingJobContent = (DbJobContent)entity;
                if (existingJobContent.getItemUuid().equals(itemUuid)) {
                    return;
                }
            }
        }

        DbJobContent jobContent = new DbJobContent();
        jobContent.setJobUuid(jobUuid);
        jobContent.setItemUuid(itemUuid);
        jobContent.setAuditUuid(auditUuid);
        jobContent.setSaveMode(TableSaveMode.INSERT); //because the primary keys have been explicitly set, we need to force insert mode
        toSave.add(jobContent);

        //then recurse to find the dependent items on this item
        List<DbItemDependency> itemDependencies = DbItemDependency.retrieveForActiveItem(activeItem);
        for (DbItemDependency itemDependency: itemDependencies) {
            UUID childItemUuid = itemDependency.getDependentItemUuid();
            createReportContents(jobUuid, childItemUuid, toSave);
        }
    }


    public static void findNonCompletedJobsAndContents() throws Exception {

        //retrieve Jobs where status is Executing (should only be ONE in reality, if jobs are always completed before another created)
        List<DbJob> jobs = DbJob.retrieveForStatus(ExecutionStatus.Executing);
        for (DbJob job: jobs) {

            //retrieve JobReports for job
            UUID jobUuid = job.getJobUuid();
            List<DbJobReport> jobReports = DbJobReport.retrieveForJob(jobUuid);
            List<DbJobContent> jobContents = DbJobContent.retrieveForJob(jobUuid);

            for (DbJobReport jobReport: jobReports) {

                UUID jobReportUuid = jobReport.getJobReportUuid();
                List<DbJobReportItem> jobReportItems = DbJobReportItem.retrieveForJobReport(jobReportUuid);

                //also get the parameters objects and query document
                RequestParameters requestParameters = getRequestParametersFromJobReport(jobReport);
                QueryDocument queryDocument = getQueryDocumentComponentsFromJobReport(jobReport);
            }
        }
    }

    public static void markingJobReportAsFinished(DbJobReport jobReport, ExecutionStatus status) throws Exception {
        jobReport.setStatusId(status);
        jobReport.writeToDb();
    }

    public static void markingJobAsFinished(DbJob job, ExecutionStatus status) throws Exception {
        job.setEndDateTime(Instant.now());
        job.setStatusId(status);
        job.writeToDb();
    }

    public static RequestParameters getRequestParametersFromJobReport(DbJobReport jobReport) throws Exception {

        RequestParameters requestParameters = RequestParametersSerializer.readFromJobReport(jobReport);
        return requestParameters;
    }

    public static QueryDocument getQueryDocumentComponentsFromJobReport(DbJobReport jobReport) throws Exception {

        UUID reportUuid = jobReport.getReportUuid();
        UUID auditUuid = jobReport.getAuditUuid();

        DbItem item = DbItem.retrieveForUuidAndAudit(reportUuid, auditUuid);
        Report report = QueryDocumentSerializer.readReportFromItem(item);

        QueryDocument queryDocument = new QueryDocument();
        queryDocument.getReport().add(report);

        //get dependent items, by recursing down the dependency table
        recursivelyGetDependentLibraryItems(reportUuid, queryDocument);

        return queryDocument;
    }
    private static void recursivelyGetDependentLibraryItems(UUID itemUuid, QueryDocument queryDocument) throws Exception {

        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);
        UUID auditUuid = activeItem.getAuditUuid();

        List<DbItem> dependentItems = DbItem.retrieveDependentItems(itemUuid, auditUuid, DependencyType.Uses);
        for (DbItem dependentItem: dependentItems) {

            LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromItem(dependentItem);
            queryDocument.getLibraryItem().add(libraryItem);

            UUID libraryItemUuid = dependentItem.getItemUuid();
            recursivelyGetDependentLibraryItems(libraryItemUuid, queryDocument);
        }
    }

    public static HashSet<String> getConceptCodesForCodeSet(CodeSet codeSet) throws Exception {
        HashSet<String> codes = TerminologyService.enumerateConcepts(codeSet);
        return codes;
    }

}
