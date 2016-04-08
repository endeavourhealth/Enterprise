package org.endeavour.enterprise.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.database.lookups.DbSourceOrganisation;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonSourceOrganisation implements Comparable {
    private String odsCode = null;
    private String name = null;

    public JsonSourceOrganisation() {}

    public JsonSourceOrganisation(DbSourceOrganisation org) {
        this.odsCode = org.getOdsCode();
        this.name = org.getName();
    }

    /**
     * gets/sets
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    @Override
    public int compareTo(Object o) {
        JsonSourceOrganisation other = (JsonSourceOrganisation)o;
        return name.compareTo(other.getName());
    }
}
