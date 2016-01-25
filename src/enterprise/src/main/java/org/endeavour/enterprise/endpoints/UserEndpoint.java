package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.helpers.ResponseBuilder;
import org.endeavour.enterprise.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserEndpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        User user = new User();
        user.setName("David Stables");
        user.setOrganisation("Endeavour Health Charitable Trust");
        user.setPurpose("Do good things");
        user.setEmail("david.stables@endeavourhealth.org");

        return Response.ok(user).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(User user) {
        try {
            if (user == null)
                return ResponseBuilder.build(Response.Status.BAD_REQUEST);

            // save user

            return ResponseBuilder.build(Response.Status.CREATED);
        } catch (Exception e) {
            return ResponseBuilder.build(Response.Status.SERVICE_UNAVAILABLE);
        }
    }


}
