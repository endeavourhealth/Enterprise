package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavour.enterprise.entity.database.DbOrganisation;
import org.endeavour.enterprise.model.EndUserRole;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonOrganisation implements Serializable
{

    private UUID organisationUuid = null;
    private String name = null;
    private String nationanId = null;
    private Integer permissions = null;

    public JsonOrganisation()
    {}
    public JsonOrganisation(DbOrganisation org, EndUserRole permissions)
    {
        this.organisationUuid = org.getPrimaryUuid();
        this.name = org.getName();
        this.nationanId = org.getNationalId();
        if (permissions != null)
        {
            this.permissions = new Integer(permissions.getValue());
        }
    }

    /**
     * gets/sets
     */
    public UUID getOrganisationUuid() {
        return organisationUuid;
    }

    public void setOrganisationUuid(UUID organisationUuid) {
        this.organisationUuid = organisationUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationanId() {
        return nationanId;
    }

    public void setNationanId(String nationanId) {
        this.nationanId = nationanId;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
