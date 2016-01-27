package org.endeavour.enterprise.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter
{
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException
    {
        try
        {
            Map<String, Cookie> cookies = containerRequestContext.getCookies();

            if (!cookies.containsKey(AuthenticationConstants.COOKIE_NAME))
                throw new NotAuthorizedException("Cookie not found");

            Cookie cookie = cookies.get(AuthenticationConstants.COOKIE_NAME);

            String token = cookie.getValue();

            validateToken(token);
        }
        catch (Exception e)
        {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private void validateToken(String token) throws Exception
    {
        Claims claims = Jwts
                .parser()
                .setSigningKey(AuthenticationConstants.TOKEN_SIGNING_SECRET)
                .parseClaimsJws(token)
                .getBody();

        UUID userUuid = UUID.fromString((String) claims.get(AuthenticationConstants.TOKEN_USER));
        UUID orgUuid = UUID.fromString((String) claims.get(AuthenticationConstants.TOKEN_ORGANISATION));
        String role = (String)claims.get(AuthenticationConstants.TOKEN_ROLE);
        Date issuedAt = new Date(Long.parseLong((String)claims.get(AuthenticationConstants.TOKEN_ISSUED_AT)) * 1000L);
    }
}
