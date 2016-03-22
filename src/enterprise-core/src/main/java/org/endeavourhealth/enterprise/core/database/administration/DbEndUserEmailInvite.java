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

public final class DbEndUserEmailInvite extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbEndUserEmailInvite.class,
            "EndUserEmailInviteUuid,EndUserUuid,UniqueToken,DtCompleted", "EndUserEmailInviteUuid");

    private UUID endUserUuid = null;
    private String uniqueToken = null;
    private Instant dtCompleted = null;


    public DbEndUserEmailInvite() {
    }

    public static List<DbEndUserEmailInvite> retrieveForEndUserNotCompleted(UUID userUuid) throws Exception {
        return DatabaseManager.db().retrieveEndUserEmailInviteForUserNotCompleted(userUuid);
    }

    public static DbEndUserEmailInvite retrieveForToken(String token) throws Exception {
        return DatabaseManager.db().retrieveEndUserEmailInviteForToken(token);
    }

    public static DbEndUserEmailInvite retrieveForUuid(UUID uuid) throws Exception {
        return (DbEndUserEmailInvite) DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(endUserUuid);
        builder.add(uniqueToken);
        builder.add(dtCompleted);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        endUserUuid = reader.readUuid();
        uniqueToken = reader.readString();
        dtCompleted = reader.readDateTime();
    }

    /**
     * sends the invite email for this person
     */
    public void sendInviteEmail(DbEndUser user, DbOrganisation org) {
        String emailTo = user.getEmail();

        String forename = user.getForename();

        //TODO: 2016-02-22 DL - send invite email to the new user
    }

    /**
     * sends an email to the person, telling them about the new access that
     * has been added to their account
     */
    public static void sendNewAccessGrantedEmail(DbEndUser user, DbOrganisation org) {

        //TODO: 2016-02-22 DL - send email to the user about new acess granted
    }

    /**
     * gets/sets
     */
    public UUID getEndUserUuid() {
        return endUserUuid;
    }

    public void setEndUserUuid(UUID endUserUuid) {
        this.endUserUuid = endUserUuid;
    }

    public String getUniqueToken() {
        return uniqueToken;
    }

    public void setUniqueToken(String uniqueToken) {
        this.uniqueToken = uniqueToken;
    }

    public Instant getDtCompleted() {
        return dtCompleted;
    }

    public void setDtCompleted(Instant dtCompleted) {
        this.dtCompleted = dtCompleted;
    }
}