package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbActiveItemDependency;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavour.enterprise.model.DependencyType;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Drew on 25/02/2016.
 */
public abstract class ItemEndpoint extends Endpoint
{
    private static Pattern guidRegex = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    protected DbActiveItem retrieveActiveItem(UUID itemUuid, UUID orgUuid, DefinitionItemType itemTypeDesired) throws Exception
    {
        //retrieve the activeItem, so we know the latest version
        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);
        if (activeItem == null)
        {
            throw new BadRequestException("UUID does not exist");
        }
        if (!activeItem.getOrganisationUuid().equals(orgUuid))
        {
            throw new BadRequestException("Item for another organisation");
        }
        if (activeItem.getItemTypeId() != itemTypeDesired)
        {
            throw new RuntimeException("Trying to retrieve a " + itemTypeDesired + " but item is a " + activeItem.getItemTypeId());
        }
        return activeItem;
    }

    protected DbItem retrieveItem(DbActiveItem activeItem) throws Exception
    {
        UUID itemUuid = activeItem.getItemUuid();
        int currentVersion = activeItem.getVersion();

        DbItem item = DbItem.retrieveForUuidVersion(itemUuid, currentVersion);
        if (item == null)
        {
            throw new BadRequestException("UUID doesn't exist");
        }

        return item;
    }

    protected void deleteItem(UUID itemUuid, UUID orgUuid, UUID userUuid, DefinitionItemType itemTypeDesired) throws Exception
    {
        DbActiveItem activeItem = retrieveActiveItem(itemUuid, orgUuid, itemTypeDesired);
        DbItem item = retrieveItem(activeItem);

        if (activeItem.getItemTypeId() != itemTypeDesired)
        {
            throw new RuntimeException("Trying to delete a " + itemTypeDesired + " but item is a " + activeItem.getItemTypeId());
        }

        //recursively build up the full list of items we want to delete
        List<DbItem> itemsToDelete = new ArrayList<DbItem>();
        findItemsToDelete(itemsToDelete, orgUuid, item);

        //validate we're not going to break anything with our delete
        validateDelete(itemsToDelete, orgUuid);

        //now do the deleting, working BACKWARDS, so any errors don't leave us in a broken state
        for (int i=itemsToDelete.size()-1; i>=0; i--)
        {
            DbItem itemToDelete = itemsToDelete.get(i);
            reallyDeleteItem(itemToDelete, userUuid);
        }
    }
    private void reallyDeleteItem(DbItem itemToDelete, UUID userUuid) throws Exception
    {
        UUID itemUuid = itemToDelete.getPrimaryUuid();
        int version = itemToDelete.getVersion()+1;

        //update the item
        itemToDelete.setVersion(version);
        itemToDelete.setEndUserUuid(userUuid);
        itemToDelete.setTimeStamp(new Date());
        itemToDelete.setIsDeleted(true);

        //save
        itemToDelete.saveToDbInsert(); //remember to always force an insert for items

        //2016-03-01 DL - delete the ActiveItem
        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);
        activeItem.deleteFromDb();
        //activeItem.saveToDb();

        //2016-03-01 DL - delete the ActiveItemDependencies too
        List<DbActiveItemDependency> dependencies = DbActiveItemDependency.retrieveForItem(itemUuid);
        for (int i=0; i<dependencies.size(); i++)
        {
            DbActiveItemDependency dependency = dependencies.get(i);
            dependency.deleteFromDb();
        }

        dependencies = DbActiveItemDependency.retrieveForDependentItem(itemUuid);
        for (int i=0; i<dependencies.size(); i++)
        {
            DbActiveItemDependency dependency = dependencies.get(i);
            dependency.deleteFromDb();
        }
    }
    private void findItemsToDelete(List<DbItem> itemsToDelete, UUID orgUuid, DbItem item) throws Exception
    {
        itemsToDelete.add(item);

        //find all things hanging off our entity
        UUID itemUuid = item.getPrimaryUuid();
        List<DbItem> children = DbItem.retrieveDependentItems(orgUuid, itemUuid, DependencyType.IsChildOf);
        for (int i=0; i<children.size(); i++)
        {
            DbItem child = children.get(i);
            findItemsToDelete(itemsToDelete, orgUuid, child);
        }

        List<DbItem> contents = DbItem.retrieveDependentItems(orgUuid, itemUuid, DependencyType.IsContainedWithin);
        for (int i=0; i<contents.size(); i++)
        {
            DbItem content = contents.get(i);
            findItemsToDelete(itemsToDelete, orgUuid, content);
        }
    }
    private void validateDelete(List<DbItem> itemsToDelete, UUID orgUuid) throws Exception
    {
        //create a hash of all our items being deleted
        HashSet<UUID> hsUuidsToDelete = new HashSet<UUID>();
        for (int i=0; i<itemsToDelete.size(); i++)
        {
            DbItem item = itemsToDelete.get(i);
            hsUuidsToDelete.add(item.getPrimaryUuid());
        }

        //see if there are any items USING something that we're trying to delete
        for (int i=0; i<itemsToDelete.size(); i++)
        {
            DbItem item = itemsToDelete.get(i);
            UUID itemUuid = item.getPrimaryUuid();
            List<DbActiveItemDependency> dependencies = DbActiveItemDependency.retrieveForDependentItemType(itemUuid, DependencyType.Uses);
            for (int j=0; j<dependencies.size(); j++)
            {
                DbActiveItemDependency dependency = dependencies.get(i);
                UUID parentItemUuid = dependency.getItemUuid();
                if (!hsUuidsToDelete.contains(parentItemUuid))
                {
                    throw new BadRequestException("Item " + itemUuid + " is cannot be deleted, as it's used by item " + parentItemUuid);
                }
            }
        }
    }

    private static void validateItemTypeMatchesContainingFolder(DefinitionItemType itemType, UUID containingFolderUuid) throws Exception
    {
        if (containingFolderUuid == null)
        {
            return;
        }

        DbActiveItem containingFolderActiveItem = DbActiveItem.retrieveForItemUuid(containingFolderUuid);
        DefinitionItemType containingFolderType = containingFolderActiveItem.getItemTypeId();
        if (containingFolderType == DefinitionItemType.LibraryFolder)
        {
            //library folders can only contain other library folders and queries etc.
            if (itemType != DefinitionItemType.LibraryFolder
                    && itemType != DefinitionItemType.CodeSet
                    && itemType != DefinitionItemType.Query
                    && itemType != DefinitionItemType.ListOutput)
            {
                throw new BadRequestException("Library folder UUID " + containingFolderUuid + " cannot contain a " + itemType);
            }
        }
        else if (containingFolderType == DefinitionItemType.ReportFolder)
        {
            //report folders can only contain other report folders and reports
            if (itemType != DefinitionItemType.ReportFolder
                    && itemType != DefinitionItemType.Report)
            {
                throw new BadRequestException("Library folder UUID " + containingFolderUuid + " cannot contain a " + itemType);
            }
        }
        else
        {
            throw new BadRequestException("Parent folder UUID " + containingFolderUuid + " isn't a folder");
        }
    }

    protected UUID saveItem(UUID itemUuid, UUID orgUuid, UUID userUuid, DefinitionItemType itemType,
                            String name, String description, String xmlContent, Boolean isDeleted, UUID containingFolderUuid) throws Exception
    {
        //validate the containing folder type matches the itemType we're saving
        validateItemTypeMatchesContainingFolder(itemType, containingFolderUuid);

        DbActiveItem activeItem = null;
        DbItem item = null;

        if (itemUuid == null)
        {
            //if creating a NEW item, we need to validate we have the content we need
            if (name == null)
            {
                throw new BadRequestException("No name specified");
            }
            if (description == null)
            {
                //we can live without a description, but need a non-null value
                description = "";
            }
            if (xmlContent == null)
            {
                throw new BadRequestException("No XmlContent specified");
            }
            if (isDeleted != null
                    && isDeleted.booleanValue())
            {
                throw new BadRequestException("Can't create a brand new deleted item");
            }
            if (containingFolderUuid == null
                    && itemType != DefinitionItemType.LibraryFolder
                    && itemType != DefinitionItemType.ReportFolder)
            {
                throw new BadRequestException("Must specify a containing folder for new items");
            }

            int firstVersion = 1;

            activeItem = new DbActiveItem();
            activeItem.setOrganisationUuid(orgUuid);
            //activeItem.setItemUuid(); //done after saving the item
            activeItem.setVersion(firstVersion);
            activeItem.setItemTypeId(itemType);

            item = new DbItem();
            item.setVersion(firstVersion);
        }
        else
        {
            activeItem = retrieveActiveItem(itemUuid, orgUuid, itemType);
            item = retrieveItem(activeItem);

            //deleting an item has a fair amount of special rules, so prevent deleting through the save... methods
            if (isDeleted != null
                    && isDeleted.booleanValue()
                    && !item.getIsDeleted())
            {
                throw new BadRequestException("Cannot delete items using the save... methods - use the delete... methods instead");
            }

            //update the version
            int version = activeItem.getVersion()+1;
            activeItem.setVersion(version);

            item.setVersion(version);
        }

        //always update the audit fields
        item.setEndUserUuid(userUuid);
        item.setTimeStamp(new Date());

        if (name != null)
        {
            item.setTitle(name);
        }
        if (description != null)
        {
            item.setDescription(description);
        }
        if (xmlContent != null)
        {
            item.setXmlContent(xmlContent);
        }
        //we don't allow deleting through this method, so remove this
        /*if (isDeleted != null)
        {
            item.setIsDeleted(isDeleted.booleanValue());
        }*/

        //save the item first, as we need to do this to assign the UUID for it
        //force the insert every time, since we allow duplicate rows in the Item table for the same UUID
        item.saveToDbInsert();

        //only after saving the item are we sure we've got a UUID, so get it and set on the activeItem
        itemUuid = item.getPrimaryUuid();
        activeItem.setItemUuid(itemUuid);

        //now we can save the active item
        activeItem.saveToDb();

        //if our XML has changed, update our dependencies that say we're USING other things
        updateUsingDependencies(xmlContent, itemUuid);

        //now work out the parent folder link
        DependencyType dependencyType = null;
        if (itemType == DefinitionItemType.LibraryFolder
                || itemType == DefinitionItemType.ReportFolder)
        {
            dependencyType = DependencyType.IsChildOf;
        }
        else
        {
            dependencyType = DependencyType.IsContainedWithin;
        }

        DbActiveItemDependency linkToParent = null;
        List<DbActiveItemDependency> parents = DbActiveItemDependency.retrieveForDependentItemType(itemUuid, dependencyType);
        if (parents.size() == 1)
        {
            linkToParent = parents.get(0);
        }
        else if (parents.size() > 1)
        {
            throw new BadRequestException("Multiple dependencies that folder is child in");
        }

        if (containingFolderUuid == null)
        {
            //if we want it to be a top-level folder, then we must DELETE any existing dependency that makes us a child of another folder
            //but only do this when saving FOLDERS. When saving reports etc., treat a null folder UUID to mean don't change anything
            if (linkToParent != null
                    && (itemType == DefinitionItemType.ReportFolder
                    || itemType == DefinitionItemType.LibraryFolder))
            {
                linkToParent.deleteFromDb();
            }
        }
        else
        {
            //if we want to be a child folder, we need to ensure we have a dependency entity
            if (linkToParent == null)
            {
                linkToParent = new DbActiveItemDependency();
                linkToParent.setDependentItemUuid(itemUuid);
                linkToParent.setDependencyTypeId(dependencyType);
            }

            linkToParent.setItemUuid(containingFolderUuid);
            linkToParent.saveToDb();
        }

        return itemUuid;
    }

    private static HashSet<UUID> findUuidsInXml(String xml)
    {
        HashSet<UUID> ret = new HashSet<>();

        //for now, just pull out anything that looks like a UUID
        Matcher m = guidRegex.matcher(xml);
        while (m.find())
        {
            String uuidStr = m.group();
            UUID uuid = UUID.fromString(uuidStr);
            ret.add(uuid);
        }

        return ret;
    }
    private static void updateUsingDependencies(String xml, UUID itemUuid) throws Exception
    {
        if (xml == null)
        {
            return;
        }

        //retrieve all the existing "uses" dependencies and hash by their dependent UUID
        HashMap<UUID, DbActiveItemDependency> hmDependencies = new HashMap<>();
        List<DbActiveItemDependency> usingDependencies = DbActiveItemDependency.retrieveForItemType(itemUuid, DependencyType.Uses);
        for (int i=0; i<usingDependencies.size(); i++)
        {
            DbActiveItemDependency dependency = usingDependencies.get(i);
            UUID dependentUuid = dependency.getDependentItemUuid();
            hmDependencies.put(dependentUuid, dependency);
        }

        //find all the UUIDs in the XML and then see if we need to create or delete dependencies
        HashSet<UUID> usingUuids = findUuidsInXml(xml);

        Iterator<UUID> iter = usingUuids.iterator();
        while (iter.hasNext())
        {
            UUID usingUuid = iter.next();

            DbActiveItemDependency dependency = hmDependencies.remove(usingUuid);
            if (dependency == null)
            {
                dependency = new DbActiveItemDependency();
                dependency.setItemUuid(itemUuid);
                dependency.setDependentItemUuid(usingUuid);
                dependency.setDependencyTypeId(DependencyType.Uses);
                dependency.saveToDb();
            }
        }

        //any remaining ones in the hashmap can now be deleted
        Iterator<DbActiveItemDependency> remainingIterator = hmDependencies.values().iterator();
        while (remainingIterator.hasNext())
        {
            DbActiveItemDependency remainder = remainingIterator.next();
            remainder.deleteFromDb();;
        }
    }
}
