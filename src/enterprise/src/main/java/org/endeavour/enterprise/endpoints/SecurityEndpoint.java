package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AdministrationData;
import org.endeavour.enterprise.entity.database.DbPerson;
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
    public Response login(Credentials credentials) throws Throwable
    {
        String email = credentials.getUsername();
        String password = credentials.getPassword();

        DbPerson person = DbPerson.retrieveForEmail(email);
        if (person == null)
        {
            throw new NotAuthorizedException("No user found for email");
        }





        AdministrationData administrationData = new AdministrationData();

        String passwordHash = administrationData.getPasswordHash(credentials.getUsername());

        if ((passwordHash == null) || (passwordHash.isEmpty()))
            throw new NotAuthorizedException("User does not have a password");

        if (!PasswordHash.validatePassword(credentials.getPassword(), passwordHash))
            throw new NotAuthorizedException("Invalid credentials");

        User user = administrationData.getUser(credentials.getUsername());

        if (user == null)
            throw new NotAuthorizedException("User not found");

        if (user.getCurrentUserInRole() == null)
            throw new NotAuthorizedException("CurrentUserInRole not found");

        NewCookie cookie = TokenHelper.createTokenAsCookie(user);

/*
        System.err.println("Going to create response");
        Response r = null;
        try {
            r = Response
                    .ok()
                    .entity(user)
                    .cookie(cookie)
                    .build();
        }
        catch (Throwable t)
        {
            System.err.println("Had exception!");
            t.printStackTrace(System.err);
        }
        return r;
*/

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
        User user = new AdministrationData().getUser(getUserContext().getUserUuid());

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