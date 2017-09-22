package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.enterprise.core.json.JsonOrganisationGroup;
import org.endeavourhealth.enterprise.core.json.JsonPrevIncGraph;
import org.endeavourhealth.enterprise.core.database.UtilityManager;
import org.endeavourhealth.enterprise.core.json.JsonPrevInc;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/incidence")
    public Response getIncidenceResults(@Context SecurityContext sc, JsonPrevIncGraph params) throws Exception {
        System.out.println("Retrieving Incidence results");
        super.setLogbackMarkers(sc);


        List results = new UtilityManager().getIncidenceResults(params);

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


        List results = new UtilityManager().getPrevalenceResults(params);

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


        List results = new UtilityManager().getPopulationResults(params);

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/organisationGroups")
    public Response organisationGroups(@Context SecurityContext sc) throws Exception {
        System.out.println("Retrieving organisation groups ");
        super.setLogbackMarkers(sc);

        List results = new UtilityManager().getOrganisationGroups();

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/organisationsInGroup")
    public Response organisationsInGroup(@Context SecurityContext sc,
                                         @QueryParam("groupId") Integer groupId) throws Exception {
        System.out.println("Retrieving organisations for group  " + groupId.toString());
        super.setLogbackMarkers(sc);

        List results = new UtilityManager().getOrganisationsInGroup(groupId);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/availableOrganisations")
    public Response availableOrganisations(@Context SecurityContext sc) throws Exception {
        System.out.println("Retrieving available organisations ");
        super.setLogbackMarkers(sc);

        List results = new UtilityManager().getAvailableOrganisations();

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveGroup")
    public Response updateGroup(@Context SecurityContext sc,
                                JsonOrganisationGroup group) throws Exception {
        System.out.println("updating Group " + group.getName());
        super.setLogbackMarkers(sc);

        UtilityManager utilityManager = new UtilityManager();

        Integer groupId = group.getId();

        if (groupId.equals(0)) {
            groupId = utilityManager.saveNewGroup(group);
            group.setId(groupId);
        } else {
            utilityManager.updateGroup(group);
        }

        utilityManager.deleteOrganisationsInGroup(group);
        if (group.getOrganisations().size() > 0)
            utilityManager.insertGroupOrganisations(group);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(groupId)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getOptions")
    public Response getOptions(@Context SecurityContext sc) throws Exception {
        System.out.println("Retrieving JSON Options ");
        super.setLogbackMarkers(sc);

        JsonPrevInc options = new UtilityManager().getReportOptions();

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(options)
                .build();
    }
}
