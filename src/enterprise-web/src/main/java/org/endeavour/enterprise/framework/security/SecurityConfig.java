package org.endeavour.enterprise.framework.security;

public abstract class SecurityConfig {

    public static final String TOKEN_SIGNING_SECRET = "DLKV342nNaCapGgSieNde18OFRYwg3etCabRfsPcrnc=";
    public static final long TOKEN_EXPIRY_MINUTES = 60L * 12L;
    public static final String AUTH_COOKIE_NAME = "AUTH";

    public static final String AUTH_COOKIE_VALID_DOMAIN = "localhost";

    public static final String AUTH_COOKIE_VALID_PATH = "/";
    public static final boolean AUTH_COOKIE_REQUIRES_HTTPS = false;
}
