package org.endeavour.enterprise.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.entity.EndUserRole;
import org.endeavourhealth.enterprise.core.entity.database.DbOrganisation;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonOrganisation implements Serializable {
    private UUID uuid = null;
    private String name = null;
    private String nationalId = null;
    private Integer permissions = null;

    public JsonOrganisation() {
    }

    public JsonOrganisation(DbOrganisation org, EndUserRole permissions) {
        this.uuid = org.getPrimaryUuid();
        this.name = org.getName();
        this.nationalId = org.getNationalId();
        if (permissions != null) {
            this.permissions = new Integer(permissions.getValue());
        }
    }

    /**
     * gets/sets
     */
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
