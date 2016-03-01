package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.framework.configuration.Configuration;
import org.endeavour.enterprise.model.DatabaseName;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavour.enterprise.model.DependencyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Drew on 29/02/2016.
 * Database implementation for SQL Server. To support other DB types, create a new sub-class of DatabaseI
 */
public final class SqlServerDatabase implements DatabaseI
{
    private static final Logger LOG = LoggerFactory.getLogger(SqlServerDatabase.class);

    private static final String ALIAS = "z";

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * converts objects to Strings for SQL, escaping as required
     */
    private static String convertToString(Object o)
    {
        if (o == null)
        {
            return "'null'";
        }
        else if (o instanceof String)
        {
            String s = ((String)o).replaceAll("'", "''");
            return "'" + s + "'";
        }
        else if (o instanceof Integer)
        {
            return "" + ((Integer)o).intValue();
        }
        else if (o instanceof UUID)
        {
            return "'" + ((UUID)o).toString() + "'";
        }
        else if (o instanceof Boolean)
        {
            if (((Boolean)o).booleanValue())
            {
                return "1";
            }
            else
            {
                return "0";
            }
        }
        else if (o instanceof Date)
        {
            return "'" + dateFormatter.format((Date)o) + "'";
        }
        else if (o instanceof DependencyType)
        {
            return "" + ((DependencyType)o).getValue();
        }
        else if (o instanceof DefinitionItemType)
        {
            return "" + ((DefinitionItemType)o).getValue();
        }
        else
        {
            LOG.error("Unsupported entity for database", o.getClass());
            return null;
        }
    }

    private int executeScalarCountQuery(String sql, String databaseName) throws Exception
    {
        Connection connection = getConnection(databaseName);
        Statement s = connection.createStatement();
        try
        {
            LOG.trace("Executing {}", sql);
            s.execute(sql);
        }
        catch (SQLException sqlEx)
        {
            LOG.error("Error with SQL " + sql);
            throw sqlEx;
        }

        ResultSet rs = s.getResultSet();
        rs.next();
        return rs.getInt(1);
    }

    private Connection getConnection(String databaseName) throws ClassNotFoundException, SQLException
    {
        // databaseName not used at present

        Class.forName(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName());
        return DriverManager.getConnection(Configuration.DB_CONNECTION_STRING);
    }


    @Override
    public void writeDpdate(DbAbstractTable entity) throws Exception
    {
        TableAdapter a = entity.getAdapter();

        ArrayList<Object> values = new ArrayList<Object>();
        entity.writeForDb(values);

        String[] primaryKeyCols = a.getPrimaryKeyColumns();
        String[] cols = a.getColumns();

        List<String> nonKeyCols = new ArrayList<String>();
        HashMap<String, String> hmColValues = new HashMap<String, String>();

        for (int i=0; i<cols.length; i++)
        {
            String col = cols[i];
            Object value = values.get(i);
            String s = convertToString(value);

            hmColValues.put(col, s);

            //see if a primary key column
            boolean isPrimaryKey = false;
            for (int j=0; j<primaryKeyCols.length; j++)
            {
                String primaryKeyCol = primaryKeyCols[j];
                if (col.equalsIgnoreCase(primaryKeyCol))
                {
                    isPrimaryKey = true;
                    break;
                }
            }
            if (!isPrimaryKey)
            {
                nonKeyCols.add(col);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(a.getSchema());
        sb.append(".");
        sb.append(a.getTableName());
        sb.append(" SET ");

        for (int i=0; i<nonKeyCols.size(); i++)
        {
            String nonKeyCol = nonKeyCols.get(i);
            String val = hmColValues.get(nonKeyCol);

            if (i>0)
            {
                sb.append(", ");
            }

            sb.append(nonKeyCol);
            sb.append(" = ");
            sb.append(val);
        }

        sb.append(" WHERE ");

        for (int i=0; i<primaryKeyCols.length; i++)
        {
            String primaryKeyCol = primaryKeyCols[i];
            String val = hmColValues.get(primaryKeyCol);

            if (i>0)
            {
                sb.append("AND ");
            }

            sb.append(primaryKeyCol);
            sb.append(" = ");
            sb.append(val);
        }

        String sql = sb.toString();

        Connection connection = getConnection(a.getDatabase());
        Statement s = connection.createStatement();

        try
        {
            LOG.trace("Executing {}", sql);
            s.execute(sql);
        }
        catch (SQLException sqlEx)
        {
            LOG.error("Error with SQL " + sql);
            throw sqlEx;
        }
    }

    @Override
    public void writeInsert(DbAbstractTable entity) throws Exception
    {
        TableAdapter a = entity.getAdapter();

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(a.getSchema());
        sb.append(".");
        sb.append(a.getTableName());
        sb.append(" VALUES (");

        ArrayList<Object> values = new ArrayList<Object>();
        entity.writeForDb(values);

        for (int i=0; i<values.size(); i++)
        {
            Object value = values.get(i);

            if (i>0)
            {
                sb.append(", ");
            }

            String s = convertToString(value);
            sb.append(s);
        }

        sb.append(")");

        String sql = sb.toString();

        Connection connection = getConnection(a.getDatabase());
        Statement s = connection.createStatement();

        try
        {
            LOG.trace("Executing {}", sql);
            s.execute(sql);
        }
        catch (SQLException sqlEx)
        {
            LOG.error("Error with SQL " + sql);
            throw sqlEx;
        }
    }

    @Override
    public void writeDelete(DbAbstractTable entity) throws Exception
    {
        TableAdapter a = entity.getAdapter();

        ArrayList<Object> values = new ArrayList<Object>();
        entity.writeForDb(values);

        String[] primaryKeyCols = a.getPrimaryKeyColumns();
        String[] cols = a.getColumns();

        HashMap<String, String> hmColValues = new HashMap<String, String>();

        for (int i=0; i<cols.length; i++)
        {
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

        for (int i=0; i<primaryKeyCols.length; i++)
        {
            String primaryKeyCol = primaryKeyCols[i];
            String val = hmColValues.get(primaryKeyCol);

            if (i>0)
            {
                sb.append("AND ");
            }

            sb.append(primaryKeyCol);
            sb.append(" = ");
            sb.append(val);
        }

        String sql = sb.toString();

        Connection connection = getConnection(a.getDatabase());
        Statement s = connection.createStatement();

        try
        {
            LOG.trace("Executing {}", sql);
            s.execute(sql);
        }
        catch (SQLException sqlEx)
        {
            LOG.error("Error with SQL " + sql);
            throw sqlEx;
        }

    }

    @Override
    public DbAbstractTable retrieveForPrimaryKeys(TableAdapter a, Object... keys) throws Exception
    {
        String[] primaryKeyCols = a.getPrimaryKeyColumns();
        if (primaryKeyCols.length != keys.length)
        {
            throw new RuntimeException("Primary keys length (" + primaryKeyCols.length + ")doesn't match keys length (" + keys.length + ")");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("WHERE ");

        for (int i=0; i<primaryKeyCols.length; i++)
        {
            String primaryKeyCol = primaryKeyCols[i];
            Object o = keys[i];
            String val = convertToString(o);

            if (i>0)
            {
                sb.append(" AND ");
            }

            sb.append(primaryKeyCol);
            sb.append(" = ");
            sb.append(val);
        }

        String whereStatement = sb.toString();
        return retrieveSingleForWhere(a, whereStatement);
    }
    private DbAbstractTable retrieveSingleForWhere(TableAdapter a, String whereStatement) throws Exception
    {
        List<DbAbstractTable> v = new ArrayList<DbAbstractTable>();
        retrieveForWhere(a, whereStatement, v);

        if (v.size() == 0)
        {
            return null;
        }
        else
        {
            return v.get(0);
        }
    }
    private void retrieveForWhere(TableAdapter a, String conditions, List ret) throws Exception
    {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");

        String[] cols = a.getColumns();
        for (int i=0; i<cols.length; i++)
        {
            String col = cols[i];

            if (i>0)
            {
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

        Connection connection = getConnection(a.getDatabase());
        Statement s = connection.createStatement();
        try
        {
            LOG.trace("Executing {}", sql);
            s.execute(sql);

            ResultSet rs = s.getResultSet();

            ResultReader rr = new ResultReader(rs);

            while (rr.nextResult())
            {
                DbAbstractTable entity = a.newEntity();
                entity.readFromDb(rr);
                ret.add(entity);
            }
        }
        catch (SQLException sqlEx)
        {
            LOG.error("Error with SQL " + sql);
            throw sqlEx;
        }
    }

    @Override
    public DbEndUser retrieveEndUserForEmail(String email) throws Exception
    {
        String where = "WHERE Email = " + convertToString(email); //make sure to convert, to prevent SQL injection
        return (DbEndUser)retrieveSingleForWhere(new DbEndUser().getAdapter(), where);
    }

    @Override
    public List<DbEndUser> retrieveSuperUsers() throws Exception
    {
        List<DbEndUser> ret = new ArrayList<DbEndUser>();
        retrieveForWhere(new DbEndUser().getAdapter(), "WHERE IsSuperUser = 1", ret);
        return ret;
    }

    @Override
    public List<DbOrganisation> retrieveAllOrganisations() throws Exception
    {
        List<DbOrganisation> ret = new ArrayList<DbOrganisation>();
        retrieveForWhere(new DbOrganisation().getAdapter(), "WHERE 1=1", ret);
        return ret;
    }

    @Override
    public List<DbEndUserEmailInvite> retrieveEndUserEmailInviteForUserNotCompleted(UUID userUuid) throws Exception
    {
        List<DbEndUserEmailInvite> ret = new ArrayList<DbEndUserEmailInvite>();
        String where = "WHERE UserUuid = " + convertToString(userUuid)
                     + " AND DtCompleted > GETDATE()";
        retrieveForWhere(new DbEndUserEmailInvite().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public DbEndUserEmailInvite retrieveEndUserEmailInviteForToken(String token) throws Exception
    {
        String where = "WHERE Token = " + convertToString(token); //make sure to convert, to prevent SQL injection
        return (DbEndUserEmailInvite)retrieveSingleForWhere(new DbEndUserEmailInvite().getAdapter(), where);
    }

    @Override
    public DbActiveItem retrieveActiveItemForItemUuid(UUID itemUuid) throws Exception
    {
        String where = "WHERE ItemUuid = " + convertToString(itemUuid);
        return (DbActiveItem)retrieveSingleForWhere(new DbActiveItem().getAdapter(), where);
    }

    @Override
    public DbEndUserPwd retrieveEndUserPwdForUserNotExpired(UUID endUserUuid) throws Exception
    {
        String where = "WHERE EndUserUuid = " + convertToString(endUserUuid)
                     + " AND DtExpired > GETDATE()";
        return (DbEndUserPwd)retrieveSingleForWhere(new DbEndUserPwd().getAdapter(), where);
    }

    @Override
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForOrganisationNotExpired(UUID organisationUuid) throws Exception
    {
        List<DbOrganisationEndUserLink> ret = new ArrayList<DbOrganisationEndUserLink>();
        String where = "WHERE OrganisationUuid = " + convertToString(organisationUuid)
                     + " AND DtExpired > GETDATE()";
        retrieveForWhere(new DbOrganisationEndUserLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForUserNotExpired(UUID endUserUuid) throws Exception
    {
        List<DbOrganisationEndUserLink> ret = new ArrayList<DbOrganisationEndUserLink>();
        String where = "WHERE EndUserUuid = " + convertToString(endUserUuid)
                + " AND DtExpired > GETDATE()";
        retrieveForWhere(new DbOrganisationEndUserLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public DbOrganisationEndUserLink retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception
    {
        String where = "WHERE OrganisationUuid = " + convertToString(organisationUuid)
                     + " AND EndUserUuid = " + convertToString(endUserUuid)
                     + " AND DtExpired > GETDATE()";
        return (DbOrganisationEndUserLink)retrieveSingleForWhere(new DbOrganisationEndUserLink().getAdapter(), where);
    }

    @Override
    public DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception
    {
        String where = "WHERE Name = " + convertToString(name)
                + " AND NationalId = " + convertToString(nationalId);
        return (DbOrganisation)retrieveSingleForWhere(new DbOrganisation().getAdapter(), where);
    }

    @Override
    public List<DbItem> retrieveDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception
    {
        List<DbItem> ret = new ArrayList<DbItem>();

        String where = "INNER JOIN Definition.ActiveItemDependency d"
                     + " ON d.ItemUuid = " + convertToString(itemUuid)
                     + " AND d.DependencyType = " + convertToString(dependencyType)
                     + " AND d.DependentItemUuid = " + ALIAS + ".ItemUuid"
                     + " INNER JOIN Definition.ActiveItem a"
                     + " ON a.ItemUuid = d.ItemUuid"
                     + " AND a.Version = " + ALIAS + ".version"
                     + " AND a.OrganisationUuid = " + convertToString(organisationUuid);

        retrieveForWhere(new DbItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception
    {
        List<DbItem> ret = new ArrayList<DbItem>();

        String where = "INNER JOIN Definition.ActiveItem a"
                     + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                     + " AND a.Version = " + ALIAS + ".version"
                     + " AND a.ItemType = " + convertToString(itemType)
                     + " AND a.OrganisationUuid = " + convertToString(organisationUuid)
                     + " WHERE NOT EXISTS ("
                     + "SELECT 1 FROM Definition.ActiveItemDependency d"
                     + " WHERE d.DependentItemUuid = " + ALIAS + ".ItemUuid"
                     + " AND d.DependencyType = " + convertToString(dependencyType)
                     + ")";

        retrieveForWhere(new DbItem().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public int retrieveCountDependencies(UUID itemUuid, DependencyType dependencyType) throws Exception
    {
        String sql = "SELECT COUNT(1)"
                + " FROM Definition.ActiveItemDependency"
                + " WHERE ItemUuid = " + convertToString(itemUuid)
                + " AND DependencyType = " + convertToString(dependencyType);

        return executeScalarCountQuery(sql, DatabaseName.ENDEAVOUR_ENTERPRISE);
    }

    @Override
    public DbItem retrieveForUuidLatestVersion(UUID organisationUuid, UUID itemUuid) throws Exception
    {
        String where = "INNER JOIN Definition.ActiveItem a"
                + " ON a.ItemUuid = " + ALIAS + ".ItemUuid"
                + " AND a.Version = " + ALIAS + ".version"
                + " AND a.OrganisationUuid = " + convertToString(organisationUuid)
                + " WHERE " + ALIAS + ".ItemUuid = " + convertToString(itemUuid);
        return (DbItem)retrieveSingleForWhere(new DbItem().getAdapter(), where);
    }

    @Override
    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForDependentItemType(UUID dependentItemUuid, DependencyType dependencyType) throws Exception
    {
        List<DbActiveItemDependency> ret = new ArrayList<DbActiveItemDependency>();

        String where = "WHERE DependentItemUuid = " + convertToString(dependentItemUuid)
                     + " AND DependencyType = " + convertToString(dependencyType);
        retrieveForWhere(new DbActiveItemDependency().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbActiveItem> retrieveActiveItemDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception
    {
        List<DbActiveItem> ret = new ArrayList<DbActiveItem>();

        String where = "INNER JOIN Definition.ActiveItemDependency d"
                + " ON d.ItemUuid = " + convertToString(itemUuid)
                + " AND d.DependencyType = " + convertToString(dependencyType)
                + " AND d.DependentItemUuid = " + ALIAS + ".ItemUuid"
                + " WHERE " + ALIAS + ".OrganisationUuid = " + convertToString(organisationUuid);

        retrieveForWhere(new DbActiveItem().getAdapter(), where, ret);
        return ret;
    }


}
