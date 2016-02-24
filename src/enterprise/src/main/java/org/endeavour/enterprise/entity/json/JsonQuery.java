package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Drew on 23/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonQuery implements Serializable
{
    //requirements from Darren
    /*Id uniqueidentifier not null,
    Content varchar(max) null,
    IsDeleted bit not null,
    OwnerOrganisationUuid uniqueidentifier not null,
    Title varchar(100) not null,
    [Description] varchar(max) null,*/

    private UUID uuid = null;
    private String name = null;
    private String description = null;
    private String xmlContent = null;
    private boolean isDeleted = false;

    public JsonQuery()
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }


}
