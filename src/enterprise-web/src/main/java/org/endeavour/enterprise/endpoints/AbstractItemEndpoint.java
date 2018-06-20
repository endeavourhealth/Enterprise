package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.utility.QueryDocumentReaderFindDependentUuids;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;

import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.json.JsonDeleteResponse;
import org.endeavourhealth.enterprise.core.json.JsonMoveItem;
import org.endeavourhealth.enterprise.core.json.JsonMoveItems;
import org.endeavourhealth.enterprise.core.querydocument.CodeSetHelper;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.CodeSet;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;

import javax.ws.rs.BadRequestException;
import java.util.*;

public abstract class AbstractItemEndpoint extends AbstractEndpoint {

    private static final int MAX_VALIDATION_ERRORS_FOR_DELETE = 5;

    protected ActiveItemEntity retrieveActiveItem(String itemUuid, String orgUuid, Short itemTypeDesired) throws Exception {

        ActiveItemEntity activeItem = retrieveActiveItem(itemUuid, orgUuid);

        if (activeItem.getItemTypeId() != itemTypeDesired) {
            throw new RuntimeException("Trying to retrieve a " + itemTypeDesired + " but item is a " + String.valueOf(activeItem.getItemTypeId()));
        }
        return activeItem;
    }

    protected ActiveItemEntity retrieveActiveItem(String itemUuid, String orgUuid) throws Exception {
        ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(itemUuid);

        if (activeItem == null) {
            throw new BadRequestException("UUID does not exist");
        }

        if (!activeItem.getOrganisationUuid().equals(orgUuid)) {
            throw new BadRequestException("Item for another organisation");
        }

        return activeItem;
    }

    protected JsonDeleteResponse deleteItem(String itemUuid, String orgUuid, String userUuid) throws Exception {
        ActiveItemEntity activeItem = retrieveActiveItem(itemUuid, orgUuid);
        ItemEntity item = ItemEntity.retrieveForActiveItem(activeItem);

        //recursively build up the full list of items we want to delete
        List<ItemEntity> itemsToDelete = new ArrayList<>();
        List<ActiveItemEntity> activeItemsToDelete = new ArrayList<>();
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
        String auditUuid = audit.getAuditUuid();

        List<ItemEntity> iToDelete = new ArrayList<>();
        List<ActiveItemEntity> aiToDelete = new ArrayList<>();

        for (ItemEntity itemToDelete: itemsToDelete) {
            itemToDelete.setAuditUuid(auditUuid);
            itemToDelete.setIsDeleted((byte)1);
            iToDelete.add(itemToDelete);
        }

        for (ActiveItemEntity activeItemToDelete: activeItemsToDelete) {
            activeItemToDelete.setAuditUuid(auditUuid);
            activeItemToDelete.setIsDeleted((byte)1);
            aiToDelete.add(activeItemToDelete);
        }

        DataManager db = new DataManager();
        db.saveDeletedItems(audit, iToDelete, aiToDelete);

        return ret;
    }


    private void findItemsToDelete(ItemEntity item, ActiveItemEntity activeItem, List<ItemEntity> itemsToDelete, List<ActiveItemEntity> activeItemsToDelete) throws Exception {

        itemsToDelete.add(item);
        activeItemsToDelete.add(activeItem);

        List<ItemDependencyEntity> dependencies = ItemDependencyEntity.retrieveForDependentItem(item.getItemUuid());
        for (ItemDependencyEntity dependency: dependencies) {

            //only recurse for containing or child folder-type dependencies
            if (dependency.getDependencyTypeId() == DependencyType.IsChildOf.getValue()
                    || dependency.getDependencyTypeId() == DependencyType.IsContainedWithin.getValue()) {
                ActiveItemEntity childActiveItem = ActiveItemEntity.retrieveForItemUuid(dependency.getItemUuid());
                ItemEntity childItem = ItemEntity.retrieveForActiveItem(childActiveItem);
                findItemsToDelete(childItem, childActiveItem, itemsToDelete, activeItemsToDelete);
            }
        }
    }

    private void validateDelete(List<ItemEntity> itemsToDelete, String orgUuid, JsonDeleteResponse response) throws Exception {
        //create a hash of all our items being deleted
        HashSet<String> hsUuidsToDelete = new HashSet<>();
        for (ItemEntity item: itemsToDelete) {
            hsUuidsToDelete.add(item.getItemUuid());
        }

        //see if there are any items USING something that we're trying to delete
        for (int i = 0; i < itemsToDelete.size(); i++) {
            ItemEntity item = itemsToDelete.get(i);
            String itemUuid = item.getItemUuid();
            List<ItemDependencyEntity> dependencies = ItemDependencyEntity.retrieveForDependentItemType(itemUuid, (short)DependencyType.Uses.getValue());
            for (int j = 0; j < dependencies.size(); j++) {
                ItemDependencyEntity dependency = dependencies.get(i);
                String parentItemUuid = dependency.getItemUuid();
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

    private static void validateItemTypeMatchesContainingFolder(boolean insert, Integer itemType, String containingFolderUuid) throws Exception {

        //if saving a new library item or report, it must have a containing folder item
        if (insert
                && containingFolderUuid == null
                && itemType != DefinitionItemType.ReportFolder.getValue()
                && itemType != DefinitionItemType.LibraryFolder.getValue()) {
            throw new BadRequestException("LibraryItems and Reports must have a containing folder UUID");
        }

        //if saving a folder or we're AMENDING a library item or report, then there's nothing more to validate
        if (containingFolderUuid == null) {
            return;
        }

        ActiveItemEntity containingFolderActiveItem = ActiveItemEntity.retrieveForItemUuid(containingFolderUuid);
        Short containingFolderType = containingFolderActiveItem.getItemTypeId();
        if (containingFolderType == DefinitionItemType.LibraryFolder.getValue()) {
            //library folders can only contain other library folders and queries etc.
            if (itemType != DefinitionItemType.LibraryFolder.getValue()
                    && itemType != DefinitionItemType.CodeSet.getValue()
                    && itemType != DefinitionItemType.Query.getValue()
                    && itemType != DefinitionItemType.ListOutput.getValue()
										&& itemType != DefinitionItemType.Report.getValue()) {
                throw new BadRequestException("Library folder UUID " + containingFolderUuid + " cannot contain a " + itemType);
            }
        } else {
            throw new BadRequestException("Parent folder UUID " + containingFolderUuid + " isn't a folder");
        }
    }

    protected void saveItem(boolean insert, String itemUuid, String orgUuid, String userUuid, Integer itemType,
                            String name, String description, QueryDocument queryDocument, String containingFolderUuid) throws Exception {

        //validate the containing folder type matches the itemType we're saving
        validateItemTypeMatchesContainingFolder(insert, itemType, containingFolderUuid);

        ActiveItemEntity activeItem = null;
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

            activeItem = new ActiveItemEntity();
            activeItem.setActiveItemUuid(UUID.randomUUID().toString());
            activeItem.setOrganisationUuid(orgUuid);
            activeItem.setItemUuid(itemUuid);
            activeItem.setItemTypeId(itemType.shortValue());

            item = new ItemEntity();
            item.setItemUuid(itemUuid);
            item.setXmlContent(""); //when creating folders, we don't store XML, so this needs to be non-null
        } else {
            activeItem = retrieveActiveItem(itemUuid, orgUuid, itemType.shortValue());
            item = ItemEntity.retrieveForActiveItem(activeItem);
        }

        String previousAuditUuid = activeItem.getAuditUuid();

        //update the AuditUuid on both objects
        AuditEntity audit = AuditEntity.factoryNow(userUuid, orgUuid);
        activeItem.setAuditUuid(audit.getAuditUuid());
        item.setAuditUuid(audit.getAuditUuid());

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

        //work out any UUIDs our new item is dependent on
        List<ItemDependencyEntity> itemdependencyEntities = null;

        if (queryDocument != null) {
            itemdependencyEntities = createUsingDependencies(queryDocument, activeItem);
        } else if (item.getXmlContent().length() > 0) {
            //if a new queryDocument wasn't provided, but the item already had one, we still need to recreate the "using" dependencies
            QueryDocument oldQueryDocument = QueryDocumentSerializer.readQueryDocumentFromXml(item.getXmlContent());
            itemdependencyEntities = createUsingDependencies(oldQueryDocument, activeItem);
        }

        //work out the child/contains dependency
        ItemDependencyEntity linkToParent = createFolderDependency(insert, itemType.shortValue(), item, previousAuditUuid, containingFolderUuid);
        if (itemdependencyEntities == null)
            itemdependencyEntities = new ArrayList<>();
        itemdependencyEntities.add(linkToParent);

        //we can now commit to the DB
        DataManager db = new DataManager();
        db.saveItems(audit, item, activeItem, itemdependencyEntities);

        //if the document contains a code set, then populate the CodeSet table accordingly
        if (queryDocument != null)
            populateCodeSets(queryDocument, itemUuid);
    }

    private void populateCodeSets(QueryDocument queryDocument, String itemUuid) throws Exception {

        List<CodeSet> codeSets = new ArrayList<>();

        for (LibraryItem item: queryDocument.getLibraryItem()) {
            CodeSet codeSet = item.getCodeSet();
            if (codeSet != null) {
                codeSets.add(codeSet);
            }
        }

        if (codeSets.isEmpty()) {
            return;
        }

        if (codeSets.size() > 1) {
            throw new Exception("Cannot support query document with multiple code sets (item " + itemUuid + ")");
        }

        CodeSet codeSet = codeSets.get(0);

        try {
            CodeSetHelper.populateCodeSet(codeSet, itemUuid);
        } catch (Exception ex) {
            throw new Exception("Failed to populate code set for item " + itemUuid, ex);
        }
    }

    /**
     * when a libraryItem or report is saved, process the query document to find all UUIDs that it requires to be run
     */
    private static List<ItemDependencyEntity> createUsingDependencies(QueryDocument queryDocument, ActiveItemEntity activeItem) throws Exception {

        List<ItemDependencyEntity> dependencies = new ArrayList<>();

        //find all the UUIDs in the XML and then see if we need to create or delete dependencies
        QueryDocumentReaderFindDependentUuids reader = new QueryDocumentReaderFindDependentUuids(queryDocument);
        HashSet<String> uuidsInDoc = reader.findUuids();

        Iterator<String> iter = uuidsInDoc.iterator();
        while (iter.hasNext()) {
            String uuidInDoc = iter.next();

            ItemDependencyEntity dependency = new ItemDependencyEntity();
            dependency.setItemUuid(activeItem.getItemUuid());
            dependency.setAuditUuid(activeItem.getAuditUuid());
            dependency.setDependentItemUuid(uuidInDoc);
            dependency.setDependencyTypeId((short)DependencyType.Uses.getValue());

            dependencies.add(dependency);
        }

        return dependencies;
    }

    /**
     * when a libraryItem, report or folder is saved, link it to the containing folder
     */
    private static ItemDependencyEntity createFolderDependency(boolean insert, Short itemType, ItemEntity item, String previousAuditUuid, String containingFolderUuid) throws Exception {

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
            List<ItemDependencyEntity> oldFolderDependencies = ItemDependencyEntity.retrieveForItemType(item.getItemUuid(), previousAuditUuid, dependencyType.shortValue());
            if (!oldFolderDependencies.isEmpty()) {
                ItemDependencyEntity oldFolderDependency = oldFolderDependencies.get(0);
                containingFolderUuid = oldFolderDependency.getDependentItemUuid();
            }
        }

        ItemDependencyEntity linkToParent = new ItemDependencyEntity();
        linkToParent.setItemUuid(item.getItemUuid());
        linkToParent.setAuditUuid(item.getAuditUuid());
        linkToParent.setDependentItemUuid(containingFolderUuid);
        linkToParent.setDependencyTypeId(dependencyType.shortValue());

        return linkToParent;
    }

    protected String parseUuidFromStr(String uuidStr) {
        if (uuidStr == null || uuidStr.isEmpty()) {
            return null;
        } else {
            return uuidStr;
        }
    }

    protected void moveItems(String userUuid, String orgUuid, JsonMoveItems parameters) throws Exception {

        String folderUuid = parameters.getDestinationFolder();
        if (folderUuid == null) {
            throw new BadRequestException("No destination folder UUID supplied");
        }
        ActiveItemEntity folderActiveItem = ActiveItemEntity.retrieveForItemUuid(folderUuid);
        if (!folderActiveItem.getOrganisationUuid().equals(orgUuid)) {
            throw new BadRequestException("Cannot move items to folder owned by another organisation");
        }

        AuditEntity audit = AuditEntity.factoryNow(userUuid, orgUuid);
        String auditUuid = audit.getAuditUuid();

        List<ItemEntity> iToMove = new ArrayList<>();
        List<ActiveItemEntity> aiToMove = new ArrayList<>();
        List<ItemDependencyEntity> idToMove = new ArrayList<>();

        for (JsonMoveItem itemParameter: parameters.getItems()) {
            String itemUuid = itemParameter.getUuid();

            ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(itemUuid);
            ItemEntity item = ItemEntity.retrieveForActiveItem(activeItem);

            if (!activeItem.getOrganisationUuid().equals(orgUuid)) {
                throw new BadRequestException("Cannot move items belonging to another organisation");
            }

            item.setAuditUuid(auditUuid);
            iToMove.add(item);

            String previousAuditUuid = activeItem.getAuditUuid();
            activeItem.setAuditUuid(auditUuid);
            aiToMove.add(activeItem);

            //"using" dependencies
            QueryDocument oldQueryDocument = QueryDocumentSerializer.readQueryDocumentFromXml(item.getXmlContent());
            idToMove.addAll(createUsingDependencies(oldQueryDocument, activeItem));

            //folder dependecies
            idToMove.add(createFolderDependency(false, activeItem.getItemTypeId(), item, previousAuditUuid, folderUuid));
        }

        DataManager db = new DataManager();
        db.saveMovedItems(audit, iToMove, aiToMove, idToMove);
    }
}
