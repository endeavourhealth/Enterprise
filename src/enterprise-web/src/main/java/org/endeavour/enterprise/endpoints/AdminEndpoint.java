package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.*;
import org.endeavour.enterprise.entity.json.JsonEndUser;
import org.endeavour.enterprise.entity.json.JsonEndUserList;
import org.endeavour.enterprise.entity.json.JsonOrganisation;
import org.endeavour.enterprise.framework.exceptions.BadRequestException;
import org.endeavour.enterprise.model.DefinitionItemType;
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
public final class AdminEndpoint extends Endpoint {


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveOrganisation")
    public Response saveOrganisation(@Context SecurityContext sc, JsonOrganisation organisationParameters) throws Exception
    {
        //validate our user is a super user
        DbEndUser user = getEndUserFromSession(sc);
        if (!user.getIsSuperUser())
        {
            throw new org.endeavour.enterprise.framework.exceptions.NotAuthorizedException();
        }

        //get the parameters passed up
        UUID uuid = organisationParameters.getUuid();
        String name = organisationParameters.getName();
        String id = organisationParameters.getNationalId();

        DbOrganisation duplicate = DbOrganisation.retrieveOrganisationForNameNationalId(name, id);

        DbOrganisation org = null;
        boolean creteRootFolders = false;

        //if no UUID was passed, then we're creating a new org
        if (uuid == null)
        {
            //ensure we're not creating a duplicate
            if (duplicate != null)
            {
                throw new BadRequestException("Organisation already exists with that name and ID");
            }

            org = new DbOrganisation();
            org.setName(name);
            org.setNationalId(id);

            //whenever we create an org, we'll want to create new root folders too
            creteRootFolders = true;
        }
        else
        {
            org = DbOrganisation.retrieveForUuid(uuid);

            //ensure we're not creating a new duplicate
            if (duplicate != null
                    && !duplicate.equals(org))
            {
                throw new BadRequestException("Organisation already exists with that name and ID");
            }

            org.setName(name);
            org.setNationalId(id);
        }

        //save to db
        org.saveToDb();

        //if we've created new root folders, save them now
        if (creteRootFolders)
        {
            UUID userUuid = getEndUserUuidFromToken(sc);
            UUID orgUuid = org.getPrimaryUuid();

            FolderEndpoint.createTopLevelFolder(orgUuid, userUuid, DefinitionItemType.ReportFolder);
            FolderEndpoint.createTopLevelFolder(orgUuid, userUuid, DefinitionItemType.LibraryFolder);
        }

        //return the organisation UUID
        JsonOrganisation ret = new JsonOrganisation();
        ret.setUuid(org.getPrimaryUuid());

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveUser")
    public Response saveUser(@Context SecurityContext sc, JsonEndUser userParameters) throws Exception
    {
        //first, verify the user is an admin
        EndUserRole currentRole = super.getRoleFromSession(sc);
        if (currentRole != EndUserRole.ADMIN)
        {
            throw new org.endeavour.enterprise.framework.exceptions.NotAuthorizedException();
        }

        //userParameters
        UUID uuid = userParameters.getUuid();
        String email = userParameters.getUsername();
        String title = userParameters.getTitle();
        String forename = userParameters.getForename();
        String surname = userParameters.getSurname();
        Integer permissions = userParameters.getPermissions();
        Boolean isSuperUser = userParameters.getIsSuperUser();

        DbOrganisation org = getOrganisationFromSession(sc);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //validate the parameters passed in
        if (email == null || email.length() == 0)
        {
            throw new BadRequestException("Cannot set blank username");
        }
        if (title == null)
        {
            //allow a blank title
            title = "";
        }
        if (forename == null || forename.length() == 0)
        {
            throw new BadRequestException("Cannot set blank forename");
        }
        if (surname == null || surname.length() == 0)
        {
            throw new BadRequestException("Cannot set blank surname");
        }
        if (permissions == null)
        {
            throw new BadRequestException("Cannot set blank permissions");
        }
        if (isSuperUser == null)
        {
            isSuperUser = new Boolean(false);
        }

        //if doing anything to a super user, verify the current user is a super-user
        if (isSuperUser.booleanValue())
        {
            DbEndUser user = getEndUserFromSession(sc);
            if (!user.getIsSuperUser())
            {
                throw new NotAuthorizedException("Non-super user cannot create or modify super users");
            }
        }

        DbEndUser user = null;
        DbOrganisationEndUserLink link = null;
        Boolean createdNewPerson = null;

        //if the uuid is null, we're creating a new person
        if (uuid == null)
        {
            //see if we have a person for this email address, that the admin user couldn't see, which we can just use
            user = DbEndUser.retrieveForEmail(email);
            if (user == null)
            {
                //if the user doesn't already exist, create it and save to the DB
                createdNewPerson = new Boolean(true);

                user = new DbEndUser();
                user.setEmail(email);
                user.setTitle(title);
                user.setForename(forename);
                user.setSurname(surname);
                user.setIsSuperUser(isSuperUser);

                //we need the UUID of this person, so save right now to generate it
                user.saveToDb();
                uuid = user.getPrimaryUuid();
            }
            //if we're trying to create a new user, but they already exist at another org,
            //then we can just use that same user record and link it to the new organisation
            else
            {
                createdNewPerson = new Boolean(false);

                //validate the name matches what's already on the DB
                if (!user.getForename().equals(forename)
                    || !user.getForename().equals(surname))
                {
                    throw new BadRequestException("User already exists but with different name");
                }

                //validate the person isn't already a user at our org
                uuid = user.getPrimaryUuid();
                link = DbOrganisationEndUserLink.retrieveForOrganisationEndUserNotExpired(orgUuid, uuid);
                if (link != null)
                {
                    throw new BadRequestException("User already is registered here");
                }
            }

            //create the user/org link for non-superusers only, as superusers don't require them
            if (!isSuperUser.booleanValue())
            {
                link = new DbOrganisationEndUserLink();
                link.setOrganisationUuid(orgUuid);
                link.setEndUserUuid(uuid);
                link.setPermissions(permissions);
                link.setDtExpired(DatabaseManager.getEndOfTime());
            }
        }
        //if we have a uuid, we're updating an existing person
        else
        {
            user = DbEndUser.retrieveForUuid(uuid);

            //if we're changing the email, validate that the email isn't already on the DB
            String existingEmail = user.getEmail();
            if (!existingEmail.equals(email))
            {
                DbEndUser duplicateEmail = DbEndUser.retrieveForEmail(email);
                if (duplicateEmail != null)
                {
                    throw new BadRequestException("New email address already in use");
                }
            }

            //we can turn a super-user into a NON-super user, but don't allow going the other way
            if (!user.getIsSuperUser()
                    && isSuperUser.booleanValue())
            {
                throw new BadRequestException("Cannot promote a user to super-user status");
            }

            user.setEmail(email);
            user.setTitle(title);
            user.setForename(forename);
            user.setSurname(surname);
            user.setIsSuperUser(isSuperUser);
            user.saveToDb();

            //retrieve the link entity, as we may want to change the permissions on there
            link = DbOrganisationEndUserLink.retrieveForOrganisationEndUserNotExpired(orgUuid, uuid);

            //the link will be null if we're a super-user, so that's fine
            if (link != null)
            {
                link.setPermissions(permissions);
            }
        }

        //save our things to db
        user.saveToDb();

        if (link != null)
        {
            link.saveToDb();
        }

        //if we just updated a person, then we don't want to generate any invite email
        if (createdNewPerson == null)
        {
            //do nothing
        }
        //if we created a new person, generate the invite email
        else if (createdNewPerson.booleanValue())
        {
            createAndSendInvite(user, org);
        }
        //if we didn't create a new person, then we don't need them to verify and create
        //a password, but we still want to tell the person that they were given new access
        else
        {
            DbEndUserEmailInvite.sendNewAccessGrantedEmail(user, org);
        }

        //return the UUID of the person back to the client
        JsonEndUser ret = new JsonEndUser();
        ret.setUuid(uuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }
    private static void createAndSendInvite(DbEndUser user, DbOrganisation org) throws Exception
    {
        UUID userUuid = user.getPrimaryUuid();

        DbEndUserEmailInvite invite = new DbEndUserEmailInvite();
        invite.setEndUserUuid(userUuid);
        invite.setUniqueToken("" + UUID.randomUUID());
        invite.setDtCompleted(DatabaseManager.getEndOfTime());

        //send the invite email before saving to the DB
        invite.sendInviteEmail(user, org);

        //only save AFTER we've successfully send the invite email
        invite.saveToDb();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteUser")
    public Response deleteUser(@Context SecurityContext sc, JsonEndUser userParameters) throws Exception
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

        List<DbOrganisationEndUserLink> links = DbOrganisationEndUserLink.retrieveForEndUserNotExpired(userUuid);
        for (int i=0; i<links.size(); i++)
        {
            DbOrganisationEndUserLink link = links.get(i);
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
    public Response getUsers(@Context SecurityContext sc) throws Exception
    {
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        JsonEndUserList ret = new JsonEndUserList();

        //retrieve all users at this organisation
        List<DbOrganisationEndUserLink> links = DbOrganisationEndUserLink.retrieveForOrganisationNotExpired(orgUuid);
        for (int i=0; i<links.size(); i++)
        {
            DbOrganisationEndUserLink link = links.get(i);
            UUID endUserUuid = link.getEndUserUuid();
            EndUserRole role = link.getRole();
            DbEndUser endUser = DbEndUser.retrieveForUuid(endUserUuid);

            ret.add(endUser, role);
        }

        //if we're a super-user then we should also include all other super-users in the result
        DbEndUser user = getEndUserFromSession(sc);
        if (user.getIsSuperUser())
        {
            List<DbEndUser> superUsers = DbEndUser.retrieveSuperUsers();
            for (int i=0; i<superUsers.size(); i++)
            {
                DbEndUser superUser = superUsers.get(i);

                //super-users are always treated as admins
                ret.add(superUser, EndUserRole.ADMIN);
            }
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
    public Response resendInviteEmail(@Context SecurityContext sc, JsonEndUser userParameters) throws Exception
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
        List<DbEndUserEmailInvite> invites = DbEndUserEmailInvite.retrieveForEndUserNotCompleted(userUuid);
        for (int i=0; i<invites.size(); i++)
        {
            DbEndUserEmailInvite invite = invites.get(i);
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
