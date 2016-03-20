package org.endeavourhealth.enterprise.core;

import org.endeavourhealth.enterprise.core.entity.DependencyType;
import org.endeavourhealth.enterprise.core.entity.ExecutionStatus;
import org.endeavourhealth.enterprise.core.entity.database.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentParser;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersParser;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.core.terminology.TerminologyService;
import org.omg.CORBA.Request;

import java.util.*;

/**
 * Created by Drew on 19/03/2016.
 */
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
        job.setStartDateTime(new Date());
        job.setPatientsInDatabase(patientsInDb);
        toSave.add(job);

        //retrieve pending requests
        for (DbRequest request: pendingRequests) {

            UUID reportUuid = request.getReportUuid();
            UUID orgUuid = request.getOrganisationUuuid();

            DbJobReport jobReport = new DbJobReport();
            UUID jobReportUuid = jobReport.assignPrimaryUUid();
            jobReport.setJobUuid(jobUuid);
            jobReport.setReportUuid(reportUuid);
            jobReport.setOrganisationUuid(orgUuid);
            jobReport.setEndUserUuid(request.getEndUserUuid());
            jobReport.setTimeStamp(new Date());
            jobReport.setParameters(request.getParameters());
            toSave.add(jobReport);

            //update the request to link back to the job
            request.setJobUuid(jobUuid);
            toSave.add(request);

            //then create the sub-items for each query in the report being requested
            DbItem dbItem = DbItem.retrieveForUuidLatestVersion(orgUuid, reportUuid);
            String itemXml = dbItem.getXmlContent();
            Report report = QueryDocumentParser.readReportFromXml(itemXml);
            List<ReportItem> items = report.getReportItem();
            for (ReportItem item: items) {
                String queryUuidStr = item.getQueryLibraryItemUuid();
                UUID queryUuid = UUID.fromString(queryUuidStr);

                DbJobReportItem jobReportItem = new DbJobReportItem();
                jobReportItem.setJobReportUuid(jobReportUuid);
                jobReportItem.setItemUuid(queryUuid);
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

            }
        }
    }

    public static void markingJobReportAsFinished(DbJobReport jobReport, ExecutionStatus status) throws Exception {
        jobReport.setStatusId(status);
        jobReport.writeToDb();
    }

    public static void markingJobAsFinished(DbJob job, ExecutionStatus status) throws Exception {
        job.setEndDateTime(new Date());
        job.setStatusId(status);
        job.writeToDb();
    }

    public static RequestParameters getRequestParametersFromJobReport(DbJobReport jobReport) throws Exception {

        RequestParameters requestParameters = RequestParametersParser.readFromJobReport(jobReport);
        return requestParameters;
    }

    public static QueryDocument getQueryDocumentComponentsFromJobReport(DbJobReport jobReport) throws Exception {

        UUID reportUuid = jobReport.getReportUuid();
        UUID orgUuid = jobReport.getOrganisationUuid();

        DbItem item = DbItem.retrieveForUuidLatestVersion(orgUuid, reportUuid);
        Report report = QueryDocumentParser.readReportFromItem(item);

        QueryDocument queryDocument = new QueryDocument();
        queryDocument.getReport().add(report);

        //get dependent items, by recursing down the dependency table
        recursivelyGetDependentLibraryItems(orgUuid, reportUuid, queryDocument);

        return queryDocument;
    }
    private static void recursivelyGetDependentLibraryItems(UUID orgUuid, UUID itemUuid, QueryDocument queryDocument) throws Exception {

        List<DbItem> dependentItems = DbItem.retrieveDependentItems(orgUuid, itemUuid, DependencyType.Uses);
        for (DbItem dependentItem: dependentItems) {

            LibraryItem libraryItem = QueryDocumentParser.readLibraryItemFromItem(dependentItem);
            queryDocument.getLibraryItem().add(libraryItem);

            UUID libraryItemUuid = dependentItem.getPrimaryUuid();
            recursivelyGetDependentLibraryItems(orgUuid, libraryItemUuid, queryDocument);
        }
    }

    public static HashSet<String> getConceptCodesForCodeSet(CodeSet codeSet) throws Exception {
        HashSet<String> codes = TerminologyService.enumerateConcepts(codeSet);
        return codes;
    }

}
