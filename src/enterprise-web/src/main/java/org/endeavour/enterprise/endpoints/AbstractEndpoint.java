package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.framework.exceptions.BadRequestException;
import org.endeavour.enterprise.framework.security.SecurityConfig;
import org.endeavour.enterprise.framework.security.UserPrincipal;
import org.endeavour.enterprise.framework.security.UserContext;
import org.endeavourhealth.enterprise.core.database.administration.DbEndUser;
import org.endeavourhealth.enterprise.core.database.administration.DbOrganisation;
import org.slf4j.MDC;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.UUID;

public abstract class AbstractEndpoint {

    private static final String MDC_MARKER_UUID = "UserUuid";

    @Context
    protected SecurityContext securityContext;

    /**
     * used to set LogBack to include the user UUID in all logging
     */
    protected void setLogbackMarkers(SecurityContext sc) {
        UUID userUuid = getEndUserUuidFromToken(sc);
        if (userUuid != null) {
            MDC.put(MDC_MARKER_UUID, userUuid.toString());
        }
    }
    public static void clearLogbackMarkers() {
        MDC.remove(MDC_MARKER_UUID);
    }


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

    protected boolean isAdminFromSession(SecurityContext sc) throws Exception {
        UserPrincipal up = (UserPrincipal) sc.getUserPrincipal();

        UserContext uc = up.getUserContext();
        return uc.isAdmin();
    }

}
