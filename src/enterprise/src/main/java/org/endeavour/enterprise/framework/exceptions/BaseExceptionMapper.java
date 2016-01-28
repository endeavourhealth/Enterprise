package org.endeavour.enterprise.framework.exceptions;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BaseExceptionMapper implements ExceptionMapper<Exception>
{
    public Response toResponse(Exception exception)
    {
        if (MappedException.class.isInstance(exception))
        {
            return Response
                    .status(((MappedException)exception).getResponseStatus())
                    .build();
        }
        else if (javax.ws.rs.WebApplicationException.class.isInstance(exception))
        {
            return Response
                    .status(((WebApplicationException)exception).getResponse().getStatus())
                    .build();
        }
        else
        {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
