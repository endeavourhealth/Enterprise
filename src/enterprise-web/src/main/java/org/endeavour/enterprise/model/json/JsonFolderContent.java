package org.endeavour.enterprise.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonFolderContent implements Serializable {
    private UUID uuid = null;
    private Integer type = null;
    private String typeDesc = null;
    private String name = null;
    private Instant lastModified = null;
    private Instant lastRun = null; //only applicable when showing reports
    private Boolean isScheduled = null; //only applicable when showing reports

    public JsonFolderContent() {

    }

    public JsonFolderContent(DbActiveItem activeItem, DbItem item, DbAudit audit) {
        this(item, audit);
        setTypeEnum(activeItem.getItemTypeId());
    }
    public JsonFolderContent(DbItem item, DbAudit audit) {
        this.uuid = item.getPrimaryUuid();
        this.name = item.getTitle();

        if (audit != null) {
            this.lastModified = audit.getTimeStamp();
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

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public Instant getLastRun() {
        return lastRun;
    }

    public void setLastRun(Instant lastRun) {
        this.lastRun = lastRun;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean scheduled) {
        isScheduled = scheduled;
    }
}
