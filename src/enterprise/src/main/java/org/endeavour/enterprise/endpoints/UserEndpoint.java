package org.endeavour.enterprise.endpoints;

import javax.ws.rs.*;

@Path("/user")
public class UserEndpoint extends Endpoint
{
	//2016-02-22 DL - removed these fns, since replaced with fns in SecurityEndpoint
	/*
	//
	// Example unsecure GET
	// (uses the @Unsecured attribute)
	//
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Unsecured
	public Response getUnsecured()
	{
		User user = new AdministrationData().getUser("david.stables@endeavourhealth.org");

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

		User user = new AdministrationData().getUser(context.getUserUuid());

		return Response
				.ok(user)
				.build();
	}

	//
	// Example secure GET with restriction to endUserRole of ADMIN
	// (absence of @Unsecured attribute)
	// (and use of @Roles({EndUserRole.ADMIN}))
	//
	// And use of context in token
	// (Endpoint.getUserContext())
	//
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/securedAdmin")
	@Roles({EndUserRole.ADMIN})
	public Response getSecuredWithAdminRole()
	{
		UserContext context = this.getUserContext();

		User user = new AdministrationData().getUser(context.getUserUuid());

		return Response
				.ok(user)
				.build();
	}

	//
	// Example secure GET with restriction to endUserRole of SUPER
	// (absence of @Unsecured attribute)
	// (and use of @Roles({EndUserRole.SUPER}))
	//
	// And use of context in token
	// (Endpoint.getUserContext())
	//
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/securedSuper")
	@Roles({EndUserRole.SUPER})
	public Response getSecuredWithSuperRole()
	{
		UserContext context = this.getUserContext();

		User user = new AdministrationData().getUser(context.getUserUuid());

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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getUserList")
	public Response getUserList()
	{
		List<UserSummary> userSummaryList = new AdministrationData().getUsers()
				.stream()
				.map(UserSummary::createFromUser)
				.collect(Collectors.toList());

		return Response
				.ok(userSummaryList)
				.build();
	}
	*/
}
