package org.endeavour.enterprise.model.database;

import org.endeavour.enterprise.model.DatabaseName;
import org.endeavour.enterprise.model.EndUserRole;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 * DB entity linking endUsers to organisations
 */
public final class DbOrganisationEndUserLink extends DbAbstractTable {


    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbOrganisationEndUserLink.class, "OrganisationEndUserLink", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
            "OrganisationEndUserLinkUuid,OrganisationUuid,EndUserUuid,Permissions,DtExpired", "OrganisationEndUserLinkUuid");


    private UUID organisationUuid = null;
    private UUID endUserUuid = null;
    private int permissions = -1;
    private Date dtExpired = null;


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
        builder.add(permissions);
        builder.add(dtExpired);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        organisationUuid = reader.readUuid();
        endUserUuid = reader.readUuid();
        permissions = reader.readInt();
        dtExpired = reader.readDateTime();
    }

    /**
     * non-db gets/sets
     */
    public EndUserRole getRole() {
        return EndUserRole.get(permissions);
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
