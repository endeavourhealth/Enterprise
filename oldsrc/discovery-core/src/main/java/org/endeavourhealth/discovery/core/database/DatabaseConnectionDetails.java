package org.endeavourhealth.discovery.core.database;

public class DatabaseConnectionDetails {
    private String ipAddress;
    private int port;
    private String databaseName;
    private String username;
    private String password;

    public DatabaseConnectionDetails(String ipAddress, int port, String databaseName, String username, String password) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public String getConnectionString() {

        String url = String.format(
                "jdbc:jtds:sqlserver://%s:%s/%s;user=%s;password=%s",
                ipAddress,
                port,
                databaseName,
                username,
                password);

        //String url = "jdbc:jtds:sqlserver://127.0.0.1:1433/Discovery_Core;user=Discovery;password=Discovery";

        return url;
    }
}
