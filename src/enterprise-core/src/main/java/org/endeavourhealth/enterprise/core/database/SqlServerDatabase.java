package org.endeavourhealth.enterprise.core.database;

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
import org.endeavourhealth.enterprise.core.database.definition.DbItemDependency;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbJob;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReportItem;
import org.endeavourhealth.enterprise.core.database.execution.DbRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Database implementation for SQL Server. To support other DB types, create a new sub-class of DatabaseI
 */
final class SqlServerDatabase implements DatabaseI {
    private static final Logger LOG = LoggerFactory.getLogger(SqlServerDatabase.class);
    private static final String ALIAS = "z";

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
        } else if (o instanceof Instant) {
            Timestamp ts = Timestamp.from((Instant)o);
            return "'" + ts.toString() + "'";
//            String s = ((Instant)o).toString();
//            String s2 = formatter.format((Instant)o);
//            return formatter.format((Instant)o);
            //return DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format((Instant)o);
            //return DateTimeFormatter.ISO_DATE_TIME.format((Instant)o);
        } else if (o instanceof DependencyType) {
            return "" + ((DependencyType) o).getValue();
        } else if (o instanceof DefinitionItemType) {
            return "" + ((DefinitionItemType) o).getValue();
        } else if (o instanceof ExecutionStatus) {
            return "" + ((ExecutionStatus) o).getValue();
        } else if (o instanceof List) {
            List uuids = (List)o;
            List<String> uuidStrs = new ArrayList<>();
            for (Object uuid: uuids) {
                uuidStrs.add(convertToString(uuid));
            }
            return String.join(", ", uuidStrs);
        } else {
            throw new RuntimeException("Unsupported entity for database " + o.getClass());
        }
    }

    private int executeScalarCountQuery(String sql) throws Exception {
        Connection connection = DatabaseManager.getConnection();
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
            DatabaseManager.closeConnection(connection);
        }
    }

    @Override
    public SQLDialectCode getLogbackDbDialectCode() {
        return SQLDialectCode.MSSQL_DIALECT;
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

        Connection connection = DatabaseManager.getConnection();
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
            DatabaseManager.closeConnection(connection);
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
        a.appendSchemaAndTableName(sb);
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
        a.appendSchemaAndTableName(sb);
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
        a.appendSchemaAndTableName(sb);
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

        /*Field[] flds2 = a.getCls().getDeclaredFields();
        for (Field fld: flds2) {
            if (fld.isAnnotationPresent(DatabaseColumn.class)) {
                System.out.println("fld "  + fld.getName() + " has annotation");
            }
            if (fld.isAnnotationPresent(PrimaryKeyColumn.class)) {
                System.out.println("fld "  + fld.getName() + " has is primary key");
            }
        }*/


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
        a.appendSchemaAndTableName(sb);
        sb.append(" ");
        sb.append(ALIAS);
        sb.append(" ");
        sb.append(conditions);

        String sql = sb.toString();

        Connection connection = DatabaseManager.getConnection();
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
            DatabaseManager.closeConnection(connection);
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
                + " AND DtCompleted IS NULL";
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
                + " AND DtExpired IS NULL";
        return (DbEndUserPwd) retrieveSingleForWhere(new DbEndUserPwd().getAdapter(), where);
    }

    @Override
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForOrganisationNotExpired(UUID organisationUuid) throws Exception {
        List<DbOrganisationEndUserLink> ret = new ArrayList<DbOrganisationEndUserLink>();
        String where = "WHERE OrganisationUuid = " + convertToString(organisationUuid)
                + " AND DtExpired IS NULL";
        retrieveForWhere(new DbOrganisationEndUserLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForUserNotExpired(UUID endUserUuid) throws Exception {
        List<DbOrganisationEndUserLink> ret = new ArrayList<DbOrganisationEndUserLink>();
        String where = "WHERE EndUserUuid = " + convertToString(endUserUuid)
                + " AND DtExpired IS NULL";
        retrieveForWhere(new DbOrganisationEndUserLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public DbOrganisationEndUserLink retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception {
        String where = "WHERE OrganisationUuid = " + convertToString(organisationUuid)
                + " AND EndUserUuid = " + convertToString(endUserUuid)
                + " AND DtExpired IS NULL";
        return (DbOrganisationEndUserLink) retrieveSingleForWhere(new DbOrganisationEndUserLink().getAdapter(), where);
    }

    @Override
    public DbItem retrieveItemForUuid(UUID itemUuid) throws Exception {
        String where = "INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.AuditUuid = " + ALIAS + ".AuditUuid"
                + " WHERE a.ItemUuid = " + convertToString(itemUuid);
        return (DbItem) retrieveSingleForWhere(new DbItem().getAdapter(), where);
    }

    @Override
    public DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception {
        String where = "WHERE Name = " + convertToString(name)
                + " AND NationalId = " + convertToString(nationalId);
        return (DbOrganisation) retrieveSingleForWhere(new DbOrganisation().getAdapter(), where);
    }

    @Override
    public List<DbItem> retrieveDependentItems(UUID itemUuid, UUID auditUuid, DependencyType dependencyType) throws Exception {
        List<DbItem> ret = new ArrayList<>();

        String where = "INNER JOIN Definition.ItemDependency d"
                + " ON d.ItemUuid = " + convertToString(itemUuid)
                + " AND d.AuditUuid = " + convertToString(auditUuid)
                + " AND d.DependencyTypeId = " + convertToString(dependencyType)
                + "INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.AuditUuid = " + ALIAS + ".AuditUuid"
                + " AND a.ItemUuid = d.DependentItemUuid"
                + " AND a.IsDeleted = 0";

        retrieveForWhere(new DbItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception {
        List<DbItem> ret = new ArrayList<DbItem>();

        String where = "INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.AuditUuid = " + ALIAS + ".AuditUuid"
                + " AND a.ItemTypeId = " + convertToString(itemType)
                + " AND a.OrganisationUuid = " + convertToString(organisationUuid)
                + " AND a.IsDeleted = 0"
                + " WHERE NOT EXISTS ("
                + "SELECT 1 FROM Definition.ItemDependency d, Definition.ActiveItem da"
                + " WHERE d.DependentItemUuid = " + ALIAS + ".ItemUuid"
                + " AND d.DependencyTypeId = " + convertToString(dependencyType)
                + " AND da.IsDeleted = 0"
                + " AND da.ItemUuid = d.ItemUuid"
                + " AND da.AuditUuid = d.AuditUuid"
                + ")";

        retrieveForWhere(new DbItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbItem> retrieveItemsForActiveItems(List<DbActiveItem> activeItems) throws Exception {
        List<DbItem> ret = new ArrayList<DbItem>();
        if (activeItems.isEmpty()) {
            return ret;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("WHERE ");

        for (int i=0; i<activeItems.size(); i++) {
            DbActiveItem activeItem = activeItems.get(i);
            UUID itemUuid = activeItem.getItemUuid();
            UUID auditUuid = activeItem.getAuditUuid();

            if (i > 0){
                sb.append(" OR ");
            }
            sb.append("(");
            sb.append("ItemUuid = ");
            sb.append(convertToString(itemUuid));
            sb.append(" AND AuditUuid = ");
            sb.append(convertToString(auditUuid));
            sb.append(")");
        }
        String where = sb.toString();

        retrieveForWhere(new DbItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public int retrieveCountDependencies(UUID itemUuid, DependencyType dependencyType) throws Exception {
        String sql = "SELECT COUNT(1)"
                + " FROM Definition.ItemDependency"
                + " WHERE ItemUuid = " + convertToString(itemUuid)
                + " AND DependencyTypeId = " + convertToString(dependencyType);

        return executeScalarCountQuery(sql);
    }

    @Override
    public List<DbItemDependency> retrieveItemDependenciesForItem(UUID itemUuid, UUID auditUuid) throws Exception {
        List<DbItemDependency> ret = new ArrayList<DbItemDependency>();

        String where = "WHERE ItemUuid = " + convertToString(itemUuid)
                + " AND AuditUuid = " + convertToString(auditUuid);
        retrieveForWhere(new DbItemDependency().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbItemDependency> retrieveItemDependenciesForItemType(UUID itemUuid, UUID auditUuid, DependencyType dependencyType) throws Exception {
        List<DbItemDependency> ret = new ArrayList<DbItemDependency>();

        String where = "WHERE ItemUuid = " + convertToString(itemUuid)
                + " AND AuditUuid = " + convertToString(auditUuid)
                + " AND DependencyTypeId = " + convertToString(dependencyType);
        retrieveForWhere(new DbItemDependency().getAdapter(), where, ret);
        return ret;
    }    

    @Override
    public List<DbItemDependency> retrieveItemDependenciesForDependentItem(UUID dependentItemUuid) throws Exception {
        List<DbItemDependency> ret = new ArrayList<DbItemDependency>();

        String where = "INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.AuditUuid = " + ALIAS + ".AuditUuid"
                + " AND a.IsDeleted = 0"
                + " WHERE DependentItemUuid = " + convertToString(dependentItemUuid);
        retrieveForWhere(new DbItemDependency().getAdapter(), where, ret);
        return ret;
    }


    @Override
    public List<DbItemDependency> retrieveItemDependenciesForDependentItemType(UUID dependentItemUuid, DependencyType dependencyType) throws Exception {
        List<DbItemDependency> ret = new ArrayList<>();

        String where = "INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.AuditUuid = " + ALIAS + ".AuditUuid"
                + " AND a.IsDeleted = 0"
                + " WHERE DependentItemUuid = " + convertToString(dependentItemUuid)
                + " AND DependencyTypeId = " + convertToString(dependencyType);
        retrieveForWhere(new DbItemDependency().getAdapter(), where, ret);
        return ret;
    }




    @Override
    public List<DbRequest> retrievePendingRequestsForItems(UUID organisationUuid, List<UUID> itemUuids) throws Exception {
        List<DbRequest> ret = new ArrayList<>();
        if (itemUuids.isEmpty()) {
            return ret;
        }

        String where = "WHERE JobUuid IS NULL"
                + " AND OrganisationUuid = " + convertToString(organisationUuid)
                + " AND ReportUuid IN (" + convertToString(itemUuids) + ")";
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
    public List<DbJob> retrieveJobsForUuids(List<UUID> uuids) throws Exception {
        List<DbJob> ret = new ArrayList<>();
        if (uuids.isEmpty()) {
            return ret;
        }

        String where = "WHERE JobUuid IN (" + convertToString(uuids) + ")";
        retrieveForWhere(new DbJob().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbJobReport> retrieveJobReports(UUID organisationUuid, int count) throws Exception {
        List<DbJobReport> ret = new ArrayList<>();

        String where = "INNER JOIN Execution.Job a"
                + " ON a.JobUuid = " + ALIAS + ".JobUuid"
                + " ORDER BY a.StartDateTime DESC";
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

        String where = "INNER JOIN Execution.Job j"
                + " ON j.JobUuid = " + ALIAS + ".JobUuid"
                + " WHERE OrganisationUuid = " + convertToString(organisationUuid)
                + " AND ReportUuid IN (" + convertToString(itemUuids) + ")"
                + " AND NOT EXISTS (SELECT 1 FROM Execution.JobReport laterJobReport, Execution.Job laterJob"
                + " WHERE laterJobReport.JobReportUuid = " + ALIAS + ".JobReportUuid"
                + " AND laterJob.JobUuid = laterJobReport.JobUuid"
                + " AND laterJob.StartDateTime > j.StartDateTime)";
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
    public List<DbAudit> retrieveAuditsForUuids(List<UUID> uuids) throws Exception {
        List<DbAudit> ret = new ArrayList<>();
        if (uuids.isEmpty()) {
            return ret;
        }

        String where = "WHERE AuditUuid IN (" + convertToString(uuids) + ")";
        retrieveForWhere(new DbAudit().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbActiveItem> retrieveActiveItemDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception {
        List<DbActiveItem> ret = new ArrayList<>();

        String where = "INNER JOIN Definition.ItemDependency d"
                + " ON d.ItemUuid = " + convertToString(itemUuid)
                + " AND d.DependencyTypeId = " + convertToString(dependencyType)
                + " AND d.DependentItemUuid = " + ALIAS + ".ItemUuid"
                + " INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.AuditUuid = d.AuditUuid"
                + " WHERE " + ALIAS + ".OrganisationUuid = " + convertToString(organisationUuid);

        retrieveForWhere(new DbActiveItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbActiveItem> retrieveActiveItemRecentItems(UUID userUuid, int count) throws Exception {
        List<DbActiveItem> ret = new ArrayList<>();

        String where = "INNER JOIN Definition.Item i"
                + " ON i.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND i.AuditUuid = " + ALIAS + ".AuditUuid"
                + " AND " + ALIAS + ".IsDeleted = 0"
                + " INNER JOIN Definition.Audit a"
                + " ON a.AuditUuid = i.AuditUuid"
                + " AND a.EndUserUuid = " + convertToString(userUuid)
                + " WHERE " + ALIAS + ".ItemTypeId NOT IN (" + DefinitionItemType.LibraryFolder.getValue() + ", " + DefinitionItemType.ReportFolder.getValue() + ")"
                + " ORDER BY a.TimeStamp DESC";

        retrieveForWhere(new DbActiveItem().getAdapter(), count, where, ret);
        return ret;
    }

}