package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbActiveItemDependency;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavour.enterprise.model.DependencyType;

import javax.ws.rs.BadRequestException;
import java.util.*;

/**
 * Created by Drew on 25/02/2016.
 */
public abstract class ItemEndpoint extends Endpoint
{
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
        if (activeItem.getItemType() != itemTypeDesired)
        {
            throw new RuntimeException("Trying to retrieve a " + itemTypeDesired + " but item is a " + activeItem.getItemType());
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

        if (activeItem.getItemType() != itemTypeDesired)
        {
            throw new RuntimeException("Trying to delete a " + itemTypeDesired + " but item is a " + activeItem.getItemType());
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
        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);
        int version = activeItem.getVersion()+1;

        //update the activeItem
        activeItem.setVersion(version);

        //update the item
        itemToDelete.setVersion(version);
        itemToDelete.setEndUserUuid(userUuid);
        itemToDelete.setTimeStamp(new Date());
        itemToDelete.setIsDeleted(true);

        //save
        itemToDelete.saveToDbInsert(); //remember to always force an insert for items
        activeItem.saveToDb();
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
            List<DbActiveItemDependency> dependencies = DbActiveItemDependency.retrieveForDependentItem(itemUuid, DependencyType.Uses);
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


    protected UUID saveItem(UUID itemUuid, UUID orgUuid, UUID userUuid, DefinitionItemType itemType,
                            String name, String description, String xmlContent, Boolean isDeleted, UUID containingFolderUuid) throws Exception
    {
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

            int firstVersion = 1;

            activeItem = new DbActiveItem();
            activeItem.setOrganisationUuid(orgUuid);
            //activeItem.setItemUuid(); //done after saving the item
            activeItem.setVersion(firstVersion);
            activeItem.setItemType(itemType);

            item = new DbItem();
            item.setVersion(firstVersion);
        }
        else
        {
            activeItem = retrieveActiveItem(itemUuid, orgUuid, itemType);
            item = retrieveItem(activeItem);

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
        if (isDeleted != null)
        {
            item.setIsDeleted(isDeleted.booleanValue());
        }

        //TODO: 2016-02-25 DL - need to examine XML to work out and save dependencies

        //save the item first, as we need to do this to assign the UUID for it
        //force the insert every time, since we allow duplicate rows in the Item table for the same UUID
        item.saveToDbInsert();

        //only after saving the item are we sure we've got a UUID, so get it and set on the activeItem
        itemUuid = item.getPrimaryUuid();
        activeItem.setItemUuid(itemUuid);

        //now we can save the active item
        activeItem.saveToDb();

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
        List<DbActiveItemDependency> parents = DbActiveItemDependency.retrieveForDependentItem(itemUuid, dependencyType);
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
            if (linkToParent != null)
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
                linkToParent.setDependencyType(dependencyType);
            }

            linkToParent.setItemUuid(containingFolderUuid);
            linkToParent.saveToDb();
        }

        return itemUuid;
    }
}
