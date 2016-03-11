package org.endeavour.enterprise.framework.exceptions;

import javax.ws.rs.core.Response;

public class InvalidParameterException extends MappedException {
    public InvalidParameterException() {
        super();
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    @Override
    public Response.Status getResponseStatus() {
        return Response.Status.BAD_REQUEST;
    }
}
