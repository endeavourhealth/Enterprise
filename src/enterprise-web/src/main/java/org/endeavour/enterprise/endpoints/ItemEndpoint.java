package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.model.DefinitionItemType;

import javax.ws.rs.BadRequestException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Drew on 25/02/2016.
 */
public abstract class ItemEndpoint extends Endpoint
{
    protected DbActiveItem retrieveActiveItem(UUID itemUuid, UUID orgUuid, DefinitionItemType itemTypeDesired) throws Exception
    {
        //retrieve the activeItem, so we know the latest version
        DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(itemUuid);
        if (activeItem == null
                || activeItem.getItemType() != DefinitionItemType.Query)
        {
            throw new BadRequestException("UUID is not a valid query");
        }
        if (!activeItem.getOrganisationUuid().equals(orgUuid))
        {
            throw new BadRequestException("Item for another organisation");
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

        //TODO: 2016-02-25 DL - need to follow dependancies UP to validate the delete

        int version = activeItem.getVersion()+1;

        //update the activeItem
        activeItem.setVersion(version);

        //update the item
        item.setVersion(version);
        item.setEndUserUuid(userUuid);
        item.setTimeStamp(new Date());
        item.setIsDeleted(true);

        //save
        item.saveToDbInsert(); //remember to always force an insert for items
        activeItem.saveToDb();
    }

    protected UUID saveItem(UUID itemUuid, UUID orgUuid, UUID userUuid, DefinitionItemType itemType,
                            String name, String description, String xmlContent, Boolean isDeleted) throws Exception
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

        return itemUuid;
    }
}
