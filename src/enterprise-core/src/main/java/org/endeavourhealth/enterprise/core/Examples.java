package org.endeavourhealth.enterprise.core;

import org.endeavourhealth.enterprise.core.entity.ExecutionStatus;
import org.endeavourhealth.enterprise.core.entity.database.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentParser;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.endeavourhealth.enterprise.core.querydocument.models.ReportItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 19/03/2016.
 */
public abstract class Examples {


    public static void findingPendingRequestsAndCreatingJobs() throws Exception {

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
        List<DbRequest> pendingRequests = DbRequest.retrieveAllPending();
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
                jobReportItem.setQueryUuid(queryUuid);
                toSave.add(jobReportItem);
            }
        }

        //commit all changes to the DB in one atomic batch
        DatabaseManager.db().writeEntities(toSave);
    }

    public static void markingJobAsFinished(DbJob job, ExecutionStatus status) throws Exception {
        job.setEndDateTime(new Date());
        job.setStatusId(status);
        job.writeToDb();
    }


}
