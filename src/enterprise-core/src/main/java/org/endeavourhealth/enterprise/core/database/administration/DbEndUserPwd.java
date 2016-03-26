package org.endeavourhealth.enterprise.core.database.administration;

import org.endeavourhealth.enterprise.core.database.*;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class DbEndUserPwd extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbEndUserPwd.class);

    @DatabaseColumn
    @PrimaryKeyColumn
    private UUID endUserPwdUuid = null;
    @DatabaseColumn
    private UUID endUserUuid = null;
    @DatabaseColumn
    private String pwdHash = null;
    @DatabaseColumn
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

    /**
     * gets/sets
     */
    public UUID getEndUserPwdUuid() {
        return endUserPwdUuid;
    }

    public void setEndUserPwdUuid(UUID endUserPwdUuid) {
        this.endUserPwdUuid = endUserPwdUuid;
    }

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
