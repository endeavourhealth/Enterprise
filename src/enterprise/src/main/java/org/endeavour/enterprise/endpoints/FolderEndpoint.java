package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbAbstractTable;
import org.endeavour.enterprise.entity.database.DbFolder;
import org.endeavour.enterprise.entity.database.DbFolderItemLink;
import org.endeavour.enterprise.entity.json.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
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
    @Path("/saveFolder")
    public Response saveFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception
    {
        //get the parameters out
        UUID folderUuid = folderParameters.getUuid();
        String folderName = folderParameters.getFolderName();
        Integer folderType = folderParameters.getFolderType();
        UUID parentUuid = folderParameters.getParentFolderUuid();

        //validate the minimum requirements
        if (folderName == null || folderName.length() == 0)
        {
            throw new BadRequestException("Missing or empty folder name");
        }
        if (folderType == null)
        {
            throw new BadRequestException("Missing folder type");
        }

        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        DbFolder f = null;

        //if we have no UUID then we're creating a new folder
        if (folderUuid == null)
        {
            //validate name isn't a duplicate
            DbFolder existingFolder = DbFolder.retrieveForOrganisationTitleParentType(orgUuid, folderName, parentUuid, folderType);
            if (existingFolder != null)
            {
                throw new BadRequestException("Folder already exists");
            }

            //validate parent is at same org
            if (parentUuid != null)
            {
                DbFolder parent = DbFolder.retrieveForUuid(parentUuid);
                if (parent != null
                    || !parent.getOrganisationUuid().equals(orgUuid))
                {
                    throw new BadRequestException("Invalid or missing parent folder");
                }
            }

            //create the new folder
            f = new DbFolder();
            f.setTitle(folderName);
            f.setFolderType(folderType);
            f.setParentFolderUuid(parentUuid);
            f.setOrganisationUuid(orgUuid);

        }
        //if we have a UUID then we're updating an existing folder
        else
        {
            f = getFolderForUuidAndValidateOrganisation(sc, folderUuid);

            String existingName = f.getTitle();
            int existingType = f.getFolderType();
            UUID existingParentUuid = f.getParentFolderUuid();

            //we don't permit changing of a folder type
            if (existingType != folderType.intValue())
            {
                throw new BadRequestException("Folder already exists");
            }

            //validate if there's a duplicate folder with our new name/parent
            DbFolder existingFolder = DbFolder.retrieveForOrganisationTitleParentType(orgUuid, folderName, parentUuid, folderType);
            if (existingFolder != null
                    && !existingFolder.equals(f))
            {
                throw new BadRequestException("Folder already exists");
            }

            //if we're changing the parent UUID to a non-null value, we need to validate that we're not
            //moving the folder to be a child of itself
            if (existingParentUuid != parentUuid
                    && parentUuid != null)
            {
                UUID nextParent = parentUuid;
                while (nextParent != null)
                {
                    DbFolder parentFolder = DbFolder.retrieveForUuid(nextParent);
                    if (parentFolder.equals(f))
                    {
                        throw new BadRequestException("Cannot make a folder a child of itself");
                    }

                    nextParent = parentFolder.getParentFolderUuid();
                }
            }

            //set the new parameters in the folder
            f.setTitle(folderName);
            f.setParentFolderUuid(parentUuid);
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

        //save
        f.saveToDb();

        //return the UUID of the folder we just saved or updated
        JsonFolder ret = new JsonFolder();
        ret.setUuid(f.getPrimaryUuid());

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    /* @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createFolder")
    public Response createFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception
    {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken(sc);

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
    public Response renameFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception
    {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //get the parameters out
        UUID folderUuid = folderParameters.getUuid();
        String newFolderName = folderParameters.getFolderName();

        System.out.println("Renaming folder " + folderUuid + " to [" + newFolderName + "]");

        DbFolder folder = getFolderForUuidAndValidateOrganisation(sc, folderUuid);

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
    public Response moveFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception
    {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //get the parameters out
        UUID folderUuid = folderParameters.getUuid();
        UUID newParentFolderUuid = folderParameters.getParentFolderUuid();

        //System.out.println("Moving folder " + folderUuid + " to [" + newParentFolderUuid + "]");

        DbFolder folder = getFolderForUuidAndValidateOrganisation(sc, folderUuid);

        //ensure we're not going to create a duplicate with the move
        String folderName = folder.getTitle();
        DbFolder duplicate = DbFolder.retrieveForOrganisationTitleParent(orgUuid, folderName, newParentFolderUuid);
        if (duplicate != null)
        {
            throw new BadRequestException("Folder with that name already exists in new parent folder");
        }

        //TODO: 2016-02-22 DL - validate that we're not moving a folder to be a child of itself

        //update the entity
        folder.setParentFolderUuid(newParentFolderUuid);

        //save
        folder.saveToDb();

        return Response.ok().build();
    }
*/
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteFolder")
    public Response deleteFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception {
        //get the organisation from the server token
        //UUID orgUuid = getOrganisationUuidFromToken(sc);

        //get the parameters out
        UUID folderUuid = folderParameters.getUuid();

        DbFolder folder = getFolderForUuidAndValidateOrganisation(sc, folderUuid);
        if (folder == null)
        {
            throw new BadRequestException("No folder for UUID");
        }

        //delete the lot
        deleteFolderAndContents(folder);

        return Response.ok().build();
    }
    private static void deleteFolderAndContents(DbFolder folder) throws Exception
    {
        //see if we have any child folders, which we should delete first
        UUID orgUuid = folder.getOrganisationUuid();
        UUID parentUuid = folder.getPrimaryUuid();
        int folderType = folder.getFolderType();
        List<DbAbstractTable> childFolders = DbFolder.retrieveForOrganisationParentType(orgUuid, parentUuid, folderType);
        for (int i=0; i<childFolders.size(); i++)
        {
            DbFolder childFolder = (DbFolder)childFolders.get(i);
            deleteFolderAndContents(childFolder);
        }

        //retrieve the link entities for the folder and delete them
        UUID folderUuid = folder.getPrimaryUuid();
        List<DbAbstractTable> links = DbFolderItemLink.retrieveForFolder(folderUuid);
        for (int i=0; i<links.size(); i++)
        {
            DbFolderItemLink link = (DbFolderItemLink)links.get(i);
            link.deleteFromDb();

            //TODO: 2016-02-22 DL - actually delete item after delting folderItemLink
        }

        //now our folder is empty
        folder.deleteFromDb();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolders")
    public Response getFolders(@Context SecurityContext sc, @QueryParam("folderType") int folderType, @QueryParam("parentUuid") String uuidStr) throws Exception
    {
        //TODO: 2016-02-22 DL - this would be a lot more efficient to join Folder and FolderItemLink in SQL
        UUID uuid = null;
        if (uuidStr != null
                && uuidStr.length() > 0)
        {
            uuid = UUID.fromString(uuidStr);
        }

        //get all our folders at the desired level
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        List<DbAbstractTable> folders = DbFolder.retrieveForOrganisationParentType(orgUuid, uuid, folderType);
        JsonFolderList ret = new JsonFolderList(folders.size());

        for (int i=0; i<folders.size(); i++)
        {
            DbFolder folder = (DbFolder)folders.get(i);
            UUID folderUuid = folder.getPrimaryUuid();
            List<DbAbstractTable> items = DbFolderItemLink.retrieveForFolder(folderUuid);
            int contentCount = items.size();

            ret.add(folder, contentCount);
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }
/*    public Response getFolders(@Context SecurityContext sc, @PathParam("folderType") String folderType, @PathParam("parentUuid") String id) throws Exception
    {
        //get all our folders
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        List<DbAbstractTable> folders = DbFolder.retrieveForOrganisation(orgUuid);
        JsonFolderList ret = new JsonFolderList(folders.size());

        for (int i=0; i<folders.size(); i++)
        {
            DbFolder folder = (DbFolder)folders.get(i);
            UUID folderUuid = folder.getPrimaryUuid();
            List<DbAbstractTable> items = DbFolderItemLink.retrieveForFolder(folderUuid);
            int contentCount = items.size();

            ret.add(folder, contentCount);
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }*/

    /**
     * several of our functions perform the same checks, so refactored out to here
     */
    private DbFolder getFolderForUuidAndValidateOrganisation(SecurityContext sc, UUID folderUuid) throws Exception
    {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken(sc);

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolderContents")
    public Response getFolderContents(@Context SecurityContext sc, @QueryParam("folderUuid") String uuidStr) throws Exception
    {
        UUID folderUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //retrieve the folder and validate it's for our org
        DbFolder folder = DbFolder.retrieveForUuid(folderUuid);
        if (!folder.getOrganisationUuid().equals(orgUuid))
        {
            throw new BadRequestException("Folder at different organisation");
        }

        JsonFolderContentsList ret = new JsonFolderContentsList();

        //TODO: 2016-02-23 DL - need proper implementation of getting folder contents

        /*List<DbAbstractTable> items = DbFolderItemLink.retrieveForFolder(folderUuid);
        for (int i=0; i<items.size(); i++)
        {
            DbFolderItemLink item = (DbFolderItemLink)items.get(i);
        }*/

        if (folder.getFolderType() == DbFolder.FOLDER_TYPE_REPORTS)
        {
            JsonReport r = new JsonReport();
            r.setName("Report 1");
            r.setUuid(UUID.randomUUID());
            ret.addReport(r);

            r = new JsonReport();
            r.setName("Report 2");
            r.setUuid(UUID.randomUUID());
            ret.addReport(r);

            r = new JsonReport();
            r.setName("Report 3");
            r.setUuid(UUID.randomUUID());
            ret.addReport(r);
        }
        else if (folder.getFolderType() == DbFolder.FOLDER_TYPE_LIBRARY)
        {
            JsonListOutput l = new JsonListOutput();
            l.setName("List Output 1");
            l.setUuid(UUID.randomUUID());
            ret.addListOutput(l);

            l = new JsonListOutput();
            l.setName("List Output 2");
            l.setUuid(UUID.randomUUID());
            ret.addListOutput(l);

            l = new JsonListOutput();
            l.setName("List Output 3");
            l.setUuid(UUID.randomUUID());
            ret.addListOutput(l);

            JsonQuery q = new JsonQuery();
            q.setName("Query 1");
            q.setUuid(UUID.randomUUID());
            ret.addQuery(q);

            q = new JsonQuery();
            q.setName("Query 2");
            q.setUuid(UUID.randomUUID());
            ret.addQuery(q);

            q = new JsonQuery();
            q.setName("Query 3");
            q.setUuid(UUID.randomUUID());
            ret.addQuery(q);
        }
        else
        {
            throw new BadRequestException("Unsupported folder type");
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }
}
