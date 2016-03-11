package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavour.enterprise.model.DefinitionItemType;

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
    private String typeDesc = null; //2016-03-01 DL - to make it easier to display
    private String name = null;
    private Date lastModified = null;
    private Date lastRun = null; //only applicable when showing reports
    private Boolean isScheduled = null; //only applicable when showing reports

    public JsonFolderContent() {

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
