package org.endeavour.enterprise.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Drew on 23/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonFolderContentsList implements Serializable {
    private List<JsonFolderContent> contents = null;

    public JsonFolderContentsList() {
    }



    public void addContent(JsonFolderContent content) {
        if (contents == null) {
            contents = new ArrayList<JsonFolderContent>();
        }
        contents.add(content);
    }


    /**
     * gets/sets
     */
    public List<JsonFolderContent> getContents() {
        return contents;
    }

    public void setContents(List<JsonFolderContent> contents) {
        this.contents = contents;
    }


}
