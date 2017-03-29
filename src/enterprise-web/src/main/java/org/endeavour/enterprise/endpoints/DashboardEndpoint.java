package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.json.JsonFolderContent;
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

        String userUuid = SecurityUtils.getCurrentUserId(sc).toString();

        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        LOG.trace("getRecentDocuments {}", count);

        List<JsonFolderContent> ret = new ArrayList<>();

        ActiveItemEntity aI = new ActiveItemEntity();

        List<ActiveItemEntity> activeItems = aI.retrieveActiveItemRecentItems(userUuid, orgUuid, count);
        for (ActiveItemEntity activeItem: activeItems) {
            ItemEntity item = ItemEntity.retrieveForActiveItem(activeItem);
            AuditEntity audit = AuditEntity.retrieveForUuid(item.getAuditUuid());

            JsonFolderContent content = new JsonFolderContent(activeItem, item, audit, null);
            ret.add(content);
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }







}
