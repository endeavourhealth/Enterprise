package org.endeavour.enterprise.framework.security;

import org.endeavour.enterprise.framework.exceptions.NotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Priority(Priorities.AUTHENTICATION)
public abstract class AbstractAuthenticationFilter implements ContainerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractAuthenticationFilter.class);

    @Context
    protected HttpServletRequest request;

    public AbstractAuthenticationFilter() {}

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        try {
            Map<String, Cookie> cookies = containerRequestContext.getCookies();

            if (!cookies.containsKey(SecurityConfig.AUTH_COOKIE_NAME)) {
                throw new NotAuthorizedException("Cookie not found");
            }

            Cookie cookie = cookies.get(SecurityConfig.AUTH_COOKIE_NAME);

            String tokenString = cookie.getValue();

            UserContext userContext = TokenHelper.parseUserContextFromToken(request, tokenString);

            //let our sub-classes perform additional validation
            doSpecificAuthoriationCheck(userContext);

            containerRequestContext.setSecurityContext(new UserSecurityContext(userContext));
        } catch (Exception e) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    public abstract void doSpecificAuthoriationCheck(UserContext cx) throws NotAuthorizedException;
}
