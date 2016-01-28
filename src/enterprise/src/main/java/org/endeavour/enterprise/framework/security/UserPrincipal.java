package org.endeavour.enterprise.framework.security;

import org.endeavour.enterprise.model.UserContext;

import java.security.Principal;

public class UserPrincipal implements Principal
{
    private UserContext userContext;

    public UserPrincipal(UserContext userContext)
    {
        this.userContext = userContext;
    }

    @Override
    public String getName()
    {
        return null;
    }

    public UserContext getUserContext()
    {
        return userContext;
    }
}
