package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.json.JsonListOutput;

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
public class ListOutputEndpoint extends Endpoint
{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getListOutput")
    public Response getListOutput(@Context SecurityContext sc, @PathParam("uuid") String uuidStr) throws Exception
    {
        UUID listOutputUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        JsonListOutput ret = new JsonListOutput();

        //TODO: 2016-02-23 DL - get listOutput from DB
        ret.setUuid(listOutputUuid);
        ret.setName("Dummy listOutput");

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveListOutput")
    public Response saveListOutput(@Context SecurityContext sc, JsonListOutput listOutputParameters) throws Exception {

        UUID listOutputUuid = listOutputParameters.getUuid();

        if (listOutputUuid == null)
        {
            //TODO: 2016-02-23 DL - save listOutput to DB
        }
        else
        {
            //TODO: 2016-02-23 DL - update listOutput on DB

        }

        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteListOutput")
    public Response deleteListOutput(@Context SecurityContext sc, JsonListOutput listOutputParameters) throws Exception {

        UUID listOutputUuid = listOutputParameters.getUuid();

        //TODO: 2016-02-23 DL - delete listOutput from DB

        return Response.ok().build();
    }

}
