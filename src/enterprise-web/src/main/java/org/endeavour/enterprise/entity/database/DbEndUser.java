package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 */
public final class DbEndUser extends DbAbstractTable
{

    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbEndUser.class,
            "EndUser", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
            "EndUserUuid,Title,Forename,Surname,Email,IsSuperUser", "EndUserUuid");

    private String title = null;
    private String forename = null;
    private String surname = null;
    private String email = null;
    private boolean isSuperUser = false;


    public DbEndUser()
    {

    }

    public static DbEndUser retrieveForEmail(String email) throws Exception
    {
        //2016-02-29 DL - changed how we connect to db
        return (DbEndUser)DatabaseManager.db().retrieveEndUserForEmail(email);
        //return (DbEndUser)adapter.retrieveSingleEntity("Administration.EndUser_SelectForEmail", email);
    }
    public static DbEndUser retrieveForUuid(UUID uuid) throws Exception
    {
        //2016-02-29 DL - changed how we connect to db
        return (DbEndUser)DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
        //return (DbEndUser)adapter.retrieveSingleEntity("Administration._EndUser_SelectForUuid", uuid);
    }
    public static List<DbEndUser> retrieveSuperUsers() throws Exception
    {
        return DatabaseManager.db().retrieveSuperUsers();
    }


    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(title);
        builder.add(forename);
        builder.add(surname);
        builder.add(email);
        builder.add(isSuperUser);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        title = reader.readString();
        forename = reader.readString();
        surname = reader.readString();
        email = reader.readString();
        isSuperUser = reader.readBoolean();
    }


    /**
     * gets/sets
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsSuperUser() {
        return isSuperUser;
    }

    public void setIsSuperUser(boolean superUser) {
        isSuperUser = superUser;
    }


}
