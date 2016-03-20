package org.endeavourhealth.enterprise.core.entity.database;

abstract class SqlServerConfig {

    //Azure
    public static final String DB_URL = "jdbc:jtds:sqlserver://mpydpvsu61.database.windows.net:1433/Endeavour_Enterprise";
    public static final String DB_USER = "devuser@mpydpvsu61";
    public static final String DB_PASSWORD = "7oaG7FVsvK08sE9T5NEe";

    //local SQL server
    /*public static final String DB_URL = "jdbc:jtds:sqlserver://127.0.0.1:1433/Endeavour_Enterprise;instance=SQLEXPRESS";
    public static final String DB_USER = "Endeavour_Enterprise_ApplicationUser";
    public static final String DB_PASSWORD = "TheQuickBrownFox1234%^&*";*/
}
