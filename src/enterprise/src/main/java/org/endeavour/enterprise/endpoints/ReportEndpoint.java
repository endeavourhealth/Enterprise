package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.json.JsonReport;

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
public class ReportEndpoint extends Endpoint
{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReport")
    public Response getReport(@Context SecurityContext sc, @PathParam("uuid") String uuidStr) throws Exception
    {
        UUID reportUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        JsonReport ret = new JsonReport();

        //TODO: 2016-02-23 DL - get report from DB
        ret.setUuid(reportUuid);
        ret.setName("Dummy report");

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveReport")
    public Response saveReport(@Context SecurityContext sc, JsonReport reportParameters) throws Exception {

        UUID reportUuid = reportParameters.getUuid();

        if (reportUuid == null)
        {
            //TODO: 2016-02-23 DL - save report to DB
        }
        else
        {
            //TODO: 2016-02-23 DL - update report on DB

        }

        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteReport")
    public Response deleteReport(@Context SecurityContext sc, JsonReport reportParameters) throws Exception {

        UUID reportUuid = reportParameters.getUuid();

        //TODO: 2016-02-23 DL - delete report from DB

        return Response.ok().build();
    }

}
