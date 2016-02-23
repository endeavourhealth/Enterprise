package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.*;
import org.endeavour.enterprise.entity.json.*;
import org.endeavour.enterprise.framework.security.*;
import org.endeavour.enterprise.model.EndUserRole;

import javax.ws.rs.*;
import javax.ws.rs.NotAuthorizedException;
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

        DbEndUser user = DbEndUser.retrieveForEmail(email);
        if (user == null)
        {
            throw new NotAuthorizedException("No user found for email");
        }

        //retrieve the most recent password for the person
        UUID uuid = user.getPrimaryUuid();

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
        EndUserRole endUserRoleToAutoSelect = null;

        //now see what organisations the person can access
        //if the person is a superUser, then we want to now prompt them to log on to ANY organisation
        if (user.getIsSuperUser())
        {
            List<DbAbstractTable> orgs = DbOrganisation.retrieveForAll();
            ret = new JsonOrganisationList(orgs.size());

            //super-users are assumed to be admins at every organisation
            EndUserRole endUserRole = EndUserRole.ADMIN;

            for (int i=0; i<orgs.size(); i++)
            {
                DbOrganisation o = (DbOrganisation)orgs.get(i);

                ret.add(o, endUserRole);

                //if there's only one organisation, automatically select it
                if (orgs.size() == 1)
                {
                    orgToAutoSelect = o;
                    endUserRoleToAutoSelect = endUserRole;
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
                DbOrganisation o = DbOrganisation.retrieveForUuid(orgUuid);
                EndUserRole role = orgLink.getRole();
                ret.add(o, role);

                //if there's only one organisation, automatically select it
                if (orgLinks.size() == 1)
                {
                    orgToAutoSelect = o;
                }
            }
        }

        //set the user details in the return object as well
        ret.setUser(new JsonEndUser(user, null));

        NewCookie cookie = TokenHelper.createTokenAsCookie(user, orgToAutoSelect, endUserRoleToAutoSelect);

        return Response
                .ok()
                .entity(ret)
                .cookie(cookie)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/selectOrganisation")
    public Response selectOrganisation(@Context SecurityContext sc, JsonOrganisation orgParameters) throws Throwable
    {
        DbEndUser endUser = getEndUserFromSession(sc);
        UUID endUserUuid = endUser.getPrimaryUuid();

        //the only parameter is the org UUID
        UUID orgUuid = orgParameters.getUuid();

        //validate the organisation exists
        DbOrganisation org = DbOrganisation.retrieveForUuid(orgUuid);
        if (org == null)
        {
            throw new org.endeavour.enterprise.framework.exceptions.BadRequestException("Invalid organisation " + orgUuid);
        }

        //validate the person can log on there
        DbOrganisationEndUserLink link = null;
        List<DbAbstractTable> links = DbOrganisationEndUserLink.retrieveForEndUserNotExpired(endUserUuid);
        for (int i=0; i<links.size(); i++)
        {
            DbOrganisationEndUserLink l = (DbOrganisationEndUserLink)links.get(i);
            if (l.getOrganisationUuid().equals(orgUuid))
            {
                link = l;
                break;
            }
        }

        if (link == null)
        {
            throw new org.endeavour.enterprise.framework.exceptions.BadRequestException("Invalid organisation " + orgUuid + " or user doesn't have access");
        }

        EndUserRole role = link.getRole();

        //issue a new cookie, with the newly selected organisation
        NewCookie cookie = TokenHelper.createTokenAsCookie(endUser, org, role);

        return Response
                .ok()
                //.entity(ret)
                .cookie(cookie)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/logoff")
    @Unsecured
    public Response logoff(@Context SecurityContext sc) throws Throwable
    {
        //TODO: 2016-02-22 DL - once we have server-side sessions, should remove it here

        //replace the cookie on the client with an empty one
        NewCookie cookie = TokenHelper.createTokenAsCookie(null, null, null);

        return Response
                .ok()
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
        p.setDtExpired(new Date(Long.MAX_VALUE)); //TODO: 2016-02-22 DL - encapsulate this

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

    /**
     @Path("customer")
     public class CustomerResource {
     @GET
     @Path("id/{id}")
     @Produces(MediaType.APPLICATION_JSON)
     public Customer getCustomer(@PathParam("id") String id) {
     Customer customer = new Customer();
     customer.setId(id);
     customer.setCity("Austin");
     customer.setState("TX");
     customer.setName("Mighty Pulpo");
     return customer;
     }
     }
     */

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setPasswordFromInviteEmail")
    @Unsecured
    public Response setPasswordFromInviteEmail(@Context SecurityContext sc, JsonEmailInviteParameters parameters) throws Throwable
    {
        String token = parameters.getToken();
        String password = parameters.getPassword();

        //find the invite for the token
        DbEndUserEmailInvite invite = DbEndUserEmailInvite.retrieveForToken(token);
        if (invite == null)
        {
            throw new javax.ws.rs.BadRequestException("No invite found for token");
        }

        UUID userUuid = invite.getEndUserUuid();
        String hash = PasswordHash.createHash(password);

        //now we've found the invite, we can set up the new password for the user
        DbEndUserPwd p = new DbEndUserPwd();
        p.setEndUserUuid(userUuid);
        p.setPwdHash(hash);
        p.setDtExpired(new Date(Long.MAX_VALUE)); //TODO: 2016-02-22 DL - encapsulate this

        //save
        p.saveToDb();

        //now we've correctly set up the new password for the user, we can delete the invite
        invite.setDtCompleted(new Date());
        invite.saveToDb();

        //retrieve the link entity for the org and person
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        DbOrganisationEndUserLink link = DbOrganisationEndUserLink.retrieveForOrganisationEndUserNotExpired(orgUuid, userUuid);
        EndUserRole role = link.getRole();

        //create cookie
        DbEndUser user = DbEndUser.retrieveForUuid(userUuid);
        DbOrganisation org = getOrganisationFromSession(sc);

        NewCookie cookie = TokenHelper.createTokenAsCookie(user, org, role);

        return Response
                .ok()
                .cookie(cookie)
                .build();
    }


/*    @POST
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
    }*/
}