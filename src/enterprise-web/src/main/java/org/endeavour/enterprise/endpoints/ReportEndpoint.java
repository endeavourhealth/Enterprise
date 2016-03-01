package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.entity.json.JsonQuery;
import org.endeavour.enterprise.entity.json.JsonReport;
import org.endeavour.enterprise.model.DefinitionItemType;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.UUID;

/**
 * Created by Drew on 23/02/2016.
 */
@Path("/report")
public final class ReportEndpoint extends ItemEndpoint
{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReport")
    public Response getReport(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception
    {
        UUID reportUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //retrieve the activeItem, so we know the latest version
        DbActiveItem activeItem = super.retrieveActiveItem(reportUuid, orgUuid, DefinitionItemType.ListOutput);
        DbItem item = super.retrieveItem(activeItem);

        JsonReport ret = new JsonReport(item, null);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveReport")
    public Response saveReport(@Context SecurityContext sc, JsonReport reportParameters) throws Exception
    {
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        UUID reportUuid = reportParameters.getUuid();
        String name = reportParameters.getName();
        String description = reportParameters.getDescription();
        String xmlContent = reportParameters.getXmlContent();
        Boolean isDeleted = reportParameters.getIsDeleted();
        UUID folderUuid = reportParameters.getFolderUuid();

        reportUuid = super.saveItem(reportUuid, orgUuid, userUuid, DefinitionItemType.Report, name, description, xmlContent, isDeleted, folderUuid);

        //return the UUID of the query
        JsonQuery ret = new JsonQuery();
        ret.setUuid(reportUuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteReport")
    public Response deleteReport(@Context SecurityContext sc, JsonReport reportParameters) throws Exception {

        UUID reportUuid = reportParameters.getUuid();
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        super.deleteItem(reportUuid, orgUuid, userUuid, DefinitionItemType.Report);

        return Response.ok().build();
    }

}
