package org.endeavourhealth.enterprise.enginecore;

import org.endeavourhealth.enterprise.core.DefinitionItemType;

import java.util.UUID;

public class LibraryItem {
    private final String name;
    private final UUID uuid;
    private final DefinitionItemType itemType;
    private final String xml;

    public LibraryItem(String name, UUID uuid, DefinitionItemType itemType, String xml) {

        this.name = name;
        this.uuid = uuid;
        this.itemType = itemType;
        this.xml = xml;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public DefinitionItemType getItemType() {
        return itemType;
    }

    public String getXml() {
        return xml;
    }
}
