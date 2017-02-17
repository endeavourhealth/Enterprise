package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.JsonDeleteResponse;
import org.endeavour.enterprise.json.JsonFolderContent;
import org.endeavour.enterprise.json.JsonFolderContentsList;
import org.endeavour.enterprise.json.JsonMoveItems;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;

import org.endeavourhealth.enterprise.core.database.models.ActiveItemEntity;
import org.endeavourhealth.enterprise.core.database.models.ItemDependencyEntity;
import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.UUID;

@Path("/library")
public final class LibraryEndpoint extends AbstractItemEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getLibraryItem")
    public Response getLibraryItem(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception {
        super.setLogbackMarkers(sc);

        String libraryItemUuid = uuidStr;

        LOG.trace("GettingLibraryItem for UUID {}", libraryItemUuid);

        ItemEntity item = ItemEntity.retrieveLatestForUUid(libraryItemUuid);
        String xml = item.getXmlContent();

        LibraryItem ret = QueryDocumentSerializer.readLibraryItemFromXml(xml);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveLibraryItem")
    public Response saveLibraryItem(@Context SecurityContext sc, LibraryItem libraryItem) throws Exception {
        super.setLogbackMarkers(sc);

        String userUuid = "B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32";
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        String libraryItemUuid = parseUuidFromStr(libraryItem.getUuid());
        String name = libraryItem.getName();
        String description = libraryItem.getDescription();
        String folderUuid = parseUuidFromStr(libraryItem.getFolderUuid());

        Query query = libraryItem.getQuery();
        DataSource dataSource = libraryItem.getDataSource();
        Test test = libraryItem.getTest();
        CodeSet codeSet = libraryItem.getCodeSet();
        ListReport listOutput = libraryItem.getListReport();

        LOG.trace(String.format("SavingLibraryItem UUID %s, Name %s FolderUuid %s", libraryItemUuid, name, folderUuid));

        QueryDocument doc = new QueryDocument();
        doc.getLibraryItem().add(libraryItem);

        //work out the item type (query, test etc.) from the content passed up
        Short type = null;
        if (query != null) {
            type = (short)DefinitionItemType.Query.getValue();
        } else if (dataSource != null) {
            type = (short)DefinitionItemType.DataSource.getValue();
        } else if (test != null) {
            type = (short)DefinitionItemType.Test.getValue();
        } else if (codeSet != null) {
            type = (short)DefinitionItemType.CodeSet.getValue();
        } else if (listOutput != null) {
            type = (short)DefinitionItemType.ListOutput.getValue();
        } else {
            //if we've been passed no proper content, we might just be wanting to rename an existing item,
            //so work out the type from what's on the DB already
            if (libraryItemUuid == null) {
                throw new BadRequestException("Can't save LibraryItem without some content (e.g. query, test etc.)");
            }

            ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(libraryItemUuid);
            type = activeItem.getItemTypeId();
            doc = null; //clear this, because we don't want to overwrite what's on the DB with an empty query doc
        }

        boolean inserting = libraryItemUuid == null;
        if (inserting) {
            libraryItemUuid = UUID.randomUUID().toString();
            libraryItem.setUuid(libraryItemUuid.toString());
        }

        super.saveItem(inserting, libraryItemUuid, orgUuid, userUuid, type.intValue(), name, description, doc, folderUuid);

        //return the UUID of the libraryItem
        LibraryItem ret = new LibraryItem();
        ret.setUuid(libraryItemUuid.toString());

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteLibraryItem")
    public Response deleteLibraryItem(@Context SecurityContext sc, LibraryItem libraryItem) throws Exception {
        super.setLogbackMarkers(sc);

        String libraryItemUuid = parseUuidFromStr(libraryItem.getUuid());
        String userUuid = "B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32";
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";
        
        LOG.trace("DeletingLibraryItem UUID {}", libraryItemUuid);

        JsonDeleteResponse ret = deleteItem(libraryItemUuid, orgUuid, userUuid);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getContentNamesForReportLibraryItem")
    public Response getContentNamesForReportLibraryItem(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception {
        super.setLogbackMarkers(sc);

        String itemUuid = uuidStr;

        LOG.trace("getContentNamesforReportLibraryItem for UUID {}", itemUuid);

        JsonFolderContentsList ret = new JsonFolderContentsList();

        ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(itemUuid);
        List<ItemDependencyEntity> dependentItems = ItemDependencyEntity.retrieveForActiveItemType(activeItem, (short)DependencyType.Uses.getValue());

        for (ItemDependencyEntity dependentItem: dependentItems) {
            String dependentItemUuid = dependentItem.getDependentItemUuid();
            ItemEntity item = ItemEntity.retrieveLatestForUUid(dependentItemUuid);

            JsonFolderContent content = new JsonFolderContent(item, null);
            ret.addContent(content);
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/moveLibraryItems")
    public Response moveLibraryItems(@Context SecurityContext sc, JsonMoveItems parameters) throws Exception {
        super.setLogbackMarkers(sc);

        String userUuid = "B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32";
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        LOG.trace("moveLibraryItems");

        super.moveItems(userUuid, orgUuid, parameters);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }
}
