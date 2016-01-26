package org.endeavour.enterprise.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.UUID;


@XmlRootElement
public class UserInRole implements Serializable
{
    public UUID userInRoleUuid;

    public UUID getUserInRoleUuid()
    {
        return userInRoleUuid;
    }

    public void setUserInRoleUuid(UUID userInRoleUuid)
    {
        this.userInRoleUuid = userInRoleUuid;
    }

    public UUID organisationUuid;
    public String organisationName;
    public Role role;

    public UUID getOrganisationUuid()
    {
        return organisationUuid;
    }

    public void setOrganisationUuid(UUID organisationUuid)
    {
        this.organisationUuid = organisationUuid;
    }

    public String getOrganisationName()
    {
        return organisationName;
    }

    public void setOrganisationName(String organisationName)
    {
        this.organisationName = organisationName;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }
}
