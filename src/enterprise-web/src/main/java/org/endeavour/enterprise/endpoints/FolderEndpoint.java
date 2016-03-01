package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.entity.json.JsonFolder;
import org.endeavour.enterprise.entity.json.JsonFolderContent;
import org.endeavour.enterprise.entity.json.JsonFolderContentsList;
import org.endeavour.enterprise.entity.json.JsonFolderList;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavour.enterprise.model.DependencyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 * Endpoint for functions related to creating and managing folders
 */
@Path("/folder")
public final class FolderEndpoint extends ItemEndpoint
{
    private static final Logger LOG = LoggerFactory.getLogger(FolderEndpoint.class);

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

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        //validate the minimum requirements
        if (folderName == null || folderName.length() == 0)
        {
            throw new BadRequestException("Missing or empty folder name");
        }
        if (folderType == null)
        {
            throw new BadRequestException("Missing folder type");
        }

        DefinitionItemType itemType = null;
        if (folderType == JsonFolder.FOLDER_TYPE_LIBRARY)
        {
            itemType = DefinitionItemType.LibraryFolder;
        }
        else if (folderType == JsonFolder.FOLDER_TYPE_REPORTS)
        {
            itemType = DefinitionItemType.ReportFolder;
        }
        else
        {
            throw new BadRequestException("Invalid folder type " + folderType);
        }

        LOG.trace("SavingFolder FolderUUID {}, FolderName {} FolderType {} ParentUUID {} ItemType {}", folderUuid, folderName, folderType, parentUuid, itemType);

        folderUuid = super.saveItem(folderUuid, orgUuid, userUuid, itemType, folderName, "", "", false, parentUuid);

        //return the UUID of the folder we just saved or updated
        JsonFolder ret = new JsonFolder();
        ret.setUuid(folderUuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    /*public Response saveFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception
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
    }*/

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteFolder")
    public Response deleteFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception
    {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        //get the parameters out
        UUID folderUuid = folderParameters.getUuid();

        //to delete it, we need to find out the item type
        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(folderUuid);
        DefinitionItemType itemType = activeItem.getItemType();
        if (itemType != DefinitionItemType.LibraryFolder
                && itemType != DefinitionItemType.ReportFolder)
        {
            throw new BadRequestException("UUID is a " + itemType + " not a folder");
        }

        deleteItem(folderUuid, orgUuid, userUuid, itemType);

        return Response.ok().build();
    }
    /*private static void deleteFolderAndContents(DbFolder folder) throws Exception
    {
        //see if we have any child folders, which we should delete first
        UUID orgUuid = folder.getOrganisationUuid();
        UUID parentUuid = folder.getPrimaryUuid();
        int folderType = folder.getFolderType();
        List<DbFolder> childFolders = DbFolder.retrieveForOrganisationParentType(orgUuid, parentUuid, folderType);
        for (int i=0; i<childFolders.size(); i++)
        {
            DbFolder childFolder = childFolders.get(i);
            deleteFolderAndContents(childFolder);
        }

        //retrieve the link entities for the folder and delete them
        UUID folderUuid = folder.getPrimaryUuid();
        List<DbFolderItemLink> links = DbFolderItemLink.retrieveForFolder(folderUuid);
        for (int i=0; i<links.size(); i++)
        {
            DbFolderItemLink link = links.get(i);
            link.deleteFromDb();

            //TODO: 2016-02-22 DL - actually delete item after delting folderItemLink
        }

        //now our folder is empty
        folder.deleteFromDb();
    }*/

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolders")
    public Response getFolders(@Context SecurityContext sc, @QueryParam("folderType") int folderType, @QueryParam("parentUuid") String parentUuidStr) throws Exception
    {
        //convert the nominal folder type to the actual Item DefinitionType
        DefinitionItemType itemType = null;
        if (folderType == JsonFolder.FOLDER_TYPE_LIBRARY)
        {
            itemType = DefinitionItemType.LibraryFolder;
        }
        else if (folderType == JsonFolder.FOLDER_TYPE_REPORTS)
        {
            itemType = DefinitionItemType.ReportFolder;
        }
        else
        {
            throw new BadRequestException("Invalid folder type " + folderType);
        }

        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("Getting folders under parent UUID {} and folderType {}, which is itemType {}", parentUuidStr, folderType, itemType);

        List<DbItem> items = null;

        //if we have no parent, then we're looking for the TOP-LEVEL folder
        if (parentUuidStr == null)
        {
            items = DbItem.retrieveNonDependentItems(orgUuid, DependencyType.IsChildOf, itemType);

            //if we don't have a top-level folder, for some reason, re-create it
            if (items.size() == 0)
            {
                UUID userUuid = getEndUserUuidFromToken(sc);
                FolderEndpoint.createTopLevelFolder(orgUuid, userUuid, itemType);

                //then re-run the select
                items = DbItem.retrieveNonDependentItems(orgUuid, DependencyType.IsChildOf, itemType);
            }
        }
        //if we have a parent, then we want the child folders under it
        else
        {
            UUID parentUuid = UUID.fromString(parentUuidStr);
            items = DbItem.retrieveDependentItems(orgUuid, parentUuid, DependencyType.IsChildOf);
        }

        LOG.trace("Found {} child folders", items.size());

        JsonFolderList ret = new JsonFolderList();

        for (int i=0; i<items.size(); i++)
        {
            DbItem item = items.get(i);
            UUID itemUuid = item.getPrimaryUuid();

            int childFolders = DbActiveItem.retrieveCountDependencies(itemUuid, DependencyType.IsChildOf);
            int contentCount = DbActiveItem.retrieveCountDependencies(itemUuid, DependencyType.IsContainedWithin);

            LOG.trace("Child folder {}, UUID {} has {} child folders and {} contents", item.getTitle(), item.getPrimaryUuid(), childFolders, contentCount);

            JsonFolder folder = new JsonFolder(item, contentCount, childFolders > 0);
            ret.add(folder);
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }
    public static void createTopLevelFolder(UUID organisationUuid, UUID userUuid, DefinitionItemType itemType) throws Exception
    {
        String title = null;
        if (itemType == DefinitionItemType.LibraryFolder)
        {
            title = "Library";
        }
        else if (itemType == DefinitionItemType.ReportFolder)
        {
            title = "Reports";
        }
        else
        {
            throw new RuntimeException("Trying to create folder for type " + itemType);
        }

        DbItem item = DbItem.factoryNew(userUuid, title);
        item.saveToDb();

        DbActiveItem activeItemReports = DbActiveItem.factoryNew(item, organisationUuid, itemType);
        activeItemReports.saveToDb();
    }
    /*public Response getFolders(@Context SecurityContext sc, @QueryParam("folderType") int folderType, @QueryParam("parentUuid") String uuidStr) throws Exception
    {
        UUID uuid = null;
        if (uuidStr != null
                && uuidStr.length() > 0)
        {
            uuid = UUID.fromString(uuidStr);
        }

        //get all our folders at the desired level
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        List<DbFolder> folders = DbFolder.retrieveForOrganisationParentType(orgUuid, uuid, folderType);
        JsonFolderList ret = new JsonFolderList(folders.size());

        for (int i=0; i<folders.size(); i++)
        {
            DbFolder folder = folders.get(i);
            UUID folderUuid = folder.getPrimaryUuid();
            List<DbFolderItemLink> items = DbFolderItemLink.retrieveForFolder(folderUuid);
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
    /*private DbFolder getFolderForUuidAndValidateOrganisation(SecurityContext sc, UUID folderUuid) throws Exception
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
    }*/

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolderContents")
    public Response getFolderContents(@Context SecurityContext sc, @QueryParam("folderUuid") String uuidStr) throws Exception
    {
        UUID folderUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //retrieve the folder and validate it's for our org
        DbItem folder = DbItem.retrieveForUuidLatestVersion(orgUuid, folderUuid);

        JsonFolderContentsList ret = new JsonFolderContentsList();

        LOG.trace("Getting folder contents for folder {}", folderUuid);

        List<DbActiveItem> childActiveItems = DbActiveItem.retrieveDependentItems(orgUuid, folderUuid, DependencyType.IsContainedWithin);
        for (int i=0; i<childActiveItems.size(); i++)
        {
            DbActiveItem activeItem = childActiveItems.get(i);
            UUID uuid = activeItem.getItemUuid();
            int version = activeItem.getVersion();
            DbItem item = DbItem.retrieveForUuidVersion(uuid, version);
            DefinitionItemType itemType = activeItem.getItemType();

            JsonFolderContent c = new JsonFolderContent();
            c.setUuid(uuid);
            c.setName(item.getTitle());
            c.setTypeEnum(itemType);
            c.setLastModified(item.getTimeStamp());

            ret.addContent(c);

            //and set any extra data we need
            if (itemType == DefinitionItemType.Report)
            {
                //TODO: 2016-03-01 DL - set last run date etc. on folder content
                c.setLastRun(new Date());
                c.setIsScheduled(true);
            }
            else if (itemType == DefinitionItemType.Query)
            {


            }
            else if (itemType == DefinitionItemType.ListOutput)
            {


            }
            else
            {
                throw new RuntimeException("Unexpected content " + item + " in folder");
            }
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    /*public Response getFolderContents(@Context SecurityContext sc, @QueryParam("folderUuid") String uuidStr) throws Exception
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

        *//*List<DbAbstractTable> items = DbFolderItemLink.retrieveForFolder(folderUuid);
        for (int i=0; i<items.size(); i++)
        {
            DbFolderItemLink item = (DbFolderItemLink)items.get(i);
        }*//*

        if (folder.getFolderType() == DbFolder.FOLDER_TYPE_REPORTS)
        {
            JsonFolderContent c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("Report 1");
            c.setTypeEnum(DefinitionItemType.Report);
            c.setLastModified(new Date());
            c.setLastRun(new Date());
            c.setIsScheduled(true);
            ret.addContent(c);

            c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("Report 2");
            c.setTypeEnum(DefinitionItemType.Report);
            c.setLastModified(new Date());
            c.setIsScheduled(false);
            ret.addContent(c);

            c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("Report 3");
            c.setTypeEnum(DefinitionItemType.Report);
            c.setLastModified(new Date());
            c.setIsScheduled(false);
            ret.addContent(c);
        }
        else if (folder.getFolderType() == DbFolder.FOLDER_TYPE_LIBRARY)
        {
            JsonFolderContent c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("List Output 1");
            c.setTypeEnum(DefinitionItemType.ListOutput);
            c.setLastModified(new Date());
            ret.addContent(c);

            c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("List Output 2");
            c.setTypeEnum(DefinitionItemType.ListOutput);
            c.setLastModified(new Date());
            ret.addContent(c);

            c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("List Output 3");
            c.setTypeEnum(DefinitionItemType.ListOutput);
            c.setLastModified(new Date());
            ret.addContent(c);

            c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("Query 1");
            c.setTypeEnum(DefinitionItemType.Query);
            c.setLastModified(new Date());
            ret.addContent(c);

            c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("Query 2");
            c.setTypeEnum(DefinitionItemType.Query);
            c.setLastModified(new Date());
            ret.addContent(c);

            c = new JsonFolderContent();
            c.setUuid(UUID.randomUUID());
            c.setName("Query 3");
            c.setTypeEnum(DefinitionItemType.Query);
            c.setLastModified(new Date());
            ret.addContent(c);
        }
        else
        {
            throw new BadRequestException("Unsupported folder type");
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }*/
}
