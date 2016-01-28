package org.endeavour.enterprise.framework.authentication;

public class NotAuthorizedException extends Exception
{
    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String message) {
        super(message);
    }

}
