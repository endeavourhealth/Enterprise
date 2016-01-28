package org.endeavour.enterprise.framework.security;

import org.endeavour.enterprise.model.Role;
import org.endeavour.enterprise.model.UserContext;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Roles
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter
{
    @Context
    private ResourceInfo resourceInfo;

    @Context
    protected SecurityContext securityContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException
    {
        List<Role> classRoles = extractRoles(resourceInfo.getResourceClass());
        List<Role> methodRoles = extractRoles(resourceInfo.getResourceMethod());

        UserContext userContext = UserContext.fromSecurityContext(securityContext);

        try
        {
            if (methodRoles.isEmpty())
                checkPermissions(userContext, classRoles);
            else
                checkPermissions(userContext, methodRoles);

        }
        catch (Exception e)
        {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    private List<Role> extractRoles(AnnotatedElement annotatedElement)
    {
        if (annotatedElement == null)
            return new ArrayList<>();

        Roles roles = annotatedElement.getAnnotation(Roles.class);

        if (roles == null)
            return new ArrayList<>();

        Role[] allowedRoles = roles.value();
        return Arrays.asList(allowedRoles);
    }

    private void checkPermissions(UserContext userContext, List<Role> allowedRoles) throws Exception
    {
        if (userContext == null)
            throw new NotAuthorizedException("Could not find userContext");

        if (!isAllowedIn(userContext.getRole(), allowedRoles))
            throw new NotAuthorizedException("Role not allowed");
    }

    private static boolean isAllowedIn(Role userRole, List<Role> allowedRoles)
    {
        for (Role allowedRole : allowedRoles)
            if (userRole.isGreaterThanOrEqualTo(allowedRole))
                return true;

        return false;
    }
}
