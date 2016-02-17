package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 */
public final class DbOrganisation extends DbAbstractTable {

    private UUID organisationUuid = null;
    private String name = null;
    private String nationalId = null;

    //register as a DB entity
    private static TableAdapter adapter = new TableAdapter(DbOrganisation.class,
                                                "Organisation", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
                                                new String[] { "OrganisationUuid", "Name", "NationalId"});



    public DbOrganisation()
    {}

    @Override
    public TableAdapter getAdapter() {
        return adapter;
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
