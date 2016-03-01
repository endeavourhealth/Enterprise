package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.entity.json.JsonListOutput;
import org.endeavour.enterprise.entity.json.JsonQuery;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.UUID;

/**
 * Created by Drew on 23/02/2016.
 */
@Path("/listOutput")
public final class ListOutputEndpoint extends ItemEndpoint
{
    private static final Logger LOG = LoggerFactory.getLogger(ListOutputEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getListOutput")
    public Response getListOutput(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception
    {
        UUID listOutputUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("GettingListOutput for UUID {}", listOutputUuid);

        //retrieve the activeItem, so we know the latest version
        DbActiveItem activeItem = super.retrieveActiveItem(listOutputUuid, orgUuid, DefinitionItemType.ListOutput);
        DbItem item = super.retrieveItem(activeItem);

        JsonListOutput ret = new JsonListOutput(item, null);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveListOutput")
    public Response saveListOutput(@Context SecurityContext sc, JsonListOutput listOutputParameters) throws Exception
    {
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        UUID listOutputUuid = listOutputParameters.getUuid();
        String name = listOutputParameters.getName();
        String description = listOutputParameters.getDescription();
        String xmlContent = listOutputParameters.getXmlContent();
        Boolean isDeleted = listOutputParameters.getIsDeleted();
        UUID folderUuid = listOutputParameters.getFolderUuid();

        LOG.trace("SavingListOutput UUID {}, Name {} IsDeleted {} FolderUuid", listOutputUuid, name, isDeleted, folderUuid);

        listOutputUuid = super.saveItem(listOutputUuid, orgUuid, userUuid, DefinitionItemType.ListOutput, name, description, xmlContent, isDeleted, folderUuid);

        //return the UUID of the query
        JsonQuery ret = new JsonQuery();
        ret.setUuid(listOutputUuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteListOutput")
    public Response deleteListOutput(@Context SecurityContext sc, JsonListOutput listOutputParameters) throws Exception {

        UUID listOutputUuid = listOutputParameters.getUuid();
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        LOG.trace("DeletingListOutput UUID {}", listOutputUuid);

        super.deleteItem(listOutputUuid, orgUuid, userUuid, DefinitionItemType.ListOutput);

        return Response.ok().build();
    }

}
