package org.endeavour.enterprise.authentication;

class AuthenticationConstants
{
    public static final String COOKIE_NAME = "AUTH";
    public static final String COOKIE_VALID_DOMAIN = "127.0.0.1";
    public static final String COOKIE_VALID_PATH = "/";
    public static final boolean COOKIE_REQUIRES_HTTPS = false;

    public static final String TOKEN_SIGNING_SECRET = "DLKV342nNaCapGgSieNde18OFRYwg3etCabRfsPcrnc=";
    public static final String TOKEN_TYPE = "typ";
    public static final String TOKEN_TYPE_JWT = "JWT";
    public static final String TOKEN_ISSUED_AT = "iat";
    public static final String TOKEN_USER = "usr";
    public static final String TOKEN_ROLE = "rol";
    public static final String TOKEN_ORGANISATION = "org";
}
