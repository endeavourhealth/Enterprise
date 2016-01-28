package org.endeavour.enterprise.framework.security;

public class NotAuthorizedException extends Exception
{
    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String message) {
        super(message);
    }

}
