package org.endeavourhealth.enterprise.core.json;

import java.util.List;

public final class JsonMoveItems {
    private String destinationFolder = null;
    private List<JsonMoveItem> items = null;

    /**
     * gets/sets
     */
    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public List<JsonMoveItem> getItems() {
        return items;
    }

    public void setItems(List<JsonMoveItem> items) {
        this.items = items;
    }
}
