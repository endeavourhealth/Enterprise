package org.endeavour.enterprise.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BaseExceptionMapper implements ExceptionMapper<Exception> {

    public Response toResponse(Exception exception) {
        return Response
                .status(javax.ws.rs.core.Response.Status.BAD_REQUEST)
                .build();
    }
}
