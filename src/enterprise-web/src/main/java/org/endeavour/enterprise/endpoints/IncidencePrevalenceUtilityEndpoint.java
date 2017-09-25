package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.enterprise.core.database.UtilityManagerCommon;
import org.endeavourhealth.enterprise.core.json.JsonOrganisationGroup;
import org.endeavourhealth.enterprise.core.json.JsonPrevIncGraph;
import org.endeavourhealth.enterprise.core.database.IncidencePrevalenceUtilityManager;
import org.endeavourhealth.enterprise.core.json.JsonPrevInc;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/incidencePrevalenceUtility")
public final class IncidencePrevalenceUtilityEndpoint extends AbstractItemEndpoint {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/prevInc")
    public Response reportResult(@Context SecurityContext sc, JsonPrevInc options) throws Exception {
        super.setLogbackMarkers(sc);

        boolean success = new IncidencePrevalenceUtilityManager().runPrevIncReport(options);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(success)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/incidence")
    public Response getIncidenceResults(@Context SecurityContext sc, JsonPrevIncGraph params) throws Exception {
        System.out.println("Retrieving Incidence results");
        super.setLogbackMarkers(sc);


        List results = new IncidencePrevalenceUtilityManager().getIncidenceResults(params);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/prevalence")
    public Response getPrevanlenceResults(@Context SecurityContext sc, JsonPrevIncGraph params) throws Exception {
        System.out.println("Retrieving Prevalence results");
        super.setLogbackMarkers(sc);


        List results = new IncidencePrevalenceUtilityManager().getPrevalenceResults(params);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/population")
    public Response getPopulationResults(@Context SecurityContext sc, JsonPrevIncGraph params) throws Exception {
        System.out.println("Retrieving Population results");
        super.setLogbackMarkers(sc);


        List results = new IncidencePrevalenceUtilityManager().getPopulationResults(params);

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

        List results = new UtilityManagerCommon().getDistinctValuesForGraphing(columnName, "enterprise_admin.incidence_prevalence_population_list");

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getOptions")
    public Response getOptions(@Context SecurityContext sc) throws Exception {
        System.out.println("Retrieving JSON Options ");
        super.setLogbackMarkers(sc);

        JsonPrevInc options = new IncidencePrevalenceUtilityManager().getReportOptions();

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(options)
                .build();
    }
}
