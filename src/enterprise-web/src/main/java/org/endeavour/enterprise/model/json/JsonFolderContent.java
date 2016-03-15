package org.endeavour.enterprise.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavour.enterprise.model.database.DbItem;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Drew on 25/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonFolderContent implements Serializable {
    private UUID uuid = null;
    private Integer type = null;
    private String typeDesc = null;
    private String name = null;
    private Date lastModified = null;
    private Date lastRun = null; //only applicable when showing reports
    private Boolean isScheduled = null; //only applicable when showing reports

    public JsonFolderContent() {

    }

    public JsonFolderContent(DbItem item) {
        this.uuid = item.getPrimaryUuid();
        this.name = item.getTitle();
    }

    public void setTypeEnum(DefinitionItemType t) {
        setType(t.getValue());
        setTypeDesc(t.toString());
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastRun() {
        return lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean scheduled) {
        isScheduled = scheduled;
    }
}
