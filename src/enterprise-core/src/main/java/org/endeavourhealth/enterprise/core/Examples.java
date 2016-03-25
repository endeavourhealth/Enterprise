package org.endeavourhealth.enterprise.core;

import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbJob;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReportItem;
import org.endeavourhealth.enterprise.core.database.execution.DbRequest;
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

        //create the job
        DbJob job = new DbJob();
        UUID jobUuid = job.assignPrimaryUUid();
        job.setPrimaryUuid(jobUuid);
        job.setStartDateTime(Instant.now());
        job.setPatientsInDatabase(patientsInDb);
        toSave.add(job);

        //retrieve pending requests
        for (DbRequest request: pendingRequests) {

            UUID reportUuid = request.getReportUuid();

            //find the current audit for the report
            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(reportUuid);
            UUID auditUuid = activeItem.getAuditUuid();

            UUID orgUuid = request.getOrganisationUuuid();

            DbJobReport jobReport = new DbJobReport();
            UUID jobReportUuid = jobReport.assignPrimaryUUid();
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

            //then create the sub-items for each query in the report being requested
            DbItem dbItem = DbItem.retrieveForUuidAndAudit(reportUuid, auditUuid);
            String itemXml = dbItem.getXmlContent();
            Report report = QueryDocumentSerializer.readReportFromXml(itemXml);
            List<ReportItem> items = report.getReportItem();
            for (ReportItem item: items) {

                //report item may have a queryUuid or listOutputUuid
                String queryUuidStr = item.getQueryLibraryItemUuid();
                String listOutputUuidStr = item.getListReportLibraryItemUuid();
//                String parentUuidStr = item.getParentUuid();

                String uuidStr = queryUuidStr;
                if (uuidStr == null) {
                    uuidStr = listOutputUuidStr;
                }
                UUID uuid = UUID.fromString(uuidStr);

//                UUID parentUuid = null;
//                if (parentUuidStr != null) {
//                    parentUuid = UUID.fromString(parentUuidStr);
//                }

                DbJobReportItem jobReportItem = new DbJobReportItem();
                jobReportItem.setJobReportUuid(jobReportUuid);
                jobReportItem.setItemUuid(uuid);
                //jobReportItem.setParentJobReportItemUuid(parentUuid);
                toSave.add(jobReportItem);
            }
        }

        //commit all changes to the DB in one atomic batch
        DatabaseManager.db().writeEntities(toSave);
    }

    public static void findNonCompletedJobsAndContents() throws Exception {

        //retrieve Jobs where status is Executing (should only be ONE in reality, if jobs are always completed before another created)
        List<DbJob> jobs = DbJob.retrieveForStatus(ExecutionStatus.Executing);
        for (DbJob job: jobs) {

            //retrieve JobReports for job
            UUID jobUuid = job.getPrimaryUuid();
            List<DbJobReport> jobReports = DbJobReport.retrieveForJob(jobUuid);

            for (DbJobReport jobReport: jobReports) {

                UUID jobReportUuid = jobReport.getPrimaryUuid();
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

            UUID libraryItemUuid = dependentItem.getPrimaryUuid();
            recursivelyGetDependentLibraryItems(libraryItemUuid, queryDocument);
        }
    }

    public static HashSet<String> getConceptCodesForCodeSet(CodeSet codeSet) throws Exception {
        HashSet<String> codes = TerminologyService.enumerateConcepts(codeSet);
        return codes;
    }

}
