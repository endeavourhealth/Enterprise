package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;
import org.endeavour.enterprise.model.DependencyType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 29/02/2016.
 */
public final class DbActiveItemDependency extends DbAbstractTable
{
    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbActiveItemDependency.class, "ActiveItemDependency", "Definition", DatabaseName.ENDEAVOUR_ENTERPRISE,
            "ActiveItemDependencyUuid,ItemUuid,DependentItemUuid,DependencyType", "ActiveItemDependencyUuid");

    private UUID itemUuid = null;
    private UUID dependentItemUuid = null;
    private DependencyType dependencyType = null;

    public DbActiveItemDependency()
    {}

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

    public static List<DbActiveItemDependency> retrieveForDependentItem(UUID dependentItemUuid, DependencyType dependencyType) throws Exception
    {
        return DatabaseManager.db().retrieveActiveItemDependenciesForDependentItemType(dependentItemUuid, dependencyType);
    }

    @Override
    public TableAdapter getAdapter()
    {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(itemUuid);
        builder.add(dependentItemUuid);
        builder.add(dependencyType.getValue());
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        itemUuid = reader.readUuid();
        dependentItemUuid = reader.readUuid();
        dependencyType = DependencyType.get(reader.readInt());
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

    public UUID getDependentItemUuid() {
        return dependentItemUuid;
    }

    public void setDependentItemUuid(UUID dependentItemUuid) {
        this.dependentItemUuid = dependentItemUuid;
    }

    public DependencyType getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(DependencyType dependencyType) {
        this.dependencyType = dependencyType;
    }
}
