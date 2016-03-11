package org.endeavour.enterprise.framework.exceptions;

import javax.ws.rs.core.Response;

public class NotAuthorizedException extends MappedException {
    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String message) {
        super(message);
    }

    @Override
    public Response.Status getResponseStatus() {
        return Response.Status.UNAUTHORIZED;
    }
}
