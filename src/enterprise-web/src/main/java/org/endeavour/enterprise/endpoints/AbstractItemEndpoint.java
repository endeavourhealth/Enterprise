package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavour.enterprise.model.DependencyType;
import org.endeavour.enterprise.model.database.*;
import org.endeavour.enterprise.model.json.JsonDeleteResponse;
import org.endeavour.enterprise.model.utility.QueryDocumentReaderFindDependentUuids;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentParser;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;

import javax.ws.rs.BadRequestException;
import java.util.*;

/**
 * Created by Drew on 25/02/2016.
 */
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

    protected DbItem retrieveItem(DbActiveItem activeItem) throws Exception {
        UUID itemUuid = activeItem.getItemUuid();
        int currentVersion = activeItem.getVersion();

        DbItem item = DbItem.retrieveForUuidVersion(itemUuid, currentVersion);
        if (item == null) {
            throw new BadRequestException("UUID doesn't exist");
        }

        return item;
    }

    protected JsonDeleteResponse deleteItem(UUID itemUuid, UUID orgUuid, UUID userUuid) throws Exception {
        DbActiveItem activeItem = retrieveActiveItem(itemUuid, orgUuid);
        DbItem item = retrieveItem(activeItem);

        //recursively build up the full list of items we want to delete
        List<DbItem> itemsToDelete = new ArrayList<>();
        findItemsToDelete(itemsToDelete, orgUuid, item);

        JsonDeleteResponse ret = new JsonDeleteResponse();

        //validate we're not going to break anything with our delete
        validateDelete(itemsToDelete, orgUuid, ret);

        //if we encountered a validation error when checking for references, then return out without deleting
        if (ret.size() > 0) {
            return ret;
        }

        //now do the deleting, building up a list of all entities to update, which is then done atomically
        List<DbAbstractTable> toSave = new ArrayList<>();

        for (DbItem itemToDelete: itemsToDelete) {
            reallyDeleteItem(itemToDelete, userUuid, toSave);
        }

        DatabaseManager.db().writeEntities(toSave);
        return ret;
    }

    private void reallyDeleteItem(DbItem itemToDelete, UUID userUuid, List<DbAbstractTable> toSave) throws Exception {
        UUID itemUuid = itemToDelete.getPrimaryUuid();
        int version = itemToDelete.getVersion() + 1;

        //update the item
        itemToDelete.setVersion(version);
        itemToDelete.setEndUserUuid(userUuid);
        itemToDelete.setTimeStamp(new Date());
        itemToDelete.setIsDeleted(true);

        //save
        itemToDelete.setSaveMode(TableSaveMode.INSERT); //remember to always force an insert for items
        toSave.add(itemToDelete);

        //delete the ActiveItem
        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);
        activeItem.setSaveMode(TableSaveMode.DELETE);
        toSave.add(activeItem);

        //delete the ActiveItemDependencies too
        List<DbActiveItemDependency> dependencies = DbActiveItemDependency.retrieveForItem(itemUuid);
        for (int i = 0; i < dependencies.size(); i++) {
            DbActiveItemDependency dependency = dependencies.get(i);
            dependency.setSaveMode(TableSaveMode.DELETE);
            toSave.add(dependency);
        }

        dependencies = DbActiveItemDependency.retrieveForDependentItem(itemUuid);
        for (int i = 0; i < dependencies.size(); i++) {
            DbActiveItemDependency dependency = dependencies.get(i);
            dependency.setSaveMode(TableSaveMode.DELETE);
            toSave.add(dependency);
        }
    }

    private void findItemsToDelete(List<DbItem> itemsToDelete, UUID orgUuid, DbItem item) throws Exception {
        itemsToDelete.add(item);

        //find all things hanging off our entity
        UUID itemUuid = item.getPrimaryUuid();
        List<DbItem> children = DbItem.retrieveDependentItems(orgUuid, itemUuid, DependencyType.IsChildOf);
        for (int i = 0; i < children.size(); i++) {
            DbItem child = children.get(i);
            findItemsToDelete(itemsToDelete, orgUuid, child);
        }

        List<DbItem> contents = DbItem.retrieveDependentItems(orgUuid, itemUuid, DependencyType.IsContainedWithin);
        for (int i = 0; i < contents.size(); i++) {
            DbItem content = contents.get(i);
            findItemsToDelete(itemsToDelete, orgUuid, content);
        }
    }

    private void validateDelete(List<DbItem> itemsToDelete, UUID orgUuid, JsonDeleteResponse response) throws Exception {
        //create a hash of all our items being deleted
        HashSet<UUID> hsUuidsToDelete = new HashSet<>();
        for (DbItem item: itemsToDelete) {
            hsUuidsToDelete.add(item.getPrimaryUuid());
        }

        //see if there are any items USING something that we're trying to delete
        for (int i = 0; i < itemsToDelete.size(); i++) {
            DbItem item = itemsToDelete.get(i);
            UUID itemUuid = item.getPrimaryUuid();
            List<DbActiveItemDependency> dependencies = DbActiveItemDependency.retrieveForDependentItemType(itemUuid, DependencyType.Uses);
            for (int j = 0; j < dependencies.size(); j++) {
                DbActiveItemDependency dependency = dependencies.get(i);
                UUID parentItemUuid = dependency.getItemUuid();
                if (!hsUuidsToDelete.contains(parentItemUuid)) {

                    DbItem usingItem = DbItem.retrieveForUuidLatestVersion(orgUuid, parentItemUuid);
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

    private static void validateItemTypeMatchesContainingFolder(DefinitionItemType itemType, UUID containingFolderUuid) throws Exception {
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
        validateItemTypeMatchesContainingFolder(itemType, containingFolderUuid);

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

            int firstVersion = 1;

            activeItem = new DbActiveItem();
            activeItem.setOrganisationUuid(orgUuid);
            activeItem.setItemUuid(itemUuid);
            activeItem.setVersion(firstVersion);
            activeItem.setItemTypeId(itemType);

            item = new DbItem();
            item.setPrimaryUuid(itemUuid);
            item.setVersion(firstVersion);
            item.setXmlContent(""); //when creating folders, we don't store XML, so this needs to be non-null
        } else {
            activeItem = retrieveActiveItem(itemUuid, orgUuid, itemType);
            item = retrieveItem(activeItem);

            //update the version
            int version = activeItem.getVersion() + 1;

            activeItem.setVersion(version);
            item.setVersion(version);
        }

        //always update the audit fields
        item.setEndUserUuid(userUuid);
        item.setTimeStamp(new Date());

        if (name != null) {
            item.setTitle(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (queryDocument != null) {
            String xmlContent = QueryDocumentParser.writeToXml(queryDocument);
            item.setXmlContent(xmlContent);
        }

        //build up a list of entities to save, so they can all be inserted atomically
        List<DbAbstractTable> toSave = new ArrayList<>();

        item.setSaveMode(TableSaveMode.INSERT); //force the insert every time, since we allow duplicate rows in the Item table for the same UUID
        toSave.add(item);

        toSave.add(activeItem);

        //work out any UUIDs our new item is dependent on
        updateUsingDependencies(queryDocument, itemUuid, toSave);

        //work out the child/contains dependency
        updateFolderDependency(itemType, itemUuid, containingFolderUuid, toSave);

        //we can now commit to the DB
        DatabaseManager.db().writeEntities(toSave);
    }

    private static void updateFolderDependency(DefinitionItemType itemType, UUID itemUuid, UUID containingFolderUuid, List<DbAbstractTable> toSave) throws Exception {

        //if we're saving a folder, we're working with "child of" dependencies
        //if we're saving anything else, we're working with "contained within" dependencies
        DependencyType dependencyType = null;
        if (itemType == DefinitionItemType.LibraryFolder
                || itemType == DefinitionItemType.ReportFolder) {
            dependencyType = DependencyType.IsChildOf;
        } else {
            dependencyType = DependencyType.IsContainedWithin;
        }

        DbActiveItemDependency linkToParent = null;
        List<DbActiveItemDependency> parents = DbActiveItemDependency.retrieveForDependentItemType(itemUuid, dependencyType);
        if (parents.size() == 1) {
            linkToParent = parents.get(0);
        } else if (parents.size() > 1) {
            throw new BadRequestException("Multiple dependencies that folder is child in");
        }

        if (containingFolderUuid == null) {
            //if we want it to be a top-level folder, then we must DELETE any existing dependency that makes us a child of another folder
            //but only do this when saving FOLDERS. When saving reports etc., treat a null folder UUID to mean don't change anything
            if (linkToParent != null
                    && (itemType == DefinitionItemType.ReportFolder
                    || itemType == DefinitionItemType.LibraryFolder)) {

                linkToParent.setSaveMode(TableSaveMode.DELETE);
                toSave.add(linkToParent);
            }
        } else {
            //if we want to be a child folder, we need to ensure we have a dependency entity
            if (linkToParent == null) {
                linkToParent = new DbActiveItemDependency();
                linkToParent.setDependentItemUuid(itemUuid);
                linkToParent.setDependencyTypeId(dependencyType);
            }

            linkToParent.setItemUuid(containingFolderUuid);
            toSave.add(linkToParent);
        }

    }

    private static HashSet<UUID> findUuidsInXml(QueryDocument queryDocument) {

        QueryDocumentReaderFindDependentUuids reader = new QueryDocumentReaderFindDependentUuids(queryDocument);
        return reader.findUuids();
    }

    private static void updateUsingDependencies(QueryDocument queryDocument, UUID itemUuid, List<DbAbstractTable> toSave) throws Exception {

        if (queryDocument == null) {
            return;
        }

        //retrieve all the existing "uses" dependencies and hash by their dependent UUID
        HashMap<UUID, DbActiveItemDependency> hmDependencies = new HashMap<>();
        List<DbActiveItemDependency> usingDependencies = DbActiveItemDependency.retrieveForItemType(itemUuid, DependencyType.Uses);
        for (int i = 0; i < usingDependencies.size(); i++) {
            DbActiveItemDependency dependency = usingDependencies.get(i);
            UUID dependentUuid = dependency.getDependentItemUuid();
            hmDependencies.put(dependentUuid, dependency);
        }

        //find all the UUIDs in the XML and then see if we need to create or delete dependencies
        HashSet<UUID> usingUuids = findUuidsInXml(queryDocument);

        Iterator<UUID> iter = usingUuids.iterator();
        while (iter.hasNext()) {
            UUID usingUuid = iter.next();

            DbActiveItemDependency dependency = hmDependencies.remove(usingUuid);
            if (dependency == null) {
                dependency = new DbActiveItemDependency();
                dependency.setItemUuid(itemUuid);
                dependency.setDependentItemUuid(usingUuid);
                dependency.setDependencyTypeId(DependencyType.Uses);

                toSave.add(dependency);
            }
        }

        //any remaining ones in the hashmap can now be deleted
        Iterator<DbActiveItemDependency> remainingIterator = hmDependencies.values().iterator();
        while (remainingIterator.hasNext()) {
            DbActiveItemDependency remainder = remainingIterator.next();

            remainder.setSaveMode(TableSaveMode.DELETE);
            toSave.add(remainder);
        }
    }

    protected UUID parseUuidFromStr(String uuidStr) {
        if (uuidStr == null || uuidStr.isEmpty()) {
            return null;
        } else {
            return UUID.fromString(uuidStr);
        }
    }
}
