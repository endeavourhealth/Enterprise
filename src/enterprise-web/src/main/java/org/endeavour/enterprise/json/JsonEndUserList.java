package org.endeavour.enterprise.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.database.administration.DbEndUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonEndUserList {
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

    public void add(DbEndUser endUser, boolean isAdmin) {
        JsonEndUser jsonEndUser = new JsonEndUser(endUser, isAdmin, null);
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
