package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavour.enterprise.entity.database.DbFolder;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 * JSON object used to manipulate folders, such as creating, moving and renaming
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonFolder implements Serializable {

    private UUID uuid = null;
    private String folderName = null;
    private Integer folderType = null;
    private UUID parentFolderUuid = null;
    private Integer contentCount = null;

    public JsonFolder()
    {
    }
    public JsonFolder(DbFolder folder, int count)
    {
        this.uuid = folder.getPrimaryUuid();
        this.folderName = folder.getTitle();
        this.folderType = new Integer(folder.getFolderType());
        this.parentFolderUuid = folder.getParentFolderUuid();
        if (count > -1)
        {
            contentCount = new Integer(count);
        }
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

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Integer getFolderType() {
        return folderType;
    }

    public void setFolderType(Integer folderType) {
        this.folderType = folderType;
    }

    public UUID getParentFolderUuid() {
        return parentFolderUuid;
    }

    public void setParentFolderUuid(UUID parentUuid) {
        this.parentFolderUuid = parentUuid;
    }

    public Integer getContentCount() {
        return contentCount;
    }

    public void setContentCount(Integer contentCount) {
        this.contentCount = contentCount;
    }
}
