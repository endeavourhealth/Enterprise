package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavour.enterprise.entity.database.DbItem;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Drew on 23/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonReport implements Serializable
{
    private UUID uuid = null;
    private String name = null;
    private String description = null;
    private String xmlContent = null;
    private Boolean isDeleted = false;

    public JsonReport()
    {}
    public JsonReport(DbItem item)
    {
        this.uuid = item.getPrimaryUuid();
        this.name = item.getTitle();
        this.description = item.getDescription();
        this.xmlContent = item.getXmlContent();
        this.isDeleted = item.getIsDeleted();
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}