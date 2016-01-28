package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AuthenticationData;
import org.endeavour.enterprise.framework.authentication.Unsecured;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserContext;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserEndpoint extends Endpoint
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Unsecured
	public Response get()
	{
		User user = new AuthenticationData().getUser("david.stables@endeavourhealth.org");

		return Response
				.ok(user)
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/secured")
	public Response getSecured()
	{
		UserContext context = this.getUserContext();

		return get();
	}

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
