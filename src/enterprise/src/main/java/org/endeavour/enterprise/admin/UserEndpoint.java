package org.endeavour.enterprise.admin;

import org.endeavour.enterprise.authentication.Unsecured;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserInRole;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/user")
public class UserEndpoint {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Unsecured
	public Response get() {
		User user = new User();
		user.setTitle("Dr");
		user.setForename("David");
		user.setSurname("Stables");
		user.setEmail("david.stables@endeavourhealth.org");
		user.setInitialUserInRoleUuid(UUID.randomUUID());

		UserInRole uir = new UserInRole();
		uir.setUserInRoleUuid(user.getInitialUserInRoleUuid());
		uir.setOrganisationName("Organisation 1");
		user.addUserInRole(uir);

		uir = new UserInRole();
		uir.setUserInRoleUuid(UUID.randomUUID());
		uir.setOrganisationName("Organisation 2");
		user.addUserInRole(uir);

		uir = new UserInRole();
		uir.setUserInRoleUuid(UUID.randomUUID());
		uir.setOrganisationName("Organisation 3");
		user.addUserInRole(uir);

		return Response
				.ok(user)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(User user) {
		try {
			if (user == null) {
				return Response
						.status(Response.Status.BAD_REQUEST)
						.build();
			}

			// save user

			return Response
					.status(Response.Status.CREATED)
					.build();

		} catch (Exception e) {
			return Response
					.status(Response.Status.SERVICE_UNAVAILABLE)
					.build();
		}
	}
}
