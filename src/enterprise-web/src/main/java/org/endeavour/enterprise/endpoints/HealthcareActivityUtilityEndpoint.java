package org.endeavour.enterprise.endpoints;


import org.endeavourhealth.enterprise.core.database.HealthcareActivityUtilityManager;
import org.endeavourhealth.enterprise.core.database.UtilityManagerCommon;
import org.endeavourhealth.enterprise.core.json.JsonHealthcareActivity;
import org.endeavourhealth.enterprise.core.json.JsonHealthcareActivityGraph;
import org.endeavourhealth.enterprise.core.json.JsonPrevInc;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/HealthcareActivityUtility")
public class HealthcareActivityUtilityEndpoint extends AbstractItemEndpoint {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/runActivityReport")
    public Response reportResult(@Context SecurityContext sc, JsonHealthcareActivity options) throws Exception {
        super.setLogbackMarkers(sc);

        boolean success = new HealthcareActivityUtilityManager().getDenominatorPopulation(options);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(success)
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

        List results = new UtilityManagerCommon().getDistinctValuesForGraphing(columnName, "enterprise_admin.healthcare_activity_raw_data");

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/incidence")
    public Response getIncidenceResults(@Context SecurityContext sc, JsonHealthcareActivityGraph params) throws Exception {
        System.out.println("Retrieving Incidence results");
        super.setLogbackMarkers(sc);


        List results = new HealthcareActivityUtilityManager().getIncidenceResults(params);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }
}
