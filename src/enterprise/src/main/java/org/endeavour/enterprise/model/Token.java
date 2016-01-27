package org.endeavour.enterprise.model;

import java.util.Date;
import java.util.UUID;

public class Token
{
    private UUID userUuid;
    private UUID organisationUuid;
    private Role role;
    private Date issuedAt;

    public UUID getUserUuid()
    {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid)
    {
        this.userUuid = userUuid;
    }

    public void setUserUuidFromString(String userUuid)
    {
        setUserUuid(UUID.fromString(userUuid));
    }

    public UUID getOrganisationUuid()
    {
        return organisationUuid;
    }

    public void setOrganisationUuid(UUID organisationUuid)
    {
        this.organisationUuid = organisationUuid;
    }

    public void setOrganisationUuidFromString(String organisationUuid)
    {
        setOrganisationUuid(UUID.fromString(organisationUuid));
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }

    public void setRoleFromString(String role)
    {
        setRole(Enum.valueOf(Role.class, role));
    }

    public Date getIssuedAt()
    {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt)
    {
        this.issuedAt = issuedAt;
    }

    public void setIssuedAtFromUnixEpoch(long issuedAt)
    {
        setIssuedAt(new Date(issuedAt * 1000L));
    }
}
