package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.framework.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Drew on 29/02/2016.
 */
public final class SqlServerDatabase implements DatabaseI
{
    private static final Logger LOG = LoggerFactory.getLogger(SqlServerDatabase.class);

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
        else
        {
            LOG.error("Unsupported entity for database", o.getClass());
            return null;
        }
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
    private void retrieveForWhere(TableAdapter a, String whereStatement, List ret) throws Exception
    {
        StringBuilder sb = new StringBuilder();

        String[] cols = a.getColumns();
        String colStr = String.join(", ", cols);

        sb.append("SELECT ");
        sb.append(colStr);
        sb.append(" FROM ");
        sb.append(a.getSchema());
        sb.append(".");
        sb.append(a.getTableName());
        sb.append(" WHERE ");
        sb.append(whereStatement);

        String sql = sb.toString();

        Connection connection = getConnection(a.getDatabase());
        Statement s = connection.createStatement();
        try
        {
            s.execute(sql);
        }
        catch (SQLException sqlEx)
        {
            LOG.error("Error with SQL " + sql);
            throw sqlEx;
        }

        ResultSet rs = s.getResultSet();

        ResultReader rr = new ResultReader(rs);

        while (rr.nextResult())
        {
            DbAbstractTable entity = a.newEntity();
            entity.readFromDb(rr);
            ret.add(entity);
        }
    }

    @Override
    public DbEndUser retrieveEndUserForEmail(String email) throws Exception
    {
        String where = "Email = " + convertToString(email); //make sure to convert, to prevent SQL injection
        return (DbEndUser)retrieveSingleForWhere(new DbEndUser().getAdapter(), where);
    }

    @Override
    public List<DbOrganisation> retrieveAllOrganisations() throws Exception
    {
        List<DbOrganisation> ret = new ArrayList<DbOrganisation>();
        retrieveForWhere(new DbOrganisation().getAdapter(), "1=1", ret);
        return ret;
    }

    @Override
    public List<DbEndUserEmailInvite> retrieveEndUserEmailInviteForUserNotCompleted(UUID userUuid) throws Exception
    {
        List<DbEndUserEmailInvite> ret = new ArrayList<DbEndUserEmailInvite>();
        String where = "UserUuid = " + convertToString(userUuid)
                     + " AND DtCompleted > GETDATE()";
        retrieveForWhere(new DbEndUserEmailInvite().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public DbEndUserEmailInvite retrieveEndUserEmailInviteForToken(String token) throws Exception
    {
        String where = "Token = " + convertToString(token); //make sure to convert, to prevent SQL injection
        return (DbEndUserEmailInvite)retrieveSingleForWhere(new DbEndUserEmailInvite().getAdapter(), where);
    }

    @Override
    public DbActiveItem retrieveActiveItemForItemUuid(UUID itemUuid) throws Exception
    {
        String where = "ItemUuid = " + convertToString(itemUuid);
        return (DbActiveItem)retrieveSingleForWhere(new DbActiveItem().getAdapter(), where);
    }

    @Override
    public DbEndUserPwd retrieveEndUserPwdForUserNotExpired(UUID endUserUuid) throws Exception
    {
        String where = "EndUserUuid = " + convertToString(endUserUuid)
                     + " AND DtExpired > GETDATE()";
        return (DbEndUserPwd)retrieveSingleForWhere(new DbEndUserPwd().getAdapter(), where);
    }

    @Override
    public List<DbFolderItemLink> retrieveFolderItemLinksForFolder(UUID folderUuid) throws Exception
    {
        List<DbFolderItemLink> ret = new ArrayList<DbFolderItemLink>();
        String where = "FolderUuid = " + convertToString(folderUuid);
        retrieveForWhere(new DbFolderItemLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForOrganisationNotExpired(UUID organisationUuid) throws Exception
    {
        List<DbOrganisationEndUserLink> ret = new ArrayList<DbOrganisationEndUserLink>();
        String where = "OrganisationUuid = " + convertToString(organisationUuid)
                     + " AND DtExpired > GETDATE()";
        retrieveForWhere(new DbOrganisationEndUserLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public List<DbFolder> retrieveFoldersForOrganisationParentType(UUID organisationUuid, UUID parentUuid, int folderType) throws Exception
    {
        List<DbFolder> ret = new ArrayList<DbFolder>();
        String where = "OrganisationUuid = " + convertToString(organisationUuid)
                     + " AND ParentUuid = " + convertToString(parentUuid)
                     + " AND FolderType = " + convertToString(folderType);
        retrieveForWhere(new DbFolder().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public DbFolder retrieveFolderForOrganisationTitleParentType(UUID organisationUuid, String title, UUID parentUuid, int folderType) throws Exception
    {
        String where = "OrganisationUuid = " + convertToString(organisationUuid)
                     + " AND Title = " + convertToString(title)
                     + " AND ParentUuid = " + convertToString(parentUuid)
                     + " AND FolderType = " + convertToString(folderType);

        return (DbFolder)retrieveSingleForWhere(new DbFolder().getAdapter(), where);
    }

    @Override
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForUserNotExpired(UUID endUserUuid) throws Exception
    {
        List<DbOrganisationEndUserLink> ret = new ArrayList<DbOrganisationEndUserLink>();
        String where = "EndUserUuid = " + convertToString(endUserUuid)
                + " AND DtExpired > GETDATE()";
        retrieveForWhere(new DbOrganisationEndUserLink().getAdapter(), where, ret);
        return ret;
    }

    @Override
    public DbOrganisationEndUserLink retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception
    {
        String where = "OrganisationUuid = " + convertToString(organisationUuid)
                     + " AND EndUserUuid = " + convertToString(endUserUuid)
                     + " AND DtExpired > GETDATE()";
        return (DbOrganisationEndUserLink)retrieveSingleForWhere(new DbOrganisationEndUserLink().getAdapter(), where);
    }

    @Override
    public DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception
    {
        String where = "Name = " + convertToString(name)
                + " AND NationalId = " + convertToString(nationalId);
        return (DbOrganisation)retrieveSingleForWhere(new DbOrganisation().getAdapter(), where);
    }
}
