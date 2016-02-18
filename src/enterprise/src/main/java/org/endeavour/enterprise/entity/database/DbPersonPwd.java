package org.endeavour.enterprise.entity.database;

import net.sourceforge.jtds.jdbc.DateTime;
import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 */
public class DbPersonPwd extends DbAbstractTable {

    private UUID personUuid = null;
    private String pwdHash = null;
    private Date dtExpired = null;

    //register as a DB entity
    private static TableAdapter adapter = new TableAdapter(DbPersonPwd.class,
            "PersonPwd", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
            new String[] {"PersonPwdUuid", "PersonUuid", "PwdHash", "DtExpired"});


    public DbPersonPwd()
    {

    }

    public static DbPersonPwd retrieveForPersonNotExpired(UUID personUuid) throws Throwable
    {
        return (DbPersonPwd)adapter.retrieveSingleEntity("Administration.PersonPwd_SelectForPersonNotExpired", personUuid);
    }
    public static DbPersonPwd retrieveForUuid(UUID uuid) throws Throwable
    {
        return (DbPersonPwd)adapter.retrieveSingleEntity("Administration.PersonPwd_SelectForUuid", uuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(InsertBuilder builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(personUuid);
        builder.add(pwdHash);
        builder.add(dtExpired);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        personUuid = reader.readUuid();
        pwdHash = reader.readString();
        dtExpired = reader.readDateTime();
    }


    /**
     * gets/sets
     */
    public UUID getPersonUuid() {
        return personUuid;
    }

    public void setPersonUuid(UUID personUuid) {
        this.personUuid = personUuid;
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
