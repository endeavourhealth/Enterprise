package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.email.EmailProvider;
import org.endeavour.enterprise.framework.security.PasswordHash;
import org.endeavour.enterprise.framework.security.SecurityConfig;
import org.endeavour.enterprise.framework.security.TokenHelper;
import org.endeavour.enterprise.framework.security.Unsecured;
import org.endeavour.enterprise.json.*;
import org.endeavourhealth.enterprise.core.database.*;

import org.endeavourhealth.enterprise.core.database.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Path("/security")
public final class SecurityEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityEndpoint.class);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    @Unsecured
    public Response login(@Context SecurityContext sc, JsonEndUser personParameters) throws Exception {
        super.setLogbackMarkers(sc);

        String email = personParameters.getUsername();
        String password = personParameters.getPassword();

        LOG.trace("Login for {}", email);

        if (email == null
                || email.length() == 0
                || password == null
                || password.length() == 0) {
            throw new BadRequestException("Missing username or password in request");
        }

        EnduserEntity user = EnduserEntity.retrieveForEmail(email);

        if (user == null) {
            throw new NotAuthorizedException("No user found for email");
        }

        //retrieve the most recent password for the person
        UUID uuid = user.getEnduseruuid();

        EnduserpwdEntity pwd = EnduserpwdEntity.retrieveEndUserPwdForUserNotExpired(uuid);

        if (pwd == null) {
            throw new NotAuthorizedException("No active password for email");
        }

        //validate the password
        String hash = pwd.getPwdhash();
        if (!PasswordHash.validatePassword(password, hash)) {

            int failedAttempts = pwd.getFailedattempts();
            failedAttempts ++;
            pwd.setFailedattempts(failedAttempts);
            if (failedAttempts >= SecurityConfig.MAX_FAILED_PASSWORD_ATTEMPTS) {
                pwd.setDtexpired(Timestamp.from(Instant.now()));
            }
            pwd.writeToDb(pwd);

            throw new NotAuthorizedException("Invalid password");
        }

        Boolean mustChangePassword = null;
        if (pwd.getIsonetimeuse()) {
            pwd.setDtexpired(Timestamp.from(Instant.now()));
            mustChangePassword = Boolean.TRUE;
        }

        pwd.setFailedattempts(0);
        pwd.writeToDb(pwd);

        JsonOrganisationList ret = ret = new JsonOrganisationList();
        OrganisationEntity orgToAutoSelect = null;
        boolean isAdminForAutoSelect = false;
        boolean isSuperUser = user.getIssuperuser();

        //now see what organisations the person can access
        //if the person is a superUser, then we want to now prompt them to log on to ANY organisation
        if (isSuperUser) {
            List<OrganisationEntity> orgs = OrganisationEntity.retrieveForAll();

            for (int i = 0; i < orgs.size(); i++) {
                OrganisationEntity o = orgs.get(i);

                //super-users are assumed to be admins at every organisation
                ret.add(o, new Boolean(true));

                //if there's only one organisation, automatically select it
                if (orgs.size() == 1) {
                    orgToAutoSelect = o;
                    isAdminForAutoSelect = true;
                }
            }
        }
        //if the person ISN'T a superUser, then we look at the person/org link, so see where they can log on to
        else {
            List<OrganisationenduserlinkEntity> orgLinks = OrganisationenduserlinkEntity.retrieveForEndUserNotExpired(uuid);
            if (orgLinks.isEmpty()) {
                throw new NotAuthorizedException("No organisations to log on to");
            }

            for (int i = 0; i < orgLinks.size(); i++) {
                OrganisationenduserlinkEntity orgLink = orgLinks.get(i);
                UUID orgUuid = orgLink.getOrganisationuuid();
                OrganisationEntity o = OrganisationEntity.retrieveForUuid(orgUuid);
                Boolean isAdmin = orgLink.getIsadmin();
                ret.add(o, isAdmin);

                //if there's only one organisation, automatically select it
                if (orgLinks.size() == 1) {
                    orgToAutoSelect = o;
                    isAdminForAutoSelect = isAdmin;
                }
            }
        }

        //set the user details in the return object as well
        ret.setUser(new JsonEndUser(user, null, mustChangePassword));

        String host = TokenHelper.getRequestingHostFromRequest(request);

        NewCookie cookie = TokenHelper.createTokenAsCookie(host, user, orgToAutoSelect, isAdminForAutoSelect, isSuperUser);

        clearLogbackMarkers();

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
    public Response selectOrganisation(@Context SecurityContext sc, JsonOrganisation orgParameters) throws Exception {
        super.setLogbackMarkers(sc);

        EnduserEntity endUser = getEndUserFromSession(sc);
        UUID endUserUuid = endUser.getEnduseruuid();

        //the only parameter is the org UUID
        UUID orgUuid = orgParameters.getUuid();

        LOG.trace("Selecting organisationUUID {}", orgUuid);

        //validate the organisation exists
        OrganisationEntity org = OrganisationEntity.retrieveForUuid(orgUuid);
        if (org == null) {
            throw new BadRequestException("Invalid organisation " + orgUuid);
        }

        EnduserEntity user = getEndUserFromSession(sc);

        //validate the person can log on there
        boolean isAdmin = false;
        boolean isSuperUser = user.getIssuperuser();

        if (isSuperUser) {
            //super users are always admin
            isAdmin = true;
        } else {
            OrganisationenduserlinkEntity link = null;
            List<OrganisationenduserlinkEntity> links = OrganisationenduserlinkEntity.retrieveForEndUserNotExpired(endUserUuid);
            for (int i = 0; i < links.size(); i++) {
                OrganisationenduserlinkEntity l = links.get(i);
                if (l.getOrganisationuuid().equals(orgUuid)) {
                    link = l;
                    break;
                }
            }

            if (link == null) {
                throw new BadRequestException("Invalid organisation " + orgUuid + " or user doesn't have access");
            }

            isAdmin = link.getIsadmin();
        }

        String host = getRequestingHostFromSecurityContext(sc);

        //issue a new cookie, with the newly selected organisation
        NewCookie cookie = TokenHelper.createTokenAsCookie(host, endUser, org, isAdmin, isSuperUser);

        //return the full org details and the user's role at this place
        JsonOrganisation ret = new JsonOrganisation(org, isAdmin);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .cookie(cookie)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/logoff")
    @Unsecured
    public Response logoff(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        LOG.trace("Logoff");

        //replace the cookie on the client with an empty one
        NewCookie cookie = TokenHelper.createTokenAsCookie(null, null, null, false, false);

        clearLogbackMarkers();

        return Response
                .ok()
                .cookie(cookie)
                .build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setPasswordFromInviteToken")
    @Unsecured
    public Response setPasswordFromInviteToken(@Context SecurityContext sc, JsonEmailInviteParameters parameters) throws Exception {
        super.setLogbackMarkers(sc);

        String token = parameters.getToken();
        String password = parameters.getPassword();

        LOG.trace("SettingPasswordFromInviteEmail");

        //find the invite for the token
        EnduseremailinviteEntity invite = EnduseremailinviteEntity.retrieveForToken(token);
        if (invite == null) {
            throw new javax.ws.rs.BadRequestException("No invite found for token");
        }

        UUID userUuid = invite.getEnduseruuid();
        String hash = PasswordHash.createHash(password);

        //now we've found the invite, we can set up the new password for the user
        EnduserpwdEntity p = new EnduserpwdEntity();
        p.setEnduseruuid(userUuid);
        p.setPwdhash(hash);

        //save
        p.writeToDb(p);

        //now we've correctly set up the new password for the user, we can delete the invite
        invite.setDtcompleted(Timestamp.from(Instant.now()));
        DataManager db = new DataManager();
        db.saveUserInvite(invite);

        //retrieve the link entity for the org and person
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        OrganisationenduserlinkEntity link = OrganisationenduserlinkEntity.retrieveForOrganisationEndUserNotExpired(orgUuid, userUuid);
        boolean isAdmin = link.getIsadmin();

        boolean isSuperUser = isSuperUserFromSecurityContext(sc);
        String host = getRequestingHostFromSecurityContext(sc);

        //create cookie
        EnduserEntity user = EnduserEntity.retrieveForUuid(userUuid);
        OrganisationEntity org = getOrganisationFromSession(sc);

        NewCookie cookie = TokenHelper.createTokenAsCookie(host, user, org, isAdmin, isSuperUser);

        clearLogbackMarkers();

        return Response
                .ok()
                .cookie(cookie)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sendPasswordForgottenEmail")
    @Unsecured
    public Response sendPasswordForgottenEmail(@Context SecurityContext sc, JsonUserEmail parameters) throws Exception {
        super.setLogbackMarkers(sc);

        String email = parameters.getEmail();

        LOG.trace("sendPasswordForgottenEmail {}", email);

        EnduserEntity user = EnduserEntity.retrieveForEmail(email);
        if (user != null) {

            UUID userUuid = user.getEnduseruuid();
            List<EnduseremailinviteEntity> invitesToSave = new ArrayList<>();

            //the email needs to be linked to an organisation, so just choose one the user can access
            List<OrganisationenduserlinkEntity> orgLinks = OrganisationenduserlinkEntity.retrieveForEndUserNotExpired(userUuid);
            if (!orgLinks.isEmpty()) {
                OrganisationenduserlinkEntity firstLink = orgLinks.get(0);
                UUID orgUuid = firstLink.getOrganisationuuid();
                OrganisationEntity org = OrganisationEntity.retrieveForUuid(orgUuid);

                //expire any existing email invite records, so old tokens won't work
                List<EnduseremailinviteEntity> invites = EnduseremailinviteEntity.retrieveForEndUserNotCompleted(userUuid);
                for (int i = 0; i < invites.size(); i++) {
                    EnduseremailinviteEntity invite = invites.get(i);
                    invite.setDtcompleted(Timestamp.from(Instant.now()));
                    invitesToSave.add(invite);
                }

                //use a base64 encoded version of a random UUID
                String tokenUuid = UUID.randomUUID().toString();
                String token = Base64.getEncoder().encodeToString(tokenUuid.getBytes());

                //now generate a new invite and send it
                EnduseremailinviteEntity invite = new EnduseremailinviteEntity();
                invite.setEnduseruuid(userUuid);
                invite.setUniquetoken(token);

                //send the email. If the send fails, it'll be logged on the server, but don't return any failure
                //indication to the client, so we don't give away whether the email existed or not
                if (EmailProvider.getInstance().sendInviteEmail(user, org, token)) {
                    //only save AFTER we've successfully send the invite email
                    DataManager db = new DataManager();
                    db.saveUserInvites(invitesToSave, invite);
                }
            }
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }

}