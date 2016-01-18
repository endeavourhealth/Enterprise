package org.endeavourhealth.discovery.client.services.folders;

import java.util.UUID;

public class Folder {
    private UUID folderGuid;
    private UUID parentFolderGuid;
    private Boolean hasChildren;
    private String name;
    private int auditId;

    public UUID getFolderGuid() {
        return folderGuid;
    }

    public void setFolderGuid(UUID folderGuid) {
        this.folderGuid = folderGuid;
    }

    public UUID getParentFolderGuid() {
        return parentFolderGuid;
    }

    public void setParentFolderGuid(UUID parentFolderGuid) {
        this.parentFolderGuid = parentFolderGuid;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public int getAuditId() {
        return auditId;
    }
}
