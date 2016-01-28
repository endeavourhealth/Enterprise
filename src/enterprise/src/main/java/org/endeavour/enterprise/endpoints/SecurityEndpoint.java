package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AdminData;
import org.endeavour.enterprise.framework.security.*;
import org.endeavour.enterprise.model.Credentials;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserInRole;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/security")
public class SecurityEndpoint extends Endpoint
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    @Unsecured
    public Response login(Credentials credentials) throws NotAuthorizedException
    {
        AdminData adminData = new AdminData();

        if (!adminData.areCredentialsValid(credentials))
            throw new NotAuthorizedException("Invalid credentials");

        User user = adminData.getUser(credentials.getUsername());

        if (user == null)
            throw new NotAuthorizedException("User not found");

        if (user.getCurrentUserInRole() == null)
            throw new NotAuthorizedException("CurrentUserInRole not found");

        NewCookie cookie = TokenHelper.createTokenAsCookie(user);

        return Response
                .ok()
                .entity(user)
                .cookie(cookie)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/switchUserInRole")
    public Response switchUserInRole(UUID userInRoleUuid) throws NotAuthorizedException
    {
        User user = new AdminData().getUser(getUserContext().getUserUuid());

        if (user == null)
            throw new NotFoundException("User not found");

        UserInRole userInRole = user
                .getUserInRoles()
                .stream()
                .filter(t -> t.getUserInRoleUuid().equals(userInRoleUuid))
                .findFirst()
                .orElse(null);

        if (userInRole == null)
            throw new NotFoundException("UserInRoleUuid does not exist for that user");

        user.setCurrentUserInRoleUuid(userInRole.getUserInRoleUuid());

        NewCookie cookie = TokenHelper.createTokenAsCookie(user);

        return Response
                .ok()
                .entity(user)
                .cookie(cookie)
                .build();
    }
}