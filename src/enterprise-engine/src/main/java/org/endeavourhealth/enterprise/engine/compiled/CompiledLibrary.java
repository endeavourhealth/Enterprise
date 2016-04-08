package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentHelper;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompiledLibrary {
    private Map<UUID, CompiledLibraryItem> libraryItemMap = new HashMap<>();

    private static class CompiledLibraryItem {
        private final String name;
        private final UUID uuid;
        private final boolean isInline;
        private final Object compiledItem;

        private CompiledLibraryItem(String name, UUID uuid, boolean isInline, Object compiledItem) {
            this.name = name;
            this.uuid = uuid;
            this.isInline = isInline;
            this.compiledItem = compiledItem;
        }

        public Object getCompiledItem() {
            return compiledItem;
        }
    }

    public void add(LibraryItem libraryItem, ICompiledDataSource compiledItem) {
        addCompiledLibraryItem(libraryItem, compiledItem);
    }

    public void add(LibraryItem libraryItem, CompiledQuery compiledItem) {
        addCompiledLibraryItem(libraryItem, compiledItem);
    }

    public void add(LibraryItem libraryItem, ICompiledTest compiledItem) {
        addCompiledLibraryItem(libraryItem, compiledItem);
    }

    public void addInlineDataSource(UUID dataSourceUuid, ICompiledDataSource compiledItem) {

        CompiledLibraryItem item = new CompiledLibraryItem(
                "Inline datasource",
                dataSourceUuid,
                true,
                compiledItem);

        libraryItemMap.put(dataSourceUuid, item);
    }

    public void addInlineTest(UUID testUuid, ICompiledTest compiledItem) {

        CompiledLibraryItem item = new CompiledLibraryItem(
                "Inline test",
                testUuid,
                true,
                compiledItem);

        libraryItemMap.put(testUuid, item);
    }

    private void addCompiledLibraryItem(LibraryItem libraryItem, Object compiledItem) {
        UUID uuid = QueryDocumentHelper.parseMandatoryUuid(libraryItem.getUuid());

        CompiledLibraryItem item = new CompiledLibraryItem(
                libraryItem.getName(),
                uuid,
                false,
                compiledItem);

        libraryItemMap.put(uuid, item);
    }

    public ICompiledDataSource getCompiledDataSource(UUID dataSourceUuid) throws UnableToCompileExpection {
        CompiledLibraryItem item = getCompiledLibraryItem(dataSourceUuid, "DataSource");
        return (ICompiledDataSource)item.getCompiledItem();
    }

    public CompiledQuery getCompiledQuery(UUID queryUuid) throws UnableToCompileExpection {
        CompiledLibraryItem item = getCompiledLibraryItem(queryUuid, "Query");
        return (CompiledQuery)item.getCompiledItem();
    }

    public ICompiledTest getCompiledTest(UUID testUuid) throws UnableToCompileExpection {
        CompiledLibraryItem item = getCompiledLibraryItem(testUuid, "Test");
        return (ICompiledTest)item.getCompiledItem();
    }

    public boolean isItemOfTypeQuery(UUID itemUuid) throws UnableToCompileExpection {
        CompiledLibraryItem compiledLibraryItem = getCompiledLibraryItem(itemUuid, "Query");

        return (compiledLibraryItem.getCompiledItem() instanceof CompiledQuery);
    }

    public boolean isItemOfTypeListReport(UUID itemUuid) throws UnableToCompileExpection {
        CompiledLibraryItem compiledLibraryItem = getCompiledLibraryItem(itemUuid, "ListReport");

        return false;
        //return (compiledLibraryItem.getCompiledItem() instanceof CompiledReport.CompiledReportListReport);
    }

    public void checkHasDataSource(UUID dataSourceUuid) throws UnableToCompileExpection {
        CompiledLibraryItem item = getCompiledLibraryItem(dataSourceUuid, "DataSource");

        if (!(item.getCompiledItem() instanceof ICompiledDataSource))
            throw new UnableToCompileExpection("Item found but not data source: " + dataSourceUuid);
    }

    private CompiledLibraryItem getCompiledLibraryItem(UUID uuid, String requestedType) throws UnableToCompileExpection {
        if (!libraryItemMap.containsKey(uuid))
            throw new UnableToCompileExpection("UUID not found in compiled library.  Requested type: " + requestedType + ".  UUID: " + uuid);

        return libraryItemMap.get(uuid);
    }
}
