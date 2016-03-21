package org.endeavourhealth.enterprise.core.database.definition;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.ResultReader;
import org.endeavourhealth.enterprise.core.database.TableAdapter;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbItem extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbItem.class, "Item", "Definition",
            "ItemUuid,AuditUuid,XmlContent,Title,Description,IsDeleted", "ItemUuid,AuditUuid");

    private UUID auditUuid = null;
    private String xmlContent = null; //xml
    private String title = null;
    private String description = null;
    private boolean isDeleted = false;

    public DbItem() {
    }

    public static DbItem factoryNew(String title, DbAudit audit) {
        DbItem ret = new DbItem();
        ret.setAuditUuid(audit.getPrimaryUuid());
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

    public static List<DbItem> retrieveDependentItems(UUID itemUuid, UUID auditUuid, DependencyType dependencyType) throws Exception {
        return DatabaseManager.db().retrieveDependentItems(itemUuid, auditUuid, dependencyType);
    }

    public static List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception {
        return DatabaseManager.db().retrieveNonDependentItems(organisationUuid, dependencyType, itemType);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(auditUuid);
        builder.add(xmlContent);
        builder.add(title);
        builder.add(description);
        builder.add(isDeleted);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        auditUuid = reader.readUuid();
        xmlContent = reader.readString();
        title = reader.readString();
        description = reader.readString();
        isDeleted = reader.readBoolean();
    }

    /**
     * gets/sets
     */
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
