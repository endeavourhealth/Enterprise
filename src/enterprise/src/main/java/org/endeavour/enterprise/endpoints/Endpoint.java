package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.model.UserContext;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.UUID;

public abstract class Endpoint
{
    @Context
    protected SecurityContext securityContext;

    protected UserContext getUserContext()
    {
        return UserContext.fromSecurityContext(securityContext);
    }


    protected UUID getOrganisationUuidFromToken() throws Exception
    {
               /* UserContext context = this.getUserContext();
        UUID orgUuid = context.getOrganisationUuid();*/


        UUID orgUuid = UUID.randomUUID();
        return orgUuid;
    }
}
