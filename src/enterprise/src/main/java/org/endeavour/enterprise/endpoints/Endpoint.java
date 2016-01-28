package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.framework.authentication.UserPrincipal;
import org.endeavour.enterprise.model.UserContext;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public abstract class Endpoint
{
    @Context
    protected SecurityContext securityContext;

    protected UserContext getUserContext()
    {
        if (securityContext != null)
            if (securityContext.getUserPrincipal() != null)
                if (UserPrincipal.class.isInstance(securityContext.getUserPrincipal()))
                    return ((UserPrincipal)securityContext.getUserPrincipal()).getUserContext();

        return null;
    }
}
