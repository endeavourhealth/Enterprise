package org.endeavourhealth.discovery.core.database;

import net.sourceforge.jtds.jdbc.JtdsResultSet;
import org.endeavourhealth.discovery.core.entitymap.EntityMapException;

import java.sql.*;

public class DatabaseHelper {

    public static Connection getConnection(DatabaseConnectionDetails details) throws ClassNotFoundException, SQLException {

        String driver = "net.sourceforge.jtds.jdbc.Driver";
        Class.forName(driver);

        String url = details.getConnectionString();
        return DriverManager.getConnection(url);
    }

    public static Timestamp currentDateTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static TableInformation queryTable(DatabaseConnectionDetails connectionDetails, String schemaName, String tableName) throws SQLException, ClassNotFoundException, EntityMapException {
        return DataQuerying.queryTable(connectionDetails, schemaName, tableName);
    }
}