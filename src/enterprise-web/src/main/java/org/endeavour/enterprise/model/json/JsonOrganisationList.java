package org.endeavour.enterprise.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.database.administration.DbOrganisation;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonOrganisationList implements Serializable {
    private JsonEndUser user = null;
    private JsonOrganisation[] organisations = null;

    public JsonOrganisationList(int size) {
        organisations = new JsonOrganisation[size];
    }

    public void add(JsonOrganisation jsonOrg) {
        //find the next non-null index
        for (int i = 0; i < organisations.length; i++) {
            if (organisations[i] == null) {
                organisations[i] = jsonOrg;
                return;
            }
        }

        throw new RuntimeException("Trying to add too many organisations to JsonOrganisationList");
    }

    public void add(DbOrganisation org, boolean isAdmin) {
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

    public JsonOrganisation[] getOrganisations() {
        return organisations;
    }

    public void setOrganisations(JsonOrganisation[] organisations) {
        this.organisations = organisations;
    }
}
