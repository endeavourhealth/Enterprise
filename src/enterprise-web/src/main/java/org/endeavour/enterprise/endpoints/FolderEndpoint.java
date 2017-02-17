package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.*;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;

import org.endeavourhealth.enterprise.core.database.DataManager;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;

/**
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
        super.setLogbackMarkers(sc);

        String folderUuid = folderParameters.getUuid();
        String folderName = folderParameters.getFolderName();
        Integer folderType = folderParameters.getFolderType();
        String parentUuid = folderParameters.getParentFolderUuid();

        String userUuid = "B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32";
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        //work out the ItemType, either from the parameters passed up or from our parent folder
        //if the folder type wasn't specified, see if we can derive it from our parent
        Short itemType = null;

        //if folder type was passed up from client
        if (folderType != null) {
            if (folderType == JsonFolder.FOLDER_TYPE_LIBRARY) {
                itemType = (short)DefinitionItemType.LibraryFolder.getValue();
            } else if (folderType == JsonFolder.FOLDER_TYPE_REPORTS) {
                itemType = (short)DefinitionItemType.ReportFolder.getValue();
            } else {
                throw new BadRequestException("Invalid folder type " + folderType);
            }
        }
        //if we're amending an existing folder, we can use its item type
        else if (folderUuid != null) {
            ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(folderUuid);
            itemType = activeItem.getItemTypeId();
        }
        //if we're creating a new folder, we can get the item type from our parent
        else if (parentUuid != null) {
            ActiveItemEntity parentActiveItem = ActiveItemEntity.retrieveForItemUuid(parentUuid);
            itemType = parentActiveItem.getItemTypeId();
        } else {
            throw new BadRequestException("Must specify folder type");
        }

        LOG.trace(String.format("SavingFolder FolderUUID %s, FolderName %s FolderType %s ParentUUID %s ItemType %s", folderUuid, folderName, folderType, parentUuid, itemType));

        //before letting our superclass do the normal item saving,
        //validate that we're not making a folder a child of itself
        if (parentUuid != null
                && folderUuid != null) {

            String currentParentUuid = parentUuid;
            while (currentParentUuid != null) {
                if (currentParentUuid.equals(folderUuid)) {
                    throw new BadRequestException("Cannot move a folder to be a child of itself");
                }

                ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(currentParentUuid);
                List<ItemDependencyEntity> parents = ItemDependencyEntity.retrieveForActiveItemType(activeItem, (short)DependencyType.IsChildOf.getValue());
                if (parents.isEmpty()) {
                    currentParentUuid = null;
                } else {
                    ItemDependencyEntity parent = parents.get(0);
                    currentParentUuid = parent.getDependentItemUuid();
                }
            }
        }

        boolean inserting = folderUuid == null;
        if (inserting) {
            folderUuid = UUID.randomUUID().toString();
        }

        super.saveItem(inserting, folderUuid, orgUuid, userUuid, itemType.intValue(), folderName, "", null, parentUuid);

        //return the UUID of the folder we just saved or updated
        JsonFolder ret = new JsonFolder();
        ret.setUuid(folderUuid);

        clearLogbackMarkers();

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
        super.setLogbackMarkers(sc);

        String userUuid = "B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32";
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        String folderUuid = folderParameters.getUuid();

        LOG.trace("DeletingFolder FolderUUID {}", folderUuid);

        //to delete it, we need to find out the item type
        ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(folderUuid);
        Short itemType = activeItem.getItemTypeId();
        if (itemType != DefinitionItemType.LibraryFolder.getValue()
                && itemType != DefinitionItemType.ReportFolder.getValue()) {
            throw new BadRequestException("UUID is a " + itemType + " not a folder");
        }

        JsonDeleteResponse ret = deleteItem(folderUuid, orgUuid, userUuid);

        clearLogbackMarkers();

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
        super.setLogbackMarkers(sc);

        //convert the nominal folder type to the actual Item DefinitionType
        DefinitionItemType itemType = null;
        if (folderType == JsonFolder.FOLDER_TYPE_LIBRARY) {
            itemType = DefinitionItemType.LibraryFolder;
        } else if (folderType == JsonFolder.FOLDER_TYPE_REPORTS) {
            itemType = DefinitionItemType.ReportFolder;
        } else {
            throw new BadRequestException("Invalid folder type " + folderType);
        }


        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        LOG.trace(String.format("GettingFolders under parent UUID %s and folderType %s, which is itemType %s", parentUuidStr, folderType, itemType));

        List<ItemEntity> items = null;

        //if we have no parent, then we're looking for the TOP-LEVEL folder
        if (parentUuidStr == null) {
            items = ItemEntity.retrieveNonDependentItems(orgUuid, (short)DependencyType.IsChildOf.getValue(), (short)itemType.getValue());

            //if we don't have a top-level folder, for some reason, re-create it
            if (items.size() == 0) {
                String userUuid = "B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32";
                FolderEndpoint.createTopLevelFolder(orgUuid, userUuid, itemType);

                //then re-run the select
                items = ItemEntity.retrieveNonDependentItems(orgUuid, (short)DependencyType.IsChildOf.getValue(), (short)itemType.getValue());
            }
        }
        //if we have a parent, then we want the child folders under it
        else {
            String parentUuid = parseUuidFromStr(parentUuidStr);
            items = ItemEntity.retrieveDependentItems(parentUuid, (short)DependencyType.IsChildOf.getValue());
        }

        LOG.trace("Found {} child folders", items.size());

        JsonFolderList ret = new JsonFolderList();

        for (int i = 0; i < items.size(); i++) {
            ItemEntity item = items.get(i);
            String itemUuid = item.getItemUuid();

            int childFolders = ActiveItemEntity.retrieveCountDependencies(itemUuid, (short)DependencyType.IsChildOf.getValue());
            int contentCount = ActiveItemEntity.retrieveCountDependencies(itemUuid, (short)DependencyType.IsContainedWithin.getValue());

            LOG.trace(String.format("Child folder %s, UUID %s has %s child folders and %s contents", item.getTitle(), item.getItemUuid(), childFolders, contentCount));

            JsonFolder folder = new JsonFolder(item, contentCount, childFolders > 0);
            ret.add(folder);
        }

        Collections.sort(ret.getFolders());

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    public static void createTopLevelFolder(String organisationUuid, String userUuid, DefinitionItemType itemType) throws Exception {

        LOG.trace("Creating top-level folder of type {}", itemType);

        String title = null;
        if (itemType == DefinitionItemType.LibraryFolder) {
            title = "Library";
        } else if (itemType == DefinitionItemType.ReportFolder) {
            title = "Reports";
        } else {
            throw new RuntimeException("Trying to create folder for type " + itemType);
        }

        AuditEntity audit = AuditEntity.factoryNow(userUuid, organisationUuid);

        ItemEntity item = ItemEntity.factoryNew(title, audit);
        item.setItemUuid(UUID.randomUUID().toString());
        item.setXmlContent(""); //need non-null values
        item.setDescription("");

        ActiveItemEntity activeItemReports = ActiveItemEntity.factoryNew(item, organisationUuid, (short)itemType.getValue());

        DataManager db = new DataManager();
        db.saveFolders(audit, item, activeItemReports);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolderContents")
    public Response getFolderContents(@Context SecurityContext sc, @QueryParam("folderUuid") String uuidStr) throws Exception {
        super.setLogbackMarkers(sc);

        String folderUuid = uuidStr;
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        LOG.trace("GettingFolderContents for folder {}", folderUuid);

        JsonFolderContentsList ret = new JsonFolderContentsList();

        List<ActiveItemEntity> childActiveItems = ActiveItemEntity.retrieveDependentItems(orgUuid, folderUuid, (short)DependencyType.IsContainedWithin.getValue());

        List<ActiveItemEntity> reportActiveItems = new ArrayList<>();
        for (ActiveItemEntity activeItem: childActiveItems) {
            if (activeItem.getItemTypeId() == DefinitionItemType.Report.getValue()) {
                reportActiveItems.add(activeItem);
            }
        }

        HashMap<String, AuditEntity> hmAuditsByAuditUuid = new HashMap<>();
        List<AuditEntity> audits = AuditEntity.retrieveForActiveItems(childActiveItems);
        for (AuditEntity audit: audits) {
            hmAuditsByAuditUuid.put(audit.getAuditUuid(), audit);
        }

        HashMap<String, ItemEntity> hmItemsByItemUuid = new HashMap<>();
        List<ItemEntity> items = ItemEntity.retrieveForActiveItems(childActiveItems);
        for (ItemEntity item: items) {
            hmItemsByItemUuid.put(item.getItemUuid(), item);
        }

        for (int i = 0; i < childActiveItems.size(); i++) {

            ActiveItemEntity activeItem = childActiveItems.get(i);
            ItemEntity item = hmItemsByItemUuid.get(activeItem.getItemUuid());
            Short itemType = activeItem.getItemTypeId();
            AuditEntity audit = hmAuditsByAuditUuid.get(item.getAuditUuid());

            JsonFolderContent c = new JsonFolderContent(activeItem, item, audit);
            ret.addContent(c);

            if (itemType == DefinitionItemType.Query.getValue()) {

            } else if (itemType == DefinitionItemType.Test.getValue()) {

            } else if (itemType == DefinitionItemType.DataSource.getValue()) {

            } else if (itemType == DefinitionItemType.CodeSet.getValue()) {

            } else if (itemType == DefinitionItemType.ListOutput.getValue()) {

            } else {
                //throw new RuntimeException("Unexpected content " + item + " in folder");
            }
        }

        if (ret.getContents() != null) {
            Collections.sort(ret.getContents());
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

}
