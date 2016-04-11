package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.JsonDeleteResponse;
import org.endeavour.enterprise.json.JsonMoveItem;
import org.endeavour.enterprise.json.JsonMoveItems;
import org.endeavour.enterprise.utility.QueryDocumentReaderFindDependentUuids;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.definition.DbItemDependency;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;

import javax.ws.rs.BadRequestException;
import java.util.*;

public abstract class AbstractItemEndpoint extends AbstractEndpoint {

    private static final int MAX_VALIDATION_ERRORS_FOR_DELETE = 5;

    protected DbActiveItem retrieveActiveItem(UUID itemUuid, UUID orgUuid, DefinitionItemType itemTypeDesired) throws Exception {

        DbActiveItem activeItem = retrieveActiveItem(itemUuid, orgUuid);

        if (activeItem.getItemTypeId() != itemTypeDesired) {
            throw new RuntimeException("Trying to retrieve a " + itemTypeDesired + " but item is a " + activeItem.getItemTypeId());
        }
        return activeItem;
    }

    protected DbActiveItem retrieveActiveItem(UUID itemUuid, UUID orgUuid) throws Exception {
        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);

        if (activeItem == null) {
            throw new BadRequestException("UUID does not exist");
        }

        if (!activeItem.getOrganisationUuid().equals(orgUuid)) {
            throw new BadRequestException("Item for another organisation");
        }

        return activeItem;
    }

    protected JsonDeleteResponse deleteItem(UUID itemUuid, UUID orgUuid, UUID userUuid) throws Exception {
        DbActiveItem activeItem = retrieveActiveItem(itemUuid, orgUuid);
        DbItem item = DbItem.retrieveForActiveItem(activeItem);

        //recursively build up the full list of items we want to delete
        List<DbItem> itemsToDelete = new ArrayList<>();
        List<DbActiveItem> activeItemsToDelete = new ArrayList<>();
        findItemsToDelete(item, activeItem, itemsToDelete, activeItemsToDelete);

        JsonDeleteResponse ret = new JsonDeleteResponse();

        //validate we're not going to break anything with our delete
        validateDelete(itemsToDelete, orgUuid, ret);

        //if we encountered a validation error when checking for references, then return out without deleting
        if (ret.size() > 0) {
            return ret;
        }

        //now do the deleting, building up a list of all entities to update, which is then done atomically
        List<DbAbstractTable> toSave = new ArrayList<>();

        DbAudit audit = DbAudit.factoryNow(userUuid, orgUuid);
        toSave.add(audit);
        UUID auditUuid = audit.getAuditUuid();

        for (DbItem itemToDelete: itemsToDelete) {
            itemToDelete.setAuditUuid(auditUuid);
            itemToDelete.setDeleted(true);
            itemToDelete.setSaveMode(TableSaveMode.INSERT); //remember to always force an insert for items
            toSave.add(itemToDelete);
        }

        for (DbActiveItem activeItemToDelete: activeItemsToDelete) {
            activeItemToDelete.setAuditUuid(auditUuid);
            activeItemToDelete.setDeleted(true);
            toSave.add(activeItemToDelete);
        }

        DatabaseManager.db().writeEntities(toSave);
        return ret;
    }


    private void findItemsToDelete(DbItem item, DbActiveItem activeItem, List<DbItem> itemsToDelete, List<DbActiveItem> activeItemsToDelete) throws Exception {

        itemsToDelete.add(item);
        activeItemsToDelete.add(activeItem);

        List<DbItemDependency> dependencies = DbItemDependency.retrieveForDependentItem(item.getItemUuid());
        for (DbItemDependency dependency: dependencies) {

            //only recurse for containing or child folder-type dependencies
            if (dependency.getDependencyTypeId() == DependencyType.IsChildOf
                    || dependency.getDependencyTypeId() == DependencyType.IsContainedWithin) {
                DbActiveItem childActiveItem = DbActiveItem.retrieveForItemUuid(dependency.getItemUuid());
                DbItem childItem = DbItem.retrieveForActiveItem(childActiveItem);
                findItemsToDelete(childItem, childActiveItem, itemsToDelete, activeItemsToDelete);
            }
        }
    }

    private void validateDelete(List<DbItem> itemsToDelete, UUID orgUuid, JsonDeleteResponse response) throws Exception {
        //create a hash of all our items being deleted
        HashSet<UUID> hsUuidsToDelete = new HashSet<>();
        for (DbItem item: itemsToDelete) {
            hsUuidsToDelete.add(item.getItemUuid());
        }

        //see if there are any items USING something that we're trying to delete
        for (int i = 0; i < itemsToDelete.size(); i++) {
            DbItem item = itemsToDelete.get(i);
            UUID itemUuid = item.getItemUuid();
            List<DbItemDependency> dependencies = DbItemDependency.retrieveForDependentItemType(itemUuid, DependencyType.Uses);
            for (int j = 0; j < dependencies.size(); j++) {
                DbItemDependency dependency = dependencies.get(i);
                UUID parentItemUuid = dependency.getItemUuid();
                if (!hsUuidsToDelete.contains(parentItemUuid)) {

                    DbItem usingItem = DbItem.retrieveLatestForUUid(parentItemUuid);
                    String err = "" + item.getTitle() + " is used by " + usingItem.getTitle();
                    response.addValidationFailure(err);

                    //if we've already found as many validation failures as we display to the user, then return out
                    if (response.size() >= MAX_VALIDATION_ERRORS_FOR_DELETE)
                    {
                        return;
                    }
                }
            }
        }
    }

    private static void validateItemTypeMatchesContainingFolder(boolean insert, DefinitionItemType itemType, UUID containingFolderUuid) throws Exception {

        //if saving a new library item or report, it must have a containing folder item
        if (insert
                && containingFolderUuid == null
                && itemType != DefinitionItemType.ReportFolder
                && itemType != DefinitionItemType.LibraryFolder) {
            throw new BadRequestException("LibraryItems and Reports must have a containing folder UUID");
        }

        //if saving a folder or we're AMENDING a library item or report, then there's notning more to validate
        if (containingFolderUuid == null) {
            return;
        }

        DbActiveItem containingFolderActiveItem = DbActiveItem.retrieveForItemUuid(containingFolderUuid);
        DefinitionItemType containingFolderType = containingFolderActiveItem.getItemTypeId();
        if (containingFolderType == DefinitionItemType.LibraryFolder) {
            //library folders can only contain other library folders and queries etc.
            if (itemType != DefinitionItemType.LibraryFolder
                    && itemType != DefinitionItemType.CodeSet
                    && itemType != DefinitionItemType.Query
                    && itemType != DefinitionItemType.ListOutput) {
                throw new BadRequestException("Library folder UUID " + containingFolderUuid + " cannot contain a " + itemType);
            }
        } else if (containingFolderType == DefinitionItemType.ReportFolder) {
            //report folders can only contain other report folders and reports
            if (itemType != DefinitionItemType.ReportFolder
                    && itemType != DefinitionItemType.Report) {
                throw new BadRequestException("Library folder UUID " + containingFolderUuid + " cannot contain a " + itemType);
            }
        } else {
            throw new BadRequestException("Parent folder UUID " + containingFolderUuid + " isn't a folder");
        }
    }

    protected void saveItem(boolean insert, UUID itemUuid, UUID orgUuid, UUID userUuid, DefinitionItemType itemType,
                            String name, String description, QueryDocument queryDocument, UUID containingFolderUuid) throws Exception {

        //validate the containing folder type matches the itemType we're saving
        validateItemTypeMatchesContainingFolder(insert, itemType, containingFolderUuid);

        DbActiveItem activeItem = null;
        DbItem item = null;

        if (insert) {
            //if creating a NEW item, we need to validate we have the content we need
            if (name == null) {
                throw new BadRequestException("No name specified");
            }
            if (description == null) {
                //we can live without a description, but need a non-null value
                description = "";
            }
            if (containingFolderUuid == null
                    && itemType != DefinitionItemType.LibraryFolder
                    && itemType != DefinitionItemType.ReportFolder) {
                throw new BadRequestException("Must specify a containing folder for new items");
            }

            activeItem = new DbActiveItem();
            activeItem.setOrganisationUuid(orgUuid);
            activeItem.setItemUuid(itemUuid);
            activeItem.setItemTypeId(itemType);

            item = new DbItem();
            item.setItemUuid(itemUuid);
            item.setXmlContent(""); //when creating folders, we don't store XML, so this needs to be non-null
        } else {
            activeItem = retrieveActiveItem(itemUuid, orgUuid, itemType);
            item = DbItem.retrieveForActiveItem(activeItem);
        }

        UUID previousAuditUuid = activeItem.getAuditUuid();

        //update the AuditUuid on both objects
        DbAudit audit = DbAudit.factoryNow(userUuid, orgUuid);
        activeItem.setAuditUuid(audit.getAuditUuid());
        item.setAuditUuid(audit.getAuditUuid());

        //force the insert every time, since we allow duplicate rows in the Item table for the same UUID
        item.setSaveMode(TableSaveMode.INSERT);

        if (name != null) {
            item.setTitle(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (queryDocument != null) {
            String xmlContent = QueryDocumentSerializer.writeToXml(queryDocument);
            item.setXmlContent(xmlContent);
        }

        //build up a list of entities to save, so they can all be inserted atomically
        List<DbAbstractTable> toSave = new ArrayList<>();
        toSave.add(audit);
        toSave.add(item);
        toSave.add(activeItem);

        //work out any UUIDs our new item is dependent on
        if (queryDocument != null) {
            createUsingDependencies(queryDocument, activeItem, toSave);
        } else if (item.getXmlContent().length() > 0) {
            //if a new queryDocument wasn't provided, but the item already had one, we still need to recreate the "using" dependencies
            QueryDocument oldQueryDocument = QueryDocumentSerializer.readQueryDocumentFromXml(item.getXmlContent());
            createUsingDependencies(oldQueryDocument, activeItem, toSave);
        }

        //work out the child/contains dependency
        createFolderDependency(insert, itemType, item, previousAuditUuid, containingFolderUuid, toSave);

        //we can now commit to the DB
        DatabaseManager.db().writeEntities(toSave);
    }

    /**
     * when a libraryItem or report is saved, process the query document to find all UUIDs that it requires to be run
     */
    private static void createUsingDependencies(QueryDocument queryDocument, DbActiveItem activeItem, List<DbAbstractTable> toSave) throws Exception {

        //find all the UUIDs in the XML and then see if we need to create or delete dependencies
        QueryDocumentReaderFindDependentUuids reader = new QueryDocumentReaderFindDependentUuids(queryDocument);
        HashSet<UUID> uuidsInDoc = reader.findUuids();

        Iterator<UUID> iter = uuidsInDoc.iterator();
        while (iter.hasNext()) {
            UUID uuidInDoc = iter.next();

            DbItemDependency dependency = new DbItemDependency();
            dependency.setItemUuid(activeItem.getItemUuid());
            dependency.setAuditUuid(activeItem.getAuditUuid());
            dependency.setDependentItemUuid(uuidInDoc);
            dependency.setDependencyTypeId(DependencyType.Uses);
            dependency.setSaveMode(TableSaveMode.INSERT); //since we've explicitly set all the primaryKey values, we need to set this

            toSave.add(dependency);
        }
    }

    /**
     * when a libraryItem, report or folder is saved, link it to the containing folder
     */
    private static void createFolderDependency(boolean insert, DefinitionItemType itemType, DbItem item, UUID previousAuditUuid, UUID containingFolderUuid, List<DbAbstractTable> toSave) throws Exception {

        //work out the dependency type, based on what item type we're saving
        DependencyType dependencyType = null;
        if (itemType == DefinitionItemType.LibraryFolder
                || itemType == DefinitionItemType.ReportFolder) {
            //if we're saving a folder, we're working with "child of" dependencies
            dependencyType = DependencyType.IsChildOf;
        } else {
            //if we're saving anything else, we're working with "contained within" dependencies
            dependencyType = DependencyType.IsContainedWithin;
        }

        if (containingFolderUuid == null) {

            //if saving a new item without a folder, then return out as there's no dependency to create
            if (insert) {
                return;
            }

            //if we're just renaming an item, then no folder UUID would have been supplied,
            //so find out the old folder UUID so we can maintain the relationship
            List<DbItemDependency> oldFolderDependencies = DbItemDependency.retrieveForItemType(item.getItemUuid(), previousAuditUuid, dependencyType);
            if (!oldFolderDependencies.isEmpty()) {
                DbItemDependency oldFolderDependency = oldFolderDependencies.get(0);
                containingFolderUuid = oldFolderDependency.getDependentItemUuid();
            }
        }

        DbItemDependency linkToParent = new DbItemDependency();
        linkToParent.setItemUuid(item.getItemUuid());
        linkToParent.setAuditUuid(item.getAuditUuid());
        linkToParent.setDependentItemUuid(containingFolderUuid);
        linkToParent.setDependencyTypeId(dependencyType);
        linkToParent.setSaveMode(TableSaveMode.INSERT); //since we've explicitly set all the primaryKey values, we need to set this
        toSave.add(linkToParent);
    }

    protected UUID parseUuidFromStr(String uuidStr) {
        if (uuidStr == null || uuidStr.isEmpty()) {
            return null;
        } else {
            return UUID.fromString(uuidStr);
        }
    }

    protected void moveItems(UUID userUuid, UUID orgUuid, JsonMoveItems parameters) throws Exception {

        UUID folderUuid = parameters.getDestinationFolder();
        if (folderUuid == null) {
            throw new BadRequestException("No destination folder UUID supplied");
        }
        DbActiveItem folderActiveItem = DbActiveItem.retrieveForItemUuid(folderUuid);
        if (!folderActiveItem.getOrganisationUuid().equals(orgUuid)) {
            throw new BadRequestException("Cannot move items to folder owned by another organisation");
        }

        List<DbAbstractTable> toSave = new ArrayList<>();

        DbAudit audit = DbAudit.factoryNow(userUuid, orgUuid);
        UUID auditUuid = audit.getAuditUuid();
        toSave.add(audit);

        for (JsonMoveItem itemParameter: parameters.getItems()) {
            UUID itemUuid = itemParameter.getUuid();

            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);
            DbItem item = DbItem.retrieveForActiveItem(activeItem);

            if (!activeItem.getOrganisationUuid().equals(orgUuid)) {
                throw new BadRequestException("Cannot move items belonging to another organisation");
            }

            item.setAuditUuid(auditUuid);
            item.setSaveMode(TableSaveMode.INSERT); //force insert of new item
            toSave.add(item);

            UUID previousAuditUuid = activeItem.getAuditUuid();
            activeItem.setAuditUuid(auditUuid);
            toSave.add(activeItem);

            //"using" dependencies
            QueryDocument oldQueryDocument = QueryDocumentSerializer.readQueryDocumentFromXml(item.getXmlContent());
            createUsingDependencies(oldQueryDocument, activeItem, toSave);

            //folder dependecies
            createFolderDependency(false, activeItem.getItemTypeId(), item, previousAuditUuid, folderUuid, toSave);
        }

        DatabaseManager.db().writeEntities(toSave);
    }
}
