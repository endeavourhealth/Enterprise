package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.JsonDeleteResponse;
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

        DbAudit audit = DbAudit.factoryNow(userUuid);
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

        List<DbItemDependency> dependecies = DbItemDependency.retrieveForActiveItem(activeItem);
        for (DbItemDependency dependency: dependecies) {

            //only recurse for containing or child folder-type dependencies
            if (dependency.getDependencyTypeId() == DependencyType.IsChildOf
                    || dependency.getDependencyTypeId() == DependencyType.IsContainedWithin) {
                DbActiveItem childActiveItem = DbActiveItem.retrieveForItemUuid(dependency.getDependentItemUuid());
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

                    DbItem usingItem = DbItem.retrieveForUUid(parentItemUuid);
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

        item.setSaveMode(TableSaveMode.INSERT); //force the insert every time, since we allow duplicate rows in the Item table for the same UUID

        //update the AuditUuid on both objects
        DbAudit audit = DbAudit.factoryNow(userUuid);
        UUID previousAuditUuid = item.getAuditUuid();
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

        //build up a list of entities to save, so they can all be inserted atomically
        List<DbAbstractTable> toSave = new ArrayList<>();
        toSave.add(audit);
        toSave.add(item);
        toSave.add(activeItem);

        //work out any UUIDs our new item is dependent on
        if (itemType == DefinitionItemType.LibraryFolder
                || itemType == DefinitionItemType.ReportFolder) {
            carryOverChildDependencies(previousAuditUuid, activeItem, toSave);
        } else {
            createUsingDependencies(queryDocument, activeItem, toSave);
        }

        //work out the child/contains dependency
        updateFolderDependency(itemType, itemUuid, containingFolderUuid, toSave);

        //we can now commit to the DB
        DatabaseManager.db().writeEntities(toSave);
    }

    /**
     * when a folder is renamed or moved, we need to carry over all the child dependencies that link us to our child folders and contents
     */
    private static void carryOverChildDependencies(UUID previousAuditUuid, DbActiveItem activeItem, List<DbAbstractTable> toSave) throws Exception {

        UUID itemUuid = activeItem.getItemUuid();
        UUID newAuditUuid = activeItem.getAuditUuid();
        List<DbItemDependency> oldDependencies = DbItemDependency.retrieveForItem(itemUuid, previousAuditUuid);

        for (DbItemDependency itemDependency: oldDependencies) {

            DbItemDependency newDependency = new DbItemDependency();
            newDependency.setItemUuid(itemUuid);
            newDependency.setAuditUuid(newAuditUuid);
            newDependency.setDependentItemUuid(itemDependency.getDependentItemUuid());
            newDependency.setDependencyTypeId(itemDependency.getDependencyTypeId());

            toSave.add(newDependency);
        }
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

            toSave.add(dependency);
        }
    }

    /**
     * when a libraryItem, report or folder is saved, link it to the containing folder
     */
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

        DbItemDependency linkToParent = null;
        List<DbItemDependency> parents = DbItemDependency.retrieveForDependentItemType(itemUuid, dependencyType);
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
                linkToParent = new DbItemDependency();
                linkToParent.setDependentItemUuid(itemUuid);
                linkToParent.setDependencyTypeId(dependencyType);
            }

            DbActiveItem containingFolderActiveItem = DbActiveItem.retrieveForItemUuid(containingFolderUuid);

            linkToParent.setItemUuid(containingFolderUuid);
            linkToParent.setAuditUuid(containingFolderActiveItem.getAuditUuid());
            toSave.add(linkToParent);
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
