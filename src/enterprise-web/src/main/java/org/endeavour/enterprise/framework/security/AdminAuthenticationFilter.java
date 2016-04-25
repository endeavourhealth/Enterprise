package org.endeavour.enterprise.framework.security;

import org.endeavour.enterprise.framework.exceptions.NotAuthorizedException;

public final class AdminAuthenticationFilter extends AbstractAuthenticationFilter {

    @Override
    public void doSpecificAuthoriationCheck(UserContext cx) throws NotAuthorizedException {
        if (!cx.isAdmin()) {
            throw new NotAuthorizedException("Requires admin permissions");
        }
    }
}
