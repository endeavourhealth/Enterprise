package org.endeavour.enterprise.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.database.administration.DbOrganisation;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonOrganisation {
    private UUID uuid = null;
    private String name = null;
    private String nationalId = null;
    private Boolean isAdmin = null;
    private Integer permissions = null; //to be removed once web client changed to use isAdmin


    public JsonOrganisation() {
    }

    public JsonOrganisation(DbOrganisation org, Boolean isAdmin) {
        this.uuid = org.getOrganisationUuid();
        this.name = org.getName();
        this.nationalId = org.getNationalId();
        this.isAdmin = isAdmin;

        if (isAdmin != null) {
            this.permissions = new Integer(2);
        } else {
            this.permissions = new Integer(1);
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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
