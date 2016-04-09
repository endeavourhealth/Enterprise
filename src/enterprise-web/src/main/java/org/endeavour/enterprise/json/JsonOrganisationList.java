package org.endeavour.enterprise.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.database.administration.DbOrganisation;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonOrganisationList {
    private JsonEndUser user = null;
    private List<JsonOrganisation> organisations = new ArrayList<>();

    public JsonOrganisationList() {}

    public void add(JsonOrganisation jsonOrg) {
        organisations.add(jsonOrg);
    }

    public void add(DbOrganisation org, Boolean isAdmin) {
        JsonOrganisation jsonOrg = new JsonOrganisation(org, isAdmin);
        add(jsonOrg);
    }


    /**
     * gets/sets
     */
    public JsonEndUser getUser() {
        return user;
    }

    public void setUser(JsonEndUser user) {
        this.user = user;
    }

    public List<JsonOrganisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<JsonOrganisation> organisations) {
        this.organisations = organisations;
    }
}
