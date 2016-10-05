package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.JsonDeleteResponse;
import org.endeavour.enterprise.json.JsonMoveItem;
import org.endeavour.enterprise.json.JsonMoveItems;
import org.endeavour.enterprise.utility.QueryDocumentReaderFindDependentUuids;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;

import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;

import javax.ws.rs.BadRequestException;
import java.util.*;

public abstract class AbstractItemEndpoint extends AbstractEndpoint {

    private static final int MAX_VALIDATION_ERRORS_FOR_DELETE = 5;

    protected ActiveitemEntity retrieveActiveItem(UUID itemUuid, UUID orgUuid, Short itemTypeDesired) throws Exception {

        ActiveitemEntity activeItem = retrieveActiveItem(itemUuid, orgUuid);

        if (activeItem.getItemtypeid() != itemTypeDesired) {
            throw new RuntimeException("Trying to retrieve a " + itemTypeDesired + " but item is a " + String.valueOf(activeItem.getItemtypeid()));
        }
        return activeItem;
    }

    protected ActiveitemEntity retrieveActiveItem(UUID itemUuid, UUID orgUuid) throws Exception {
        ActiveitemEntity activeItem = ActiveitemEntity.retrieveForItemUuid(itemUuid);

        if (activeItem == null) {
            throw new BadRequestException("UUID does not exist");
        }

        if (!activeItem.getOrganisationuuid().equals(orgUuid)) {
            throw new BadRequestException("Item for another organisation");
        }

        return activeItem;
    }

    protected JsonDeleteResponse deleteItem(UUID itemUuid, UUID orgUuid, UUID userUuid) throws Exception {
        ActiveitemEntity activeItem = retrieveActiveItem(itemUuid, orgUuid);
        ItemEntity item = ItemEntity.retrieveForActiveItem(activeItem);

        //recursively build up the full list of items we want to delete
        List<ItemEntity> itemsToDelete = new ArrayList<>();
        List<ActiveitemEntity> activeItemsToDelete = new ArrayList<>();
        findItemsToDelete(item, activeItem, itemsToDelete, activeItemsToDelete);

        JsonDeleteResponse ret = new JsonDeleteResponse();

        //validate we're not going to break anything with our delete
        validateDelete(itemsToDelete, orgUuid, ret);

        //if we encountered a validation error when checking for references, then return out without deleting
        if (ret.size() > 0) {
            return ret;
        }

        //now do the deleting, building up a list of all entities to update, which is then done atomically
        AuditEntity audit = AuditEntity.factoryNow(userUuid, orgUuid);
        UUID auditUuid = audit.getAudituuid();

        List<ItemEntity> iToDelete = new ArrayList<>();
        List<ActiveitemEntity> aiToDelete = new ArrayList<>();

        for (ItemEntity itemToDelete: itemsToDelete) {
            itemToDelete.setAudituuid(auditUuid);
            itemToDelete.setIsdeleted(true);
            iToDelete.add(itemToDelete);
        }

        for (ActiveitemEntity activeItemToDelete: activeItemsToDelete) {
            activeItemToDelete.setAudituuid(auditUuid);
            activeItemToDelete.setIsdeleted(true);
            aiToDelete.add(activeItemToDelete);
        }

        DataManager db = new DataManager();
        db.saveDeletedItems(audit, iToDelete, aiToDelete);

        return ret;
    }


    private void findItemsToDelete(ItemEntity item, ActiveitemEntity activeItem, List<ItemEntity> itemsToDelete, List<ActiveitemEntity> activeItemsToDelete) throws Exception {

        itemsToDelete.add(item);
        activeItemsToDelete.add(activeItem);

        List<ItemdependencyEntity> dependencies = ItemdependencyEntity.retrieveForDependentItem(item.getItemuuid());
        for (ItemdependencyEntity dependency: dependencies) {

            //only recurse for containing or child folder-type dependencies
            if (dependency.getDependencytypeid() == DependencyType.IsChildOf.getValue()
                    || dependency.getDependencytypeid() == DependencyType.IsContainedWithin.getValue()) {
                ActiveitemEntity childActiveItem = ActiveitemEntity.retrieveForItemUuid(dependency.getItemuuid());
                ItemEntity childItem = ItemEntity.retrieveForActiveItem(childActiveItem);
                findItemsToDelete(childItem, childActiveItem, itemsToDelete, activeItemsToDelete);
            }
        }
    }

    private void validateDelete(List<ItemEntity> itemsToDelete, UUID orgUuid, JsonDeleteResponse response) throws Exception {
        //create a hash of all our items being deleted
        HashSet<UUID> hsUuidsToDelete = new HashSet<>();
        for (ItemEntity item: itemsToDelete) {
            hsUuidsToDelete.add(item.getItemuuid());
        }

        //see if there are any items USING something that we're trying to delete
        for (int i = 0; i < itemsToDelete.size(); i++) {
            ItemEntity item = itemsToDelete.get(i);
            UUID itemUuid = item.getItemuuid();
            List<ItemdependencyEntity> dependencies = ItemdependencyEntity.retrieveForDependentItemType(itemUuid, (short)DependencyType.Uses.getValue());
            for (int j = 0; j < dependencies.size(); j++) {
                ItemdependencyEntity dependency = dependencies.get(i);
                UUID parentItemUuid = dependency.getItemuuid();
                if (!hsUuidsToDelete.contains(parentItemUuid)) {

                    ItemEntity usingItem = ItemEntity.retrieveLatestForUUid(parentItemUuid);
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

    private static void validateItemTypeMatchesContainingFolder(boolean insert, Integer itemType, UUID containingFolderUuid) throws Exception {

        //if saving a new library item or report, it must have a containing folder item
        if (insert
                && containingFolderUuid == null
                && itemType != DefinitionItemType.ReportFolder.getValue()
                && itemType != DefinitionItemType.LibraryFolder.getValue()) {
            throw new BadRequestException("LibraryItems and Reports must have a containing folder UUID");
        }

        //if saving a folder or we're AMENDING a library item or report, then there's notning more to validate
        if (containingFolderUuid == null) {
            return;
        }

        ActiveitemEntity containingFolderActiveItem = ActiveitemEntity.retrieveForItemUuid(containingFolderUuid);
        Short containingFolderType = containingFolderActiveItem.getItemtypeid();
        if (containingFolderType == DefinitionItemType.LibraryFolder.getValue()) {
            //library folders can only contain other library folders and queries etc.
            if (itemType != DefinitionItemType.LibraryFolder.getValue()
                    && itemType != DefinitionItemType.CodeSet.getValue()
                    && itemType != DefinitionItemType.Query.getValue()
                    && itemType != DefinitionItemType.ListOutput.getValue()) {
                throw new BadRequestException("Library folder UUID " + containingFolderUuid + " cannot contain a " + itemType);
            }
        } else if (containingFolderType == DefinitionItemType.ReportFolder.getValue()) {
            //report folders can only contain other report folders and reports
            if (itemType != DefinitionItemType.ReportFolder.getValue()
                    && itemType != DefinitionItemType.Report.getValue()) {
                throw new BadRequestException("Library folder UUID " + containingFolderUuid + " cannot contain a " + itemType);
            }
        } else {
            throw new BadRequestException("Parent folder UUID " + containingFolderUuid + " isn't a folder");
        }
    }

    protected void saveItem(boolean insert, UUID itemUuid, UUID orgUuid, UUID userUuid, Integer itemType,
                            String name, String description, QueryDocument queryDocument, UUID containingFolderUuid) throws Exception {

        //validate the containing folder type matches the itemType we're saving
        validateItemTypeMatchesContainingFolder(insert, itemType, containingFolderUuid);

        ActiveitemEntity activeItem = null;
        ItemEntity item = null;

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
                    && itemType != DefinitionItemType.LibraryFolder.getValue()
                    && itemType != DefinitionItemType.ReportFolder.getValue()) {
                throw new BadRequestException("Must specify a containing folder for new items");
            }

            activeItem = new ActiveitemEntity();
            activeItem.setActiveitemuuid(UUID.randomUUID());
            activeItem.setOrganisationuuid(orgUuid);
            activeItem.setItemuuid(itemUuid);
            activeItem.setItemtypeid(itemType.shortValue());

            item = new ItemEntity();
            item.setItemuuid(itemUuid);
            item.setXmlcontent(""); //when creating folders, we don't store XML, so this needs to be non-null
        } else {
            activeItem = retrieveActiveItem(itemUuid, orgUuid, itemType.shortValue());
            item = ItemEntity.retrieveForActiveItem(activeItem);
        }

        UUID previousAuditUuid = activeItem.getAudituuid();

        //update the AuditUuid on both objects
        AuditEntity audit = AuditEntity.factoryNow(userUuid, orgUuid);
        activeItem.setAudituuid(audit.getAudituuid());
        item.setAudituuid(audit.getAudituuid());

        if (name != null) {
            item.setTitle(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (queryDocument != null) {
            String xmlContent = QueryDocumentSerializer.writeToXml(queryDocument);
            item.setXmlcontent(xmlContent);
        }

        //work out any UUIDs our new item is dependent on
        List<ItemdependencyEntity> itemdependencyEntities = null;

        if (queryDocument != null) {
            itemdependencyEntities = createUsingDependencies(queryDocument, activeItem);
        } else if (item.getXmlcontent().length() > 0) {
            //if a new queryDocument wasn't provided, but the item already had one, we still need to recreate the "using" dependencies
            QueryDocument oldQueryDocument = QueryDocumentSerializer.readQueryDocumentFromXml(item.getXmlcontent());
            itemdependencyEntities = createUsingDependencies(oldQueryDocument, activeItem);
        }

        //work out the child/contains dependency
        ItemdependencyEntity linkToParent = createFolderDependency(insert, itemType.shortValue(), item, previousAuditUuid, containingFolderUuid);
        if (itemdependencyEntities == null)
            itemdependencyEntities = new ArrayList<>();
        itemdependencyEntities.add(linkToParent);

        //we can now commit to the DB
        DataManager db = new DataManager();
        db.saveItems(audit, item, activeItem, itemdependencyEntities);
    }


    /**
     * when a libraryItem or report is saved, process the query document to find all UUIDs that it requires to be run
     */
    private static List<ItemdependencyEntity> createUsingDependencies(QueryDocument queryDocument, ActiveitemEntity activeItem) throws Exception {

        List<ItemdependencyEntity> dependencies = new ArrayList<>();

        //find all the UUIDs in the XML and then see if we need to create or delete dependencies
        QueryDocumentReaderFindDependentUuids reader = new QueryDocumentReaderFindDependentUuids(queryDocument);
        HashSet<UUID> uuidsInDoc = reader.findUuids();

        Iterator<UUID> iter = uuidsInDoc.iterator();
        while (iter.hasNext()) {
            UUID uuidInDoc = iter.next();

            ItemdependencyEntity dependency = new ItemdependencyEntity();
            dependency.setItemuuid(activeItem.getItemuuid());
            dependency.setAudituuid(activeItem.getAudituuid());
            dependency.setDependentitemuuid(uuidInDoc);
            dependency.setDependencytypeid((short)DependencyType.Uses.getValue());

            dependencies.add(dependency);
        }

        return dependencies;
    }

    /**
     * when a libraryItem, report or folder is saved, link it to the containing folder
     */
    private static ItemdependencyEntity createFolderDependency(boolean insert, Short itemType, ItemEntity item, UUID previousAuditUuid, UUID containingFolderUuid) throws Exception {

        //work out the dependency type, based on what item type we're saving
        Integer dependencyType = null;
        if (itemType == DefinitionItemType.LibraryFolder.getValue()
                || itemType == DefinitionItemType.ReportFolder.getValue()) {
            //if we're saving a folder, we're working with "child of" dependencies
            dependencyType = DependencyType.IsChildOf.getValue();
        } else {
            //if we're saving anything else, we're working with "contained within" dependencies
            dependencyType = DependencyType.IsContainedWithin.getValue();
        }

        if (containingFolderUuid == null) {

            //if saving a new item without a folder, then return out as there's no dependency to create
            if (insert) {
                return null;
            }

            //if we're just renaming an item, then no folder UUID would have been supplied,
            //so find out the old folder UUID so we can maintain the relationship
            List<ItemdependencyEntity> oldFolderDependencies = ItemdependencyEntity.retrieveForItemType(item.getItemuuid(), previousAuditUuid, dependencyType.shortValue());
            if (!oldFolderDependencies.isEmpty()) {
                ItemdependencyEntity oldFolderDependency = oldFolderDependencies.get(0);
                containingFolderUuid = oldFolderDependency.getDependentitemuuid();
            }
        }

        ItemdependencyEntity linkToParent = new ItemdependencyEntity();
        linkToParent.setItemuuid(item.getItemuuid());
        linkToParent.setAudituuid(item.getAudituuid());
        linkToParent.setDependentitemuuid(containingFolderUuid);
        linkToParent.setDependencytypeid(dependencyType.shortValue());

        return linkToParent;
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
        ActiveitemEntity folderActiveItem = ActiveitemEntity.retrieveForItemUuid(folderUuid);
        if (!folderActiveItem.getOrganisationuuid().equals(orgUuid)) {
            throw new BadRequestException("Cannot move items to folder owned by another organisation");
        }

        AuditEntity audit = AuditEntity.factoryNow(userUuid, orgUuid);
        UUID auditUuid = audit.getAudituuid();

        List<ItemEntity> iToMove = new ArrayList<>();
        List<ActiveitemEntity> aiToMove = new ArrayList<>();
        List<ItemdependencyEntity> idToMove = new ArrayList<>();

        for (JsonMoveItem itemParameter: parameters.getItems()) {
            UUID itemUuid = itemParameter.getUuid();

            ActiveitemEntity activeItem = ActiveitemEntity.retrieveForItemUuid(itemUuid);
            ItemEntity item = ItemEntity.retrieveForActiveItem(activeItem);

            if (!activeItem.getOrganisationuuid().equals(orgUuid)) {
                throw new BadRequestException("Cannot move items belonging to another organisation");
            }

            item.setAudituuid(auditUuid);
            iToMove.add(item);

            UUID previousAuditUuid = activeItem.getAudituuid();
            activeItem.setAudituuid(auditUuid);
            aiToMove.add(activeItem);

            //"using" dependencies
            QueryDocument oldQueryDocument = QueryDocumentSerializer.readQueryDocumentFromXml(item.getXmlcontent());
            idToMove.addAll(createUsingDependencies(oldQueryDocument, activeItem));

            //folder dependecies
            idToMove.add(createFolderDependency(false, activeItem.getItemtypeid(), item, previousAuditUuid, folderUuid));
        }

        DataManager db = new DataManager();
        db.saveMovedItems(audit, iToMove, aiToMove, idToMove);
    }
}
