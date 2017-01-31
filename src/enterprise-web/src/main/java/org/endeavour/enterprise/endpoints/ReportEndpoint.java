package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.*;
import org.endeavourhealth.core.security.SecurityUtils;
import org.endeavourhealth.enterprise.core.DefinitionItemType;

import org.endeavourhealth.enterprise.core.database.DataManager;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.endeavourhealth.enterprise.core.querydocument.models.ReportItem;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Path("/report")
public final class ReportEndpoint extends AbstractItemEndpoint
{
    private static final Logger LOG = LoggerFactory.getLogger(ReportEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReport")
    public Response getReport(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception {
        super.setLogbackMarkers(sc);

        UUID reportUuid = UUID.fromString(uuidStr);

        LOG.trace("GettingReport for UUID {}", reportUuid);

        ItemEntity item = ItemEntity.retrieveLatestForUUid(reportUuid);
        String xml = item.getXmlcontent();

        Report ret = QueryDocumentSerializer.readReportFromXml(xml);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveReport")
    public Response saveReport(@Context SecurityContext sc, Report report) throws Exception {
        super.setLogbackMarkers(sc);

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = SecurityUtils.getCurrentUserId(sc);

        UUID reportUuid = parseUuidFromStr(report.getUuid());
        String name = report.getName();
        String description = report.getDescription();
        UUID folderUuid = parseUuidFromStr(report.getFolderUuid());

        LOG.trace(String.format("SavingReport UUID %s, Name %s FolderUuid %s", reportUuid, name, folderUuid));

        QueryDocument doc = new QueryDocument();
        doc.getReport().add(report);

        //if we're just renaming or moving a report, the report won't containg report items,
        //so null the query document, so we don't overwrite the one on the DB with an empty one
        if (report.getReportItem().isEmpty()) {
            doc = null;
        }

        boolean inserting = reportUuid == null;
        if (inserting) {
            reportUuid = UUID.randomUUID();
            report.setUuid(reportUuid.toString());
        }

        super.saveItem(inserting, reportUuid, orgUuid, userUuid, DefinitionItemType.Report.getValue(), name, description, doc, folderUuid);

        //return the UUID of the query
        Report ret = new Report();
        ret.setUuid(reportUuid.toString());

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteReport")
    public Response deleteReport(@Context SecurityContext sc, Report reportParameters) throws Exception {
        super.setLogbackMarkers(sc);

        UUID reportUuid = parseUuidFromStr(reportParameters.getUuid());
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = SecurityUtils.getCurrentUserId(sc);

        LOG.trace("DeletingReport UUID {}", reportUuid);

        JsonDeleteResponse ret = deleteItem(reportUuid, orgUuid, userUuid);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/scheduleReport")
    public Response scheduleReport(@Context SecurityContext sc, RequestParameters requestParameters) throws Exception {
        super.setLogbackMarkers(sc);

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = SecurityUtils.getCurrentUserId(sc);

        UUID reportUuid = parseUuidFromStr(requestParameters.getReportUuid());
        String parameterXml = RequestParametersSerializer.writeToXml(requestParameters);

        if (reportUuid == null) {
            throw new BadRequestException("Missing report UUID");
        }

        LOG.trace("ScheduilingReport UUID {}", reportUuid);

        RequestEntity request = new RequestEntity();
        request.setRequestuuid(UUID.randomUUID());
        request.setReportuuid(reportUuid);
        request.setOrganisationuuid(orgUuid);
        request.setEnduseruuid(userUuid);
        request.setTimestamp(Timestamp.from(Instant.now()));
        request.setParameters(parameterXml);

        DataManager db = new DataManager();
        db.saveSchedule(request);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReportSchedules")
    public Response getReportSchedules(@Context SecurityContext sc, @QueryParam("uuid") String reportUuidStr, @QueryParam("count") int count) throws Exception {
        super.setLogbackMarkers(sc);

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID reportUuid = UUID.fromString(reportUuidStr);

        LOG.trace("getPastSchedules for report UUID {} and count {}", reportUuid, count);

        List<RequestEntity> requests = RequestEntity.retrieveForItem(orgUuid, reportUuid, count);
        List<JobreportEntity> jobReports = JobreportEntity.retrieveForRequests(requests);
        List<JobEntity> jobs = JobEntity.retrieveForJobReports(jobReports);

        HashMap<UUID, JobreportEntity> hmJobReportsByUuid = new HashMap<>();
        for (JobreportEntity jobReport: jobReports) {
            hmJobReportsByUuid.put(jobReport.getJobreportuuid(), jobReport);
        }

        HashMap<UUID, JobEntity> hmJobsByUuid = new HashMap<>();
        for (JobEntity job: jobs) {
            hmJobsByUuid.put(job.getJobuuid(), job);
        }

        HashMap<UUID, EnduserEntity> hmUsersByUuid = new HashMap<>();
        List<EnduserEntity> users = EnduserEntity.retrieveForRequests(requests);
        for (EnduserEntity user: users) {
            hmUsersByUuid.put(user.getEnduseruuid(), user);
        }

        List<JsonReportRequest> ret = new ArrayList<>();

        for (RequestEntity request: requests) {

            String parameterXml = request.getParameters();
            JobEntity job = null;

            JobreportEntity jobReport = hmJobReportsByUuid.get(request.getJobreportuuid());
            if (jobReport != null) {
                parameterXml = jobReport.getParameters(); //if our request has been run, use the parameters of when it was run
                job = hmJobsByUuid.get(jobReport.getJobuuid());
            }

            EnduserEntity user = hmUsersByUuid.get(request.getEnduseruuid());

            ret.add(new JsonReportRequest(request, job, user, parameterXml));
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getScheduleResults")
    public Response getScheduleResults(@Context SecurityContext sc, @QueryParam("uuid") String requestUuidStr, @QueryParam("organisation") String organisationOdsCode) throws Exception {
        super.setLogbackMarkers(sc);

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID requestUuid = UUID.fromString(requestUuidStr);

        LOG.trace("getScheduleResults for request UUID {}", requestUuid);

        RequestEntity request = RequestEntity.retrieveForUuid(requestUuid);
        UUID jobReportUuid = request.getJobreportuuid();
        UUID reportUuid = request.getReportuuid();
        String parameters = request.getParameters();

        if (jobReportUuid == null) {
            throw new BadRequestException("Schedule not run yet");
        }

        if (!request.getOrganisationuuid().equals(orgUuid)) {
            throw new BadRequestException("Requesting a schedule at another organisation");
        }

        //get the population count and results for each query
        Integer populationCount = null;
        HashMap<UUID, Integer> hmResultsByQuery = new HashMap<>();

        JobreportEntity jobReport = JobreportEntity.retrieveForUuid(jobReportUuid);
        if (organisationOdsCode == null) {
            populationCount = jobReport.getPopulationcount();
        } else {
            JobreportorganisationEntity jobReportOrganisation = JobreportorganisationEntity.retrieveForJobReportAndOdsCode(jobReport, organisationOdsCode);
            populationCount = jobReportOrganisation.getPopulationcount();
        }

        List<JobreportitemEntity> jobReportItems = JobreportitemEntity.retrieveForJobReport(jobReport.getJobreportuuid());
        for (JobreportitemEntity jobReportItem: jobReportItems) {

            if (organisationOdsCode == null) {
                hmResultsByQuery.put(jobReportItem.getItemuuid(), jobReportItem.getResultcount());
            } else {
                JobreportitemorganisationEntity jobReportItemOrganisation = JobreportitemorganisationEntity.retrieveForJobReportItemAndOdsCode(jobReportItem, organisationOdsCode);
                if (jobReportItemOrganisation != null) { //this may be null if the reportItem is a listOutput
                    hmResultsByQuery.put(jobReportItem.getItemuuid(), jobReportItemOrganisation.getResultcount());
                }

            }
        }

        //retrieve the ItemEntity for the report, so we can work out the report query hierarchy
        UUID auditUuid = jobReport.getAudituuid();
        ItemEntity reportItemObj = ItemEntity.retrieveForUuidAndAudit(reportUuid, auditUuid);
        String xml = reportItemObj.getXmlcontent();
        Report report = QueryDocumentSerializer.readReportFromXml(xml);

        //we'll need the all the child queries in the report, so get them in as few DB hits as possile
        HashMap<UUID, ItemEntity> hmItemsByUuid = getItemsForReport(report);

        List<ReportItem> reportItems = report.getReportItem();
        JsonQueryResult dummyResult = new JsonQueryResult();
        populateReportResults(dummyResult, hmResultsByQuery, hmItemsByUuid, reportItems, populationCount);
        List<JsonQueryResult> queryResults = dummyResult.getChildQueries();

        JsonReportResult ret = new JsonReportResult();
        ret.setPopulationCount(populationCount);
        ret.setQueryResults(queryResults);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    private static void getQueryUuids(List<ReportItem> reportItems, List<UUID> uuids) {
        for (ReportItem reportItem: reportItems) {
            String queryUuidStr = reportItem.getQueryLibraryItemUuid();
            if (queryUuidStr == null || queryUuidStr.isEmpty()) {
                continue;
            }

            UUID queryUuid = UUID.fromString(queryUuidStr);
            uuids.add(queryUuid);
        }
    }
    private static HashMap<UUID, ItemEntity> getItemsForReport(Report report) throws Exception {
        List<UUID> itemUuids = new ArrayList<>();
        getQueryUuids(report.getReportItem(), itemUuids);

        HashMap<UUID, ItemEntity> ret = new HashMap<>();

        List<ItemEntity> items = ItemEntity.retrieveLatestForUuids(itemUuids);
        for (ItemEntity item: items) {
            ret.put(item.getItemuuid(), item);
        }

        return ret;
    }

    private static void populateReportResults(JsonQueryResult parent, HashMap<UUID, Integer> hmResultsByItem,
                                              HashMap<UUID, ItemEntity> hmItemsByUuid, List<ReportItem> reportItems, Integer parentResult) {
        for (ReportItem reportItem: reportItems) {
            String queryUuidStr = reportItem.getQueryLibraryItemUuid();
            if (queryUuidStr == null || queryUuidStr.isEmpty()) {
                continue;
            }

            UUID queryUuid = UUID.fromString(queryUuidStr);
            Integer queryResult = hmResultsByItem.get(queryUuid);
            ItemEntity item = hmItemsByUuid.get(queryUuid);

            JsonQueryResult result = new JsonQueryResult(item, queryResult, parentResult);
            parent.addChildReult(result);

            //if this report item has child report items of its own, then recurse
            List<ReportItem> childReportItems = reportItem.getReportItem();
            if (!childReportItems.isEmpty()) {
                populateReportResults(result, hmResultsByItem, hmItemsByUuid, childReportItems, queryResult);
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/moveReports")
    public Response moveReports(@Context SecurityContext sc, JsonMoveItems parameters) throws Exception {
        super.setLogbackMarkers(sc);

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = SecurityUtils.getCurrentUserId(sc);

        LOG.trace("moveReports");

        super.moveItems(userUuid, orgUuid, parameters);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }
}
