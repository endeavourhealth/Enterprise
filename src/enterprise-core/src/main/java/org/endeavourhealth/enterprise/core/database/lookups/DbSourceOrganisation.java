package org.endeavourhealth.enterprise.core.database.lookups;

import org.endeavourhealth.enterprise.core.database.*;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.UUID;

public final class DbSourceOrganisation extends DbAbstractTable {
    private static final TableAdapter adapter = new TableAdapter(DbSourceOrganisation.class);

    @DatabaseColumn
    @PrimaryKeyColumn
    private String odsCode = null;
    @DatabaseColumn
    private String name = null;
    @DatabaseColumn
    private boolean isReferencedByData = false;

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    public static List<DbSourceOrganisation> retrieveAll(boolean includeUnreferencedOnes) throws Exception {
        return DatabaseManager.db().retrieveAllSourceOrganisations(includeUnreferencedOnes);
    }
    public static List<DbSourceOrganisation> retrieveForSearch(String searchTerm) throws Exception {
        return DatabaseManager.db().retrieveSourceOrganisationsForSearch(searchTerm);
    }
    public static List<DbSourceOrganisation> retrieveForOdsCodes(List<String> odsCodes) throws Exception {
        return DatabaseManager.db().retrieveSourceOrganisationsForOdsCodes(odsCodes);
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

    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    public boolean isReferencedByData() {
        return isReferencedByData;
    }

    public void setReferencedByData(boolean referencedByData) {
        isReferencedByData = referencedByData;
    }
}
