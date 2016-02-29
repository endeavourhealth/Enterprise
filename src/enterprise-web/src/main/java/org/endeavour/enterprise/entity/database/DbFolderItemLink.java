package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 22/02/2016.
 */
public final class DbFolderItemLink extends DbAbstractTable
{
    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbFolderItemLink.class, "FolderItemLink", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
            "FolderItemLinkUuid,FolderUuid,ItemUuid", "FolderItemLinkUuid");


    private UUID folderUuid = null;
    private UUID itemUuid = null;

    public DbFolderItemLink()
    {}

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(folderUuid);
        builder.add(itemUuid);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        folderUuid = reader.readUuid();
        itemUuid = reader.readUuid();
    }

    public static List<DbFolderItemLink> retrieveForFolder(UUID folderUuid) throws Exception
    {
        //2016-02-29 DL - changed how we connect to db
        return DatabaseManager.db().retrieveFolderItemLinksForFolder(folderUuid);
        //return adapter.retrieveEntities("Administration.FolderItemLink_SelectForFolder", folderUuid);
    }
    public static DbFolderItemLink retrieveForUuid(UUID uuid) throws Exception
    {
        //2016-02-29 DL - changed how we connect to db
        return (DbFolderItemLink)DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
        //return (DbFolderItemLink)adapter.retrieveSingleEntity("Administration._Folder_SelectForUuid", uuid);
    }


    /**
     * gets/sets
     */
    public UUID getFolderUuid() {
        return folderUuid;
    }

    public void setFolderUuid(UUID folderUuid) {
        this.folderUuid = folderUuid;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }
}
