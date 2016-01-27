package org.endeavour.enterprise.authentication;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.endeavour.enterprise.model.Credentials;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserInRole;

import javax.ws.rs.*;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Path("/authentication")
public class AuthenticationEndpoint
{
    // get from database and place in cache
    final String COOKIE_NAME = "enterprise.endeavourhealth.org/authentication";
    final String COOKIE_VALID_DOMAIN = "127.0.0.1";
    final boolean COOKIE_REQUIRES_HTTPS = false;
    final int COOKIE_MAX_AGE_SECONDS = 60 * 60 * 18;
    final String TOKEN_SIGNING_SECRET = "DLKV342nNaCapGgSieNde18OFRYwg3etCabRfsPcrnc=";

    @GET
    @Produces("application/json")
    @Consumes("application/json")
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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, COOKIE_MAX_AGE_SECONDS);
        Date expiryDate = calendar.getTime();

        return new NewCookie(COOKIE_NAME, token, "/", COOKIE_VALID_DOMAIN, 1, null, COOKIE_MAX_AGE_SECONDS, expiryDate, COOKIE_REQUIRES_HTTPS, true);
    }

    private String createToken(User user, UserInRole userInRole)
    {
        Map<String, Object> bodyParameterMap = new HashMap<>();
        bodyParameterMap.put("usr", user.getUserUuid());
        bodyParameterMap.put("org", userInRole.getOrganisationUuid());
        bodyParameterMap.put("rol", userInRole.getRole().name());

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setClaims(bodyParameterMap)
                .signWith(SignatureAlgorithm.HS256, TOKEN_SIGNING_SECRET);

        return builder.compact();
    }
}