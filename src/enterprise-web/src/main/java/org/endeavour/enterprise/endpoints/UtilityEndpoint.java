package org.endeavour.enterprise.endpoints;

import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.enterprise.core.database.ReportManager;
import org.endeavourhealth.enterprise.core.database.UtilityManager;
import org.endeavourhealth.enterprise.core.database.models.data.ReportResultEntity;
import org.endeavourhealth.enterprise.core.json.JsonPrevInc;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.UUID;

@Path("/utility")
public final class UtilityEndpoint extends AbstractItemEndpoint {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/prevInc")
    public Response reportResult(@Context SecurityContext sc, JsonPrevInc options) throws Exception {
        super.setLogbackMarkers(sc);

        boolean success = new UtilityManager().runPrevIncReport(options);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(success)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/incprev")
    public Response getIncPrevResults(@Context SecurityContext sc, @QueryParam("breakdown") String breakdown, @QueryParam("filter") List<String> filter) throws Exception {
        System.out.println("Retrieving Incidence & Prevalence results");
        super.setLogbackMarkers(sc);

        System.out.printf("Breakdown : %s\n", (breakdown==null ? "Null" : breakdown));
        System.out.printf("Filter : %s\n", (filter == null ? "Null" : StringUtils.join(filter, ',')));

        List results = new UtilityManager().getIncPrevResults();

        clearLogbackMarkers();

        return Response
            .ok()
            .entity(results)
            .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/distinctValues")
    public Response distinctValues(@Context SecurityContext sc,
                                      @QueryParam("columnName") String columnName) throws Exception {
        System.out.println("Retrieving distinct values for " + columnName);
        super.setLogbackMarkers(sc);

        List results = new UtilityManager().getDistinctValuesForGraphing(columnName);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }
}
