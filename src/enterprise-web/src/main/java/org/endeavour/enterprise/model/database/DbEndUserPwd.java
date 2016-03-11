package org.endeavour.enterprise.model.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 */
public class DbEndUserPwd extends DbAbstractTable {

    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbEndUserPwd.class,
            "EndUserPwd", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
            "EndUserPwdUuid,EndUserUuid,PwdHash,DtExpired", "EndUserPwdUuid");


    private UUID endUserUuid = null;
    private String pwdHash = null;
    private Date dtExpired = null;
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

    public Date getDtExpired() {
        return dtExpired;
    }

    public void setDtExpired(Date dtExpired) {
        this.dtExpired = dtExpired;
    }

}
