package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 */
public final class DbPerson extends DbAbstractTable {

    private String title = null;
    private String forename = null;
    private String surname = null;
    private String email = null;
    private boolean isSuperUser = false;

    //register as a DB entity
    private static TableAdapter adapter = new TableAdapter(DbPerson.class,
                                        "Person", "Administration", DatabaseName.ENDEAVOUR_ENTERPRISE,
                                        new String[] {"PersonUuid", "Title", "Forename", "Surname", "Email", "IsSuperUser"});


    public DbPerson()
    {

    }

    public static DbPerson retrieveForEmail(String email) throws Throwable
    {
        return (DbPerson)adapter.retrieveSingleEntity("Administration.Person_SelectForEmail", email);
    }
    public static DbPerson retrieveForUuid(UUID uuid) throws Throwable
    {
        return (DbPerson)adapter.retrieveSingleEntity("Administration.Person_SelectForUuid", uuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(InsertBuilder builder)
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
