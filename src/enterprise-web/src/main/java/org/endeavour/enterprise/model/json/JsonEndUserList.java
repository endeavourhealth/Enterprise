package org.endeavour.enterprise.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.entity.EndUserRole;
import org.endeavourhealth.enterprise.core.entity.database.DbEndUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Drew on 22/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonEndUserList implements Serializable {
    private List<JsonEndUser> users = new ArrayList<JsonEndUser>();

    public JsonEndUserList() {
    }

    public void add(JsonEndUser jsonEndUser) {
        users.add(jsonEndUser);

        //find the next non-null index
        /*for (int i=0; i<users.length; i++)
        {
            if (users[i] == null)
            {
                users[i] = jsonEndUser;
                return;
            }
        }

        throw new RuntimeException("Trying to add too many organisations to JsonOrganisationList");*/
    }

    public void add(DbEndUser endUser, EndUserRole role) {
        JsonEndUser jsonEndUser = new JsonEndUser(endUser, role);
        add(jsonEndUser);
    }


    /**
     * gets/sets
     */
    public List<JsonEndUser> getUsers() {
        return users;
    }

    public void setUsers(List<JsonEndUser> users) {
        this.users = users;
    }
}
