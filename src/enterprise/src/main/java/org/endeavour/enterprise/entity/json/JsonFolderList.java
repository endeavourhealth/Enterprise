package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavour.enterprise.entity.database.DbFolder;

import java.io.Serializable;

/**
 * Created by Drew on 22/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonFolderList implements Serializable {

    private JsonFolder[] folders = null;

    public JsonFolderList(int size)
    {
        folders = new JsonFolder[size];
    }

    public void add(JsonFolder jsonFolder)
    {
        //find the next non-null index
        for (int i=0; i<folders.length; i++)
        {
            if (folders[i] == null)
            {
                folders[i] = jsonFolder;
                return;
            }
        }

        throw new RuntimeException("Trying to add too many organisations to JsonOrganisationList");
    }
    public void add(DbFolder folder, int count)
    {
        JsonFolder jsonFolder = new JsonFolder(folder, count);
        add(jsonFolder);
    }


    /**
     * gets/sets
     */
    public JsonFolder[] getFolders() {
        return folders;
    }

    public void setFolders(JsonFolder[] folders) {
        this.folders = folders;
    }
}
