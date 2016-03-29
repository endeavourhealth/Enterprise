package org.endeavourhealth.enterprise.core.database.definition;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.*;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static DbItem retrieveForUUid(UUID itemUuid) throws Exception {
        return (DbItem)DatabaseManager.db().retrieveItemForUuid(itemUuid);
    }

    public static DbItem retrieveForActiveItem(DbActiveItem activeItem) throws Exception {
        return retrieveForUuidAndAudit(activeItem.getItemUuid(), activeItem.getAuditUuid());
    }

    public static DbItem retrieveForUuidAndAudit(UUID uuid, UUID auditUuid) throws Exception {
        return (DbItem)DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid, auditUuid);
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
