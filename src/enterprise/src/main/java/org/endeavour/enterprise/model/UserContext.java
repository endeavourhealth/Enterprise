package org.endeavour.enterprise.model;

import java.util.Date;
import java.util.UUID;

public class UserContext
{
    private UUID userUuid;
    private UUID organisationUuid;
    private Role role;
    private Date tokenIssued;

    public UserContext(UUID userUuid, UUID organisationUuid, Role role, Date tokenIssued)
    {
        this.userUuid = userUuid;
        this.organisationUuid = organisationUuid;
        this.role = role;
        this.tokenIssued = tokenIssued;
    }

    public UUID getUserUuid()
    {
        return userUuid;
    }

    public UUID getOrganisationUuid()
    {
        return organisationUuid;
    }

    public Role getRole()
    {
        return role;
    }

    public Date getTokenIssued()
    {
        return tokenIssued;
    }
}
