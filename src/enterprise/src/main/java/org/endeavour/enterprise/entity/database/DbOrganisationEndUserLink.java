package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;
import org.endeavour.enterprise.model.EndUserRole;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 * DB entity linking endUsers to organisations
 */
public final class DbOrganisationEndUserLink extends DbAbstractTable {

    private UUID organisationUuid = null;
    private UUID endUserUuid = null;
    private int permissions = -1;
    private Date dtExpired = null;

    //register as a DB entity
    private static TableAdapter adapter = new TableAdapter(DbOrganisationEndUserLink.class,
            "OrganisationEndUserLink", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE);



    public DbOrganisationEndUserLink()
    {}

    public static List<DbAbstractTable> retrieveForEndUserNotExpired(UUID endUserUuid) throws Exception
    {
        return adapter.retrieveEntities("Administration.OrganisationEndUserLink_SelectForEndUserNotExpired", endUserUuid);
    }
    public static DbOrganisationEndUserLink retrieveForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception
    {
        //TODO: 2016-02-22 DL - should really move this into a SP that takes two parameters
        List<DbAbstractTable> v = retrieveForEndUserNotExpired(endUserUuid);
        for (int i=0; i<v.size(); i++)
        {
            DbOrganisationEndUserLink link = (DbOrganisationEndUserLink)v.get(i);
            if (link.getOrganisationUuid().equals(organisationUuid))
            {
                return link;
            }
        }
        return null;
    }
    public static List<DbAbstractTable> retrieveForOrganisationNotExpired(UUID organisationUuid) throws Exception
    {
        return adapter.retrieveEntities("Administration.OrganisationEndUserLink_SelectForOrganisationNotExpired", organisationUuid);
    }
    public static DbOrganisationEndUserLink retrieveForUuid(UUID uuid) throws Exception
    {
        return (DbOrganisationEndUserLink)adapter.retrieveSingleEntity("Administration._OrganisationEndUserLink_SelectForUuid", uuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(InsertBuilder builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(organisationUuid);
        builder.add(endUserUuid);
        builder.add(permissions);
        builder.add(dtExpired);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        endUserUuid = reader.readUuid();
        permissions = reader.readInt();
        dtExpired = reader.readDateTime();
    }

    /**
     * non-db gets/sets
     */
    public EndUserRole getRole()
    {
        return EndUserRole.getValue(permissions);
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

    public UUID getEndUserUuid() {
        return endUserUuid;
    }

    public void setEndUserUuid(UUID endUserUuid) {
        this.endUserUuid = endUserUuid;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public Date getDtExpired() {
        return dtExpired;
    }

    public void setDtExpired(Date dtExpired) {
        this.dtExpired = dtExpired;
    }
}
