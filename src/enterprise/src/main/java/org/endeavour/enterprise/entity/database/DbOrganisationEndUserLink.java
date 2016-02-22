package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

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
    private static TableAdapter adapter = new TableAdapter(DbOrganisation.class,
            "OrganisationEndUserLink", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE);



    public DbOrganisationEndUserLink()
    {}

    public static List<DbAbstractTable> retrieveForEndUserNotExpired(UUID endUserUuid) throws Throwable
    {
        return adapter.retrieveEntities("Administration.OrganisationEndUserLink_SelectForEndUserNotExpired", endUserUuid);
    }
    public static DbOrganisationEndUserLink retrieveForUuid(UUID uuid) throws Throwable
    {
        return (DbOrganisationEndUserLink)adapter.retrieveSingleEntity("Administration.OrganisationEndUserLink_SelectForUuid", uuid);
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
