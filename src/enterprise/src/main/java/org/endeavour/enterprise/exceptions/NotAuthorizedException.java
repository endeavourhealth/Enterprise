package org.endeavour.enterprise.exceptions;

public class NotAuthorizedException extends Exception
{
    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String message) {
        super(message);
    }

}
