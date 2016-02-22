package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AdministrationData;
import org.endeavour.enterprise.entity.database.*;
import org.endeavour.enterprise.entity.json.JsonOrganisationList;
import org.endeavour.enterprise.entity.json.JsonEndUser;
import org.endeavour.enterprise.framework.security.*;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserInRole;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Path("/security")
public class SecurityEndpoint extends Endpoint
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    @Unsecured
    public Response login(@Context SecurityContext sc, JsonEndUser personParameters) throws Throwable
    {
        String email = personParameters.getUsername();
        String password = personParameters.getPassword();

        DbEndUser person = DbEndUser.retrieveForEmail(email);
        if (person == null)
        {
            throw new NotAuthorizedException("No user found for email");
        }

        //retrieve the most recent password for the person
        UUID uuid = person.getPrimaryUuid();

        DbEndUserPwd pwd = DbEndUserPwd.retrieveForEndUserNotExpired(uuid);
        if (pwd == null)
        {
            throw new NotAuthorizedException("No active password for email");
        }

        //validate the password
        String hash = pwd.getPwdHash();
        if (!PasswordHash.validatePassword(password, hash))
        {
            throw new NotAuthorizedException("Invalid password");
        }

        JsonOrganisationList ret = null;
        DbOrganisation orgToAutoSelect = null;

        //now see what organisations the person can access
        //if the person is a superUser, then we want to now prompt them to log on to ANY organisation
        if (person.getIsSuperUser())
        {
            List<DbAbstractTable> orgs = DbOrganisation.retrieveForAll();
            ret = new JsonOrganisationList(orgs.size());

            for (int i=0; i<orgs.size(); i++)
            {
                DbOrganisation org = (DbOrganisation)orgs.get(i);
                ret.add(org);

                //if there's only one organisation, automatically select it
                if (orgs.size() == 1)
                {
                    orgToAutoSelect = org;
                }
            }
        }
        //if the person ISN'T a superUser, then we look at the person/org link, so see where they can log on to
        else {
            List<DbAbstractTable> orgLinks = DbOrganisationEndUserLink.retrieveForEndUserNotExpired(uuid);
            if (orgLinks.isEmpty())
            {
                throw new NotAuthorizedException("No organisations to log on to");
            }

            ret = new JsonOrganisationList(orgLinks.size());

            for (int i=0; i<orgLinks.size(); i++)
            {
                DbOrganisationEndUserLink orgLink = (DbOrganisationEndUserLink)orgLinks.get(i);
                UUID orgUuid = orgLink.getOrganisationUuid();
                DbOrganisation org = DbOrganisation.retrieveForUuid(orgUuid);
                ret.add(org);

                //if there's only one organisation, automatically select it
                if (orgLinks.size() == 1)
                {
                    orgToAutoSelect = org;
                }
            }
        }

        if (orgToAutoSelect != null)
        {
            //orgToAutoSelect
        }

        NewCookie cookie = TokenHelper.createTokenAsCookie(person);

        return Response
                .ok()
                .entity(ret)
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


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/changePassword")
    public Response changePassword(JsonEndUser parameters) throws Throwable
    {
        //validate token
        DbEndUser person = new DbEndUser();

        String newPwd = parameters.getPassword();
        String hash = PasswordHash.createHash(newPwd);

        //retrieve the most recent password for the person
        UUID uuid = person.getPrimaryUuid();
        DbEndUserPwd oldPwd = DbEndUserPwd.retrieveForEndUserNotExpired(uuid);

        //create the new password entity
        DbEndUserPwd p = new DbEndUserPwd();
        p.setEndUserUuid(uuid);
        p.setPwdHash(hash);
        p.setDtExpired(new Date(Long.MAX_VALUE)); //should really encapsulate this...

        //save
        p.saveToDb();

        //once we've successfully save the new password entity, make the old one as expired
        if (oldPwd != null)
        {
            p.setDtExpired(new Date());
            p.saveToDb();
        }

        return Response
                .ok()
                .build();
    }
}