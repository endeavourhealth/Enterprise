package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AdministrationData;
import org.endeavour.enterprise.data.DefinitionData;
import org.endeavour.enterprise.framework.exceptions.InvalidParameterException;
import org.endeavour.enterprise.framework.security.Roles;
import org.endeavour.enterprise.framework.security.Unsecured;
import org.endeavour.enterprise.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/definition")
public class DefinitionEndpoint extends Endpoint
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getRootFolders")
	public Response getRootFolders(RootFolderRequest request) throws Exception
	{
		List<Folder> folderList = new DefinitionData()
				.getRootFolders(
						request.getOrganisationUuid(),
						Module.valueOf(request.getModuleId())
				);

		return Response
				.ok(folderList)
				.build();
	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getChildFolders")
	public Response getChildFolders(UUID itemUuid) throws Exception
	{
		List<Folder> folderList = new DefinitionData().getChildFolders(itemUuid);

		return Response
				.ok(folderList)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getFolderContents")
	public Response getFolderContents(UUID folderId) throws Exception
	{
		List<DefinitionItemSummary> itemSummaryList = new DefinitionData().getFolderContents(folderId);

		return Response
				.ok(itemSummaryList)
				.build();
	}
}
