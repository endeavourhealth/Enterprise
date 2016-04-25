package org.endeavour.enterprise.framework.security;

import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.UUID;

public class UserContext {
    private UUID userUuid;
    private UUID organisationUuid;
    private boolean isAdmin;
    private boolean isSuperUser;
    private Date tokenIssued;
    private String host;

    public UserContext(String host, UUID userUuid, UUID organisationUuid, boolean isAdmin, boolean isSuperUser, Date tokenIssued) {
        this.host = host;
        this.userUuid = userUuid;
        this.organisationUuid = organisationUuid;
        this.isAdmin = isAdmin;
        this.isSuperUser = isSuperUser;
        this.tokenIssued = tokenIssued;
    }

    public String getHost() {
        return host;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public UUID getOrganisationUuid() {
        return organisationUuid;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isSuperUser() {
        return isSuperUser;
    }

    public Date getTokenIssued() {
        return tokenIssued;
    }
}
