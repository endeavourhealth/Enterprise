package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.data.AdministrationData;
import org.endeavour.enterprise.entity.database.DbFolder;
import org.endeavour.enterprise.entity.json.JsonFolder;
import org.endeavour.enterprise.framework.exceptions.*;
import org.endeavour.enterprise.framework.security.PasswordHash;
import org.endeavour.enterprise.framework.security.TokenHelper;
import org.endeavour.enterprise.framework.security.Unsecured;
import org.endeavour.enterprise.model.Credentials;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserContext;

import javax.ws.rs.*;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 * Endpoint for functions related to creating and managing folders
 */
@Path("/folder")
public class FolderEndpoint extends Endpoint {


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createFolder")
    public Response createFolder(JsonFolder folderParameters) throws Throwable
    {
        //get the organisation from the server token
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

        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/renameFolder")
    public Response renameFolder(JsonFolder folderParameters) throws Throwable
    {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken();

        //get the parameters out
        UUID folderUuid = folderParameters.getFolderUuid();
        String newFolderName = folderParameters.getFolderName();

        System.out.println("Renaming folder " + folderUuid + " to [" + newFolderName + "]");

        DbFolder folder = getFolderForUuidAndValidateOrganisation(folderUuid);

        //ensure we're not renaming to a name that already exists
        UUID parentFolderUuid = folder.getParentFolderUuid();
        DbFolder duplicate = DbFolder.retrieveForOrganisationTitleParent(orgUuid, newFolderName, parentFolderUuid);
        if (duplicate != null)
        {
            throw new BadRequestException("Folder with that name already exists");
        }

        //update the entity
        folder.setTitle(newFolderName);

        //save
        folder.saveToDb();

        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/moveFolder")
    public Response moveFolder(JsonFolder folderParameters) throws Throwable
    {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken();

        //get the parameters out
        UUID folderUuid = folderParameters.getFolderUuid();
        UUID newParentFolderUuid = folderParameters.getParentFolderUuid();

        System.out.println("Moving folder " + folderUuid + " to [" + newParentFolderUuid + "]");

        DbFolder folder = getFolderForUuidAndValidateOrganisation(folderUuid);

        //ensure we're not going to create a duplicate with the move
        String folderName = folder.getTitle();
        DbFolder duplicate = DbFolder.retrieveForOrganisationTitleParent(orgUuid, folderName, newParentFolderUuid);
        if (duplicate != null)
        {
            throw new BadRequestException("Folder with that name already exists in new parent folder");
        }

        //update the entity
        folder.setParentFolderUuid(newParentFolderUuid);

        //save
        folder.saveToDb();

        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteFolder")
    public Response deleteFolder(JsonFolder folderParameters) throws Exception
    {
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolders")
    public Response getFolders(JsonFolder folderParameters) throws Exception
    {
        return null;
    }

    /**
     * several of our functions perform the same checks, so refactored out to here
     */
    private DbFolder getFolderForUuidAndValidateOrganisation(UUID folderUuid) throws Throwable
    {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken();

        //ensure the folder actually exists
        DbFolder folder = DbFolder.retrieveForUuid(folderUuid);
        if (folder == null)
        {
            throw new BadRequestException("Folder doesn't exist");
        }

        //ensure it's for the right organisation
        UUID folderOrgUuid = folder.getOrganisationUuid();
        if (!folderOrgUuid.equals(orgUuid))
        {
            throw new BadRequestException("Folder belongs to different organisation");
        }

        return folder;
    }

}
