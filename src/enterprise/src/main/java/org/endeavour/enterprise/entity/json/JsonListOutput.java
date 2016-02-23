package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Drew on 23/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonListOutput implements Serializable
{
    private UUID uuid = null;
    private String name = null;

    public JsonListOutput()
    {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
