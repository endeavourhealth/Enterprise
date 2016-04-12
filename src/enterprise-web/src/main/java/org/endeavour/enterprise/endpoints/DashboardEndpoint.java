package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.JsonFolderContent;
import org.endeavour.enterprise.json.JsonJob;
import org.endeavour.enterprise.json.JsonJobReport;
import org.endeavour.enterprise.json.JsonProcessorStatus;
import org.endeavour.enterprise.utility.MessagingQueueProvider;
import org.endeavourhealth.enterprise.core.ProcessorState;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbJob;
import org.endeavourhealth.enterprise.core.database.execution.DbJobProcessorResult;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.database.execution.DbProcessorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Path("/dashboard")
public final class DashboardEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(DashboardEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getRecentDocuments")
    public Response getRecentDocuments(@Context SecurityContext sc, @QueryParam("count") int count) throws Exception {
        super.setLogbackMarkers(sc);

        UUID userUuid = getEndUserUuidFromToken(sc);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("getRecentDocuments {}", count);

        List<JsonFolderContent> ret = new ArrayList<>();

        List<DbActiveItem> activeItems = DatabaseManager.db().retrieveActiveItemRecentItems(userUuid, orgUuid, count);
        for (DbActiveItem activeItem: activeItems) {
            DbItem item = DbItem.retrieveForActiveItem(activeItem);
            DbAudit audit = DbAudit.retrieveForUuid(item.getAuditUuid());

            JsonFolderContent content = new JsonFolderContent(activeItem, item, audit);
            ret.add(content);
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
    @Path("/getReportActivity")
    public Response getReportActivity(@Context SecurityContext sc, @QueryParam("count") int count) throws Exception {
        super.setLogbackMarkers(sc);

        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("getReportActivity {}", count);

        List<JsonJobReport> ret = new ArrayList<>();

        List<DbJobReport> jobReports = DbJobReport.retrieveRecent(orgUuid, count);
        for (DbJobReport jobReport: jobReports) {

            DbJob job = DbJob.retrieveForUuid(jobReport.getJobUuid());
            UUID itemUuid = jobReport.getReportUuid();
            UUID auditUuid = jobReport.getAuditUuid();
            DbItem item = DbItem.retrieveForUuidAndAudit(itemUuid, auditUuid);
            String name = item.getTitle();

            Date date = new Date(job.getStartDateTime().toEpochMilli());
            ret.add(new JsonJobReport(name, date));
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
    @Path("/getEngineHistory")
    public Response getEngineHistory(@Context SecurityContext sc, @QueryParam("count") int count) throws Exception {
        super.setLogbackMarkers(sc);

        LOG.trace("getEngineHistory {}", count);

        List<JsonJob> ret = new ArrayList<>();

        List<DbJob> jobs = DbJob.retrieveRecent(count);
        for (DbJob job: jobs) {
            JsonJob jsonJob = new JsonJob(job);
            ret.add(jsonJob);
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
    @Path("/testDatabase")
    public Response testDatabase(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        if (!getEndUserFromSession(sc).isSuperUser()) {
            throw new BadRequestException();
        }

        DbJobProcessorResult.deleteAllResults();

        LOG.trace("testDatabase");

        //DatabaseManager.getInstance().sqlTest();

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getProcessorStatus")
    public Response getProcessorStatus(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        LOG.trace("getProcessorStatus");

        String desc = null;
        DbProcessorStatus s = DbProcessorStatus.retrieveCurrentStatus();
        if (s == null) {
            desc = "Unknown";
        } else {
            desc = s.getStateId().toString();
        }
        JsonProcessorStatus ret = new JsonProcessorStatus(desc);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/startProcessor")
    public Response startProcessor(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        LOG.trace("startProcessor");

        MessagingQueueProvider.getInstance().startProcessor();
        DbProcessorStatus.setCurrentStatus(ProcessorState.Starting);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/stopProcessor")
    public Response stopProcessor(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        LOG.trace("stopProcessor");

        MessagingQueueProvider.getInstance().stopProcessor();
        DbProcessorStatus.setCurrentStatus(ProcessorState.Stopping);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }



}
