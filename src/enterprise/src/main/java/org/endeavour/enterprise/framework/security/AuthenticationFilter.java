package org.endeavour.enterprise.framework.security;

import org.endeavour.enterprise.framework.configuration.SecurityConfiguration;
import org.endeavour.enterprise.model.UserContext;

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

            if (!cookies.containsKey(SecurityConfiguration.AUTH_COOKIE_NAME))
                throw new NotAuthorizedException("Cookie not found");

            Cookie cookie = cookies.get(SecurityConfiguration.AUTH_COOKIE_NAME);

            String tokenString = cookie.getValue();

            UserContext userContext = TokenHelper.validateToken(tokenString);



            containerRequestContext.setSecurityContext(new UserSecurityContext(userContext));
        }
        catch (Exception e)
        {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
