package org.endeavour.enterprise.entity.json;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 * JSON object used to manipulaate folders, such as creating, moving and renaming
 */
public class JsonFolder implements Serializable {

    private UUID folderUuid = null;
    private String folderName = null;
    private UUID parentFolderUuid = null;

    public JsonFolder()
    {
    }

    /**
     * gets/sets
     */
    public UUID getFolderUuid() {
        return folderUuid;
    }

    public void setFolderUuid(UUID folderUuid) {
        this.folderUuid = folderUuid;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public UUID getParentFolderUuid() {
        return parentFolderUuid;
    }

    public void setParentFolderUuid(UUID parentUuid) {
        this.parentFolderUuid = parentUuid;
    }
}
