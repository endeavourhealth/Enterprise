package org.endeavour.enterprise.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonFolderContent implements Comparable {
    private UUID uuid = null;
    private Integer type = null;
    private String typeDesc = null;
    private String name = null;
    private String description = null;
    private Date lastModified = null;
    private Date lastRun = null; //only applicable when showing reports
    private Boolean isScheduled = null; //only applicable when showing reports

    public JsonFolderContent() {

    }

    public JsonFolderContent(DbActiveItem activeItem, DbItem item, DbAudit audit) {
        this(item, audit);
        setTypeEnum(activeItem.getItemTypeId());
    }
    public JsonFolderContent(DbItem item, DbAudit audit) {
        this.uuid = item.getItemUuid();
        this.name = item.getTitle();
        this.description = item.getDescription();

        if (audit != null) {
            this.lastModified = new Date(audit.getTimeStamp().toEpochMilli());
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public int compareTo(Object o) {
        JsonFolderContent other = (JsonFolderContent)o;
        return name.compareToIgnoreCase(other.name);
    }
}
