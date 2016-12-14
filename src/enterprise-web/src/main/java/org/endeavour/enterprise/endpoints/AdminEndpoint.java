package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.framework.exceptions.BadRequestException;
import org.endeavour.enterprise.framework.security.PasswordHash;
import org.endeavour.enterprise.framework.security.RequiresAdmin;
import org.endeavour.enterprise.framework.security.RequiresSuperUser;
import org.endeavour.enterprise.json.JsonEndUser;
import org.endeavour.enterprise.json.JsonEndUserList;
import org.endeavour.enterprise.json.JsonOrganisation;
import org.endeavour.enterprise.email.EmailProvider;

import org.endeavourhealth.core.data.admin.models.EndUser;
import org.endeavourhealth.core.data.admin.models.Organisation;
import org.endeavourhealth.core.security.SecurityUtils;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.DataManager;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Endpoint for the functions related to managing person and organisation entities
 */
@Path("/admin")
public final class AdminEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(AdminEndpoint.class);
/*
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveOrganisation")
    @RequiresSuperUser
    public Response saveOrganisation(@Context SecurityContext sc, JsonOrganisation organisationParameters) throws Exception {
        super.setLogbackMarkers(sc);

        //validate our user is a super user
        EndUser user = getEndUserFromSession(sc);
        if (!user.getIsSuperUser()) {
            throw new org.endeavour.enterprise.framework.exceptions.NotAuthorizedException();
        }

        //get the parameters passed up
        UUID uuid = organisationParameters.getUuid();
        String name = organisationParameters.getName();
        String id = organisationParameters.getNationalId();

        LOG.trace("SavingOrganisation OrgUUID {}, Name {} ID {}", uuid, name, id);

        OrganisationEntity duplicate = OrganisationEntity.retrieveOrganisationForNameNationalId(name, id);

        OrganisationEntity org = null;
        boolean creteRootFolders = false;

        //if no UUID was passed, then we're creating a new org
        if (uuid == null) {
            //ensure we're not creating a duplicate
            if (duplicate != null) {
                throw new BadRequestException("Organisation already exists with that name and ID");
            }

            org = new OrganisationEntity();
            org.setName(name);
            org.setNationalid(id);

            //whenever we create an org, we'll want to create new root folders too
            creteRootFolders = true;
        } else {
            org = OrganisationEntity.retrieveForUuid(uuid);

            //ensure we're not creating a new duplicate
            if (duplicate != null
                    && !duplicate.equals(org)) {
                throw new BadRequestException("Organisation already exists with that name and ID");
            }

            org.setName(name);
            org.setNationalid(id);
        }

        //save to db
        DataManager db = new DataManager();
        db.saveOrganisation(org);

        //if we've created new root folders, save them now
        if (creteRootFolders) {
            UUID userUuid = SecurityUtils.getCurrentUserId(sc);
            UUID orgUuid = org.getOrganisationuuid();

            FolderEndpoint.createTopLevelFolder(orgUuid, userUuid, DefinitionItemType.ReportFolder);
            FolderEndpoint.createTopLevelFolder(orgUuid, userUuid, DefinitionItemType.LibraryFolder);
        }

        //return the organisation UUID
        JsonOrganisation ret = new JsonOrganisation();
        ret.setUuid(org.getOrganisationuuid());

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveUser")
    public Response saveUser(@Context SecurityContext sc, JsonEndUser userParameters) throws Exception {
        super.setLogbackMarkers(sc);

        //userParameters
        UUID uuid = userParameters.getUuid();
        String email = userParameters.getUsername();
        String title = userParameters.getTitle();
        String forename = userParameters.getForename();
        String surname = userParameters.getSurname();
        Integer permissions = userParameters.getPermissions();
        Boolean isAdmin = userParameters.getAdmin();
        Boolean isSuperUser = userParameters.getSuperUser();
        String password = userParameters.getPassword();

        //until the web client is changed, we need to use the permissions value
        if (isAdmin == null) {
            if (permissions != null) {
                isAdmin = new Boolean(permissions.intValue() == 2);
            }
        }

        if (isSuperUser == null) {
            isSuperUser = new Boolean(false);
        }

        LOG.trace("SavingUser UserUUID {}, Email {} Title {} Forename {} Surname {} IsAdmin {} IsSuperUser {}", uuid, email, title, forename, surname, isAdmin, isSuperUser);

        //if doing anything to a super user, verify the current user is a super-user
        if (isSuperUser.booleanValue()) {
            EndUser user = getEndUserFromSession(sc);
            if (!user.getIsSuperUser()) {
                throw new NotAuthorizedException("Non-super user cannot create or modify super users");
            }
        }

        //validate that the user is amending themselves or is an admin
        EndUser userLoggedOn = getEndUserFromSession(sc);
        if (uuid == null
                || !userLoggedOn.getId().equals(uuid)) {
            if (!isAdminFromSession(sc)) {
                throw new NotAuthorizedException("Must be an admin to create new users or amend others");
            }
        }

        Organisation org = getOrganisationFromSession(sc);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        EnduserEntity user = null;
        OrganisationenduserlinkEntity link = null;
        Boolean createdNewPerson = null;

        //if the uuid is null, we're creating a new person
        if (uuid == null) {
            //see if we have a person for this email address, that the admin user couldn't see, which we can just use
            user = EnduserEntity.retrieveForEmail(email);

            if (isAdmin == null) {
                isAdmin = Boolean.FALSE;
            }

            if (user == null) {
                //if the user doesn't already exist, create it and save to the DB
                createdNewPerson = Boolean.TRUE;

                if (email == null || email.length() == 0) {
                    throw new BadRequestException("Cannot set blank username");
                }
                if (title == null) {
                    //allow a blank title
                    title = "";
                }
                if (forename == null || forename.length() == 0) {
                    throw new BadRequestException("Cannot set blank forename");
                }
                if (surname == null || surname.length() == 0) {
                    throw new BadRequestException("Cannot set blank surname");
                }

                user = new EnduserEntity();
                user.setEnduseruuid(UUID.randomUUID());
                user.setEmail(email);
                user.setTitle(title);
                user.setForename(forename);
                user.setSurname(surname);
                user.setIssuperuser(isSuperUser);
                uuid = user.getEnduseruuid();
            }
            //if we're trying to create a new user, but they already exist at another org,
            //then we can just use that same user record and link it to the new organisation
            else {
                createdNewPerson = Boolean.FALSE;

                //validate the name matches what's already on the DB
                if (!user.getForename().equalsIgnoreCase(forename)
                        || !user.getSurname().equalsIgnoreCase(surname)) {
                    throw new BadRequestException("User already exists but with different name");
                }

                //validate the person isn't already a user at our org
                uuid = user.getEnduseruuid();
                link = OrganisationenduserlinkEntity.retrieveForOrganisationEndUserNotExpired(orgUuid, uuid);
                if (link != null) {
                    throw new BadRequestException("User already is registered here");
                }
            }

            //create the user/org link for non-superusers only, as superusers don't require them
            if (!isSuperUser.booleanValue()) {
                link = new OrganisationenduserlinkEntity();
                link.setOrganisationuuid(orgUuid);
                link.setEnduseruuid(uuid);
                link.setIsadmin(isAdmin);
            }
        }
        //if we have a uuid, we're updating an existing person
        else {
            user = EnduserEntity.retrieveForUuid(uuid);

            //if we're changing the email, validate that the email isn't already on the DB
            String existingEmail = user.getEmail();
            if (!existingEmail.equals(email)) {
                EnduserEntity duplicateEmail = EnduserEntity.retrieveForEmail(email);
                if (duplicateEmail != null) {
                    throw new BadRequestException("New email address already in use");
                }
            }

            //we can turn a super-user into a NON-super user, but don't allow going the other way
            if (!user.getIssuperuser()
                    && isSuperUser.booleanValue()) {
                throw new BadRequestException("Cannot promote a user to super-user status");
            }

            if (email != null && email.length() > 0) {
                user.setEmail(email);
            }
            if (title != null) {
                user.setTitle(title);
            }
            if (forename != null && forename.length() > 0) {
                user.setForename(forename);
            }
            if (surname != null && surname.length() > 0) {
                user.setSurname(surname);
            }

            user.setIssuperuser(isSuperUser);

            //retrieve the link entity, as we may want to change the permissions on there
            link = OrganisationenduserlinkEntity.retrieveForOrganisationEndUserNotExpired(orgUuid, uuid);

            //the link will be null if we're a super-user, so that's fine
            if (link != null && isAdmin != null) {
                link.setIsadmin(isAdmin);
            }
        }

        DataManager db = new DataManager();
        db.saveUser(user);

        //if a password was supplied, then set or change the password
        EnduserpwdEntity euP = null;

        if (password != null) {
            euP = changePassword(user, userLoggedOn, password);
        }

        //if we just updated a person, then we don't want to generate any invite email
        EnduseremailinviteEntity euInvite = null;

        if (createdNewPerson == null) {
            //do nothing
        }
        //if we created a new person, generate the invite email
        else if (createdNewPerson.booleanValue()) {
            euInvite = createAndSendInvite(user, org);
        }
        //if we didn't create a new person, then we don't need them to verify and create
        //a password, but we still want to tell the person that they were given new access
        else {
            if (!EmailProvider.getInstance().sendNewAccessGrantedEmail(user, org)) {
                throw new InternalServerErrorException("Failed to send new organisation email");
            }
        }

        db.saveUserEntities(euP, euInvite, link);

        //return the UUID of the person back to the client
        JsonEndUser ret = new JsonEndUser();
        ret.setUuid(uuid);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    private static EnduserpwdEntity changePassword(EndUser user, EndUser loggedOnUser, String newPwd) throws Exception {

        //validate the user is changing their own password or is an admin
        Boolean oneTimeUse = Boolean.FALSE;
        if (!loggedOnUser.equals(user)) {
            oneTimeUse = Boolean.TRUE; //if an admin resets a password, then it is one-time use
        }

        String hash = PasswordHash.createHash(newPwd);

        //retrieve the most recent password for the person
        UUID uuid = user.getId();
        EnduserpwdEntity oldPwd = EnduserpwdEntity.retrieveEndUserPwdForUserNotExpired(uuid);

        //create the new password entity
        EnduserpwdEntity p = new EnduserpwdEntity();
        p.setEnduseruuid(uuid);
        p.setPwdhash(hash);
        p.setIsonetimeuse(oneTimeUse);
        p.setFailedattempts(new Integer(0));

        //expire the old password, if there was one
        if (oldPwd != null) {
            oldPwd.setDtexpired(Timestamp.from(Instant.now()));
        }

        //save to db
        DataManager db = new DataManager();
        db.saveUserPassword(p, oldPwd);

        return p;
    }

    private static EnduseremailinviteEntity createAndSendInvite(EnduserEntity user, OrganisationEntity org) throws Exception {
        UUID userUuid = user.getEnduseruuid();

        //use a base64 encoded version of a random UUID
        String tokenUuid = UUID.randomUUID().toString();
        String token = Base64.getEncoder().encodeToString(tokenUuid.getBytes());

        EnduseremailinviteEntity invite = new EnduseremailinviteEntity();
        invite.setEnduseruuid(userUuid);
        invite.setUniquetoken(token);

        //send the invite email before saving to the DB
        if (!EmailProvider.getInstance().sendInviteEmail(user, org, token)) {
            throw new InternalServerErrorException("Failed to send invitation email");
        }

        return invite;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteUser")
    @RequiresAdmin
    public Response deleteUser(@Context SecurityContext sc, JsonEndUser userParameters) throws Exception {
        super.setLogbackMarkers(sc);

        //userParameters
        UUID userUuid = userParameters.getUuid();

        LOG.trace("DeletingUser UserUUID {}", userUuid);

        UUID currentUserUuid = SecurityUtils.getCurrentUserId(sc);
        if (userUuid.equals(currentUserUuid)) {
            throw new BadRequestException("Cannot delete your own account");
        }

        //rather than actually deleting the user record, we mark their link
        //at the current organisation as expired
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        List<OrganisationenduserlinkEntity> links = OrganisationenduserlinkEntity.retrieveForEndUserNotExpired(userUuid);
        for (int i = 0; i < links.size(); i++) {
            OrganisationenduserlinkEntity link = links.get(i);
            if (link.getOrganisationuuid().equals(orgUuid)) {
                link.setDtexpired(Timestamp.from(Instant.now()));
                DataManager db = new DataManager();
                db.deleteUser(link);
            }
        }

        clearLogbackMarkers();

        //don't bother returning anything to the client
        return Response
                .ok()
                .build();
    }
*/
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getUsers")
    public Response getUsers(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("GettingUsers");

        JsonEndUserList ret = new JsonEndUserList();

        //retrieve all users at this organisation
        List<OrganisationenduserlinkEntity> links = OrganisationenduserlinkEntity.retrieveForOrganisationNotExpired(orgUuid);
        for (int i = 0; i < links.size(); i++) {
            OrganisationenduserlinkEntity link = links.get(i);
            UUID endUserUuid = link.getEnduseruuid();
            boolean isAdmin = link.getIsadmin();
            EnduserEntity endUser = EnduserEntity.retrieveForUuid(endUserUuid);

            ret.add(endUser, isAdmin);
        }

        //if we're a super-user then we should also include all other super-users in the result
        EndUser user = getEndUserFromSession(sc);
        if (user.getIsSuperUser()) {
            List<EnduserEntity> superUsers = EnduserEntity.retrieveSuperUsers();
            for (int i = 0; i < superUsers.size(); i++) {
                EnduserEntity superUser = superUsers.get(i);

                //super-users are always treated as admins
                ret.add(superUser, true);
            }
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

/*

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/resendInviteEmail")
    @RequiresAdmin
    public Response resendInviteEmail(@Context SecurityContext sc, JsonEndUser userParameters) throws Exception {
        super.setLogbackMarkers(sc);

        //userParameters
        UUID userUuid = userParameters.getUuid();
        EnduserEntity user = EnduserEntity.retrieveForUuid(userUuid);

        LOG.trace("ResendingInviteEmail UserUUID {}", userUuid);

        //retrieve any existing invite for this person and mark it as completed,
        //so clicking the link in the old email will no longer work
        List<EnduseremailinviteEntity> invitesToSave = new ArrayList<>();

        List<EnduseremailinviteEntity> invites = EnduseremailinviteEntity.retrieveForEndUserNotCompleted(userUuid);
        for (int i = 0; i < invites.size(); i++) {
            EnduseremailinviteEntity invite = invites.get(i);
            invite.setDtcompleted(Timestamp.from(Instant.now()));
            invitesToSave.add(invite);
        }

        //now generate a new invite and send it
        Organisation org = getOrganisationFromSession(sc);
        EnduseremailinviteEntity invite = createAndSendInvite(user, org);

        DataManager db = new DataManager();
        db.saveUserInvites(invitesToSave, invite);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }
*/


}
