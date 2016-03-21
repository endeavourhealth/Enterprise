package org.endeavourhealth.enterprise.core.database.definition;

import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.ResultReader;
import org.endeavourhealth.enterprise.core.database.TableAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbItemDependency extends DbAbstractTable {

    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbItemDependency.class, "ItemDependency", "Definition",
            "ItemDependencyUuid,ItemUuid,AuditUuid,DependentItemUuid,DependencyTypeId", "ItemDependencyUuid");

    private UUID itemUuid = null;
    private UUID auditUuid = null;
    private UUID dependentItemUuid = null;
    private DependencyType dependencyTypeId = null;

    public DbItemDependency() {
    }

    /*public static DbActiveItemDependency factoryNew(DbItem item, DbItem dependentItem, DependencyType dependencyType)
    {
        UUID itemUuid = item.getPrimaryUuid();
        if (itemUuid == null)
        {
            throw new RuntimeException("Cannot create ActiveItem without first saving Item to DB");
        }

        UUID dependentItemUuid = dependentItem.getPrimaryUuid();
        if (dependentItemUuid == null)
        {
            throw new RuntimeException("Cannot create ActiveItem without first saving dependent Item to DB");
        }

        DbActiveItemDependency ret = new DbActiveItemDependency();
        ret.setItemUuid(itemUuid);
        ret.setDependentItemUuid(dependentItemUuid);
        ret.setDependencyType(dependencyType);

        return ret;
    }*/

    public static List<DbItemDependency> retrieveForActiveItem(DbActiveItem activeItem) throws Exception {
        return retrieveForItem(activeItem.getItemUuid(), activeItem.getAuditUuid());
    }

    public static List<DbItemDependency> retrieveForItem(UUID itemUuid, UUID auditUuid) throws Exception {
        return DatabaseManager.db().retrieveItemDependenciesForItem(itemUuid, auditUuid);
    }

    public static List<DbItemDependency> retrieveForItemType(UUID itemUuid, UUID auditUuid, DependencyType dependencyType) throws Exception {
        return DatabaseManager.db().retrieveItemDependenciesForItemType(itemUuid, auditUuid, dependencyType);
    }

    public static List<DbItemDependency> retrieveForDependentItem(UUID dependentItemUuid) throws Exception {
        return DatabaseManager.db().retrieveItemDependenciesForDependentItem(dependentItemUuid);
    }

    public static List<DbItemDependency> retrieveForDependentItemType(UUID dependentItemUuid, DependencyType dependencyType) throws Exception {
        return DatabaseManager.db().retrieveItemDependenciesForDependentItemType(dependentItemUuid, dependencyType);
    }


    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(itemUuid);
        builder.add(auditUuid);
        builder.add(dependentItemUuid);
        builder.add(dependencyTypeId.getValue());
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        itemUuid = reader.readUuid();
        auditUuid = reader.readUuid();
        dependentItemUuid = reader.readUuid();
        dependencyTypeId = DependencyType.get(reader.readInt());
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

    public UUID getDependentItemUuid() {
        return dependentItemUuid;
    }

    public void setDependentItemUuid(UUID dependentItemUuid) {
        this.dependentItemUuid = dependentItemUuid;
    }

    public DependencyType getDependencyTypeId() {
        return dependencyTypeId;
    }

    public void setDependencyTypeId(DependencyType dependencyType) {
        this.dependencyTypeId = dependencyType;
    }
}
