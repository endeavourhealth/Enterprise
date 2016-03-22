package org.endeavourhealth.enterprise.core.database.administration;

import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.ResultReader;
import org.endeavourhealth.enterprise.core.database.TableAdapter;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class DbEndUserPwd extends DbAbstractTable {

    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbEndUserPwd.class,
            "EndUserPwdUuid,EndUserUuid,PwdHash,DtExpired", "EndUserPwdUuid");


    private UUID endUserUuid = null;
    private String pwdHash = null;
    private Instant dtExpired = null;
    //TODO: 2016-02-22 DL - should have number of lives and expiry date for passwords?


    public DbEndUserPwd() {

    }

    public static DbEndUserPwd retrieveForEndUserNotExpired(UUID endUserUuid) throws Exception {
        return (DbEndUserPwd) DatabaseManager.db().retrieveEndUserPwdForUserNotExpired(endUserUuid);
    }

    public static DbEndUserPwd retrieveForUuid(UUID uuid) throws Exception {
        return (DbEndUserPwd) DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(endUserUuid);
        builder.add(pwdHash);
        builder.add(dtExpired);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        endUserUuid = reader.readUuid();
        pwdHash = reader.readString();
        dtExpired = reader.readDateTime();
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

    public String getPwdHash() {
        return pwdHash;
    }

    public void setPwdHash(String pwdHash) {
        this.pwdHash = pwdHash;
    }

    public Instant getDtExpired() {
        return dtExpired;
    }

    public void setDtExpired(Instant dtExpired) {
        this.dtExpired = dtExpired;
    }

}
