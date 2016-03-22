package org.endeavourhealth.enterprise.core.database.administration;

import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.ResultReader;
import org.endeavourhealth.enterprise.core.database.TableAdapter;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DB entity linking endUsers to organisations
 */
public final class DbOrganisationEndUserLink extends DbAbstractTable {

    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbOrganisationEndUserLink.class,
            "OrganisationEndUserLinkUuid,OrganisationUuid,EndUserUuid,IsAdmin,DtExpired", "OrganisationEndUserLinkUuid");


    private UUID organisationUuid = null;
    private UUID endUserUuid = null;
    private boolean isAdmin = false;
    private Instant dtExpired = null;


    public DbOrganisationEndUserLink() {
    }

    public static List<DbOrganisationEndUserLink> retrieveForEndUserNotExpired(UUID endUserUuid) throws Exception {
        return DatabaseManager.db().retrieveOrganisationEndUserLinksForUserNotExpired(endUserUuid);
    }

    public static DbOrganisationEndUserLink retrieveForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception {
        return DatabaseManager.db().retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(organisationUuid, endUserUuid);
    }

    public static List<DbOrganisationEndUserLink> retrieveForOrganisationNotExpired(UUID organisationUuid) throws Exception {
        return DatabaseManager.db().retrieveOrganisationEndUserLinksForOrganisationNotExpired(organisationUuid);
    }

    public static DbOrganisationEndUserLink retrieveForUuid(UUID uuid) throws Exception {
        return (DbOrganisationEndUserLink) DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(organisationUuid);
        builder.add(endUserUuid);
        builder.add(isAdmin);
        builder.add(dtExpired);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        endUserUuid = reader.readUuid();
        isAdmin = reader.readBoolean();
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Instant getDtExpired() {
        return dtExpired;
    }

    public void setDtExpired(Instant dtExpired) {
        this.dtExpired = dtExpired;
    }
}
