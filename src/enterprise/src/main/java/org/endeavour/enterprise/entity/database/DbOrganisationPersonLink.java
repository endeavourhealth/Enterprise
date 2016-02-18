package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 * DB entity linking persons to organisations
 */
public final class DbOrganisationPersonLink extends DbAbstractTable {

    private UUID organisationUuid = null;
    private UUID personUuid = null;
    private int permissions = -1;
    private Date dtExpired = null;

    //register as a DB entity
    private static TableAdapter adapter = new TableAdapter(DbOrganisation.class,
            "OrganisationPersonLink", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
            new String[] { "OrganisationPersonLinkUuid", "OrganisationUuid", "PersonUuid", "Persmissions", "DtExpired"});



    public DbOrganisationPersonLink()
    {}

    public static List<DbAbstractTable> retrieveForPersonNotExpired(UUID personUuid) throws Throwable
    {
        return adapter.retrieveEntities("Administration.OrganisationPersonLink_SelectForPersonNotExpired", personUuid);
    }
    public static DbOrganisationPersonLink retrieveForUuid(UUID uuid) throws Throwable
    {
        return (DbOrganisationPersonLink)adapter.retrieveSingleEntity("Administration.OrganisationPersonLink_SelectForUuid", uuid);
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
        builder.add(personUuid);
        builder.add(permissions);
        builder.add(dtExpired);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        personUuid = reader.readUuid();
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

    public UUID getPersonUuid() {
        return personUuid;
    }

    public void setPersonUuid(UUID personUuid) {
        this.personUuid = personUuid;
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
