package org.endeavourhealth.enterprise.enginecore.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.sql.Timestamp;

public class DatabaseHelper {

    public static Connection getConnection(DatabaseConnectionDetails details) throws ClassNotFoundException, SQLException {

        String driver = "net.sourceforge.jtds.jdbc.Driver";
        Class.forName(driver);

        return DriverManager.getConnection(details.getUrl(), details.getUsername(), details.getPassword());
    }
//
//    public static Timestamp currentDateTime() {
//        return new Timestamp(System.currentTimeMillis());
//    }
//
//    public static TableInformation queryTable(DatabaseConnectionDetails connectionDetails, String schemaName, String tableName) throws SQLException, ClassNotFoundException, EntityMapException {
//        return DataQuerying.queryTable(connectionDetails, schemaName, tableName);
//    }
}