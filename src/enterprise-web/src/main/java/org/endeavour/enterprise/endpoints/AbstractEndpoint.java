package org.endeavour.enterprise.endpoints;

import com.sun.deploy.net.HttpRequest;
import org.endeavour.enterprise.framework.exceptions.BadRequestException;
import org.endeavour.enterprise.framework.security.SecurityConfig;
import org.endeavour.enterprise.framework.security.UserPrincipal;
import org.endeavour.enterprise.framework.security.UserContext;
import org.endeavourhealth.enterprise.core.database.models.EnduserEntity;
import org.endeavourhealth.enterprise.core.database.models.OrganisationEntity;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.UUID;

public abstract class AbstractEndpoint {

    private static final String MDC_MARKER_UUID = "UserUuid";

    @Context
    protected SecurityContext securityContext;

    @Context
    protected HttpServletRequest request;

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


    protected EnduserEntity getEndUserFromSession(SecurityContext sc) throws Exception {
        UUID uuid = getEndUserUuidFromToken(sc);
        return EnduserEntity.retrieveForUuid(uuid);
    }

    protected UUID getEndUserUuidFromToken(SecurityContext sc) {
        UserPrincipal up = (UserPrincipal)sc.getUserPrincipal();
        if (up == null) {
            return null;
        }
        UserContext uc = up.getUserContext();
        return uc.getUserUuid();
    }

    protected OrganisationEntity getOrganisationFromSession(SecurityContext sc) throws Exception {
        UUID uuid = getOrganisationUuidFromToken(sc);
        return OrganisationEntity.retrieveForUuid(uuid);
    }

    protected UUID getOrganisationUuidFromToken(SecurityContext sc) throws Exception {
        UserContext uc = getUserContext(sc);

        //an authenticated user MUST have a EndUser UUID, but they may not have an organisation selected yet
        UUID orgUuid = uc.getOrganisationUuid();
        if (orgUuid == null) {
            throw new BadRequestException("Organisation must be selected before performing any actions");
        }
        return orgUuid;
    }

    protected boolean isAdminFromSecurityContext(SecurityContext sc) throws Exception {
        UserContext uc = getUserContext(sc);
        return uc.isAdmin();
    }
    protected boolean isSuperUserFromSecurityContext(SecurityContext sc) throws Exception {
        UserContext uc = getUserContext(sc);
        return uc.isSuperUser();
    }

    private UserContext getUserContext(SecurityContext sc) {
        UserPrincipal up = (UserPrincipal) sc.getUserPrincipal();
        return up.getUserContext();
    }

    protected String getRequestingHostFromSecurityContext(SecurityContext sc) {
        UserContext uc = getUserContext(sc);
        return uc.getHost();
    }
}
