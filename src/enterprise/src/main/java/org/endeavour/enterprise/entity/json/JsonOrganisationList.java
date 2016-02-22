package org.endeavour.enterprise.entity.json;

import org.endeavour.enterprise.entity.database.DbOrganisation;
import org.endeavour.enterprise.model.EndUserRole;

import java.io.Serializable;

/**
 * Created by Drew on 18/02/2016.
 */
public final class JsonOrganisationList implements Serializable
{
    private JsonOrganisation[] organisations = null;

    public JsonOrganisationList(int size)
    {
        organisations = new JsonOrganisation[size];
    }

    public void add(JsonOrganisation jsonOrg)
    {
        //find the next non-null index
        for (int i=0; i<organisations.length; i++)
        {
            if (organisations[i] == null)
            {
                organisations[i] = jsonOrg;
                return;
            }
        }

        throw new RuntimeException("Trying to add too many organisations to JsonOrganisationList");
    }
    public void add(DbOrganisation org, EndUserRole permissions)
    {
        JsonOrganisation jsonOrg = new JsonOrganisation(org, permissions);
        add(jsonOrg);
    }


    /**
     * gets/sets
     */
    public JsonOrganisation[] getOrganisations() {
        return organisations;
    }

    public void setOrganisations(JsonOrganisation[] organisations) {
        this.organisations = organisations;
    }
}
