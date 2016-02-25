package org.endeavour.enterprise.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.endeavour.enterprise.entity.database.DbEndUser;
import org.endeavour.enterprise.entity.database.DbOrganisation;
import org.endeavour.enterprise.framework.configuration.Configuration;
import org.endeavour.enterprise.model.EndUserRole;
import org.endeavour.enterprise.model.UserContext;

import javax.naming.AuthenticationException;
import javax.ws.rs.core.NewCookie;
import java.time.Instant;
import java.util.*;

public class TokenHelper
{
    private static final String TOKEN_TYPE = "typ";
    private static final String TOKEN_TYPE_JWT = "JWT";
    private static final String TOKEN_ISSUED_AT = "iat";
    private static final String TOKEN_USER = "usr";
    private static final String TOKEN_ROLE = "rol";
    private static final String TOKEN_ORGANISATION = "org";

    //2016-02-23 DL - moved to user the DbEndUser and DbOrganisation entities
    /*public static NewCookie createTokenAsCookie(User user)
    {
        return createCookie(createToken(user));
    }

    private static String createToken(User user)
    {
        UserInRole userInRole = user.getCurrentUserInRole();

        Map<String, Object> bodyParameterMap = new HashMap<>();
        bodyParameterMap.put(TOKEN_USER, user.getUserUuid());
        bodyParameterMap.put(TOKEN_ORGANISATION, userInRole.getOrganisationUuid());
        bodyParameterMap.put(TOKEN_ROLE, userInRole.getEndUserRole().name());
        bodyParameterMap.put(TOKEN_ISSUED_AT, Long.toString(Instant.now().getEpochSecond()));

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam(TOKEN_TYPE, TOKEN_TYPE_JWT)
                .setClaims(bodyParameterMap)
                .signWith(SignatureAlgorithm.HS256, Configuration.TOKEN_SIGNING_SECRET);

        return builder.compact();
    }*/

    public static NewCookie createTokenAsCookie(DbEndUser person, DbOrganisation org, EndUserRole endUserRole)
    {
        String token = createToken(person, org, endUserRole);
        return createCookie(token);
    }
    private static String createToken(DbEndUser person, DbOrganisation org, EndUserRole endUserRole)
    {
        Map<String, Object> bodyParameterMap = new HashMap<>();
        bodyParameterMap.put(TOKEN_ISSUED_AT, Long.toString(Instant.now().getEpochSecond()));

        //when logging a user off, we create a token with a null person
        if (person != null)
        {
            bodyParameterMap.put(TOKEN_USER, person.getPrimaryUuid());
        }

        //if the person has multiple orgs they can log on to, then we may pass in null until they select one
        if (org != null)
        {
            bodyParameterMap.put(TOKEN_ORGANISATION, org.getPrimaryUuid());
            bodyParameterMap.put(TOKEN_ROLE, endUserRole.name());
        }

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam(TOKEN_TYPE, TOKEN_TYPE_JWT)
                .setClaims(bodyParameterMap)
                .signWith(SignatureAlgorithm.HS256, Configuration.TOKEN_SIGNING_SECRET);

        return builder.compact();
    }

    private static NewCookie createCookie(String token)
    {
        int maxAge = (int)(60L * Configuration.TOKEN_EXPIRY_MINUTES); //a day
        long now = System.currentTimeMillis() + (1000 * maxAge);
        Date d = new Date(now);

        return new NewCookie(Configuration.AUTH_COOKIE_NAME,
                token,
                Configuration.AUTH_COOKIE_VALID_PATH,
                Configuration.AUTH_COOKIE_VALID_DOMAIN,
                1,
                null,
                maxAge,
                d,
                Configuration.AUTH_COOKIE_REQUIRES_HTTPS,
                false);
/*        return new NewCookie(Configuration.AUTH_COOKIE_NAME,
                token,
                Configuration.AUTH_COOKIE_VALID_PATH,
                Configuration.AUTH_COOKIE_VALID_DOMAIN,
                1,
                null,
                -1,
                null,
                Configuration.AUTH_COOKIE_REQUIRES_HTTPS,
                false);*/
    }

    public static UserContext validateToken(String token) throws Exception
    {
        Claims claims = Jwts
                .parser()
                .setSigningKey(Configuration.TOKEN_SIGNING_SECRET)
                .parseClaimsJws(token)
                .getBody();

        UUID userUuid = UUID.fromString((String)claims.get(TOKEN_USER));
        UUID organisationUuid = UUID.fromString((String) claims.get(TOKEN_ORGANISATION));
        EndUserRole endUserRole = Enum.valueOf(EndUserRole.class, (String)claims.get(TOKEN_ROLE));
        long tokenIssuedMilliseconds = Long.parseLong((String) claims.get(TOKEN_ISSUED_AT)) * 1000L;
        Date tokenIssued = new Date(tokenIssuedMilliseconds);

        Date tokenExpiry = new Date(tokenIssuedMilliseconds + (Configuration.TOKEN_EXPIRY_MINUTES * 60L * 1000L));

        if (Calendar.getInstance().getTime().after(tokenExpiry))
            throw new AuthenticationException("Token expired");

        return new UserContext(userUuid, organisationUuid, endUserRole, tokenIssued);
    }
}
