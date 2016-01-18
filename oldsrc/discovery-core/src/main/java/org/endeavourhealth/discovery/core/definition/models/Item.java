package org.endeavourhealth.discovery.core.definition.models;

import java.util.List;
import java.util.UUID;

public class Item {

    private UUID itemUuid;
    private int auditId;
    private String content;
    private boolean isDeleted;
    private UUID ownerOrganisationUuid;
    private ItemType itemType;
    private String title;
    private String description;

    private List<Dependency> dependencies;

    public Item(
            UUID itemUuid,
            int auditId,
            String content,
            boolean isDeleted,
            UUID ownerOrganisationUuid,
            ItemType itemType,
            String title,
            String description
    ) {
        this.itemUuid = itemUuid;
        this.auditId = auditId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.ownerOrganisationUuid = ownerOrganisationUuid;
        this.itemType = itemType;
        this.title = title;
        this.description = description;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public UUID getOwnerOrganisationUuid() {
        return ownerOrganisationUuid;
    }

    public void setOwnerOrganisationUuid(UUID ownerOrganisationUuid) {
        this.ownerOrganisationUuid = ownerOrganisationUuid;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
}
