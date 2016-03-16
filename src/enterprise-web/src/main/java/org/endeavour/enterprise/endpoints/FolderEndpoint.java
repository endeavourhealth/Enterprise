package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.model.json.*;
import org.endeavourhealth.enterprise.core.entity.DefinitionItemType;
import org.endeavourhealth.enterprise.core.entity.DependencyType;
import org.endeavourhealth.enterprise.core.entity.database.DbActiveItem;
import org.endeavourhealth.enterprise.core.entity.database.DbActiveItemDependency;
import org.endeavourhealth.enterprise.core.entity.database.DbItem;
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
public final class FolderEndpoint extends AbstractItemEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(FolderEndpoint.class);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveFolder")
    public Response saveFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception {
        //get the parameters out
        UUID folderUuid = folderParameters.getUuid();
        String folderName = folderParameters.getFolderName();
        Integer folderType = folderParameters.getFolderType();
        UUID parentUuid = folderParameters.getParentFolderUuid();

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        //work out the ItemType, either from the parameters passed up or from our parent folder
        //if the folder type wasn't specified, see if we can derive it from our parent
        DefinitionItemType itemType = null;

        //if folder type was passed up from client
        if (folderType != null) {
            if (folderType == JsonFolder.FOLDER_TYPE_LIBRARY) {
                itemType = DefinitionItemType.LibraryFolder;
            } else if (folderType == JsonFolder.FOLDER_TYPE_REPORTS) {
                itemType = DefinitionItemType.ReportFolder;
            } else {
                throw new BadRequestException("Invalid folder type " + folderType);
            }
        }
        //if we're amending an existing folder, we can use its item type
        else if (folderUuid != null) {
            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(folderUuid);
            itemType = activeItem.getItemTypeId();
        }
        //if we're creating a new folder, we can get the item type from our parent
        else if (parentUuid != null) {
            DbActiveItem parentActiveItem = DbActiveItem.retrieveForItemUuid(parentUuid);
            itemType = parentActiveItem.getItemTypeId();
        } else {
            throw new BadRequestException("Must specify folder type");
        }

        LOG.trace("SavingFolder FolderUUID {}, FolderName {} FolderType {} ParentUUID {} ItemType {}", folderUuid, folderName, folderType, parentUuid, itemType);

        //before letting our superclass do the normal item saving,
        //validate that we're not making a folder a child of itself
        if (parentUuid != null
                && folderUuid != null) {
            UUID currentParentUuid = parentUuid;
            while (currentParentUuid != null) {
                if (currentParentUuid.equals(folderUuid)) {
                    throw new BadRequestException("Cannot move a folder to be a child of itself");
                }

                List<DbActiveItemDependency> parents = DbActiveItemDependency.retrieveForDependentItemType(currentParentUuid, DependencyType.IsChildOf);
                if (parents.isEmpty()) {
                    currentParentUuid = null;
                } else {
                    DbActiveItemDependency parent = parents.get(0);
                    currentParentUuid = parent.getItemUuid();
                }
            }
        }

        boolean inserting = folderUuid == null;
        if (inserting) {
            folderUuid = UUID.randomUUID();
        }

        super.saveItem(inserting, folderUuid, orgUuid, userUuid, itemType, folderName, "", null, parentUuid);

        //return the UUID of the folder we just saved or updated
        JsonFolder ret = new JsonFolder();
        ret.setUuid(folderUuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteFolder")
    public Response deleteFolder(@Context SecurityContext sc, JsonFolder folderParameters) throws Exception {
        //get the organisation from the server token
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        //get the parameters out
        UUID folderUuid = folderParameters.getUuid();

        LOG.trace("DeletingFolder FolderUUID {}", folderUuid);

        //to delete it, we need to find out the item type
        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(folderUuid);
        DefinitionItemType itemType = activeItem.getItemTypeId();
        if (itemType != DefinitionItemType.LibraryFolder
                && itemType != DefinitionItemType.ReportFolder) {
            throw new BadRequestException("UUID is a " + itemType + " not a folder");
        }

        JsonDeleteResponse ret = deleteItem(folderUuid, orgUuid, userUuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolders")
    public Response getFolders(@Context SecurityContext sc, @QueryParam("folderType") int folderType, @QueryParam("parentUuid") String parentUuidStr) throws Exception {
        //convert the nominal folder type to the actual Item DefinitionType
        DefinitionItemType itemType = null;
        if (folderType == JsonFolder.FOLDER_TYPE_LIBRARY) {
            itemType = DefinitionItemType.LibraryFolder;
        } else if (folderType == JsonFolder.FOLDER_TYPE_REPORTS) {
            itemType = DefinitionItemType.ReportFolder;
        } else {
            throw new BadRequestException("Invalid folder type " + folderType);
        }

        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("GettingFolders under parent UUID {} and folderType {}, which is itemType {}", parentUuidStr, folderType, itemType);

        List<DbItem> items = null;

        //if we have no parent, then we're looking for the TOP-LEVEL folder
        if (parentUuidStr == null) {
            items = DbItem.retrieveNonDependentItems(orgUuid, DependencyType.IsChildOf, itemType);

            //if we don't have a top-level folder, for some reason, re-create it
            if (items.size() == 0) {
                UUID userUuid = getEndUserUuidFromToken(sc);
                FolderEndpoint.createTopLevelFolder(orgUuid, userUuid, itemType);

                //then re-run the select
                items = DbItem.retrieveNonDependentItems(orgUuid, DependencyType.IsChildOf, itemType);
            }
        }
        //if we have a parent, then we want the child folders under it
        else {
            UUID parentUuid = UUID.fromString(parentUuidStr);
            items = DbItem.retrieveDependentItems(orgUuid, parentUuid, DependencyType.IsChildOf);
        }

        LOG.trace("Found {} child folders", items.size());

        JsonFolderList ret = new JsonFolderList();

        for (int i = 0; i < items.size(); i++) {
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

    public static void createTopLevelFolder(UUID organisationUuid, UUID userUuid, DefinitionItemType itemType) throws Exception {
        LOG.trace("Creating top-level folder of type {}", itemType);

        String title = null;
        if (itemType == DefinitionItemType.LibraryFolder) {
            title = "Library";
        } else if (itemType == DefinitionItemType.ReportFolder) {
            title = "Reports";
        } else {
            throw new RuntimeException("Trying to create folder for type " + itemType);
        }

        DbItem item = DbItem.factoryNew(userUuid, title);
        item.writeToDb();

        DbActiveItem activeItemReports = DbActiveItem.factoryNew(item, organisationUuid, itemType);
        activeItemReports.writeToDb();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolderContents")
    public Response getFolderContents(@Context SecurityContext sc, @QueryParam("folderUuid") String uuidStr) throws Exception {
        UUID folderUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        //retrieve the folder and validate it's for our org
        //DbItem folder = DbItem.retrieveForUuidLatestVersion(orgUuid, folderUuid);

        JsonFolderContentsList ret = new JsonFolderContentsList();

        LOG.trace("GettingFolderContents for folder {}", folderUuid);

        List<DbActiveItem> childActiveItems = DbActiveItem.retrieveDependentItems(orgUuid, folderUuid, DependencyType.IsContainedWithin);
        for (int i = 0; i < childActiveItems.size(); i++) {
            DbActiveItem activeItem = childActiveItems.get(i);
            UUID uuid = activeItem.getItemUuid();
            int version = activeItem.getVersion();
            DbItem item = DbItem.retrieveForUuidVersion(uuid, version);
            DefinitionItemType itemType = activeItem.getItemTypeId();

            JsonFolderContent c = new JsonFolderContent(item);
            c.setTypeEnum(itemType);
            c.setLastModified(item.getTimeStamp());

            ret.addContent(c);

            //and set any extra data we need
            if (itemType == DefinitionItemType.Report) {
                //TODO: 2016-03-01 DL - set last run date etc. on folder content
                c.setLastRun(new Date());
                c.setIsScheduled(true);
            } else if (itemType == DefinitionItemType.Query) {

            } else if (itemType == DefinitionItemType.Test) {

            } else if (itemType == DefinitionItemType.Datasource) {

            } else if (itemType == DefinitionItemType.CodeSet) {

            } else if (itemType == DefinitionItemType.ListOutput) {

            } else if (itemType == DefinitionItemType.CodeSet) {

            } else {
                throw new RuntimeException("Unexpected content " + item + " in folder");
            }
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }

}
