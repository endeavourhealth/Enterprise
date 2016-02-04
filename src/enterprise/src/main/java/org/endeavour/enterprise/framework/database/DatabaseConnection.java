package org.endeavour.enterprise.framework.database;

import org.endeavour.enterprise.framework.configuration.Configuration;

import java.sql.*;

public class DatabaseConnection
{
    public static Connection get(String databaseName) throws ClassNotFoundException, SQLException
    {
        // databaseName not used at present

        Class.forName(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName());
        return DriverManager.getConnection(Configuration.DB_CONNECTION_STRING);
    }
}