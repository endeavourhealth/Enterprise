package org.endeavour.enterprise.framework.security;

import org.endeavour.enterprise.framework.exceptions.NotAuthorizedException;

public final class UserAuthenticationFilter extends AbstractAuthenticationFilter {

    @Override
    public void doSpecificAuthoriationCheck(UserContext cx) throws NotAuthorizedException {
        //this implementation has no extra checks other than having a signed on user
    }
}
