package org.endeavourhealth.enterprise.core.database;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
import ch.qos.logback.core.db.ConnectionSource;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String LOGGING_SCHEMA_PREFIX = "Logging.";

    //singleton
    private static DatabaseManager ourInstance = new DatabaseManager();
    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    private DatabaseI databaseImplementation = null;
    private String url = null;
    private String username = null;
    private String password = null;
    private ComboPooledDataSource cpds = null;



    public void setConnectionProperties(String url, String username, String password) {

        //this would be where we plug in support for different databases
        this.databaseImplementation = new SqlServerDatabase();

        this.url = url;
        this.username = username;
        this.password = password;

        try {

            //need to force the loading of the Driver class before we try to create any connections
            Class.forName(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName());

            cpds = new ComboPooledDataSource();
            cpds.setDriverClass("net.sourceforge.jtds.jdbc.Driver");
            cpds.setJdbcUrl(url);
            cpds.setUser(username);
            cpds.setPassword(password);

            //arbitrary pool settings
            cpds.setInitialPoolSize(5);
            cpds.setMinPoolSize(5);
            cpds.setAcquireIncrement(5);
            cpds.setMaxPoolSize(20);
            cpds.setMaxStatements(180);

        } catch (ClassNotFoundException | PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        //no connection pooling option
        //return DriverManager.getConnection(getInstance().url, getInstance().username, getInstance().password);

        Connection conn = getInstance().cpds.getConnection();
        conn.setAutoCommit(false); //never want auto-commit
        return conn;
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }


    public static DatabaseI db() {
        return getInstance().databaseImplementation;
    }

    public void registerLogbackDbAppender() {

        //we need our own implementation of a conneciton source, because logback fails to detect the DB type when against Azure
        LogbackConnectionSource connectionSource = new LogbackConnectionSource();
        LogbackDbNameResolver nameResolver = new LogbackDbNameResolver();

        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        DBAppender dbAppender = new DBAppender();
        dbAppender.setContext(rootLogger.getLoggerContext());
        dbAppender.setConnectionSource(connectionSource);
        dbAppender.setName("DB Appender");
        dbAppender.setDbNameResolver(nameResolver);
        dbAppender.start();

        //use an async appender so logging to DB doesn't block
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(rootLogger.getLoggerContext());
        asyncAppender.setName("ASYNC");
        //    // excluding caller data (used for stack traces) improves appender's performance
        //    asyncAppender.setIncludeCallerData(false);
        //    // set threshold to 0 to disable discarding and keep all events
        //    asyncAppender.setDiscardingThreshold(0);
        //    asyncAppender.setQueueSize(256);
        asyncAppender.addAppender(dbAppender);
        asyncAppender.start();

        rootLogger.addAppender(asyncAppender);
    }

    /**
     * because the three logging tables are in a schema, we need to override the resolver to insert the schema name
     */
    class LogbackDbNameResolver extends DefaultDBNameResolver {
        @Override
        public <N extends Enum<?>> String getTableName(N tableName) {
            return LOGGING_SCHEMA_PREFIX + super.getTableName(tableName);
        }
    }

    /**
     * Connection source implementation for LogBack, as it seems unable to correctly work out it should use SQL Server dialect
     */
    class LogbackConnectionSource implements ConnectionSource {

        public LogbackConnectionSource() {}

        @Override
        public Connection getConnection() throws SQLException {
            return cpds.getConnection();
        }

        @Override
        public SQLDialectCode getSQLDialectCode() {
            return databaseImplementation.getLogbackDbDialectCode();
        }

        @Override
        public boolean supportsGetGeneratedKeys() {
            return false;
        }

        @Override
        public boolean supportsBatchUpdates() {
            return false;
        }

        @Override
        public void start() {}

        @Override
        public void stop() {}

        @Override
        public boolean isStarted() {
            return true;
        }
    }
}

