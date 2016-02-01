package org.endeavour.enterprise.framework.database;

import org.endeavour.enterprise.framework.configuration.Configuration;

import java.sql.*;

public class DatabaseConnection
{
    public static Connection get(String databaseName) throws ClassNotFoundException, SQLException
    {
        Class.forName(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName());
        return DriverManager.getConnection(String.format(Configuration.DB_CONNECTION_STRING, databaseName));
    }
}