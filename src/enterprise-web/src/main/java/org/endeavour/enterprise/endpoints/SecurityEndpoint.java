package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.framework.security.PasswordHash;
import org.endeavour.enterprise.framework.security.TokenHelper;
import org.endeavour.enterprise.framework.security.Unsecured;
import org.endeavour.enterprise.json.JsonEmailInviteParameters;
import org.endeavour.enterprise.json.JsonEndUser;
import org.endeavour.enterprise.json.JsonOrganisation;
import org.endeavour.enterprise.json.JsonOrganisationList;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.database.administration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Instant;
import java.util.ArrayList;
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
        String email = personParameters.getUsername();
        String password = personParameters.getPassword();

        LOG.trace("Login for {}", email);

        /*ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("EntityMap.xsd").getFile());
        LOG.trace("{}", file);

        file = new File(classLoader.getResource("logback.xml").getFile());
        LOG.trace("{}", file);

        String s = Resources.getResourceAsString("EntityMap.xsd");
        LOG.trace(s);
        s = Resources.getResourceAsString("logback.xml");
        LOG.trace(s);*/

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
            throw new NotAuthorizedException("Invalid password");
        }

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
        ret.setUser(new JsonEndUser(user, null));

        NewCookie cookie = TokenHelper.createTokenAsCookie(user, orgToAutoSelect, isAdminForAutoSelect);

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
        LOG.trace("Logoff");

        //TODO: 2016-02-22 DL - once we have server-side sessions, should remove it here

        //replace the cookie on the client with an empty one
        NewCookie cookie = TokenHelper.createTokenAsCookie(null, null, false);

        return Response
                .ok()
                .cookie(cookie)
                .build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/changePassword")
    public Response changePassword(@Context SecurityContext sc, JsonEndUser parameters) throws Exception {
        //validate token
        DbEndUser user = getEndUserFromSession(sc);

        String newPwd = parameters.getPassword();

        LOG.trace("Changing password");

        //validate we have a new password
        if (newPwd == null
                || newPwd.length() == 0) {
            throw new BadRequestException("No new password provided");
        }

        String hash = PasswordHash.createHash(newPwd);

        //retrieve the most recent password for the person
        UUID uuid = user.getEndUserUuid();
        DbEndUserPwd oldPwd = DbEndUserPwd.retrieveForEndUserNotExpired(uuid);

        //create the new password entity
        DbEndUserPwd p = new DbEndUserPwd();
        p.setEndUserUuid(uuid);
        p.setPwdHash(hash);

        //save both old and new passwords atomically
        List<DbAbstractTable> toSave = new ArrayList<>();
        toSave.add(p);
        if (oldPwd != null) {
            oldPwd.setDtExpired(Instant.now());
            toSave.add(oldPwd);
        }

        DatabaseManager.db().writeEntities(toSave);

        return Response
                .ok()
                .build();
    }

    /**
     * @Path("customer") public class CustomerResource {
     * @GET
     * @Path("id/{id}")
     * @Produces(MediaType.APPLICATION_JSON) public Customer getCustomer(@PathParam("id") String id) {
     * Customer customer = new Customer();
     * customer.setId(id);
     * customer.setCity("Austin");
     * customer.setState("TX");
     * customer.setName("Mighty Pulpo");
     * return customer;
     * }
     * }
     */

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setPasswordFromInviteEmail")
    @Unsecured
    public Response setPasswordFromInviteEmail(@Context SecurityContext sc, JsonEmailInviteParameters parameters) throws Exception {
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