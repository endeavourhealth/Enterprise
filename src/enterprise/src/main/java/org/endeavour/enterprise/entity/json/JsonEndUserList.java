package org.endeavour.enterprise.entity.json;

import org.endeavour.enterprise.entity.database.DbEndUser;
import org.endeavour.enterprise.model.EndUserRole;

import java.io.Serializable;

/**
 * Created by Drew on 22/02/2016.
 */
public final class JsonEndUserList implements Serializable
{
    private JsonEndUser[] users = null;

    public JsonEndUserList(int size)
    {
        users = new JsonEndUser[size];
    }

    public void add(JsonEndUser jsonEndUser)
    {
        //find the next non-null index
        for (int i=0; i<users.length; i++)
        {
            if (users[i] == null)
            {
                users[i] = jsonEndUser;
                return;
            }
        }

        throw new RuntimeException("Trying to add too many organisations to JsonOrganisationList");
    }
    public void add(DbEndUser endUser, EndUserRole role)
    {
        JsonEndUser jsonEndUser = new JsonEndUser(endUser, role);
        add(jsonEndUser);
    }


    /**
     * gets/sets
     */
    public JsonEndUser[] getUsers() {
        return users;
    }

    public void setUsers(JsonEndUser[] users) {
        this.users = users;
    }
}
