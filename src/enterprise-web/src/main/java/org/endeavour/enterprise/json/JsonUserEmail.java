package org.endeavour.enterprise.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonUserEmail {
    private String email = null;

    public JsonUserEmail() {}

    /**
     * gets/sets
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
