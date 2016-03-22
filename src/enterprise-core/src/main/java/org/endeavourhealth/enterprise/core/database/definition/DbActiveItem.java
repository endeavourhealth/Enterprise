package org.endeavourhealth.enterprise.core.database.definition;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbActiveItem extends DbAbstractTable {
    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbActiveItem.class,
            "ActiveItemUuid,OrganisationUuid,ItemUuid,AuditUuid,ItemTypeId,IsDeleted", "ActiveItemUuid");

    private UUID organisationUuid = null;
    private UUID itemUuid = null;
    private UUID auditUuid = null;
    private DefinitionItemType itemTypeId = null;
    private boolean isDeleted = false;

    public DbActiveItem() {
    }

    public static DbActiveItem factoryNew(DbItem item, UUID organisationUuid, DefinitionItemType itemType) {
        UUID itemUuid = item.getPrimaryUuid();
        UUID auditUuid = item.getAuditUuid();

        if (itemUuid == null) {
            throw new RuntimeException("Cannot create ActiveItem without first saving Item to DB");
        }

        DbActiveItem ret = new DbActiveItem();
        ret.setOrganisationUuid(organisationUuid);
        ret.setItemUuid(itemUuid);
        ret.setAuditUuid(auditUuid);
        ret.setItemTypeId(itemType);

        return ret;
    }

    public static DbActiveItem retrieveForItemUuid(UUID itemUuid) throws Exception {
        return DatabaseManager.db().retrieveActiveItemForItemUuid(itemUuid);
    }

    public static int retrieveCountDependencies(UUID itemUuid, DependencyType dependencyType) throws Exception {
        return DatabaseManager.db().retrieveCountDependencies(itemUuid, dependencyType);
    }

    public static List<DbActiveItem> retrieveDependentItems(UUID orgUuid, UUID itemUuid, DependencyType dependencyType) throws Exception {
        return DatabaseManager.db().retrieveActiveItemDependentItems(orgUuid, itemUuid, dependencyType);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(organisationUuid);
        builder.add(itemUuid);
        builder.add(auditUuid);
        builder.add(itemTypeId);
        builder.add(isDeleted);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        itemUuid = reader.readUuid();
        auditUuid = reader.readUuid();
        itemTypeId = DefinitionItemType.get(reader.readInt());
        isDeleted = reader.readBoolean();
    }

    /**
     * gets/sets
     */
    public UUID getOrganisationUuid() {
        return organisationUuid;
    }

    public void setOrganisationUuid(UUID organisationUuid) {
        this.organisationUuid = organisationUuid;
    }

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

    public DefinitionItemType getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(DefinitionItemType itemType) {
        this.itemTypeId = itemType;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
