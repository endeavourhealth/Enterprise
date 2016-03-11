package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavour.enterprise.entity.database.DbEndUser;
import org.endeavour.enterprise.model.EndUserRole;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonEndUser implements Serializable {

    private UUID uuid = null;
    private String username = null;
    private String password = null;
    private String title = null;
    private String forename = null;
    private String surname = null;
    private Boolean isSuperUser = null; //using non-primative types because serialisation to JSON can skip nulls, if we want
    private Integer permissions = null;

    public JsonEndUser() {
    }

    public JsonEndUser(DbEndUser endUser, EndUserRole role) {
        this.uuid = endUser.getPrimaryUuid();
        this.username = endUser.getEmail();
        this.title = endUser.getTitle();
        this.forename = endUser.getForename();
        this.surname = endUser.getSurname();
        this.isSuperUser = new Boolean(endUser.getIsSuperUser());

        if (role != null) {
            this.permissions = role.get();
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

    public Boolean getIsSuperUser() {
        return isSuperUser;
    }

    public void setIsSuperUser(Boolean superUser) {
        isSuperUser = superUser;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
