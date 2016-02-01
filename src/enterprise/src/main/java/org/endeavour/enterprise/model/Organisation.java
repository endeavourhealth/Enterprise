package org.endeavour.enterprise.model;

import java.util.UUID;

public class Organisation
{
    private UUID organisationUuid;
    private String name;

    public UUID getOrganisationUuid()
    {
        return organisationUuid;
    }

    public void setOrganisationUuid(UUID organisationUuid)
    {
        this.organisationUuid = organisationUuid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
