package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AdminData;
import org.endeavour.enterprise.framework.exceptions.InvalidParameterException;
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
		User user = new AdminData().getUser("david.stables@endeavourhealth.org");

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

		User user = new AdminData().getUser(context.getUserUuid());

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

		User user = new AdminData().getUser(context.getUserUuid());

		return Response
				.ok(user)
				.build();
	}

	//
	// Example secure GET with restriction to role of SUPER
	// (absence of @Unsecured attribute)
	// (and use of @Roles({Role.SUPER}))
	//
	// And use of context in token
	// (Endpoint.getUserContext())
	//
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/securedSuper")
	@Roles({Role.SUPER})
	public Response getSecuredWithSuperRole()
	{
		UserContext context = this.getUserContext();

		User user = new AdminData().getUser(context.getUserUuid());

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
	public Response post(User user) throws InvalidParameterException
	{
		if (user == null)
			throw new InvalidParameterException("user");

		// save user

		return Response
				.status(Response.Status.CREATED)
				.build();
	}
}
