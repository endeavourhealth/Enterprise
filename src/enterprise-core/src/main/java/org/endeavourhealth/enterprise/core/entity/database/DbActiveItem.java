package org.endeavourhealth.enterprise.core.entity.database;

import org.endeavourhealth.enterprise.core.entity.DefinitionItemType;
import org.endeavourhealth.enterprise.core.entity.DependencyType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 25/02/2016.
 */
public final class DbActiveItem extends DbAbstractTable {
    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbActiveItem.class, "ActiveItem", "Definition",
            "ActiveItemUuid,OrganisationUuid,ItemUuid,Version,ItemTypeId", "ActiveItemUuid");

    private UUID organisationUuid = null;
    private UUID itemUuid = null;
    private int version = -1;
    private DefinitionItemType itemTypeId = null;

    public DbActiveItem() {
    }

    public static DbActiveItem factoryNew(DbItem item, UUID organisationUuid, DefinitionItemType itemType) {
        UUID itemUuid = item.getPrimaryUuid();
        int version = item.getVersion();

        if (itemUuid == null) {
            throw new RuntimeException("Cannot create ActiveItem without first saving Item to DB");
        }

        DbActiveItem ret = new DbActiveItem();
        ret.setOrganisationUuid(organisationUuid);
        ret.setItemUuid(itemUuid);
        ret.setVersion(version);
        ret.setItemTypeId(itemType);

        return ret;
    }

    public static DbActiveItem retrieveForItemUuid(UUID itemUuid) throws Exception {
        return (DbActiveItem) DatabaseManager.db().retrieveActiveItemForItemUuid(itemUuid);
    }

    public static DbActiveItem retrieveForUuid(UUID uuid) throws Exception {
        return (DbActiveItem) DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
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
        builder.add(version);
        builder.add(itemTypeId.getValue());
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        itemUuid = reader.readUuid();
        version = reader.readInt();
        itemTypeId = DefinitionItemType.get(reader.readInt());
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DefinitionItemType getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(DefinitionItemType itemType) {
        this.itemTypeId = itemType;
    }
}
