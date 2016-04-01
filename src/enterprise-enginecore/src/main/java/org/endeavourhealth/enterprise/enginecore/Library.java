package org.endeavourhealth.enterprise.enginecore;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class Library {

    private HashMap<UUID, LibraryItem> itemMap = new HashMap<>();

    public void put(LibraryItem libraryItem) {
        itemMap.put(libraryItem.getUuid(), libraryItem);
    }

    public Collection<LibraryItem> getAllLibraryItems() {
        return itemMap.values();
    }

    public Report getReportDefinition(UUID reportUuid) throws Exception {

        LibraryItem libraryItem = getAndValidateLibraryItem(reportUuid, DefinitionItemType.Report);
        return QueryDocumentSerializer.readReportFromXml(libraryItem.getXml());
    }

    private LibraryItem getAndValidateLibraryItem(UUID itemUuid, DefinitionItemType definitionItemType) throws Exception {

        LibraryItem libraryItem = getLibraryItem(itemUuid);

        if (libraryItem.getItemType() != definitionItemType)
            throw new Exception("Unexpected item type found: " + libraryItem.getItemType());

        return libraryItem;
    }

    private LibraryItem getLibraryItem(UUID itemUuid) throws Exception {
        if (!itemMap.containsKey(itemUuid))
            throw new Exception("Item UUID not found: " + itemUuid);

        return itemMap.get(itemUuid);
    }

    public DefinitionItemType getType(UUID itemUuid) throws Exception {

        LibraryItem libraryItem = getLibraryItem(itemUuid);
        return libraryItem.getItemType();
    }
}
