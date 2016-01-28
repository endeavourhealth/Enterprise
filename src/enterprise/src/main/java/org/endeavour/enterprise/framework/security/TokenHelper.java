package org.endeavour.enterprise.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.endeavour.enterprise.framework.configuration.SecurityConfiguration;
import org.endeavour.enterprise.model.Role;
import org.endeavour.enterprise.model.UserContext;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserInRole;

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

    public static NewCookie createTokenAsCookie(User user)
    {
        return createCookie(createToken(user));
    }

    private static String createToken(User user)
    {
        UserInRole userInRole = user.getCurrentUserInRole();

        Map<String, Object> bodyParameterMap = new HashMap<>();
        bodyParameterMap.put(TOKEN_USER, user.getUserUuid());
        bodyParameterMap.put(TOKEN_ORGANISATION, userInRole.getOrganisationUuid());
        bodyParameterMap.put(TOKEN_ROLE, userInRole.getRole().name());
        bodyParameterMap.put(TOKEN_ISSUED_AT, Long.toString(Instant.now().getEpochSecond()));

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam(TOKEN_TYPE, TOKEN_TYPE_JWT)
                .setClaims(bodyParameterMap)
                .signWith(SignatureAlgorithm.HS256, SecurityConfiguration.TOKEN_SIGNING_SECRET);

        return builder.compact();
    }

    private static NewCookie createCookie(String token)
    {
        return new NewCookie(SecurityConfiguration.AUTH_COOKIE_NAME,
                token,
                SecurityConfiguration.AUTH_COOKIE_VALID_PATH,
                SecurityConfiguration.AUTH_COOKIE_VALID_DOMAIN,
                1,
                null,
                -1,
                null,
                SecurityConfiguration.AUTH_COOKIE_REQUIRES_HTTPS,
                true);
    }

    public static UserContext validateToken(String token) throws Exception
    {
        Claims claims = Jwts
                .parser()
                .setSigningKey(SecurityConfiguration.TOKEN_SIGNING_SECRET)
                .parseClaimsJws(token)
                .getBody();

        UUID userUuid = UUID.fromString((String)claims.get(TOKEN_USER));
        UUID organisationUuid = UUID.fromString((String) claims.get(TOKEN_ORGANISATION));
        Role role = Enum.valueOf(Role.class, (String)claims.get(TOKEN_ROLE));
        long tokenIssuedMilliseconds = Long.parseLong((String) claims.get(TOKEN_ISSUED_AT)) * 1000L;
        Date tokenIssued = new Date(tokenIssuedMilliseconds);

        Date tokenExpiry = new Date(tokenIssuedMilliseconds + (SecurityConfiguration.TOKEN_EXPIRY_MINUTES * 60L * 1000L));

        if (Calendar.getInstance().getTime().after(tokenExpiry))
            throw new AuthenticationException("Token expired");

        return new UserContext(userUuid, organisationUuid, role, tokenIssued);
    }
}
