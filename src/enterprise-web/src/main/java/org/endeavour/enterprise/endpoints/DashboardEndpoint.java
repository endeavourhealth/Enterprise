package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.model.json.*;
import org.endeavourhealth.enterprise.core.entity.EndUserRole;
import org.endeavourhealth.enterprise.core.entity.database.*;
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

/**
 * Created by Drew on 19/03/2016.
 */
@Path("/dashboard")
public final class DashboardEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(DashboardEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getRecentDocuments")
    public Response getRecentDocuments(@Context SecurityContext sc, @QueryParam("count") int count) throws Exception {
        UUID userUuid = getEndUserUuidFromToken(sc);

        LOG.trace("getRecentDocuments {}", count);

        List<JsonFolderContent> ret = new ArrayList<>();

        List<DbActiveItem> activeItems = DatabaseManager.db().retrieveActiveItemRecentItems(userUuid, count);
        for (DbActiveItem activeItem: activeItems) {
            DbItem item = DbItem.retrieveForActiveItem(activeItem);
            JsonFolderContent content = new JsonFolderContent(activeItem, item);
            ret.add(content);
        }

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

        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("getReportActivity {}", count);

        List<JsonJobReport> ret = new ArrayList<>();

        List<DbJobReport> jobReports = DbJobReport.retrieveRecent(orgUuid, count);
        for (DbJobReport jobReport: jobReports) {

            UUID itemUuid = jobReport.getReportUuid();
            DbItem item = DbItem.retrieveForUuidLatestVersion(orgUuid, itemUuid);
            String name = item.getTitle();
            Date date = jobReport.getTimeStamp();
            ret.add(new JsonJobReport(name, date));
        }

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

        LOG.trace("getEngineHistory {}", count);

        List<JsonJob> ret = new ArrayList<>();

        List<DbJob> jobs = DbJob.retrieveRecent(count);
        for (DbJob job: jobs) {
            JsonJob jsonJob = new JsonJob(job);
            ret.add(jsonJob);
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }
}
