package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.*;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.definition.DbItemDependency;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbJob;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.database.execution.DbRequest;
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

                List<DbItemDependency> parents = DbItemDependency.retrieveForDependentItemType(currentParentUuid, DependencyType.IsChildOf);
                if (parents.isEmpty()) {
                    currentParentUuid = null;
                } else {
                    DbItemDependency parent = parents.get(0);
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
            UUID parentUuid = parseUuidFromStr(parentUuidStr);
            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(parentUuid);
            UUID auditUuid = activeItem.getAuditUuid();

            items = DbItem.retrieveDependentItems(parentUuid, auditUuid, DependencyType.IsChildOf);
        }

        LOG.trace("Found {} child folders", items.size());

        JsonFolderList ret = new JsonFolderList();

        for (int i = 0; i < items.size(); i++) {
            DbItem item = items.get(i);
            UUID itemUuid = item.getItemUuid();

            int childFolders = DbActiveItem.retrieveCountDependencies(itemUuid, DependencyType.IsChildOf);
            int contentCount = DbActiveItem.retrieveCountDependencies(itemUuid, DependencyType.IsContainedWithin);

            LOG.trace("Child folder {}, UUID {} has {} child folders and {} contents", item.getTitle(), item.getItemUuid(), childFolders, contentCount);

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

        List<DbAbstractTable> toSave = new ArrayList<>();

        DbAudit audit = DbAudit.factoryNow(userUuid);
        toSave.add(audit);

        DbItem item = DbItem.factoryNew(title, audit);
        item.setXmlContent(""); //need non-null values
        item.setDescription("");
        toSave.add(item);

        DatabaseManager.db().writeEntities(toSave);

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

        LOG.trace("GettingFolderContents for folder {}", folderUuid);

        JsonFolderContentsList ret = new JsonFolderContentsList();

        List<DbActiveItem> childActiveItems = DbActiveItem.retrieveDependentItems(orgUuid, folderUuid, DependencyType.IsContainedWithin);

        //for reports, we want extra information (last run date etc.)
        HashMap<UUID, DbRequest> hmPendingRequestsByItem = new HashMap<>();
        HashMap<UUID, DbJobReport> hmLastJobReportsByItem = new HashMap<>();
        HashMap<UUID, DbJob> hmJobsByUuid = new HashMap<>();

        List<DbActiveItem> reportActiveItems = new ArrayList<>();
        for (DbActiveItem activeItem: childActiveItems) {
            if (activeItem.getItemTypeId() == DefinitionItemType.Report) {
                reportActiveItems.add(activeItem);
            }
        }
        if (!reportActiveItems.isEmpty()) {

            List<DbRequest> pendingRequests = DbRequest.retrievePendingForActiveItems(orgUuid, childActiveItems);
            for (DbRequest pendingRequest: pendingRequests ) {
                hmPendingRequestsByItem.put(pendingRequest.getReportUuid(), pendingRequest);
            }

            List<DbJobReport> jobReports = DbJobReport.retrieveLatestForActiveItems(orgUuid, childActiveItems);
            for (DbJobReport jobReport: jobReports) {
                hmLastJobReportsByItem.put(jobReport.getReportUuid(), jobReport);
            }

            List<DbJob> jobs = DbJob.retrieveForJobReports(jobReports);
            for (DbJob job: jobs) {
                hmJobsByUuid.put(job.getJobUuid(), job);
            }
        }

        HashMap<UUID, DbAudit> hmAuditsByAuditUuid = new HashMap<>();
        List<DbAudit> audits = DbAudit.retrieveForActiveItems(childActiveItems);
        for (DbAudit audit: audits) {
            hmAuditsByAuditUuid.put(audit.getAuditUuid(), audit);
        }

        HashMap<UUID, DbItem> hmItemsByItemUuid = new HashMap<>();
        List<DbItem> items = DbItem.retrieveForActiveItems(childActiveItems);
        for (DbItem item: items) {
            hmItemsByItemUuid.put(item.getItemUuid(), item);
        }

        for (int i = 0; i < childActiveItems.size(); i++) {

            DbActiveItem activeItem = childActiveItems.get(i);
            DbItem item = hmItemsByItemUuid.get(activeItem.getItemUuid());
            DefinitionItemType itemType = activeItem.getItemTypeId();
            DbAudit audit = hmAuditsByAuditUuid.get(item.getAuditUuid());

            JsonFolderContent c = new JsonFolderContent(activeItem, item, audit);
            ret.addContent(c);

            //and set any extra data we need
            if (itemType == DefinitionItemType.Report) {

                //for reports, indicate if it's currently scheduled to be run
                DbRequest pendingRequest = hmPendingRequestsByItem.get(item.getItemUuid());
                c.setIsScheduled(pendingRequest != null);

                //show when a report was last executed
                DbJobReport jobReport = hmLastJobReportsByItem.get(item.getItemUuid());
                if (jobReport != null) {
                    DbJob job = hmJobsByUuid.get(jobReport.getJobUuid());
                    c.setLastRun(new Date(job.getStartDateTime().toEpochMilli()));
                }

            } else if (itemType == DefinitionItemType.Query) {

            } else if (itemType == DefinitionItemType.Test) {

            } else if (itemType == DefinitionItemType.DataSource) {

            } else if (itemType == DefinitionItemType.CodeSet) {

            } else if (itemType == DefinitionItemType.ListOutput) {

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
