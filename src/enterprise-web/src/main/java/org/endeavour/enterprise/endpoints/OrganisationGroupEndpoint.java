package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.enterprise.core.database.IncidencePrevalenceUtilityManager;
import org.endeavourhealth.enterprise.core.json.JsonOrganisationGroup;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/organisationGroup")
public class OrganisationGroupEndpoint extends  AbstractItemEndpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/organisationGroups")
    public Response organisationGroups(@Context SecurityContext sc) throws Exception {
        System.out.println("Retrieving organisation groups ");
        super.setLogbackMarkers(sc);

        List results = new IncidencePrevalenceUtilityManager().getOrganisationGroups();

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

        List results = new IncidencePrevalenceUtilityManager().getOrganisationsInGroup(groupId);

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

        List results = new IncidencePrevalenceUtilityManager().getAvailableOrganisations();

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

        IncidencePrevalenceUtilityManager incidencePrevalenceUtilityManager = new IncidencePrevalenceUtilityManager();

        Integer groupId = group.getId();

        if (groupId.equals(0)) {
            groupId = incidencePrevalenceUtilityManager.saveNewGroup(group);
            group.setId(groupId);
        } else {
            incidencePrevalenceUtilityManager.updateGroup(group);
        }

        incidencePrevalenceUtilityManager.deleteOrganisationsInGroup(group);
        if (group.getOrganisations().size() > 0)
            incidencePrevalenceUtilityManager.insertGroupOrganisations(group);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(groupId)
                .build();
    }
}
