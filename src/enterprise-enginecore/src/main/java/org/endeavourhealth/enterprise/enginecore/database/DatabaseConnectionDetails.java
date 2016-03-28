package org.endeavourhealth.enterprise.enginecore.database;

public class DatabaseConnectionDetails {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseConnectionDetails(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

//    public String getConnectionString() {
//
//        String url = String.format(
//                "jdbc:jtds:sqlserver://%s:%s/%s;user=%s;password=%s",
//                ipAddress,
//                port,
//                databaseName,
//                username,
//                password);
//
//        //String url = "jdbc:jtds:sqlserver://127.0.0.1:1433/Discovery_Core;user=Discovery;password=Discovery";
//
//        return url;
//    }
}
