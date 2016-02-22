package org.endeavour.enterprise.framework.exceptions;

import org.endeavour.enterprise.entity.json.JsonServerException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.WebApplicationException;

@Provider
public class BaseExceptionMapper implements ExceptionMapper<Exception>
{
    /**
     * 2016-02-17 DL - rewriting to be a bit more concise
     * and also to pass the exception message back with the error code
     */
    public Response toResponse(Exception exception)
    {
        Response.ResponseBuilder r = null;

        //if the exception is one of our own exception objects
        if (exception instanceof MappedException)
        {
            MappedException me = (MappedException)exception;
            r = Response.status(me.getResponseStatus());
        }
        //if the exception is a web service application
        else if (exception instanceof WebApplicationException)
        {
            WebApplicationException we = (WebApplicationException)exception;
            r = Response.status(we.getResponse().getStatus());
        }
        //if it's some other kind of exception (e.g. SQLException) that's got this far
        else
        {
            //log on the server too, since these are unexpected
            exception.printStackTrace(System.err);

            r = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        //if our exception has a message, then send this in the reponse too
        String message = exception.getMessage();
        if (message != null)
        {
            JsonServerException wrapper = new JsonServerException(message);
            r = r.entity(wrapper);
        }


        return r.build();
    }
    /*public Response toResponse(Exception exception)
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
    }*/
}
