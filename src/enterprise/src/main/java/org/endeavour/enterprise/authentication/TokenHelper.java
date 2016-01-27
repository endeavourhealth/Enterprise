package org.endeavour.enterprise.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.endeavour.enterprise.model.Token;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserInRole;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

class TokenHelper
{
    private static final String TOKEN_TYPE = "typ";
    private static final String TOKEN_TYPE_JWT = "JWT";
    private static final String TOKEN_ISSUED_AT = "iat";
    private static final String TOKEN_USER = "usr";
    private static final String TOKEN_ROLE = "rol";
    private static final String TOKEN_ORGANISATION = "org";

    public static String createToken(User user, UserInRole userInRole)
    {
        Map<String, Object> bodyParameterMap = new HashMap<>();
        bodyParameterMap.put(TOKEN_USER, user.getUserUuid());
        bodyParameterMap.put(TOKEN_ORGANISATION, userInRole.getOrganisationUuid());
        bodyParameterMap.put(TOKEN_ROLE, userInRole.getRole().name());
        bodyParameterMap.put(TOKEN_ISSUED_AT, Long.toString(Instant.now().getEpochSecond()));

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam(TOKEN_TYPE, TOKEN_TYPE_JWT)
                .setClaims(bodyParameterMap)
                .signWith(SignatureAlgorithm.HS256, AuthenticationConstants.TOKEN_SIGNING_SECRET);

        return builder.compact();
    }

    public static Token validateToken(String token) throws Exception
    {
        Claims claims = Jwts
                .parser()
                .setSigningKey(AuthenticationConstants.TOKEN_SIGNING_SECRET)
                .parseClaimsJws(token)
                .getBody();

        Token result = new Token();

        result.setUserUuidFromString((String)claims.get(TOKEN_USER));
        result.setOrganisationUuidFromString((String)claims.get(TOKEN_ORGANISATION));
        result.setRoleFromString((String)claims.get(TOKEN_ROLE));
        result.setIssuedAtFromUnixEpoch(Long.parseLong((String)claims.get(TOKEN_ISSUED_AT)));

        return result;
    }
}
