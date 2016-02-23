package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 */
public final class DbFolder extends DbAbstractTable {

    public static final int FOLDER_TYPE_LIBRARY = 1;
    public static final int FOLDER_TYPE_REPORTS = 2;

    private UUID organisationUuid = null;
    private UUID parentFolderUuid = null;
    private String title = null;
    private int folderType = -1;

    //register as a DB entity
    private static TableAdapter adapter = new TableAdapter(DbFolder.class,
                                        "Folder", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE);

    public DbFolder()
    {}


    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(InsertBuilder builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(organisationUuid);
        builder.add(parentFolderUuid);
        builder.add(title);
        builder.add(folderType);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        parentFolderUuid = reader.readUuid();
        title = reader.readString();
        folderType = reader.readInt();
    }

    public static List<DbAbstractTable> retrieveForOrganisationParentType(UUID organisationUuid, UUID parentUuid, int folderType) throws Throwable
    {
        return adapter.retrieveEntities("Administration.Folder_SelectForOrganisationParentType", organisationUuid, parentUuid, folderType);
    }
    public static DbFolder retrieveForOrganisationTitleParentType(UUID organisationUuid, String title, UUID parentUuid, int folderType) throws Throwable
    {
        return (DbFolder)adapter.retrieveSingleEntity("Administration.Folder_SelectForOrganisationTitleParentType", organisationUuid, title, parentUuid, folderType);
    }
/*
    public static List<DbAbstractTable> retrieveForOrganisation(UUID organisationUuid) throws Throwable
    {
        return adapter.retrieveEntities("Administration.Folder_SelectForOrganisation", organisationUuid);
    }
*/
    public static DbFolder retrieveForUuid(UUID uuid) throws Throwable
    {
        return (DbFolder)adapter.retrieveSingleEntity("Administration._Folder_SelectForUuid", uuid);
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

    public UUID getParentFolderUuid() {
        return parentFolderUuid;
    }

    public void setParentFolderUuid(UUID parentFolderUuid) {
        this.parentFolderUuid = parentFolderUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFolderType() {
        return folderType;
    }

    public void setFolderType(int folderType) {
        this.folderType = folderType;
    }
}
