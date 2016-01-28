package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AuthenticationData;
import org.endeavour.enterprise.framework.security.*;
import org.endeavour.enterprise.framework.security.NotAuthorizedException;
import org.endeavour.enterprise.model.Credentials;
import org.endeavour.enterprise.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path("/security")
public class SecurityEndpoint
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    @Unsecured
    public Response login(Credentials credentials)
    {
        try
        {
            AuthenticationData authenticationData = new AuthenticationData();

            if (!authenticationData.areCredentialsValid(credentials))
                throw new NotAuthorizedException("Invalid credentials");

            User user = authenticationData.getUser(credentials.getUsername());

            if (user == null)
                throw new NotAuthorizedException("User not found");

            if (user.getCurrentUserInRole() == null)
                throw new NotAuthorizedException("InitialUserInRole not found");

            String token = TokenHelper.createToken(user, user.getCurrentUserInRole());

            NewCookie cookie = TokenHelper.createCookie(token);

            return Response
                    .ok()
                    .entity(user)
                    .cookie(cookie)
                    .build();
        }
        catch (NotAuthorizedException e)
        {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        catch (Exception e)
        {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}