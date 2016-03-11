package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Drew on 22/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonFolderList implements Serializable {

    private List<JsonFolder> folders = new ArrayList<JsonFolder>();

    public JsonFolderList() {
    }

    public void add(JsonFolder jsonFolder) {
        folders.add(jsonFolder);

        //find the next non-null index
        /*for (int i=0; i<folders.length; i++)
        {
            if (folders[i] == null)
            {
                folders[i] = jsonFolder;
                return;
            }
        }

        throw new RuntimeException("Trying to add too many organisations to JsonOrganisationList");*/
    }
    /*public void add(DbFolder folder, int count)
    {
        JsonFolder jsonFolder = new JsonFolder(folder, count);
        add(jsonFolder);
    }*/


    /**
     * gets/sets
     */
    public List<JsonFolder> getFolders() {
        return folders;
    }

    public void setFolders(List<JsonFolder> folders) {
        this.folders = folders;
    }
}
