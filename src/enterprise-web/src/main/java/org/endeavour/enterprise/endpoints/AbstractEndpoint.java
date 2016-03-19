package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.framework.exceptions.BadRequestException;
import org.endeavour.enterprise.framework.security.UserPrincipal;
import org.endeavour.enterprise.framework.security.UserContext;
import org.endeavourhealth.enterprise.core.entity.EndUserRole;
import org.endeavourhealth.enterprise.core.entity.database.DbEndUser;
import org.endeavourhealth.enterprise.core.entity.database.DbOrganisation;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.UUID;

public abstract class AbstractEndpoint {
    @Context
    protected SecurityContext securityContext;

    /*protected UserContext getUserContext()
    {
        return UserContext.fromSecurityContext(securityContext);
    }*/

    /*
    * gets session data from the Token passed up
    * TODO: change to use server-side stored session data, rather than use the token
    * */
    protected DbEndUser getEndUserFromSession(SecurityContext sc) throws Exception {
        UUID uuid = getEndUserUuidFromToken(sc);
        return DbEndUser.retrieveForUuid(uuid);
    }

    protected UUID getEndUserUuidFromToken(SecurityContext sc) {
        UserPrincipal up = (UserPrincipal)sc.getUserPrincipal();

        UserContext uc = up.getUserContext();
        return uc.getUserUuid();
    }

    protected DbOrganisation getOrganisationFromSession(SecurityContext sc) throws Exception {
        UUID uuid = getOrganisationUuidFromToken(sc);
        return DbOrganisation.retrieveForUuid(uuid);
    }

    protected UUID getOrganisationUuidFromToken(SecurityContext sc) throws Exception {
        UserPrincipal up = (UserPrincipal) sc.getUserPrincipal();
        UserContext uc = up.getUserContext();

        //an authenticated user MUST have a EndUser UUID, but they may not have an organisation selected yet
        UUID orgUuid = uc.getOrganisationUuid();
        if (orgUuid == null) {
            throw new BadRequestException("Organisation must be selected before performing any actions");
        }
        return orgUuid;
    }

    protected EndUserRole getRoleFromSession(SecurityContext sc) throws Exception {
        UserPrincipal up = (UserPrincipal) sc.getUserPrincipal();

        UserContext uc = up.getUserContext();
        return uc.getEndUserRole();
    }

}
