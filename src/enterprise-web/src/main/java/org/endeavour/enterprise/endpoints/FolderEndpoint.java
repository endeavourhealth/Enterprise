package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.*;
import org.endeavourhealth.core.security.SecurityUtils;
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

        UUID folderUuid = folderParameters.getUuid();
        String folderName = folderParameters.getFolderName();
        Integer folderType = folderParameters.getFolderType();
        UUID parentUuid = folderParameters.getParentFolderUuid();

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = SecurityUtils.getCurrentUserId(sc);

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
            ActiveitemEntity activeItem = ActiveitemEntity.retrieveForItemUuid(folderUuid);
            itemType = activeItem.getItemtypeid();
        }
        //if we're creating a new folder, we can get the item type from our parent
        else if (parentUuid != null) {
            ActiveitemEntity parentActiveItem = ActiveitemEntity.retrieveForItemUuid(parentUuid);
            itemType = parentActiveItem.getItemtypeid();
        } else {
            throw new BadRequestException("Must specify folder type");
        }

        LOG.trace(String.format("SavingFolder FolderUUID %s, FolderName %s FolderType %s ParentUUID %s ItemType %s", folderUuid, folderName, folderType, parentUuid, itemType));

        //before letting our superclass do the normal item saving,
        //validate that we're not making a folder a child of itself
        if (parentUuid != null
                && folderUuid != null) {

            UUID currentParentUuid = parentUuid;
            while (currentParentUuid != null) {
                if (currentParentUuid.equals(folderUuid)) {
                    throw new BadRequestException("Cannot move a folder to be a child of itself");
                }

                ActiveitemEntity activeItem = ActiveitemEntity.retrieveForItemUuid(currentParentUuid);
                List<ItemdependencyEntity> parents = ItemdependencyEntity.retrieveForActiveItemType(activeItem, (short)DependencyType.IsChildOf.getValue());
                if (parents.isEmpty()) {
                    currentParentUuid = null;
                } else {
                    ItemdependencyEntity parent = parents.get(0);
                    currentParentUuid = parent.getDependentitemuuid();
                }
            }
        }

        boolean inserting = folderUuid == null;
        if (inserting) {
            folderUuid = UUID.randomUUID();
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

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = SecurityUtils.getCurrentUserId(sc);

        UUID folderUuid = folderParameters.getUuid();

        LOG.trace("DeletingFolder FolderUUID {}", folderUuid);

        //to delete it, we need to find out the item type
        ActiveitemEntity activeItem = ActiveitemEntity.retrieveForItemUuid(folderUuid);
        Short itemType = activeItem.getItemtypeid();
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

        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace(String.format("GettingFolders under parent UUID %s and folderType %s, which is itemType %s", parentUuidStr, folderType, itemType));

        List<ItemEntity> items = null;

        //if we have no parent, then we're looking for the TOP-LEVEL folder
        if (parentUuidStr == null) {
            items = ItemEntity.retrieveNonDependentItems(orgUuid, (short)DependencyType.IsChildOf.getValue(), (short)itemType.getValue());

            //if we don't have a top-level folder, for some reason, re-create it
            if (items.size() == 0) {
                UUID userUuid = SecurityUtils.getCurrentUserId(sc);
                FolderEndpoint.createTopLevelFolder(orgUuid, userUuid, itemType);

                //then re-run the select
                items = ItemEntity.retrieveNonDependentItems(orgUuid, (short)DependencyType.IsChildOf.getValue(), (short)itemType.getValue());
            }
        }
        //if we have a parent, then we want the child folders under it
        else {
            UUID parentUuid = parseUuidFromStr(parentUuidStr);
            items = ItemEntity.retrieveDependentItems(parentUuid, (short)DependencyType.IsChildOf.getValue());
        }

        LOG.trace("Found {} child folders", items.size());

        JsonFolderList ret = new JsonFolderList();

        for (int i = 0; i < items.size(); i++) {
            ItemEntity item = items.get(i);
            UUID itemUuid = item.getItemuuid();

            int childFolders = ActiveitemEntity.retrieveCountDependencies(itemUuid, (short)DependencyType.IsChildOf.getValue());
            int contentCount = ActiveitemEntity.retrieveCountDependencies(itemUuid, (short)DependencyType.IsContainedWithin.getValue());

            LOG.trace(String.format("Child folder %s, UUID %s has %s child folders and %s contents", item.getTitle(), item.getItemuuid(), childFolders, contentCount));

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

        AuditEntity audit = AuditEntity.factoryNow(userUuid, organisationUuid);

        ItemEntity item = ItemEntity.factoryNew(title, audit);
        item.setXmlcontent(""); //need non-null values
        item.setDescription("");

        ActiveitemEntity activeItemReports = ActiveitemEntity.factoryNew(item, organisationUuid, (short)itemType.getValue());

        DataManager db = new DataManager();
        db.saveFolders(audit, item, activeItemReports);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getFolderContents")
    public Response getFolderContents(@Context SecurityContext sc, @QueryParam("folderUuid") String uuidStr) throws Exception {
        super.setLogbackMarkers(sc);

        UUID folderUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("GettingFolderContents for folder {}", folderUuid);

        JsonFolderContentsList ret = new JsonFolderContentsList();

        List<ActiveitemEntity> childActiveItems = ActiveitemEntity.retrieveDependentItems(orgUuid, folderUuid, (short)DependencyType.IsContainedWithin.getValue());

        //for reports, we want extra information (last run date etc.)
        HashMap<UUID, RequestEntity> hmPendingRequestsByItem = new HashMap<>();
        HashMap<UUID, JobreportEntity> hmLastJobReportsByItem = new HashMap<>();
        HashMap<UUID, JobEntity> hmJobsByUuid = new HashMap<>();

        List<ActiveitemEntity> reportActiveItems = new ArrayList<>();
        for (ActiveitemEntity activeItem: childActiveItems) {
            if (activeItem.getItemtypeid() == DefinitionItemType.Report.getValue()) {
                reportActiveItems.add(activeItem);
            }
        }
        if (!reportActiveItems.isEmpty()) {

            List<RequestEntity> pendingRequests = RequestEntity.retrievePendingForActiveItems(orgUuid, childActiveItems);
            for (RequestEntity pendingRequest: pendingRequests ) {
                hmPendingRequestsByItem.put(pendingRequest.getReportuuid(), pendingRequest);
            }

            List<JobreportEntity> jobReports = JobreportEntity.retrieveLatestForActiveItems(orgUuid, childActiveItems);
            for (JobreportEntity jobReport: jobReports) {
                hmLastJobReportsByItem.put(jobReport.getReportuuid(), jobReport);
            }

            List<JobEntity> jobs = JobEntity.retrieveForJobReports(jobReports);
            for (JobEntity job: jobs) {
                hmJobsByUuid.put(job.getJobuuid(), job);
            }
        }

        HashMap<UUID, AuditEntity> hmAuditsByAuditUuid = new HashMap<>();
        List<AuditEntity> audits = AuditEntity.retrieveForActiveItems(childActiveItems);
        for (AuditEntity audit: audits) {
            hmAuditsByAuditUuid.put(audit.getAudituuid(), audit);
        }

        HashMap<UUID, ItemEntity> hmItemsByItemUuid = new HashMap<>();
        List<ItemEntity> items = ItemEntity.retrieveForActiveItems(childActiveItems);
        for (ItemEntity item: items) {
            hmItemsByItemUuid.put(item.getItemuuid(), item);
        }

        for (int i = 0; i < childActiveItems.size(); i++) {

            ActiveitemEntity activeItem = childActiveItems.get(i);
            ItemEntity item = hmItemsByItemUuid.get(activeItem.getItemuuid());
            Short itemType = activeItem.getItemtypeid();
            AuditEntity audit = hmAuditsByAuditUuid.get(item.getAudituuid());

            JsonFolderContent c = new JsonFolderContent(activeItem, item, audit);
            ret.addContent(c);

            //and set any extra data we need
            if (itemType == DefinitionItemType.Report.getValue()) {

                //for reports, indicate if it's currently scheduled to be run
                RequestEntity pendingRequest = hmPendingRequestsByItem.get(item.getItemuuid());
                c.setIsScheduled(pendingRequest != null);

                //show when a report was last executed
                JobreportEntity jobReport = hmLastJobReportsByItem.get(item.getItemuuid());
                if (jobReport != null) {
                    JobEntity job = hmJobsByUuid.get(jobReport.getJobuuid());
                    c.setLastRun(new Date(job.getStartdatetime().getTime()));
                }

            } else if (itemType == DefinitionItemType.Query.getValue()) {

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
