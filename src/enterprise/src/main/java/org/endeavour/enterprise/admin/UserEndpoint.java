package org.endeavour.enterprise.admin;

import org.endeavour.enterprise.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserEndpoint
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get()
    {
        User user = new User();
        user.setTitle("Dr");
        user.setForename("David");
        user.setSurname("Stables");
        user.setEmail("david.stables@endeavourhealth.org");

        return Response
                .ok(user)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(User user)
    {
        try
        {
            if (user == null)
            {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .build();
            }

            // save user

            return Response
                    .status(Response.Status.CREATED)
                    .build();

        }
        catch (Exception e)
        {
            return Response
                    .status(Response.Status.SERVICE_UNAVAILABLE)
                    .build();
        }
    }
}
