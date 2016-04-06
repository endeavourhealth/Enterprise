package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.email.EmailProvider;
import org.endeavour.enterprise.framework.security.PasswordHash;
import org.endeavour.enterprise.framework.security.SecurityConfig;
import org.endeavour.enterprise.framework.security.TokenHelper;
import org.endeavour.enterprise.framework.security.Unsecured;
import org.endeavour.enterprise.json.*;
import org.endeavourhealth.enterprise.core.Examples;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.database.administration.*;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
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

        DbEndUser user = DbEndUser.retrieveForEmail(email);
        if (user == null) {
            throw new NotAuthorizedException("No user found for email");
        }

        //retrieve the most recent password for the person
        UUID uuid = user.getEndUserUuid();

        DbEndUserPwd pwd = DbEndUserPwd.retrieveForEndUserNotExpired(uuid);
        if (pwd == null) {
            throw new NotAuthorizedException("No active password for email");
        }

        //validate the password
        String hash = pwd.getPwdHash();
        if (!PasswordHash.validatePassword(password, hash)) {

            int failedAttempts = pwd.getFailedAttempts();
            failedAttempts ++;
            pwd.setFailedAttempts(failedAttempts);
            if (failedAttempts >= SecurityConfig.MAX_FAILED_PASSWORD_ATTEMPTS) {
                pwd.setDtExpired(Instant.now());
            }
            pwd.writeToDb();

            throw new NotAuthorizedException("Invalid password");
        }

        Boolean mustChangePassword = null;
        if (pwd.isOneTimeUse()) {
            pwd.setDtExpired(Instant.now());
            mustChangePassword = Boolean.TRUE;
        }

        pwd.setFailedAttempts(0);
        pwd.writeToDb();

        JsonOrganisationList ret = ret = new JsonOrganisationList();
        DbOrganisation orgToAutoSelect = null;
        Boolean isAdminForAutoSelect = null;

        //now see what organisations the person can access
        //if the person is a superUser, then we want to now prompt them to log on to ANY organisation
        if (user.isSuperUser()) {
            List<DbOrganisation> orgs = DbOrganisation.retrieveForAll();

            for (int i = 0; i < orgs.size(); i++) {
                DbOrganisation o = orgs.get(i);

                //super-users are assumed to be admins at every organisation
                ret.add(o, new Boolean(true));

                //if there's only one organisation, automatically select it
                if (orgs.size() == 1) {
                    orgToAutoSelect = o;
                    isAdminForAutoSelect = new Boolean(true);
                }
            }
        }
        //if the person ISN'T a superUser, then we look at the person/org link, so see where they can log on to
        else {
            List<DbOrganisationEndUserLink> orgLinks = DbOrganisationEndUserLink.retrieveForEndUserNotExpired(uuid);
            if (orgLinks.isEmpty()) {
                throw new NotAuthorizedException("No organisations to log on to");
            }

            for (int i = 0; i < orgLinks.size(); i++) {
                DbOrganisationEndUserLink orgLink = orgLinks.get(i);
                UUID orgUuid = orgLink.getOrganisationUuid();
                DbOrganisation o = DbOrganisation.retrieveForUuid(orgUuid);
                Boolean isAdmin = new Boolean(orgLink.isAdmin());
                ret.add(o, isAdmin);

                //if there's only one organisation, automatically select it
                if (orgLinks.size() == 1) {
                    orgToAutoSelect = o;
                    isAdminForAutoSelect = new Boolean(isAdmin);
                }
            }
        }

        //set the user details in the return object as well
        ret.setUser(new JsonEndUser(user, null, mustChangePassword));

        NewCookie cookie = TokenHelper.createTokenAsCookie(user, orgToAutoSelect, isAdminForAutoSelect);

        super.clearLogbackMarkers();

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

        DbEndUser endUser = getEndUserFromSession(sc);
        UUID endUserUuid = endUser.getEndUserUuid();

        //the only parameter is the org UUID
        UUID orgUuid = orgParameters.getUuid();

        LOG.trace("Selecting organisationUUID {}", orgUuid);

        //validate the organisation exists
        DbOrganisation org = DbOrganisation.retrieveForUuid(orgUuid);
        if (org == null) {
            throw new BadRequestException("Invalid organisation " + orgUuid);
        }

        //validate the person can log on there
        boolean isAdmin = false;

        DbEndUser user = getEndUserFromSession(sc);
        if (user.isSuperUser()) {
            //super users are always admin
            isAdmin = true;
        } else {
            DbOrganisationEndUserLink link = null;
            List<DbOrganisationEndUserLink> links = DbOrganisationEndUserLink.retrieveForEndUserNotExpired(endUserUuid);
            for (int i = 0; i < links.size(); i++) {
                DbOrganisationEndUserLink l = links.get(i);
                if (l.getOrganisationUuid().equals(orgUuid)) {
                    link = l;
                    break;
                }
            }

            if (link == null) {
                throw new BadRequestException("Invalid organisation " + orgUuid + " or user doesn't have access");
            }

            isAdmin = link.isAdmin();
        }

        //issue a new cookie, with the newly selected organisation
        NewCookie cookie = TokenHelper.createTokenAsCookie(endUser, org, isAdmin);

        //return the full org details and the user's role at this place
        JsonOrganisation ret = new JsonOrganisation(org, isAdmin);

        super.clearLogbackMarkers();

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
        NewCookie cookie = TokenHelper.createTokenAsCookie(null, null, false);

        super.clearLogbackMarkers();

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
        DbEndUserEmailInvite invite = DbEndUserEmailInvite.retrieveForToken(token);
        if (invite == null) {
            throw new javax.ws.rs.BadRequestException("No invite found for token");
        }

        UUID userUuid = invite.getEndUserUuid();
        String hash = PasswordHash.createHash(password);

        //now we've found the invite, we can set up the new password for the user
        DbEndUserPwd p = new DbEndUserPwd();
        p.setEndUserUuid(userUuid);
        p.setPwdHash(hash);

        //save
        p.writeToDb();

        //now we've correctly set up the new password for the user, we can delete the invite
        invite.setDtCompleted(Instant.now());
        invite.writeToDb();

        //retrieve the link entity for the org and person
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        DbOrganisationEndUserLink link = DbOrganisationEndUserLink.retrieveForOrganisationEndUserNotExpired(orgUuid, userUuid);
        boolean isAdmin = link.isAdmin();

        //create cookie
        DbEndUser user = DbEndUser.retrieveForUuid(userUuid);
        DbOrganisation org = getOrganisationFromSession(sc);

        NewCookie cookie = TokenHelper.createTokenAsCookie(user, org, isAdmin);

        super.clearLogbackMarkers();

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

        DbEndUser user = DbEndUser.retrieveForEmail(email);
        if (user != null) {

            UUID userUuid = user.getEndUserUuid();

            //the email needs to be linked to an organisation, so just choose one the user can access
            List<DbOrganisationEndUserLink> orgLinks = DbOrganisationEndUserLink.retrieveForEndUserNotExpired(userUuid);
            if (!orgLinks.isEmpty()) {
                DbOrganisationEndUserLink firstLink = orgLinks.get(0);
                UUID orgUuid = firstLink.getOrganisationUuid();
                DbOrganisation org = DbOrganisation.retrieveForUuid(orgUuid);

                List<DbAbstractTable> toSave = new ArrayList<>();

                //expire any existing email invite records, so old tokens won't work
                List<DbEndUserEmailInvite> invites = DbEndUserEmailInvite.retrieveForEndUserNotCompleted(userUuid);
                for (int i = 0; i < invites.size(); i++) {
                    DbEndUserEmailInvite invite = invites.get(i);
                    invite.setDtCompleted(Instant.now());
                    toSave.add(invite);
                }

                //use a base64 encoded version of a random UUID
                String tokenUuid = UUID.randomUUID().toString();
                String token = Base64.getEncoder().encodeToString(tokenUuid.getBytes());

                //now generate a new invite and send it
                DbEndUserEmailInvite invite = new DbEndUserEmailInvite();
                invite.setEndUserUuid(userUuid);
                invite.setUniqueToken(token);
                toSave.add(invite);

                //send the email. If the send fails, it'll be logged on the server, but don't return any failure
                //indication to the client, so we don't give away whether the email existed or not
                if (EmailProvider.getInstance().sendInviteEmail(user, org, token)) {
                    //only save AFTER we've successfully send the invite email
                    DatabaseManager.db().writeEntities(toSave);
                }
            }
        }

        super.clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }

}