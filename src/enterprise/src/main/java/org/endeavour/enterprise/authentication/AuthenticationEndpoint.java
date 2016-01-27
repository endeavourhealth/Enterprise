package org.endeavour.enterprise.authentication;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.endeavour.enterprise.model.Credentials;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserInRole;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Path("/authentication")
public class AuthenticationEndpoint
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/authenticateUser")
    @Unsecured
    public Response authenticateUser(Credentials credentials)
    {
        try
        {
            AuthenticationData authenticationData = new AuthenticationData();

            if (!authenticationData.areCredentialsValid(credentials))
                throw new NotAuthorizedException("Invalid credentials");

            User user = authenticationData.getUser(credentials.getUsername());

            if (user == null)
                throw new NotAuthorizedException("User not found");

            if (user.getInitialUserInRole() == null)
                throw new NotAuthorizedException("InitialUserInRole not found");

            String token = createToken(user, user.getInitialUserInRole());

            NewCookie cookie = createCookie(token);

            return Response
                    .ok()
                    .entity(user)
                    .cookie(cookie)
                    .build();
        }
        catch (NotAuthorizedException e)
        {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        catch (Exception e)
        {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private NewCookie createCookie(String token)
    {
        return new NewCookie(AuthenticationConstants.COOKIE_NAME,
                token,
                AuthenticationConstants.COOKIE_VALID_PATH,
                AuthenticationConstants.COOKIE_VALID_DOMAIN,
                1,
                null,
                -1,
                null,
                AuthenticationConstants.COOKIE_REQUIRES_HTTPS,
                true);
    }

    private String createToken(User user, UserInRole userInRole)
    {
        Map<String, Object> bodyParameterMap = new HashMap<>();
        bodyParameterMap.put(AuthenticationConstants.TOKEN_USER, user.getUserUuid());
        bodyParameterMap.put(AuthenticationConstants.TOKEN_ORGANISATION, userInRole.getOrganisationUuid());
        bodyParameterMap.put(AuthenticationConstants.TOKEN_ROLE, userInRole.getRole().name());
        bodyParameterMap.put(AuthenticationConstants.TOKEN_ISSUED_AT, Long.toString(Instant.now().getEpochSecond()));

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam(AuthenticationConstants.TOKEN_TYPE, AuthenticationConstants.TOKEN_TYPE_JWT)
                .setClaims(bodyParameterMap)
                .signWith(SignatureAlgorithm.HS256, AuthenticationConstants.TOKEN_SIGNING_SECRET);

        return builder.compact();
    }
}