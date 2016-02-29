package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 */
public final class DbOrganisation extends DbAbstractTable {

    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbOrganisation.class, "Organisation", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
            "OrganisationUuid,Name,NationalId", "OrganisationUuid");

    private String name = null;
    private String nationalId = null;


    public DbOrganisation()
    {}

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(name);
        builder.add(nationalId);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        name = reader.readString();
        nationalId = reader.readString();
    }

    public static List<DbOrganisation> retrieveForAll() throws Exception
    {
        //2016-02-29 DL - changed how we connect to db
        return DatabaseManager.db().retrieveAllOrganisations();
        //return adapter.retrieveEntities("Administration.Organisation_SelectForAll");
    }
    public static DbOrganisation retrieveForUuid(UUID uuid) throws Exception
    {
        //2016-02-29 DL - changed how we connect to db
        return (DbOrganisation)DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
        //return (DbOrganisation)adapter.retrieveSingleEntity("Administration._Organisation_SelectForUuid", uuid);
    }
    public static DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception
    {
        return DatabaseManager.db().retrieveOrganisationForNameNationalId(name, nationalId);
    }


    /**
     * gets/sets
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
}
