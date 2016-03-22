package org.endeavourhealth.enterprise.core.database.administration;

import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.ResultReader;
import org.endeavourhealth.enterprise.core.database.TableAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbOrganisation extends DbAbstractTable {

    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbOrganisation.class,
            "OrganisationUuid,Name,NationalId", "OrganisationUuid");

    private String name = null;
    private String nationalId = null;


    public DbOrganisation() {
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(name);
        builder.add(nationalId);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        name = reader.readString();
        nationalId = reader.readString();
    }

    public static List<DbOrganisation> retrieveForAll() throws Exception {
        return DatabaseManager.db().retrieveAllOrganisations();
    }

    public static DbOrganisation retrieveForUuid(UUID uuid) throws Exception {
        return (DbOrganisation) DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
    }

    public static DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception {
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
