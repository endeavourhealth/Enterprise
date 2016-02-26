package org.endeavour.enterprise.framework.configuration;

public class Configuration
{
    public static final String TOKEN_SIGNING_SECRET = "DLKV342nNaCapGgSieNde18OFRYwg3etCabRfsPcrnc=";
    public static final long TOKEN_EXPIRY_MINUTES = 60L * 12L;
    public static final String AUTH_COOKIE_NAME = "AUTH";

    //2016-02-25 DL - Browser (specifically firefox) doesn't store the cookie if the domain is the local host IP, but does for the word
    public static final String AUTH_COOKIE_VALID_DOMAIN = "localhost";
    //public static final String AUTH_COOKIE_VALID_DOMAIN = "127.0.0.1";

    public static final String AUTH_COOKIE_VALID_PATH = "/";
    public static final boolean AUTH_COOKIE_REQUIRES_HTTPS = false;

    //2016-02-16 DL - test against local DB
    //public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver:server=(local)\\SQLEXPRESS;Network Library=dbmslpcn;integrated Security=SSPI";
    //jdbc:jtds:sqlserver://MYPC:1433/Blog;instance=SQLEXPRESS;user=sa;password=s3cr3t
    // public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver://127.0.0.1:1433/Endeavour_Enterprise;instance=SQLEXPRESS;Network Library=dbmslpcn;user=Endeavour_Enterprise_ApplicationUser;password=TheQuickBrownFox1234%^&*";

    public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver://mpydpvsu61.database.windows.net:1433/Endeavour_Enterprise;user=Endeavour_Enterprise_ApplicationUser;password=TheQuickBrownFox1234%^&*";
}
