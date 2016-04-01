package org.endeavourhealth.enterprise.core.database.definition;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.database.execution.DbJob;
import org.endeavourhealth.enterprise.core.database.execution.DbJobContent;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.*;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

public final class DbItem extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbItem.class);

    @DatabaseColumn
    @PrimaryKeyColumn
    private UUID itemUuid = null;
    @DatabaseColumn
    @PrimaryKeyColumn
    private UUID auditUuid = null;
    @DatabaseColumn
    private String xmlContent = null;
    @DatabaseColumn
    private String title = null;
    @DatabaseColumn
    private String description = null;
    @DatabaseColumn
    private boolean isDeleted = false;

    public DbItem() {
    }

    public static DbItem factoryNew(String title, DbAudit audit) {
        DbItem ret = new DbItem();
        ret.setAuditUuid(audit.getAuditUuid());
        ret.setTitle(title);
        return ret;
    }

    public static DbItem retrieveLatestForUUid(UUID itemUuid) throws Exception {
        return DatabaseManager.db().retrieveLatestItemForUuid(itemUuid);
    }

    public static DbItem retrieveForActiveItem(DbActiveItem activeItem) throws Exception {
        return retrieveForUuidAndAudit(activeItem.getItemUuid(), activeItem.getAuditUuid());
    }

    public static DbItem retrieveForUuidAndAudit(UUID uuid, UUID auditUuid) throws Exception {
        return DatabaseManager.db().retrieveForPrimaryKeys(DbItem.class, uuid, auditUuid);
    }

    public static List<DbItem> retrieveDependentItems(UUID itemUuid, DependencyType dependencyType) throws Exception {
        return DatabaseManager.db().retrieveDependentItems(itemUuid, dependencyType);
    }

    public static List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception {
        return DatabaseManager.db().retrieveNonDependentItems(organisationUuid, dependencyType, itemType);
    }

    public static List<DbItem> retrieveForActiveItems(List<DbActiveItem> activeItems) throws Exception {
        return DatabaseManager.db().retrieveItemsForActiveItems(activeItems);
    }

    public static Map<UUID, Object> retrieveLibraryItemsForJob(UUID jobUuid) throws Exception {
        Map<UUID, Object> ret = new HashMap<>();

        List<DbItem> items = DatabaseManager.db().retrieveItemsForJob(jobUuid);
        for (DbItem item: items) {

            UUID itemUuid = item.getItemUuid();
            String xml = item.getXmlContent();
            LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(xml);
            if (libraryItem.getQuery() != null) {
                ret.put(itemUuid, libraryItem.getQuery());
            } else if (libraryItem.getDataSource() != null) {
                ret.put(itemUuid, libraryItem.getDataSource());
            } else if (libraryItem.getTest() != null) {
                ret.put(itemUuid, libraryItem.getTest());
            } else if (libraryItem.getCodeSet() != null) {
                ret.put(itemUuid, libraryItem.getCodeSet());
            } else if (libraryItem.getListReport() != null) {
                ret.put(itemUuid, libraryItem.getListReport());
            } else {
                throw new RuntimeException("Library item " + itemUuid + " contains no content");
            }
        }

        return ret;
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    /**
     * gets/sets
     */
    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public UUID getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(UUID auditUuid) {
        this.auditUuid = auditUuid;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String content) {
        this.xmlContent = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

}
