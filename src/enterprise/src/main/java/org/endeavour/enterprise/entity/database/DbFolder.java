package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 */
public final class DbFolder extends DbAbstractTable {

    private UUID organisationUuid = null;
    private UUID parentFolderUuid = null;
    private String title = null;

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
        builder.add(getOrganisationUuid());
        builder.add(parentFolderUuid);
        builder.add(title);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        parentFolderUuid = reader.readUuid();
        title = reader.readString();
    }

    public static DbFolder retrieveForOrganisationTitleParent(UUID organisationUuid, String title, UUID parentUuid) throws Throwable
    {
        return (DbFolder)adapter.retrieveSingleEntity("Administration.Folder_SelectForOrganisationTitleParentOrganisation", organisationUuid, title, parentUuid);
    }
    public static DbFolder retrieveForUuid(UUID uuid) throws Throwable
    {
        return (DbFolder)adapter.retrieveSingleEntity("Administration.Folder_SelectForUuid", uuid);
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

}
