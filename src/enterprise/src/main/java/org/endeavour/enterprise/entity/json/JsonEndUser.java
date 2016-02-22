package org.endeavour.enterprise.entity.json;

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

    public JsonEndUser()
    {}

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

    public boolean isSuperUser() {
        return isSuperUser;
    }

    public void setSuperUser(boolean superUser) {
        isSuperUser = superUser;
    }
}
