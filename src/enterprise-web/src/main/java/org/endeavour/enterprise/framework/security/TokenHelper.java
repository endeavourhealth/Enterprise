package org.endeavour.enterprise.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.endeavourhealth.enterprise.core.database.models.EnduserEntity;
import org.endeavourhealth.enterprise.core.database.models.OrganisationEntity;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.NewCookie;
import java.time.Instant;
import java.util.*;

public class TokenHelper {
    private static final String TOKEN_TYPE = "typ";
    private static final String TOKEN_TYPE_JWT = "JWT";
    private static final String TOKEN_ISSUED_AT = "iat";
    private static final String TOKEN_USER = "usr";
    private static final String TOKEN_ROLE_ADMIN = "adm";
    private static final String TOKEN_ROLE_SUPER = "spr";
    private static final String TOKEN_ORGANISATION = "org";
    private static final String TOKEN_HOST = "hst";

    public static NewCookie createTokenAsCookie(String host, EnduserEntity person, OrganisationEntity org, boolean isAdmin, boolean isSuperUser) {
        String token = createToken(host, person, org, isAdmin, isSuperUser);
        return createCookie(token);
    }

    private static String createToken(String host, EnduserEntity person, OrganisationEntity org, boolean isAdmin, boolean isSuperUser) {
        Map<String, Object> bodyParameterMap = new HashMap<>();
        bodyParameterMap.put(TOKEN_ISSUED_AT, Long.toString(Instant.now().getEpochSecond()));

        //when logging a user off, we create a token with a null person
        if (person != null) {
            bodyParameterMap.put(TOKEN_USER, person.getEnduseruuid());
        }

        bodyParameterMap.put(TOKEN_HOST, host);
        bodyParameterMap.put(TOKEN_ROLE_ADMIN, isAdmin);
        bodyParameterMap.put(TOKEN_ROLE_SUPER, isSuperUser);

        //if the person has multiple orgs they can log on to, then we may pass in null until they select one
        if (org != null) {
            bodyParameterMap.put(TOKEN_ORGANISATION, org.getOrganisationuuid());
        }

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam(TOKEN_TYPE, TOKEN_TYPE_JWT)
                .setClaims(bodyParameterMap)
                .signWith(SignatureAlgorithm.HS256, SecurityConfig.TOKEN_SIGNING_SECRET);

        return builder.compact();
    }

    private static NewCookie createCookie(String token) {
        int maxAge = (int) (60L * SecurityConfig.TOKEN_EXPIRY_MINUTES); //a day
        long now = System.currentTimeMillis() + (1000 * maxAge);
        Date d = new Date(now);

        return new NewCookie(SecurityConfig.AUTH_COOKIE_NAME,
                token,
                SecurityConfig.AUTH_COOKIE_VALID_PATH,
                SecurityConfig.AUTH_COOKIE_VALID_DOMAIN,
                1,
                null,
                maxAge,
                d,
                SecurityConfig.AUTH_COOKIE_REQUIRES_HTTPS,
                false);
    }

    public static UserContext parseUserContextFromToken(HttpServletRequest request, String token) throws Exception {
        Claims claims = Jwts
                .parser()
                .setSigningKey(SecurityConfig.TOKEN_SIGNING_SECRET)
                .parseClaimsJws(token)
                .getBody();

        long tokenIssuedMilliseconds = Long.parseLong((String) claims.get(TOKEN_ISSUED_AT)) * 1000L;
        Date tokenIssued = new Date(tokenIssuedMilliseconds);
        Date tokenExpiry = new Date(tokenIssuedMilliseconds + (SecurityConfig.TOKEN_EXPIRY_MINUTES * 60L * 1000L));

        if (Calendar.getInstance().getTime().after(tokenExpiry)) {
            throw new AuthenticationException("Token expired");
        }

        String userUuidStr = (String)claims.get(TOKEN_USER);
        if (userUuidStr == null) {
            throw new AuthenticationException("User logged off");
        }

        UUID userUuid = UUID.fromString(userUuidStr);
        String host = (String)claims.get(TOKEN_HOST);
        boolean isAdmin = ((Boolean)claims.get(TOKEN_ROLE_ADMIN)).booleanValue();;
        boolean isSuperUser = ((Boolean)claims.get(TOKEN_ROLE_SUPER)).booleanValue();;

        //users may not have an org selected
        UUID organisationUuid = null;
        String orgUuidStr = (String)claims.get(TOKEN_ORGANISATION);
        if (orgUuidStr != null) {
            organisationUuid = UUID.fromString(orgUuidStr);
        }

        String requestingHost = getRequestingHostFromRequest(request);
        if (!host.equals(requestingHost)) {
            throw new AuthenticationException("Host IP changed");
        }

        return new UserContext(host, userUuid, organisationUuid, isAdmin, isSuperUser, tokenIssued);
    }

    public static String getRequestingHostFromRequest(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR"); //get the true source of the request, even if behind a proxy
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}
