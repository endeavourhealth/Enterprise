package org.endeavour.enterprise.authentication;

import org.endeavour.enterprise.model.Token;

import java.security.Principal;

public class SecurityContext implements javax.ws.rs.core.SecurityContext
{
    public SecurityContext(Token token)
    {

    }

    @Override
    public Principal getUserPrincipal()
    {
        return new UserPrincipal();
    }

    @Override
    public boolean isUserInRole(String role)
    {
        return true;
    }

    @Override
    public boolean isSecure()
    {
        return false;
    }

    @Override
    public String getAuthenticationScheme()
    {
        return null;
    }
}
