package org.endeavourhealth.enterprise.core.database;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
import ch.qos.logback.core.db.ConnectionSource;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.administration.*;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.definition.DbItemDependency;
import org.endeavourhealth.enterprise.core.database.execution.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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

    public void sqlTest() throws Exception {

        List<DbOrganisation> orgs = db().retrieveAllOrganisations();

/*
            //generic read/write functions
            public void writeEntity(DbAbstractTable entity) throws Exception;

            public void writeEntities(List<DbAbstractTable> entities) throws Exception;

            public DbAbstractTable retrieveForPrimaryKeys(TableAdapter a, Object... keys) throws Exception;

            //specific functions
            public DbEndUser retrieveEndUserForEmail(String email) throws Exception;

            public List<DbEndUser> retrieveSuperUsers() throws Exception;

            public DbEndUserPwd retrieveEndUserPwdForUserNotExpired(UUID endUserUuid) throws Exception;

            public List<DbOrganisation> retrieveAllOrganisations() throws Exception;

            public DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception;

            public List<DbEndUserEmailInvite> retrieveEndUserEmailInviteForUserNotCompleted(UUID userUuid) throws Exception;

            public DbEndUserEmailInvite retrieveEndUserEmailInviteForToken(String token) throws Exception;

            public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForOrganisationNotExpired(UUID organisationUuid) throws Exception;

            public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForUserNotExpired(UUID endUserUuid) throws Exception;

            public DbOrganisationEndUserLink retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception;

            public DbItem retrieveItemForUuid(UUID itemUuid) throws Exception;

            public List<DbItem> retrieveDependentItems(UUID itemUuid, UUID auditUuid, DependencyType dependencyType) throws Exception;

            public List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception;

            public List<DbItem> retrieveItemsForActiveItems(List<DbActiveItem> activeItems) throws Exception;

            public DbActiveItem retrieveActiveItemForItemUuid(UUID itemUuid) throws Exception;

            public List<DbActiveItem> retrieveActiveItemDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception;

            public List<DbActiveItem> retrieveActiveItemRecentItems(UUID userUuid, int count) throws Exception;

            public int retrieveCountDependencies(UUID itemUuid, DependencyType dependencyType) throws Exception;

            public List<DbItemDependency> retrieveItemDependenciesForItem(UUID itemUuid, UUID auditUuid) throws Exception;

            public List<DbItemDependency> retrieveItemDependenciesForItemType(UUID itemUuid, UUID auditUuid, DependencyType dependencyType) throws Exception;

            public List<DbItemDependency> retrieveItemDependenciesForDependentItem(UUID dependentItemUuid) throws Exception;

            public List<DbItemDependency> retrieveItemDependenciesForDependentItemType(UUID dependentItemUuid, DependencyType dependencyType) throws Exception;

            public List<DbRequest> retrievePendingRequestsForItems(UUID organisationUuid, List<UUID> itemUuids) throws Exception;

            public List<DbRequest> retrievePendingRequests() throws Exception;

            public List<DbJob> retrieveRecentJobs(int count) throws Exception;

            public List<DbJob> retrieveJobsForStatus(ExecutionStatus status) throws Exception;

            public List<DbJob> retrieveJobsForUuids(List<UUID> uuids) throws Exception;

            public List<DbJobReport> retrieveJobReports(UUID organisationUuid, int count) throws Exception;

            public List<DbJobReport> retrieveJobReportsForJob(UUID jobUuid) throws Exception;

            public List<DbJobReport> retrieveLatestJobReportsForItemUuids(UUID organisationUuid, List<UUID> itemUuids) throws Exception;

            public List<DbJobReportItem> retrieveJobReportItemsForJobReport(UUID jobReportUuid) throws Exception;

            public List<DbAudit> retrieveAuditsForUuids(List<UUID> uuids) throws Exception;

            public DbAudit retrieveLatestAudit() throws Exception;

            public List<DbJobContent> retrieveJobContentsForJob(UUID jobUuid) throws Exception;*/

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

