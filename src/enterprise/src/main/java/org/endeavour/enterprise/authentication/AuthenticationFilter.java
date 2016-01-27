package org.endeavour.enterprise.authentication;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter
{
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException
    {
        try
        {
            Map<String, Cookie> cookies = containerRequestContext.getCookies();

            // Extract the token from cookie
            String token = "";

            // Validate the token
            validateToken(token);
        }
        catch (Exception e)
        {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private void validateToken(String token) throws Exception
    {
        // Check if it was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
    }
}
