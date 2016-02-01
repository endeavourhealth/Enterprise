package org.endeavour.enterprise.framework.configuration;

public class Configuration
{
    public static final String TOKEN_SIGNING_SECRET = "DLKV342nNaCapGgSieNde18OFRYwg3etCabRfsPcrnc=";
    public static final long TOKEN_EXPIRY_MINUTES = 60L * 12L;
    public static final String AUTH_COOKIE_NAME = "AUTH";
    public static final String AUTH_COOKIE_VALID_DOMAIN = "127.0.0.1";
    public static final String AUTH_COOKIE_VALID_PATH = "/";
    public static final boolean AUTH_COOKIE_REQUIRES_HTTPS = false;
    public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver://localhost:1433/$s;user=Enterprise;password=XXXXXXXX";

}
