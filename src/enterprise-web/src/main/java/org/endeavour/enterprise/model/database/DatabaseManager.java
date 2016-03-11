package org.endeavour.enterprise.model.database;

import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.core.db.ConnectionSource;
import ch.qos.logback.core.db.DriverManagerConnectionSource;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import org.endeavour.enterprise.framework.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
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

        //probably should go in a class guaranteed to be initialised earlier
        //registerLogbackDbAppender();

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


    private static void registerLogbackDbAppender()
    {
System.out.println("----------------------------------------------------------");
        try {

            Class.forName(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName());

            /*DriverManagerConnectionSource connSource = new DriverManagerConnectionSource(){
                @Override
                public SQLDialectCode getSQLDialectCode()
                {
                    return SQLDialectCode.MSSQL_DIALECT;
                }
            };
            connSource.setUrl(Configuration.DB_CONNECTION_STRING);
            connSource.setContext(logger.getLoggerContext());

            DatabaseMetaData se = connSource.getConnection().getMetaData();
            String sqle = se.getDatabaseProductName().toLowerCase();
            System.out.println("----------------------------------------------------------"  + sqle);

            connSource.start();*/

            LogbackConnectionSource src = new LogbackConnectionSource();
            src.start();

            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

            DBAppender dbAppender = new DBAppender();
            //dbAppender.setConnectionSource(connSource);
            dbAppender.setConnectionSource(src);
            dbAppender.setContext(logger.getLoggerContext());
            dbAppender.start();


            logger.addAppender(dbAppender);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        /*Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        DBAppender appender = new DBAppender();
        appender.setConnectionSource();

        //ConnectionSource cs = new Co

        rootLogger.addAppender(appender);*/

        /**
         * TODO: 2016-02-25 DL - get the DB appender for log back working
         *
         xxx 1) test logging exceptions
         xxx 4) what are the two XML files
         xxxx 5) delete one of the xml files
         6) move logback.xml to an existing folder


         2) get logging to db
         3) understand SQL timestamp?
         5) Add two xml files to repo
         6) commit to GitHub
         */

    }
}


class LogbackConnectionSource implements ConnectionSource {
    private DriverManagerConnectionSource inner = new DriverManagerConnectionSource();

    public LogbackConnectionSource() {
        inner.setUrl(Configuration.DB_CONNECTION_STRING);
    }

    @Override
    public Connection getConnection() throws SQLException {
        System.out.println("getting connection");
        return inner.getConnection();
    }

    @Override
    public SQLDialectCode getSQLDialectCode() {
        return SQLDialectCode.MSSQL_DIALECT;
    }

    @Override
    public boolean supportsGetGeneratedKeys() {
        return inner.supportsGetGeneratedKeys();
    }

    @Override
    public boolean supportsBatchUpdates() {
        return inner.supportsBatchUpdates();
    }

    @Override
    public void start() {
        inner.start();
    }

    @Override
    public void stop() {
        inner.stop();
    }

    @Override
    public boolean isStarted() {
        return inner.isStarted();
    }
}

