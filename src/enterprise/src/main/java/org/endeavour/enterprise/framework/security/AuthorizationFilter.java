package org.endeavour.enterprise.framework.security;

import org.endeavour.enterprise.framework.exceptions.NotAuthorizedException;
import org.endeavour.enterprise.model.EndUserRole;
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
        List<EndUserRole> classEndUserRoles = extractRoles(resourceInfo.getResourceClass());
        List<EndUserRole> methodEndUserRoles = extractRoles(resourceInfo.getResourceMethod());

        UserContext userContext = UserContext.fromSecurityContext(securityContext);

        try
        {
            if (methodEndUserRoles.isEmpty())
                checkPermissions(userContext, classEndUserRoles);
            else
                checkPermissions(userContext, methodEndUserRoles);

        }
        catch (Exception e)
        {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    private List<EndUserRole> extractRoles(AnnotatedElement annotatedElement)
    {
        if (annotatedElement == null)
            return new ArrayList<>();

        Roles roles = annotatedElement.getAnnotation(Roles.class);

        if (roles == null)
            return new ArrayList<>();

        EndUserRole[] allowedEndUserRoles = roles.value();
        return Arrays.asList(allowedEndUserRoles);
    }

    private void checkPermissions(UserContext userContext, List<EndUserRole> allowedEndUserRoles) throws Exception
    {
        if (userContext == null)
            throw new NotAuthorizedException("Could not find userContext");

        if (!isAllowedIn(userContext.getEndUserRole(), allowedEndUserRoles))
            throw new NotAuthorizedException("EndUserRole not allowed");
    }

    private static boolean isAllowedIn(EndUserRole userEndUserRole, List<EndUserRole> allowedEndUserRoles)
    {
        for (EndUserRole allowedEndUserRole : allowedEndUserRoles)
            if (userEndUserRole.isGreaterThanOrEqualTo(allowedEndUserRole))
                return true;

        return false;
    }
}
