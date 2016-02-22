package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.*;
import org.endeavour.enterprise.entity.json.*;
import org.endeavour.enterprise.framework.exceptions.*;
import org.endeavour.enterprise.framework.exceptions.BadRequestException;
import org.endeavour.enterprise.framework.security.PasswordHash;
import org.endeavour.enterprise.framework.security.Unsecured;
import org.endeavour.enterprise.model.EndUserRole;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 * Endpoint for the functions related to managing person and organisation entities
 */
@Path("/admin")
public class AdminEndpoint extends Endpoint {


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createOrganisation")
    public Response createOrganisation(@Context SecurityContext sc, JsonOrganisation organisationParameters) throws Throwable
    {
        //validate our user is a super user
        DbEndUser user = getEndUserFromSession(sc);
        if (!user.getIsSuperUser())
        {
            throw new org.endeavour.enterprise.framework.exceptions.NotAuthorizedException();
        }

        //get the parameters passed up
        String name = organisationParameters.getName();
        String id = organisationParameters.getNationanId();

        //create and save the new one
        DbOrganisation org = new DbOrganisation();
        org.setName(name);
        org.setNationalId(id);

        //save to db
        org.saveToDb();

        //return the new organisation
        JsonOrganisation ret = new JsonOrganisation(org, null);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createUser")
    public Response createUser(@Context SecurityContext sc, JsonEndUser userParameters) throws Throwable
    {
        //first, verify the user is an admin
        EndUserRole currentRole = super.getRoleFromSession(sc);
        if (currentRole != EndUserRole.ADMIN)
        {
            throw new org.endeavour.enterprise.framework.exceptions.NotAuthorizedException();
        }

        //userParameters
        String email = userParameters.getUsername();
        String title = userParameters.getTitle();
        String forename = userParameters.getForename();
        String surname = userParameters.getSurname();
        int permissions = userParameters.getPermissions();
        boolean isSuperUser = userParameters.getIsSuperUser();

        //if trying to create a super user, verify the current user is a super-user
        if (isSuperUser)
        {
            DbEndUser user = getEndUserFromSession(sc);
            if (!user.getIsSuperUser())
            {
                throw new org.endeavour.enterprise.framework.exceptions.NotAuthorizedException();
            }
        }

        //verify if we have a person for this email address
        boolean createdNewPerson = false;
        DbEndUser user = DbEndUser.retrieveForEmail(email);
        if (user == null)
        {
            //if the user doesn't already exist, create it and save to the DB
            createdNewPerson = true;

            user = new DbEndUser();
            user.setTitle(title);
            user.setForename(forename);
            user.setSurname(surname);
            user.setIsSuperUser(isSuperUser);

            user.saveToDb();
        }

        //verify there's not already a link between this person and organisation
        UUID userUuid = user.getPrimaryUuid();
        DbOrganisation org = getOrganisationFromSession(sc);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        List<DbAbstractTable> links = DbOrganisationEndUserLink.retrieveForEndUserNotExpired(userUuid);
        for (int i=0; i<links.size(); i++)
        {
            DbOrganisationEndUserLink link = (DbOrganisationEndUserLink)links.get(i);
            if (link.getOrganisationUuid().equals(orgUuid))
            {
                throw new BadRequestException("User already is a registered user here");
            }
        }

        //we now need to create the link between the user and the organisation
        DbOrganisationEndUserLink link = new DbOrganisationEndUserLink();
        link.setOrganisationUuid(orgUuid);
        link.setEndUserUuid(userUuid);
        link.setPermissions(permissions);
        link.setDtExpired(new Date(Long.MAX_VALUE)); //TODO: 2016-02-22 DL - encapsulate this

        link.saveToDb();

        //generate the invite email if we created a new person
        if (createdNewPerson)
        {
            createAndSendInvite(user, org);
        }
        //if we didn't create a new person, then we don't need them to verify and create
        //a password, but we still want to tell the person that they were given new access
        else
        {
            DbEndUserEmailInvite.sendNewAccessGrantedEmail(user, org);
        }

        //don't bother returning anything to the client
        return Response
                .ok()
                .build();

        //return the new person to the client
/*        EndUserRole role = link.getRole();
        JsonEndUser ret = new JsonEndUser(user, role);

        return Response
                .ok()
                .entity(ret)
                .build();*/
    }
    private static void createAndSendInvite(DbEndUser user, DbOrganisation org) throws Throwable
    {
        UUID userUuid = user.getPrimaryUuid();

        DbEndUserEmailInvite invite = new DbEndUserEmailInvite();
        invite.setEndUserUuid(userUuid);
        invite.setUniqueToken("" + UUID.randomUUID());
        invite.setDtCompleted(new Date(Long.MAX_VALUE)); //TODO: 2016-02-22 DL - encapsulate this

        //send the invite email before saving to the DB
        invite.sendInviteEmail(user, org);

        //only save AFTER we've successfully send the invite email
        invite.saveToDb();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteUser")
    public Response deleteUser(@Context SecurityContext sc, JsonEndUser userParameters) throws Throwable
    {
        //first, verify the user is an admin
        EndUserRole currentRole = super.getRoleFromSession(sc);
        if (currentRole != EndUserRole.ADMIN)
        {
            throw new org.endeavour.enterprise.framework.exceptions.NotAuthorizedException();
        }

        //userParameters
        String email = userParameters.getUsername();
        DbEndUser user = DbEndUser.retrieveForEmail(email);

        //verify they're not trying to delete themselves
        DbEndUser currentUser = super.getEndUserFromSession(sc);
        String currentEmail = currentUser.getEmail();

        if (currentEmail.equalsIgnoreCase(email))
        {
            throw new BadRequestException("Cannot delete your own account");
        }

        //rather than actually deleting the user record, we mark their link
        //at the current organisation as expired
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = user.getPrimaryUuid();

        List<DbAbstractTable> links = DbOrganisationEndUserLink.retrieveForEndUserNotExpired(userUuid);
        for (int i=0; i<links.size(); i++)
        {
            DbOrganisationEndUserLink link = (DbOrganisationEndUserLink)links.get(i);
            if (link.getOrganisationUuid().equals(orgUuid))
            {
                link.setDtExpired(new Date());
                link.saveToDb();
            }
        }

        //TODO: 2016-02-22 DL - remove any active sessions from memory for user we just deleted

        //don't bother returning anything to the client
        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getUsers")
    public Response getUsers(@Context SecurityContext sc) throws Throwable
    {
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //retrieve all users at this organisation
        List<DbAbstractTable> links = DbOrganisationEndUserLink.retrieveForOrganisationNotExpired(orgUuid);
        JsonEndUserList ret = new JsonEndUserList(links.size());

        for (int i=0; i<links.size(); i++)
        {
            DbOrganisationEndUserLink link = (DbOrganisationEndUserLink)links.get(i);
            UUID endUserUuid = link.getEndUserUuid();
            EndUserRole role = link.getRole();
            DbEndUser endUser = DbEndUser.retrieveForUuid(endUserUuid);

            ret.add(endUser, role);
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/resendInviteEmail")
    public Response resendInviteEmail(@Context SecurityContext sc, JsonEndUser userParameters) throws Throwable
    {
        //first, verify the user is an admin
        EndUserRole currentRole = super.getRoleFromSession(sc);
        if (currentRole != EndUserRole.ADMIN)
        {
            throw new org.endeavour.enterprise.framework.exceptions.NotAuthorizedException();
        }

        String email = userParameters.getUsername();
        DbEndUser user = DbEndUser.retrieveForEmail(email);
        UUID userUuid = user.getPrimaryUuid();

        //retrieve any existing invite for this person and mark it as completed,
        //so clicking the link in the old email will no longer work
        List<DbAbstractTable> invites = DbEndUserEmailInvite.retrieveForEndUserNotCompleted(userUuid);
        for (int i=0; i<invites.size(); i++)
        {
            DbEndUserEmailInvite invite = (DbEndUserEmailInvite)invites.get(i);
            invite.setDtCompleted(new Date());
            invite.saveToDb();
        }

        //now generate a new invite and send it
        DbOrganisation org = getOrganisationFromSession(sc);
        createAndSendInvite(user, org);

        return Response
                .ok()
                .build();
    }


}
