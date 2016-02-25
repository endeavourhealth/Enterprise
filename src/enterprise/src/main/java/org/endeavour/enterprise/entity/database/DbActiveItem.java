package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;
import org.endeavour.enterprise.model.DefinitionItemType;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Drew on 25/02/2016.
 */
public final class DbActiveItem extends DbAbstractTable
{
    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbActiveItem.class, "ActiveItems", "Definition", DatabaseName.ENDEAVOUR_ENTERPRISE);

    private UUID organisationUuid = null;
    private UUID itemUuid = null;
    private int version = -1;
    private DefinitionItemType itemType = null;

    public DbActiveItem()
    {}

    public static DbActiveItem retrieveForItemUuid(UUID itemUuid) throws Exception
    {
        return (DbActiveItem)adapter.retrieveSingleEntity("Definition.ActiveItem_SelectForItemUuid", itemUuid);
    }
    public static DbActiveItem retrieveForUuid(UUID uuid) throws Exception
    {
        return (DbActiveItem)adapter.retrieveSingleEntity("Definition._ActiveItem_SelectForUuid", uuid);
    }

    @Override
    public TableAdapter getAdapter()
    {
        return adapter;
    }

    @Override
    public void writeForDb(InsertBuilder builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(organisationUuid);
        builder.add(itemUuid);
        builder.add(version);
        builder.add(itemType.getValue());
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        itemUuid = reader.readUuid();
        version = reader.readInt();
        itemType = DefinitionItemType.get(reader.readInt());
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

    public DefinitionItemType getItemType() {
        return itemType;
    }

    public void setItemType(DefinitionItemType itemType) {
        this.itemType = itemType;
    }
}
