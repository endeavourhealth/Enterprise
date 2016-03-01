package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.entity.json.JsonQuery;
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
@Path("/query")
public final class QueryEndpoint extends ItemEndpoint
{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getQuery")
    public Response getQuery(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception
    {
        UUID queryUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //retrieve the activeItem, so we know the latest version
        DbActiveItem activeItem = super.retrieveActiveItem(queryUuid, orgUuid, DefinitionItemType.Query);
        DbItem item = super.retrieveItem(activeItem);

        JsonQuery ret = new JsonQuery(item, null);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveQuery")
    public Response saveQuery(@Context SecurityContext sc, JsonQuery queryParameters) throws Exception
    {
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        UUID queryUuid = queryParameters.getUuid();
        String name = queryParameters.getName();
        String description = queryParameters.getDescription();
        String xmlContent = queryParameters.getXmlContent();
        Boolean isDeleted = queryParameters.getIsDeleted();
        UUID folderUuid = queryParameters.getFolderUuid();

        queryUuid = super.saveItem(queryUuid, orgUuid, userUuid, DefinitionItemType.Query, name, description, xmlContent, isDeleted, folderUuid);

        //return the UUID of the query
        JsonQuery ret = new JsonQuery();
        ret.setUuid(queryUuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteQuery")
    public Response deleteQuery(@Context SecurityContext sc, JsonQuery queryParameters) throws Exception {

        UUID queryUuid = queryParameters.getUuid();
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        super.deleteItem(queryUuid, orgUuid, userUuid, DefinitionItemType.Query);

        return Response.ok().build();
    }

}
