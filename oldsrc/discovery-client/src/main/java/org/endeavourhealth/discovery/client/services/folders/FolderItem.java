package org.endeavourhealth.discovery.client.services.folders;

import org.endeavourhealth.discovery.core.definition.models.ItemType;
import org.joda.time.LocalDateTime;

import java.util.UUID;

public class FolderItem {
    private UUID itemUuid;
    private ItemType itemType;
    private String title;
    private UUID parentUuid;
    private LocalDateTime auditDateTime;

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(UUID parentUuid) {
        this.parentUuid = parentUuid;
    }

    public LocalDateTime getAuditDateTime() {
        return auditDateTime;
    }

    public void setAuditDateTime(LocalDateTime auditDateTime) {
        this.auditDateTime = auditDateTime;
    }
}
