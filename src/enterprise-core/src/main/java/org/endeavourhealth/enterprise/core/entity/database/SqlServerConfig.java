package org.endeavourhealth.enterprise.core.entity.database;

public abstract class SqlServerConfig {


    public static final int DB_CONNECTION_POOL_SIZE = 5; //pool up to this many connection
    public static final int DB_CONNECTION_LIVES = 1000; //let each pooled connection be used this many times
    public static final int DB_CONNECTION_MAX_AGE_MILLIS = 30 * 60 * 1000; //let each pooled connection exist for this long

    //to run against Azure
    public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver://mpydpvsu61.database.windows.net:1433/Endeavour_Enterprise;user=devuser@mpydpvsu61;password=7oaG7FVsvK08sE9T5NEe";

    //to run against local SQL Server
    //public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver://127.0.0.1:1433/Endeavour_Enterprise;instance=SQLEXPRESS;Network Library=dbmslpcn;user=Endeavour_Enterprise_ApplicationUser;password=TheQuickBrownFox1234%^&*";

    //????
    //public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver://mpydpvsu61.database.windows.net:1433/Endeavour_Enterprise;user=Endeavour_Enterprise_ApplicationUser;password=TheQuickBrownFox1234%^&*";
}
