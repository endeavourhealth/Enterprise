package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.framework.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 */
public final class TableAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger(TableAdapter.class); //2016-02-26 DL - logging

    private Class cls = null;
    private String tableName = null;
    private String schema = null;
    private String database = null;
    //private String[] columns = null; //2016-02-22 DL - removed

    //lazily derived
/*    private List<Method> cachedGetMethods = null;
    private List<Method> cachedSetMethods = null;*/

    //public TableAdapter(Class cls, String tableName, String schema, String database, String[] columns)
    public TableAdapter(Class cls, String tableName, String schema, String database)
    {
        this.cls = cls;
        this.tableName = tableName;
        this.schema = schema;
        this.database = database;
        //this.columns = columns; //2016-02-22 DL - removed
    }

    /**
     * gets only
     */
    public Class getCls() {
        return cls;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSchema() {
        return schema;
    }

    public String getDatabase() {
        return database;
    }

    /*public String[] getColumns() {
        return columns;
    }*/

    /**
     * creates a new instance of our database class
     */
    public DbAbstractTableX newEntity() throws Exception
    {
        return (DbAbstractTableX) getCls().newInstance();
    }

    /**
     * returns a list of Method objects used to get all the values from one of our
     * entities in the order the database expects them
     */
/*
    public List<Method> getGetMethods() throws NoSuchMethodException
    {
        if (cachedGetMethods == null)
        {
            List<Method> tmp = new ArrayList<Method>();

            String primaryKeyCol = getTableName() + "Uuid";

            for (int i=0; i<columns.length; i++) {
                String column = columns[i];

                //the primary UUID is always called <TableName>Uuid on the DB, so we need
                //to use an alternative get/set method pair to access that
                if (column.equals(primaryKeyCol))
                {
                    column = "PrimaryUuid";
                }

                Method m = cls.getMethod("get" + column);
                tmp.add(m);
            }

            cachedGetMethods = tmp;
        }
        return cachedGetMethods;
    }
    public List<Method> getSetMethods() throws NoSuchMethodException
    {
        if (cachedSetMethods == null)
        {
            List<Method> tmp = new ArrayList<Method>();

            String primaryKeyCol = getTableName() + "Uuid";

            for (int i=0; i<columns.length; i++) {
                String column = columns[i];

                Method m = null;

                //the primary UUID is always called <TableName>Uuid on the DB, so we need
                //to use an alternative get/set method pair to access that
                if (column.equals(primaryKeyCol))
                {
                    m = findSetMethod(cls.getSuperclass(), "PrimaryUuid");
                }
                else
                {
                    m = findSetMethod(cls, column);
                }

                tmp.add(m);
            }

            cachedSetMethods = tmp;
        }
        return cachedSetMethods;
    }
*/

    /**
     * when caching the set methods for our entity, we use this fn to look up an appropriately named
     * method with the right return type (void) and right number of parameters (one)
     */
/*    private static Method findSetMethod(Class cls, String columnName) throws NoSuchMethodException
    {
        //we're looking for a set method to match the column name
        String methodName = "set" + columnName;

        Method[] arr = cls.getMethods();
        for (int i=0; i<arr.length; i++)
        {
            Method m = arr[i];

            //check the name
            if (!m.getName().equals(methodName))
            {
                continue;
            }

            //check the return type, which should be void
            if (!m.getReturnType().equals(Void.TYPE))
            {
                continue;
            }

            //check the parameter count, as there should just be one
            if (m.getParameterCount() != 1)
            {
                continue;
            }

            //if we get here, this is out method
            return m;
        }

        throw new NoSuchMethodException("Couldn't find " + methodName);
    }*/


  /*  public static DbAbstractTable retrieveSingleEntityForUuid(TableAdapter adapter, UUID uuid) throws Exception
    {
        //the retrieve for UUID has a standard name for all entities
        String spName = adapter.getSchema() + "." + adapter.getTableName() + "_SelectForUuidUUId";
        return adapter.retrieveSingleEntity(spName, uuid);
    }*/


    public DbAbstractTableX retrieveSingleEntity(String spName, Object... parameters) throws Exception
    {
        List<? extends DbAbstractTableX> v = retrieveEntities(spName, parameters);
        //List<DbAbstractTable> v = retrieveEntities(spName, parameters);

        if (v.size() == 1)
        {
            return v.get(0);
        }
        else if (v.size() == 0)
        {
            return null;
        }
        //if we're expecting zero or one and get MORE, then something is wrong
        else
        {
            throw new RuntimeException("Retrieved multiple results from " + spName);
        }
    }
    public List<DbAbstractTableX> retrieveEntities(String spName, Object... spParameters) throws Exception
    {
        Connection connection = DatabaseConnection.get(getDatabase());

        String sql = spName;

        if (spParameters != null) {
            for (int i = 0; i < spParameters.length; i++) {
                Object parameter = spParameters[i];
                parameter = quoteAndEscapeString(parameter);

                if (i > 0) {
                    sql += ", ";
                } else {
                    sql += " ";
                }

                sql += parameter;
            }
        }

        Statement s = connection.createStatement();
        s.execute(sql);

        ResultSet rs = s.getResultSet();

        return readFromResultSet(rs);
    }
    /*public DbAbstractTable retrieveSingleEntity(String spName, Object... parameters) throws Exception
    {
        try {
            Connection connection = DatabaseConnection.get(getDatabase());

            String sql = spName;

            for (int i=0; i<parameters.length; i++)
            {
                Object parameter = parameters[i];
                parameter = quoteAndEscapeString(parameter);

                if (i > 0) {
                    sql += ", ";
                }
                else
                {
                    sql += " ";
                }

                sql += parameter;
            }

            Statement s = connection.createStatement();
            s.execute(sql);

            ResultSet rs = s.getResultSet();

            List<DbAbstractTable> v = readFromResultSet(rs);
            if (v.size() == 1)
            {
                return v.get(0);
            }
            else if (v.size() == 0)
            {
                return null;
            }
            //if we're expecting zero or one and get MORE, then something is wrong
            else
            {
                throw new RuntimeException("Retrieved multiple results from " + spName);
            }


        } catch (Exception t) {
            t.printStackTrace(System.err);
        }


        return null;
    }*/

    private static Object quoteAndEscapeString(Object obj)
    {
        if (obj instanceof String)
        {
            String s = (String)obj;
            s = s.replaceAll("'", "''");
            return "'" + s + "'";
        }
        else if (obj instanceof UUID)
        {
            //since no longer using prepared statements, just wrap the string format of the UUID in quotes
            UUID uuid = (UUID)obj;
            return "'" + uuid.toString() + "'";

            //2016-02-17 DL - JTDS doesn't seem to handle conversion of UUID objects
            //to SQL uniqueIdentifier types, so we need to convert to byte[] here
/*            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());
            byte[] bytes = bb.array();
            value = bytes;*/
        }
        else
        {
            return obj;
        }
    }


    private List<DbAbstractTableX> readFromResultSet(ResultSet rs) throws Exception
    {
        List<DbAbstractTableX> ret = new ArrayList<DbAbstractTableX>();
        ResultReader rr = new ResultReader(rs);

        while (rr.nextResult())
        {
            DbAbstractTableX entity = newEntity();
            entity.readFromDb(rr);
            ret.add(entity);
        }

        return ret;
    }
/*    private List<DbAbstractTable> readFromResultSet(ResultSet rs) throws Exception
    {
        List<DbAbstractTable> ret = new ArrayList<DbAbstractTable>();

        while (rs.next())
        {
            DbAbstractTable entity = newEntity();

            List<Method> setMethods = getSetMethods();
            for (int i=0; i<setMethods.size(); i++)
            {
                Method m = setMethods.get(i);

                //2016-02-18 DL - result set columns start at ONE, not ZERO
                Object value = rs.getObject(i+1);
                //Object value = rs.getObject(i);
                m.invoke(entity, new Object[]{value});
            }

            ret.add(entity);
        }

        return ret;
    }*/

    public void saveToDb(boolean insert, DbAbstractTableX entity) throws Exception
    {
        String spName = getSchema() + "._" + getTableName();
        if (insert) {
            spName += "_Insert";
        } else {
            spName += "_Update";
        }

        InsertBuilder ib = new InsertBuilder(spName + " ");
        entity.writeForDb(ib);

        String sql = ib.toString();

        Connection connection = DatabaseConnection.get(getDatabase());
        Statement s = connection.createStatement();

        try {
            s.execute(sql);
        }
        catch (SQLException sqlExc)
        {
            //if we get an error with the SQL, at least log it out
            LOG.error("Error with SQL " + sql);
            throw sqlExc;
        }
    }

/*    public void saveToDb(boolean insert, DbAbstractTable entity) throws Exception {

        String spName = getSchema() + "." + getTableName();
        if (insert) {
            spName += "_Insert";
        } else {
            spName += "_Update";
        }

        Connection connection = DatabaseConnection.get(getDatabase());

        String sql = spName;

        List<Method> v = getGetMethods();

        for (int i=0; i<v.size(); i++)
        {
            Method m = v.get(i);

            Object value = m.invoke(entity, new Object[] {});
            value = quoteAndEscapeString(value);

            if (i > 0)
            {
                sql += ", ";
            }
            sql += value;
        }

        Statement s = connection.createStatement();
        s.execute(sql);
    }*/

    /**
     * calls the ...delete SP for the given entity to remove from the DB
     */
    public void deleteFromDb(DbAbstractTableX entity) throws Exception
    {
        String spName = getSchema() + "._" + getTableName() + "_Delete";
        String sql = spName + " " + entity.getPrimaryUuid();

        Connection connection = DatabaseConnection.get(getDatabase());
        Statement s = connection.createStatement();
        s.execute(sql);
    }
}
