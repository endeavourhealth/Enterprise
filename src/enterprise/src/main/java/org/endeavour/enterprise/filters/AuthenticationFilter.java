package org.endeavour.enterprise.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.ws.rs.core.HttpHeaders;

public class AuthenticationFilter implements ContainerRequestFilter
{
    @Override
    public ContainerRequest filter(ContainerRequest containerRequest)
    {
        // Get the HTTP Authorization header from the request
        String authorizationHeader =
                containerRequest.getHeaderValue(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly
//        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//            throw new NotAuthorizedException("Authorization header must be provided");
//        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

//        try {
//
//            // Validate the token
//            validateToken(token);
//
//        } catch (Exception e) {
//            requestContext.abortWith(
//                    Response.status(Response.Status.UNAUTHORIZED).build());
//        }

        return null;
    }

    private void validateToken(String token) throws Exception {
    // Check if it was issued by the server and if it's not expired
    // Throw an Exception if the token is invalid
    }
}
