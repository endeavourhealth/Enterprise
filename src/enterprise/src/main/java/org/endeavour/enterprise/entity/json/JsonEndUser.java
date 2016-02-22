package org.endeavour.enterprise.entity.json;

import org.endeavour.enterprise.entity.database.DbEndUser;
import org.endeavour.enterprise.model.EndUserRole;

import java.io.Serializable;

/**
 * Created by Drew on 18/02/2016.
 */
public final class JsonEndUser implements Serializable {
    private String username = null;
    private String password = null;
    private String title = null;
    private String forename = null;
    private String surname = null;
    private boolean isSuperUser = false;
    private Integer permissions = -1;

    public JsonEndUser()
    {}
    public JsonEndUser(DbEndUser endUser, EndUserRole role)
    {
        this.username = endUser.getEmail();
        this.title = endUser.getTitle();
        this.forename = endUser.getForename();
        this.surname = endUser.getSurname();
        this.isSuperUser = endUser.getIsSuperUser();

        if (role != null)
        {
            this.permissions = role.getValue();
        }
    }

    /**
     * gets/sets
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean getIsSuperUser() {
        return isSuperUser;
    }

    public void setIsSuperUser(boolean superUser) {
        isSuperUser = superUser;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
