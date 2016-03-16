package org.endeavour.enterprise.model;

import org.endeavour.enterprise.framework.security.UserPrincipal;
import org.endeavourhealth.enterprise.core.entity.EndUserRole;

import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.UUID;

public class UserContext {
    private UUID userUuid;
    private UUID organisationUuid;
    private EndUserRole endUserRole;
    private Date tokenIssued;

    public UserContext(UUID userUuid, UUID organisationUuid, EndUserRole endUserRole, Date tokenIssued) {
        this.userUuid = userUuid;
        this.organisationUuid = organisationUuid;
        this.endUserRole = endUserRole;
        this.tokenIssued = tokenIssued;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public UUID getOrganisationUuid() {
        return organisationUuid;
    }

    public EndUserRole getEndUserRole() {
        return endUserRole;
    }

    public Date getTokenIssued() {
        return tokenIssued;
    }

    public static UserContext fromSecurityContext(SecurityContext securityContext) {
        if (securityContext != null)
            if (securityContext.getUserPrincipal() != null)
                if (UserPrincipal.class.isInstance(securityContext.getUserPrincipal()))
                    return ((UserPrincipal) securityContext.getUserPrincipal()).getUserContext();

        return null;
    }
}
