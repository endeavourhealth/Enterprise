package org.endeavour.enterprise.helpers;

import javax.ws.rs.core.Response;

public class ResponseBuilder
{
    public static Response build(final Response.Status statusCode) {
        return Response.status(statusCode).build();
    }

    public static Response build(final Response.Status statusCode, final Object entity) {
        return Response
                .status(statusCode)
                .entity(entity)
                .build();
    }

}
