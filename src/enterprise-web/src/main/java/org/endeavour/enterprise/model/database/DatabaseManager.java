package org.endeavour.enterprise.model.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Drew on 29/02/2016.
 */
public final class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

    //singleton
    private static DatabaseManager ourInstance = new DatabaseManager();

    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    private static Date endOfTime = null;

    private DatabaseI databaseImplementation = null;

    private DatabaseManager() {
        //this would be where we plug in support for different databases
        databaseImplementation = new SqlServerDatabase();
    }

    /**
     * 2016-02-25 DL - haven't got anywhere good to put these, but leaving with other DB stuff
     */
    public static Date getEndOfTime() {
        if (endOfTime == null) {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                endOfTime = formatter.parse("31/12/9999");
            } catch (ParseException pe) {
                LOG.error("Failed to create end of time date", pe);
            }
        }
        return endOfTime;
    }

    public static DatabaseI db() {
        return getInstance().databaseImplementation;
    }
}
