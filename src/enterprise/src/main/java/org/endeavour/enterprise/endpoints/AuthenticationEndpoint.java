package org.endeavour.enterprise.endpoints;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.endeavour.enterprise.exceptions.NotAuthorizedException;
import org.endeavour.enterprise.model.Credentials;

import javax.ws.rs.*;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/authentication")
public class AuthenticationEndpoint {

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response authenticateUser(Credentials credentials) {

        try
        {
            if (!authenticate(credentials.getUsername(), credentials.getPassword()))
                throw new NotAuthorizedException();

            String token = createToken(credentials.getUsername());

            return Response.ok().cookie(new NewCookie("cookieName", token)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private boolean authenticate(String username, String password) {
        return (username.equals("admin") && password.equals("1234"));

    }

    private String createToken(String username) {

        String secret = "DLKV342nNaCapGgSieNde18OFRYwg3etCabRfsPcrnc=";

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long ttlMillis = 100000;

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(username)
                .setExpiration(new Date(nowMillis + ttlMillis))
                .signWith(SignatureAlgorithm.HS256, secret);

        return builder.compact();
    }
}