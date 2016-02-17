package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 */
public final class DbFolder extends DbAbstractTable {

    //private UUID folderUuid = null;
    private UUID organisationUuid = null;
    private UUID parentFolderUuid = null;
    private String title = null;

    //register as a DB entity
    private static TableAdapter adapter = new TableAdapter(DbFolder.class,
                                        "Folder", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
                                        new String[]{"FolderUuid", "OrganisationUuid", "ParentFolderUuid", "Title"});

    public DbFolder()
    {}


    @Override
    public TableAdapter getAdapter() {
        return adapter;
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
/*    public UUID getFolderUuid() {
        return folderUuid;
    }

    public void setFolderUuid(UUID folderUuid) {
        this.folderUuid = folderUuid;
    }*/

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
