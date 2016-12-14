package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.framework.security.RequiresAdmin;
import org.endeavour.enterprise.framework.security.RequiresSuperUser;
import org.endeavour.enterprise.json.JsonFolderContent;
import org.endeavour.enterprise.json.JsonJob;
import org.endeavour.enterprise.json.JsonJobReport;
import org.endeavour.enterprise.json.JsonProcessorStatus;
import org.endeavour.enterprise.utility.MessagingQueueProvider;
import org.endeavourhealth.core.security.SecurityUtils;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.enterprise.core.ProcessorState;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.database.models.*;
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

        UUID userUuid = SecurityUtils.getCurrentUserId(sc);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("getRecentDocuments {}", count);

        List<JsonFolderContent> ret = new ArrayList<>();

        ActiveitemEntity aI = new ActiveitemEntity();
        
        List<ActiveitemEntity> activeItems = aI.retrieveActiveItemRecentItems(userUuid, orgUuid, count);
        for (ActiveitemEntity activeItem: activeItems) {
            ItemEntity item = ItemEntity.retrieveForActiveItem(activeItem);
            AuditEntity audit = AuditEntity.retrieveForUuid(item.getAudituuid());

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

        List<JobreportEntity> jobReports = JobreportEntity.retrieveRecent(orgUuid, count);
        for (JobreportEntity jobReport: jobReports) {

            JobEntity job = JobEntity.retrieveForUuid(jobReport.getJobuuid());
            UUID itemUuid = jobReport.getReportuuid();
            UUID auditUuid = jobReport.getAudituuid();
            ItemEntity item = ItemEntity.retrieveForUuidAndAudit(itemUuid, auditUuid);
            String name = item.getTitle();

            Date date = new Date(job.getStartdatetime().getTime());
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

        List<JobEntity> jobs = JobEntity.retrieveRecent(count);
        for (JobEntity job: jobs) {
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
    @RequiresSuperUser
    public Response testDatabase(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

//        if (!getEndUserFromSession(sc).getIssuperuser()) {
//            throw new BadRequestException();
//        }

        JobprocessorresultEntity.deleteAllResults();

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
        ProcessorstatusEntity s = ProcessorstatusEntity.retrieveCurrentStatus();
        if (s == null) {
            desc = "Unknown";
        } else {
            desc = String.valueOf(ProcessorState.get(s.getStateid()));
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
    @RequiresAdmin
    public Response startProcessor(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        LOG.trace("startProcessor");

        MessagingQueueProvider.getInstance().startProcessor();
        ProcessorstatusEntity.setCurrentStatus((short)ProcessorState.Starting.getValue());

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/stopProcessor")
    @RequiresAdmin
    public Response stopProcessor(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        LOG.trace("stopProcessor");

        MessagingQueueProvider.getInstance().stopProcessor();
        ProcessorstatusEntity.setCurrentStatus((short)ProcessorState.Stopping.getValue());

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }



}
