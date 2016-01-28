package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.model.UserContext;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public abstract class Endpoint
{
    @Context
    protected SecurityContext securityContext;

    protected UserContext getUserContext()
    {
        return UserContext.fromSecurityContext(securityContext);
    }
}
