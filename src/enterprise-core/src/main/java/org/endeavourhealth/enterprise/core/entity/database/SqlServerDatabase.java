package org.endeavourhealth.enterprise.core.entity.database;

import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
import ch.qos.logback.core.db.ConnectionSource;
import ch.qos.logback.core.db.DataSourceConnectionSource;
import ch.qos.logback.core.db.DriverManagerConnectionSource;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.endeavourhealth.enterprise.core.entity.DefinitionItemType;
import org.endeavourhealth.enterprise.core.entity.DependencyType;
import org.endeavourhealth.enterprise.core.entity.ExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Drew on 29/02/2016.
 * Database implementation for SQL Server. To support other DB types, create a new sub-class of DatabaseI
 */
final class SqlServerDatabase implements DatabaseI {
    private static final Logger LOG = LoggerFactory.getLogger(SqlServerDatabase.class);
    private static final String ALIAS = "z";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String LOGGING_SCHEMA_PREFIX = "Logging.";

    private ComboPooledDataSource cpds = null;

    public SqlServerDatabase() {

        try {

            //need to force the loading of the Driver class before we try to create any connections
            Class.forName(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName());

            cpds = new ComboPooledDataSource();
            cpds.setDriverClass("net.sourceforge.jtds.jdbc.Driver");
            cpds.setJdbcUrl(SqlServerConfig.DB_URL);
            cpds.setUser(SqlServerConfig.DB_USER);
            cpds.setPassword(SqlServerConfig.DB_PASSWORD);

            //arbitrary pool settings
            cpds.setMinPoolSize(5);
            cpds.setAcquireIncrement(5);
            cpds.setMaxPoolSize(20);
            cpds.setMaxStatements(180);

        } catch (ClassNotFoundException | PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    /**
     * converts objects to Strings for SQL, escaping as required
     */
    private static String convertToString(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof String) {
            String s = ((String) o).replaceAll("'", "''");
            return "'" + s + "'";
        } else if (o instanceof Integer) {
            return "" + ((Integer) o).intValue();
        } else if (o instanceof UUID) {
            return "'" + ((UUID) o).toString() + "'";
        } else if (o instanceof Boolean) {
            if (((Boolean) o).booleanValue()) {
                return "1";
            } else {
                return "0";
            }
        } else if (o instanceof Date) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
            return "'" + dateFormatter.format((Date) o) + "'";
        } else if (o instanceof DependencyType) {
            return "" + ((DependencyType) o).getValue();
        } else if (o instanceof DefinitionItemType) {
            return "" + ((DefinitionItemType) o).getValue();
        } else if (o instanceof ExecutionStatus) {
            return "" + ((ExecutionStatus) o).getValue();
        } else {
            LOG.error("Unsupported entity for database", o.getClass());
            return null;
        }
    }

    private int executeScalarCountQuery(String sql) throws Exception {
        Connection connection = getConnection();
        Statement s = connection.createStatement();
        try {
            LOG.trace("Executing {}", sql);
            s.execute(sql);

            ResultSet rs = s.getResultSet();
            rs.next();
            int ret = rs.getInt(1);

            rs.close();

            return ret;
        } catch (SQLException sqlEx) {
            LOG.error("Error with SQL {}", sql);
            throw sqlEx;
        } finally {
            closeConnection(connection);
        }
    }


    private synchronized Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = cpds.getConnection();
        conn.setAutoCommit(false); //never want auto-commit
        return conn;
    }

    private synchronized void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    @Override
    public void registerLogbackDbAppender() {

        //we need our own implementation of a conneciton source, because logback fails to detect the DB type when against Azure
        LogbackConnectionSource connectionSource = new LogbackConnectionSource();

        //because the three logging tables are in a schema, we need to override the resolver to insert the schema name
        DefaultDBNameResolver r = new DefaultDBNameResolver(){
            @Override
            public <N extends Enum<?>> String getTableName(N tableName) {
                return LOGGING_SCHEMA_PREFIX + super.getTableName(tableName);
            }
        };

        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        DBAppender dbAppender = new DBAppender();
        dbAppender.setContext(rootLogger.getLoggerContext());
        dbAppender.setConnectionSource(connectionSource);
        dbAppender.setName("DB Appender");
        dbAppender.setDbNameResolver(r);
        dbAppender.start();

        rootLogger.addAppender(dbAppender);
    }


    @Override
    public void writeEntity(DbAbstractTable entity) throws Exception {
        List<DbAbstractTable> v = new ArrayList<>();
        v.add(entity);
        writeEntities(v);
    }

    @Override
    public void writeEntities(List<DbAbstractTable> entities) throws Exception {
        if (entities.isEmpty()) {
            return;
        }

        LOG.trace("Writing {} entities to DB", entities.size());

        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        StringJoiner sqlLogging = new StringJoiner("\r\n");

        for (DbAbstractTable entity : entities) {
            String sql = writeSql(entity);
            sqlLogging.add(sql);
            statement.addBatch(sql);
        }

        LOG.trace("Executing {}", sqlLogging.toString());

        try {
            statement.executeBatch();
            connection.commit();

        } catch (SQLException sqlEx) {

            LOG.error("Error in SQL {}", sqlLogging.toString());
            connection.rollback();
            //don't return the connection, since the problem maybe at the connection level
            throw sqlEx;
        } finally {
            closeConnection(connection);
        }

    }

    private static String writeSql(DbAbstractTable entity) throws Exception {
        switch (entity.getSaveMode()) {
            case INSERT:
                return writeInsertSql(entity);
            case UPDATE:
                return writeUpdateSql(entity);
            case DELETE:
                return writeDeleteSql(entity);
            default:
                throw new RuntimeException("Invalid save mode " + entity.getSaveMode());
        }
    }

    private static String writeInsertSql(DbAbstractTable entity) {
        TableAdapter a = entity.getAdapter();

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(a.getSchema());
        sb.append(".");
        sb.append(a.getTableName());
        sb.append(" VALUES (");

        ArrayList<Object> values = new ArrayList<Object>();
        entity.writeForDb(values);

        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);

            if (i > 0) {
                sb.append(", ");
            }

            String s = convertToString(value);
            sb.append(s);
        }

        sb.append(")");

        return sb.toString();
    }

    private static String writeUpdateSql(DbAbstractTable entity) {
        TableAdapter a = entity.getAdapter();

        ArrayList<Object> values = new ArrayList<Object>();
        entity.writeForDb(values);

        String[] primaryKeyCols = a.getPrimaryKeyColumns();
        String[] cols = a.getColumns();

        List<String> nonKeyCols = new ArrayList<String>();
        HashMap<String, String> hmColValues = new HashMap<String, String>();

        for (int i = 0; i < cols.length; i++) {
            String col = cols[i];
            Object value = values.get(i);
            String s = convertToString(value);

            hmColValues.put(col, s);

            //see if a primary key column
            boolean isPrimaryKey = false;
            for (int j = 0; j < primaryKeyCols.length; j++) {
                String primaryKeyCol = primaryKeyCols[j];
                if (col.equalsIgnoreCase(primaryKeyCol)) {
                    isPrimaryKey = true;
                    break;
                }
            }
            if (!isPrimaryKey) {
                nonKeyCols.add(col);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(a.getSchema());
        sb.append(".");
        sb.append(a.getTableName());
        sb.append(" SET ");

        for (int i = 0; i < nonKeyCols.size(); i++) {
            String nonKeyCol = nonKeyCols.get(i);
            String val = hmColValues.get(nonKeyCol);

            if (i > 0) {
                sb.append(", ");
            }

            sb.append(nonKeyCol);
            sb.append(" = ");
            sb.append(val);
        }

        sb.append(" WHERE ");

        for (int i = 0; i < primaryKeyCols.length; i++) {
            String primaryKeyCol = primaryKeyCols[i];
            String val = hmColValues.get(primaryKeyCol);

            if (i > 0) {
                sb.append("AND ");
            }

            sb.append(primaryKeyCol);
            sb.append(" = ");
            sb.append(val);
        }

        return sb.toString();
    }

    private static String writeDeleteSql(DbAbstractTable entity) {
        TableAdapter a = entity.getAdapter();

        ArrayList<Object> values = new ArrayList<Object>();
        entity.writeForDb(values);

        String[] primaryKeyCols = a.getPrimaryKeyColumns();
        String[] cols = a.getColumns();

        HashMap<String, String> hmColValues = new HashMap<String, String>();

        for (int i = 0; i < cols.length; i++) {
            String col = cols[i];
            Object value = values.get(i);
            String s = convertToString(value);

            hmColValues.put(col, s);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(a.getSchema());
        sb.append(".");
        sb.append(a.getTableName());
        sb.append(" WHERE ");

        for (int i = 0; i < primaryKeyCols.length; i++) {
            String primaryKeyCol = primaryKeyCols[i];
            String val = hmColValues.get(primaryKeyCol);

            if (i > 0) {
                sb.append("AND ");
            }

            sb.append(primaryKeyCol);
            sb.append(" = ");
            sb.append(val);
        }

        return sb.toString();
    }


    @Override
    public DbAbstractTable retrieveForPrimaryKeys(TableAdapter a, Object... keys) throws Exception {
        String[] primaryKeyCols = a.getPrimaryKeyColumns();
        if (primaryKeyCols.length != keys.length) {
            throw new RuntimeException("Primary keys length (" + primaryKeyCols.length + ")doesn't match keys length (" + keys.length + ")");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("WHERE ");

        for (int i = 0; i < primaryKeyCols.length; i++) {
            String primaryKeyCol = primaryKeyCols[i];
            Object o = keys[i];
            String val = convertToString(o);

            if (i > 0) {
                sb.append(" AND ");
            }

            sb.append(primaryKeyCol);
            sb.append(" = ");
            sb.append(val);
        }

        String whereStatement = sb.toString();
        return retrieveSingleForWhere(a, whereStatement);
    }

    private DbAbstractTable retrieveSingleForWhere(TableAdapter a, String whereStatement) throws Exception {
        List<DbAbstractTable> v = new ArrayList<DbAbstractTable>();
        retrieveForWhere(a, whereStatement, v);

        if (v.size() == 0) {
            return null;
        } else {
            return v.get(0);
        }
    }

    private void retrieveForWhere(TableAdapter a, String conditions, List ret) throws Exception {
        retrieveForWhere(a, Integer.MAX_VALUE, conditions, ret);
    }

    private void retrieveForWhere(TableAdapter a, int count, String conditions, List ret) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");

        if (count < Integer.MAX_VALUE) {
            sb.append("TOP ");
            sb.append(count);
            sb.append(" ");
        }

        String[] cols = a.getColumns();
        for (int i = 0; i < cols.length; i++) {
            String col = cols[i];

            if (i > 0) {
                sb.append(", ");
            }

            sb.append(ALIAS);
            sb.append(".");
            sb.append(col);
        }

        sb.append(" FROM ");
        sb.append(a.getSchema());
        sb.append(".");
        sb.append(a.getTableName());
        sb.append(" ");
        sb.append(ALIAS);
        sb.append(" ");
        sb.append(conditions);

        String sql = sb.toString();

        Connection connection = getConnection();
        Statement s = connection.createStatement();
        try {
            LOG.trace("Executing {}", sql);
            s.execute(sql);

            ResultSet rs = s.getResultSet();

            ResultReader rr = new ResultReader(rs);

            while (rr.nextResult()) {
                DbAbstractTable entity = a.newEntity();
                entity.readFromDb(rr);
                ret.add(entity);
            }

            rs.close();

        } catch (SQLException sqlEx) {
            LOG.error("Error with SQL {}", sql);
            throw sqlEx;
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public DbEndUser retrieveEndUserForEmail(String email) throws Exception {
        String where = "WHERE Email = " + convertToString(email); //make sure to convert, to prevent SQL injection
        return (DbEndUser) retrieveSingleForWhere(new DbEndUser().getAdapter(), where);
    }

    @Override
    public List<DbEndUser> retrieveSuperUsers() throws Exception {
        List<DbEndUser> ret = new ArrayList<DbEndUser>();
        retrieveForWhere(new DbEndUser().getAdapter(), "WHERE IsSuperUser = 1", ret);
        return ret;
    }

    @Override
    public List<DbOrganisation> retrieveAllOrganisations() throws Exception {
        List<DbOrganisation> ret = new ArrayList<DbOrganisation>();
        retrieveForWhere(new DbOrganisation().getAdapter(), "WHERE 1=1", ret);
        return ret;
    }

    @Override
    public List<DbEndUserEmailInvite> retrieveEndUserEmailInviteForUserNotCompleted(UUID userUuid) throws Exception {
        List<DbEndUserEmailInvite> ret = new ArrayList<DbEndUserEmailInvite>();
        String where = "WHERE UserUuid = " + convertToString(userUuid)
                + " AND DtCompleted > GETDATE()";
        retrieveForWhere(new DbEndUserEmailInvite().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public DbEndUserEmailInvite retrieveEndUserEmailInviteForToken(String token) throws Exception {
        String where = "WHERE Token = " + convertToString(token); //make sure to convert, to prevent SQL injection
        return (DbEndUserEmailInvite) retrieveSingleForWhere(new DbEndUserEmailInvite().getAdapter(), where);
    }

    @Override
    public DbActiveItem retrieveActiveItemForItemUuid(UUID itemUuid) throws Exception {
        String where = "WHERE ItemUuid = " + convertToString(itemUuid);
        return (DbActiveItem) retrieveSingleForWhere(new DbActiveItem().getAdapter(), where);
    }

    @Override
    public DbEndUserPwd retrieveEndUserPwdForUserNotExpired(UUID endUserUuid) throws Exception {
        String where = "WHERE EndUserUuid = " + convertToString(endUserUuid)
                + " AND DtExpired > GETDATE()";
        return (DbEndUserPwd) retrieveSingleForWhere(new DbEndUserPwd().getAdapter(), where);
    }

    @Override
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForOrganisationNotExpired(UUID organisationUuid) throws Exception {
        List<DbOrganisationEndUserLink> ret = new ArrayList<DbOrganisationEndUserLink>();
        String where = "WHERE OrganisationUuid = " + convertToString(organisationUuid)
                + " AND DtExpired > GETDATE()";
        retrieveForWhere(new DbOrganisationEndUserLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForUserNotExpired(UUID endUserUuid) throws Exception {
        List<DbOrganisationEndUserLink> ret = new ArrayList<DbOrganisationEndUserLink>();
        String where = "WHERE EndUserUuid = " + convertToString(endUserUuid)
                + " AND DtExpired > GETDATE()";
        retrieveForWhere(new DbOrganisationEndUserLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public DbOrganisationEndUserLink retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception {
        String where = "WHERE OrganisationUuid = " + convertToString(organisationUuid)
                + " AND EndUserUuid = " + convertToString(endUserUuid)
                + " AND DtExpired > GETDATE()";
        return (DbOrganisationEndUserLink) retrieveSingleForWhere(new DbOrganisationEndUserLink().getAdapter(), where);
    }

    @Override
    public DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception {
        String where = "WHERE Name = " + convertToString(name)
                + " AND NationalId = " + convertToString(nationalId);
        return (DbOrganisation) retrieveSingleForWhere(new DbOrganisation().getAdapter(), where);
    }

    @Override
    public List<DbItem> retrieveDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception {
        List<DbItem> ret = new ArrayList<DbItem>();

        String where = "INNER JOIN Definition.ActiveItemDependency d"
                + " ON d.ItemUuid = " + convertToString(itemUuid)
                + " AND d.DependencyTypeId = " + convertToString(dependencyType)
                + " AND d.DependentItemUuid = " + ALIAS + ".ItemUuid"
                + " INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = d.DependentItemUuid"
                + " AND a.Version = " + ALIAS + ".version"
                + " AND a.OrganisationUuid = " + convertToString(organisationUuid);

        retrieveForWhere(new DbItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception {
        List<DbItem> ret = new ArrayList<DbItem>();

        String where = "INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.Version = " + ALIAS + ".version"
                + " AND a.ItemTypeId = " + convertToString(itemType)
                + " AND a.OrganisationUuid = " + convertToString(organisationUuid)
                + " WHERE NOT EXISTS ("
                + "SELECT 1 FROM Definition.ActiveItemDependency d"
                + " WHERE d.DependentItemUuid = " + ALIAS + ".ItemUuid"
                + " AND d.DependencyTypeId = " + convertToString(dependencyType)
                + ")";

        retrieveForWhere(new DbItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public int retrieveCountDependencies(UUID itemUuid, DependencyType dependencyType) throws Exception {
        String sql = "SELECT COUNT(1)"
                + " FROM Definition.ActiveItemDependency"
                + " WHERE ItemUuid = " + convertToString(itemUuid)
                + " AND DependencyTypeId = " + convertToString(dependencyType);

        return executeScalarCountQuery(sql);
    }

    @Override
    public DbItem retrieveForUuidLatestVersion(UUID organisationUuid, UUID itemUuid) throws Exception {
        String where = "INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.Version = " + ALIAS + ".version"
                + " AND a.OrganisationUuid = " + convertToString(organisationUuid)
                + " WHERE " + ALIAS + ".ItemUuid = " + convertToString(itemUuid);
        return (DbItem) retrieveSingleForWhere(new DbItem().getAdapter(), where);
    }

    @Override
    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForDependentItemType(UUID dependentItemUuid, DependencyType dependencyType) throws Exception {
        List<DbActiveItemDependency> ret = new ArrayList<>();

        String where = "WHERE DependentItemUuid = " + convertToString(dependentItemUuid)
                + " AND DependencyTypeId = " + convertToString(dependencyType);
        retrieveForWhere(new DbActiveItemDependency().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbRequest> retrievePendingRequestsForItems(UUID organisationUuid, List<UUID> itemUuids) throws Exception {
        List<DbRequest> ret = new ArrayList<>();
        if (itemUuids.isEmpty()) {
            return ret;
        }

        List<String> uuidStrs = new ArrayList<>();
        for (UUID uuid: itemUuids) {
            uuidStrs.add(convertToString(uuid));
        }

        String where = "WHERE JobUuid IS NULL"
                + " AND OrganisationUuid = " + convertToString(organisationUuid)
                + " AND ReportUuid IN (" + String.join(", ", uuidStrs) + ")";
        retrieveForWhere(new DbRequest().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbRequest> retrievePendingRequests() throws Exception {
        List<DbRequest> ret = new ArrayList<>();

        String where = "WHERE JobUuid IS NULL";
        retrieveForWhere(new DbRequest().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbJob> retrieveRecentJobs(int count) throws Exception {
        List<DbJob> ret = new ArrayList<>();

        String where = "ORDER BY StartDateTime DESC";
        retrieveForWhere(new DbJob().getAdapter(), count, where, ret);
        return ret;
    }

    @Override
    public List<DbJob> retrieveJobsForStatus(ExecutionStatus status) throws Exception {
        List<DbJob> ret = new ArrayList<>();

        String where = "WHERE StatusId = " + convertToString(status);
        retrieveForWhere(new DbJob().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbJobReport> retrieveJobReports(UUID organisationUuid, int count) throws Exception {
        List<DbJobReport> ret = new ArrayList<>();

        String where = "WHERE OrganisationUuid = " + convertToString(organisationUuid)
                + " ORDER BY TimeStamp DESC";
        retrieveForWhere(new DbJobReport().getAdapter(), count, where, ret);
        return ret;
    }

    @Override
    public List<DbJobReport> retrieveJobReportsForJob(UUID jobUuid) throws Exception {
        List<DbJobReport> ret = new ArrayList<>();

        String where = "WHERE JobUuid = " + convertToString(jobUuid);
        retrieveForWhere(new DbJobReport().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbJobReport> retrieveLatestJobReportsForItemUuids(UUID organisationUuid, List<UUID> itemUuids) throws Exception {

        List<DbJobReport> ret = new ArrayList<>();
        if (itemUuids.isEmpty()) {
            return ret;
        }

        List<String> uuidStrs = new ArrayList<>();
        for (UUID uuid: itemUuids) {
            uuidStrs.add(convertToString(uuid));
        }

        String where = "WHERE OrganisationUuid = " + convertToString(organisationUuid)
                + " AND ReportUuid IN (" + String.join(", ", uuidStrs) + ")"
                + " AND NOT EXISTS (SELECT 1 FROM Execution.JobReport later"
                + " WHERE later.JobReportUuid = " + ALIAS + ".JobReportUuid"
                + " AND later.TimeStamp > " + ALIAS + ".TimeStamp)";
        retrieveForWhere(new DbJobReport().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbJobReportItem> retrieveJobReportItemsForJobReport(UUID jobReportUuid) throws Exception {
        List<DbJobReportItem> ret = new ArrayList<>();

        String where = "WHERE JobReportUuid = " + convertToString(jobReportUuid);
        retrieveForWhere(new DbJobReportItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbActiveItem> retrieveActiveItemDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception {
        List<DbActiveItem> ret = new ArrayList<>();

        String where = "INNER JOIN Definition.ActiveItemDependency d"
                + " ON d.ItemUuid = " + convertToString(itemUuid)
                + " AND d.DependencyTypeId = " + convertToString(dependencyType)
                + " AND d.DependentItemUuid = " + ALIAS + ".ItemUuid"
                + " WHERE " + ALIAS + ".OrganisationUuid = " + convertToString(organisationUuid);

        retrieveForWhere(new DbActiveItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbActiveItem> retrieveActiveItemRecentItems(UUID userUuid, int count) throws Exception {
        List<DbActiveItem> ret = new ArrayList<>();

        String where = "INNER JOIN Definition.Item i"
                + " ON i.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND i.Version = " + ALIAS + ".Version"
                + " AND i.EndUserUuid = " + convertToString(userUuid)
                + " ORDER BY i.TimeStamp DESC";

        retrieveForWhere(new DbActiveItem().getAdapter(), count, where, ret);
        return ret;
    }

    @Override
    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForItem(UUID itemUuid) throws Exception {
        List<DbActiveItemDependency> ret = new ArrayList<DbActiveItemDependency>();

        String where = "WHERE ItemUuid = " + convertToString(itemUuid);
        retrieveForWhere(new DbActiveItemDependency().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForItemType(UUID itemUuid, DependencyType dependencyType) throws Exception {
        List<DbActiveItemDependency> ret = new ArrayList<DbActiveItemDependency>();

        String where = "WHERE ItemUuid = " + convertToString(itemUuid)
                + " AND DependencyTypeId = " + convertToString(dependencyType);
        retrieveForWhere(new DbActiveItemDependency().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForDependentItem(UUID dependentItemUuid) throws Exception {
        List<DbActiveItemDependency> ret = new ArrayList<DbActiveItemDependency>();

        String where = "WHERE DependentItemUuid = " + convertToString(dependentItemUuid);
        retrieveForWhere(new DbActiveItemDependency().getAdapter(), where, ret);
        return ret;
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
            return SQLDialectCode.MSSQL_DIALECT;
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