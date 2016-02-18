package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbFolder;
import org.endeavour.enterprise.entity.json.JsonFolder;
import org.endeavour.enterprise.entity.json.JsonOrganisation;
import org.endeavour.enterprise.framework.security.Unsecured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 * Endpoint for the functions related to managing person and organisation entities
 */
@Path("/admin")
public class AdminEndpoint extends Endpoint {


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createOrganisation")
    @Unsecured
    public Response createOrganisation(JsonOrganisation organisationParameters) throws Throwable
    {
        /*//get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken();

        //get the parameters out
        String newFolderName = folderParameters.getFolderName();
        UUID parentUuid = folderParameters.getParentFolderUuid();

        //first ensure that there doesn't already exist a folder for that name
        DbFolder existingFolder = DbFolder.retrieveForOrganisationTitleParent(orgUuid, newFolderName, parentUuid);
        if (existingFolder != null)
        {
            throw new BadRequestException("Folder already exists");
        }

        //now validate that the parent folder actually exists
        if (parentUuid != null)
        {
            DbFolder parent = DbFolder.retrieveForUuid(parentUuid);
            if (parent == null)
            {
                throw new BadRequestException("Parent folder doesn't exist");
            }

            UUID existingOrganisationUuid = parent.getOrganisationUuid();
            if (!existingOrganisationUuid.equals(orgUuid)) {
                throw new BadRequestException("Parent folder is for different organisation");
            }
        }

        //create the new folder
        DbFolder f = new DbFolder();
        f.setTitle(newFolderName);
        f.setParentFolderUuid(parentUuid);
        f.setOrganisationUuid(orgUuid);

        //save
        f.saveToDb();

        return Response.ok().build();*/

        return null;
    }
}
