package org.endeavour.enterprise.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.database.administration.DbEndUser;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonEndUser implements Serializable {

    private UUID uuid = null;
    private String username = null;
    private String password = null;
    private String title = null;
    private String forename = null;
    private String surname = null;
    private Boolean isSuperUser = null; //using non-primative types because serialisation to JSON can skip nulls, if we want
    private Boolean isAdmin = null;
    private Integer permissions = null; //to be removed after isAdmin is adopted


    public JsonEndUser() {
    }

    public JsonEndUser(DbEndUser endUser, Boolean isAdmin) {
        this.uuid = endUser.getPrimaryUuid();
        this.username = endUser.getEmail();
        this.title = endUser.getTitle();
        this.forename = endUser.getForename();
        this.surname = endUser.getSurname();
        this.isSuperUser = new Boolean(endUser.isSuperUser());

        if (isAdmin != null) {
            this.isAdmin = new Boolean(isAdmin);

            //to be removed once web client changed to use isAdmin
            if (isAdmin) {
                this.permissions = new Integer(2);
            } else {
                this.permissions = new Integer(1);
            }
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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getSuperUser() {
        return isSuperUser;
    }

    public void setSuperUser(Boolean superUser) {
        isSuperUser = superUser;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
