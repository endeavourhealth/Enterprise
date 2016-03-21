package org.endeavourhealth.enterprise.core.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

    //singleton
    private static DatabaseManager ourInstance = new DatabaseManager();

    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    private DatabaseI databaseImplementation = null;

    private DatabaseManager() {

        //this would be where we plug in support for different databases
        databaseImplementation = new SqlServerDatabase();
    }

    public static DatabaseI db() {
        return getInstance().databaseImplementation;
    }
}

