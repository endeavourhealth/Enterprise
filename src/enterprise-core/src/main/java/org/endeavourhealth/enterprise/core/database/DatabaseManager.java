package org.endeavourhealth.enterprise.core.database;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
import ch.qos.logback.core.Appender;
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
import org.endeavourhealth.enterprise.core.database.lookups.DbSourceOrganisation;
import org.endeavourhealth.enterprise.core.database.lookups.DbSourceOrganisationSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String LOGGING_SCHEMA_PREFIX = "Logging.";
    private static final String ASYNC_APPENDER = "ASYNC";

    //singleton
    private static DatabaseManager ourInstance = new DatabaseManager();
    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    private DatabaseI databaseImplementation = null;
    private ComboPooledDataSource cpds = null;


    public void setConnectionProperties(String url, String username, String password) {

        //this would be where we plug in support for different databases
        this.databaseImplementation = new SqlServerDatabase();

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

            LOG.info("Database connection pool set up during server startup");

        } catch (ClassNotFoundException | PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        //no connection pooling option
        //return DriverManager.getConnection(getInstance().url, getInstance().username, getInstance().password);

        Connection conn = getInstance().cpds.getConnection();

        //occasional problems getting a connection that's already closed, so try this quick check
        if (conn.isClosed()) {
            return getConnection();
        }

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
        asyncAppender.setName(ASYNC_APPENDER);
        //    // excluding caller data (used for stack traces) improves appender's performance
        //    asyncAppender.setIncludeCallerData(false);
        //    // set threshold to 0 to disable discarding and keep all events
        //    asyncAppender.setDiscardingThreshold(0);
        //    asyncAppender.setQueueSize(256);
        asyncAppender.addAppender(dbAppender);
        asyncAppender.start();



        rootLogger.addAppender(asyncAppender);
    }

    public void deregisterLogbackDbAppender() {

        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Appender appender = rootLogger.getAppender(ASYNC_APPENDER);
        if (appender != null) {
            appender.stop();
        }
    }

    public void sqlTest() throws Exception {

        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.randomUUID());
        uuidList.add(UUID.randomUUID());
        uuidList.add(UUID.randomUUID());

        db().retrieveEndUserForEmail("Email");
        db().retrieveEndUserForEmail("Ema'il");

        db().retrieveSuperUsers();

        db().retrieveEndUserPwdForUserNotExpired(UUID.randomUUID());

        db().retrieveAllOrganisations();

        db().retrieveOrganisationForNameNationalId("Name", "NationalId");

        db().retrieveEndUserEmailInviteForUserNotCompleted(UUID.randomUUID());

        db().retrieveEndUserEmailInviteForToken("Token");

        db().retrieveOrganisationEndUserLinksForOrganisationNotExpired(UUID.randomUUID());

        db().retrieveOrganisationEndUserLinksForUserNotExpired(UUID.randomUUID());

        db().retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(UUID.randomUUID(), UUID.randomUUID());

        db().retrieveLatestItemForUuid(UUID.randomUUID());

        db().retrieveDependentItems(UUID.randomUUID(), DependencyType.IsContainedWithin);

        db().retrieveNonDependentItems(UUID.randomUUID(), DependencyType.IsChildOf, DefinitionItemType.ReportFolder);

        List<DbActiveItem> activeItems = new ArrayList<>();
        DbActiveItem a = new DbActiveItem();
        a.setItemUuid(UUID.randomUUID());
        a.setAuditUuid(UUID.randomUUID());
        activeItems.add(a);
        a = new DbActiveItem();
        a.setItemUuid(UUID.randomUUID());
        a.setAuditUuid(UUID.randomUUID());
        activeItems.add(a);
        db().retrieveItemsForActiveItems(activeItems);

        db().retrieveActiveItemForItemUuid(UUID.randomUUID());

        db().retrieveActiveItemDependentItems(UUID.randomUUID(), UUID.randomUUID(), DependencyType.Uses);

        db().retrieveActiveItemRecentItems(UUID.randomUUID(), UUID.randomUUID(), 5);

        db().retrieveCountDependencies(UUID.randomUUID(), DependencyType.IsChildOf);

        db().retrieveItemDependenciesForItem(UUID.randomUUID(), UUID.randomUUID());

        db().retrieveItemDependenciesForItemType(UUID.randomUUID(), UUID.randomUUID(), DependencyType.Uses);

        db().retrieveItemDependenciesForDependentItem(UUID.randomUUID());

        db().retrieveItemDependenciesForDependentItemType(UUID.randomUUID(), DependencyType.Uses);

        db().retrievePendingRequestsForItems(UUID.randomUUID(), uuidList);

        db().retrievePendingRequests();

        db().retrieveRecentJobs(5);

        db().retrieveJobsForStatus(ExecutionStatus.Executing);

        db().retrieveJobsForUuids(uuidList);

        db().retrieveJobReports(UUID.randomUUID(), 5);

        db().retrieveJobReportsForJob(UUID.randomUUID());

        db().retrieveLatestJobReportsForItemUuids(UUID.randomUUID(), uuidList);

        db().retrieveJobReportItemsForJobReport(UUID.randomUUID());

        db().retrieveAuditsForUuids(uuidList);

        db().retrieveLatestAudit();

        db().retrieveJobContentsForJob(UUID.randomUUID());

        List<DbAbstractTable> entities = new ArrayList<>();

        DbSourceOrganisation sourceOrganisation = new DbSourceOrganisation();
        sourceOrganisation.setName("Name");
        sourceOrganisation.setOdsCode("OdsCode");
        sourceOrganisation.setReferencedByData(true);
        entities.add(sourceOrganisation);

        DbOrganisation organisation = new DbOrganisation();
        organisation.assignPrimaryUUid();
        organisation.setName("OrgName");
        organisation.setNationalId("OrgId");
        UUID orgUuid = organisation.getOrganisationUuid();
        entities.add(organisation);

        DbEndUser user = new DbEndUser();
        user.assignPrimaryUUid();
        user.setEmail("Email");
        user.setForename("Forename");
        user.setSuperUser(false);
        user.setSurname("Surname");
        user.setTitle("Title");
        UUID userUuid = user.getEndUserUuid();
        entities.add(user);

        DbEndUserEmailInvite invite = new DbEndUserEmailInvite();
        invite.assignPrimaryUUid();
        invite.setDtCompleted(null);
        invite.setEndUserUuid(userUuid);
        invite.setUniqueToken("Token");
        entities.add(invite);

        DbEndUserPwd pwd = new DbEndUserPwd();
        pwd.assignPrimaryUUid();
        pwd.setDtExpired(null);
        pwd.setFailedAttempts(0);
        pwd.setOneTimeUse(false);
        pwd.setEndUserUuid(userUuid);
        pwd.setPwdHash("PwdHash");
        entities.add(pwd);

        DbOrganisationEndUserLink organisationEndUserLink = new DbOrganisationEndUserLink();
        organisationEndUserLink.assignPrimaryUUid();
        organisationEndUserLink.setAdmin(false);
        organisationEndUserLink.setEndUserUuid(userUuid);
        organisationEndUserLink.setDtExpired(null);
        organisationEndUserLink.setOrganisationUuid(orgUuid);
        entities.add(organisationEndUserLink);

        DbSourceOrganisationSet set = new DbSourceOrganisationSet();
        set.assignPrimaryUUid();
        set.setOrganisationUuid(orgUuid);
        set.setName("Name");
        set.setOdsCodes("OdsCodes");
        entities.add(set);

        DbAudit audit = new DbAudit();
        audit.assignPrimaryUUid();
        audit.setOrganisationUuid(orgUuid);
        audit.setTimeStamp(Instant.now());
        audit.setEndUserUuid(userUuid);
        UUID auditUuid = audit.getAuditUuid();
        entities.add(audit);

        DbItem item = new DbItem();
        item.assignPrimaryUUid();
        item.setAuditUuid(auditUuid);
        item.setTitle("Title");
        item.setDescription("Description");
        item.setXmlContent("XmlContent");
        UUID itemUuid = item.getItemUuid();
        entities.add(item);

        DbActiveItem activeItem = new DbActiveItem();
        activeItem.assignPrimaryUUid();
        activeItem.setAuditUuid(auditUuid);
        activeItem.setItemUuid(itemUuid);
        activeItem.setDeleted(false);
        activeItem.setItemTypeId(DefinitionItemType.ReportFolder);
        activeItem.setOrganisationUuid(orgUuid);
        entities.add(activeItem);

        DbItemDependency itemDependency = new DbItemDependency();
        itemDependency.assignPrimaryUUid();
        itemDependency.setAuditUuid(auditUuid);
        itemDependency.setDependencyTypeId(DependencyType.Uses);
        itemDependency.setDependentItemUuid(itemUuid);
        itemDependency.setItemUuid(itemUuid);
        entities.add(itemDependency);

        DbJob job = new DbJob();
        job.assignPrimaryUUid();
        job.setBaselineAuditUuid(auditUuid);
        job.setStartDateTime(Instant.now());
        job.setStatusId(ExecutionStatus.Executing);
        job.setEndDateTime(null);
        job.setPatientsInDatabase(null);
        UUID jobUuid = job.getJobUuid();
        entities.add(job);

        DbJobContent jobContent = new DbJobContent();
        jobContent.assignPrimaryUUid();
        jobContent.setAuditUuid(auditUuid);
        jobContent.setItemUuid(itemUuid);
        jobContent.setJobUuid(jobUuid);
        entities.add(jobContent);

        DbJobReport jobReport = new DbJobReport();
        jobReport.assignPrimaryUUid();
        jobReport.setAuditUuid(auditUuid);
        jobReport.setJobUuid(jobUuid);
        jobReport.setEndUserUuid(userUuid);
        jobReport.setOrganisationUuid(orgUuid);
        jobReport.setParameters("Parameters");
        jobReport.setReportUuid(itemUuid);
        jobReport.setStatusId(ExecutionStatus.Executing);
        UUID jobReportUuid = jobReport.getJobReportUuid();
        entities.add(jobReport);

        DbJobReportItem jobReportItem = new DbJobReportItem();
        jobReportItem.assignPrimaryUUid();
        jobReportItem.setAuditUuid(auditUuid);
        jobReportItem.setParentJobReportItemUuid(null);
        jobReportItem.setItemUuid(itemUuid);
        jobReportItem.setJobReportUuid(jobReportUuid);
        jobReportItem.setResultCount(null);
        UUID jobReportItemUuid = jobReportItem.getJobReportItemUuid();
        entities.add(jobReportItem);

        DbRequest request = new DbRequest();
        request.assignPrimaryUUid();
        request.setReportUuid(itemUuid);
        request.setEndUserUuid(userUuid);
        request.setJobUuid(null);
        request.setOrganisationUuid(orgUuid);
        request.setParameters("Parameters");
        request.setTimeStamp(Instant.now());
        entities.add(request);

        DbJobProcessorResult result = new DbJobProcessorResult();
        result.setJobUuid(jobUuid);
        result.setProcessorUuid(UUID.randomUUID());
        result.setResultXml("resultXml");
        entities.add(result);

        DbJobReportOrganisation orgResult = new DbJobReportOrganisation();
        orgResult.setJobReportUuid(jobReportUuid);
        orgResult.setOrganisationOdsCode("orgOdsCode");
        orgResult.setPopulationCount(new Integer(100));
        entities.add(orgResult);

        DbJobReportItemOrganisation itemOrgResult = new DbJobReportItemOrganisation();
        itemOrgResult.setJobReportItemUuid(jobReportItemUuid);
        itemOrgResult.setOrganisationOdsCode("orgOdsCode");
        itemOrgResult.setResultCount(new Integer(99));
        entities.add(itemOrgResult);

        //now insert the new entities
        for (DbAbstractTable entity: entities) {
            entity.setSaveMode(TableSaveMode.INSERT);
            LOG.debug("INSERT " + entity.getClass());
            entity.writeToDb();
            LOG.debug("ok");
        }

        //now we've tested inserting, test an update to each item
        for (DbAbstractTable entity: entities) {
            LOG.debug("UPDATE " + entity.getClass());
            entity.setSaveMode(TableSaveMode.UPDATE);
            entity.writeToDb();
            LOG.debug("ok");
        }

        //now we've tested inserting and updating, we should test a delete
        Collections.reverse(entities); //reverse, so the FK dependencies don't cause problems

        for (DbAbstractTable entity: entities) {
            LOG.debug("DELETE " + entity.getClass());
            entity.setSaveMode(TableSaveMode.DELETE);
            entity.writeToDb();
            LOG.debug("ok");
        }


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

