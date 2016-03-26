package org.endeavourhealth.enterprise.core.database.administration;

import org.endeavourhealth.enterprise.core.database.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbEndUser extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbEndUser.class,
            "EndUserUuid,Title,Forename,Surname,Email,IsSuperUser", "EndUserUuid");

    @DatabaseColumn
    @PrimaryKeyColumn
    private String title = null;
    @DatabaseColumn
    private String forename = null;
    @DatabaseColumn
    private String surname = null;
    @DatabaseColumn
    private String email = null;
    @DatabaseColumn
    private boolean isSuperUser = false;


    public DbEndUser() {

    }

    public static DbEndUser retrieveForEmail(String email) throws Exception {
        return (DbEndUser) DatabaseManager.db().retrieveEndUserForEmail(email);
    }

    public static DbEndUser retrieveForUuid(UUID uuid) throws Exception {
        return (DbEndUser) DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid);
    }

    public static List<DbEndUser> retrieveSuperUsers() throws Exception {
        return DatabaseManager.db().retrieveSuperUsers();
    }


    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(title);
        builder.add(forename);
        builder.add(surname);
        builder.add(email);
        builder.add(isSuperUser);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
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

    public boolean isSuperUser() {
        return isSuperUser;
    }

    public void setSuperUser(boolean superUser) {
        isSuperUser = superUser;
    }

}
