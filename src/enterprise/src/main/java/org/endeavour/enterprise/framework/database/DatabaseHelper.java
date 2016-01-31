package org.endeavour.enterprise.framework.database;

import org.endeavour.enterprise.framework.configuration.Configuration;

import java.sql.*;

public class DatabaseHelper
{
    public static Connection getConnection() throws ClassNotFoundException, SQLException
    {
        Class.forName(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName());
        return DriverManager.getConnection(Configuration.DB_CONNECTION_STRING);
    }
}