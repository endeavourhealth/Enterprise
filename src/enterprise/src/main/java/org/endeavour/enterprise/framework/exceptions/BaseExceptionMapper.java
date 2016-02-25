package org.endeavour.enterprise.framework.exceptions;

import org.endeavour.enterprise.entity.json.JsonServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class BaseExceptionMapper implements ExceptionMapper<Exception>
{
    private static final Logger LOG = LoggerFactory.getLogger(BaseExceptionMapper.class);

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
            //2016-02-24 DL - use logback now
            LOG.error(null, exception);

            r = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        //if our exception has a message, then send this in the response too
        //so even if there's an error, we can give some feedback as to what it is
        String message = exception.getMessage();
        if (message != null)
        {
            JsonServerException wrapper = new JsonServerException(message);
            r = r.entity(wrapper);
        }

        return r.build();
    }


    /**
     * TODO: 2016-02-25 DL - get the DB appender for log back working
     Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
     DBAppender appender = new DBAppender();
     appender.setConnectionSource();

     ConnectionSource cs = new Co
     *
     xxx 1) test logging exceptions
     xxx 4) what are the two XML files
     xxxx 5) delete one of the xml files
     6) move logback.xml to an existing folder


     2) get logging to db
     3) understand SQL timestamp?
     5) Add two xml files to repo
     6) commit to GitHub
     */
}
