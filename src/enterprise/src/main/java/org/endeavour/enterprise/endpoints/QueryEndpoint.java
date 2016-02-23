package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.json.JsonQuery;

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
public class QueryEndpoint extends Endpoint
{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getQuery")
    public Response getQuery(@Context SecurityContext sc, @PathParam("uuid") String uuidStr) throws Throwable
    {
        UUID queryUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        JsonQuery ret = new JsonQuery();

        //TODO: 2016-02-23 DL - get query from DB
        ret.setUuid(queryUuid);
        ret.setName("Dummy query");

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveQuery")
    public Response saveQuery(@Context SecurityContext sc, JsonQuery queryParameters) throws Throwable {

        UUID queryUuid = queryParameters.getUuid();

        if (queryUuid == null)
        {
            //TODO: 2016-02-23 DL - save query to DB
        }
        else
        {
            //TODO: 2016-02-23 DL - update query on DB

        }

        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteQuery")
    public Response deleteQuery(@Context SecurityContext sc, JsonQuery queryParameters) throws Throwable {

        UUID queryUuid = queryParameters.getUuid();

        //TODO: 2016-02-23 DL - delete query from DB

        return Response.ok().build();
    }

}
