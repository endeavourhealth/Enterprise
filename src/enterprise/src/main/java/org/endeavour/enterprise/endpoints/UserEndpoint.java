package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AuthenticationData;
import org.endeavour.enterprise.framework.security.Roles;
import org.endeavour.enterprise.framework.security.Unsecured;
import org.endeavour.enterprise.model.Role;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserContext;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserEndpoint extends Endpoint
{
	//
	// Example unsecure GET
	// (uses the @Unsecured attribute)
	//
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Unsecured
	public Response getUnsecured()
	{
		User user = new AuthenticationData().getUser("david.stables@endeavourhealth.org");

		return Response
				.ok(user)
				.build();
	}

	//
	// Example secure GET
	// (absence of @Unsecured attribute)
	//
	// And use of context in token
	// (Endpoint.getUserContext())
	//
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/secured")
	public Response getSecured()
	{
		UserContext context = this.getUserContext();

		User user = new AuthenticationData().getUser(context.getUserUuid());

		return Response
				.ok(user)
				.build();
	}

	//
	// Example secure GET with restriction to role of ADMIN
	// (absence of @Unsecured attribute)
	// (and use of @Roles({Role.ADMIN}))
	//
	// And use of context in token
	// (Endpoint.getUserContext())
	//
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/securedAdmin")
	@Roles({Role.ADMIN})
	public Response getSecuredWithAdminRole()
	{
		UserContext context = this.getUserContext();

		User user = new AuthenticationData().getUser(context.getUserUuid());

		return Response
				.ok(user)
				.build();
	}

	//
	// Example secure POST
	// (absence of @Unsecured attribute)
	//
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(User user)
	{
		try
		{
			if (user == null)
			{
				return Response
						.status(Response.Status.BAD_REQUEST)
						.build();
			}

			// save user

			return Response
					.status(Response.Status.CREATED)
					.build();

		}
		catch (Exception e)
		{
			return Response
					.status(Response.Status.SERVICE_UNAVAILABLE)
					.build();
		}
	}
}
