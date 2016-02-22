package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by Drew on 22/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonEmailInviteParameters implements Serializable
{
    private String token = null;
    private String password = null;

    public JsonEmailInviteParameters()
    {

    }

    /**
     * gets/sets
     */
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
