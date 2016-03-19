package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.model.json.JsonDeleteResponse;
import org.endeavour.enterprise.model.json.JsonFolderContent;
import org.endeavour.enterprise.model.json.JsonFolderContentsList;
import org.endeavourhealth.enterprise.core.entity.DefinitionItemType;
import org.endeavourhealth.enterprise.core.entity.database.DbActiveItem;
import org.endeavourhealth.enterprise.core.entity.database.DbActiveItemDependency;
import org.endeavourhealth.enterprise.core.entity.database.DbItem;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentParser;
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

/**
 * Created by Drew on 11/03/2016.
 */
@Path("/library")
public final class LibraryEndpoint extends AbstractItemEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getLibraryItem")
    public Response getLibraryItem(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception {
        UUID libraryItemUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("GettingLibraryItem for UUID {}", libraryItemUuid);

        //retrieve the activeItem, so we know the latest version
        DbActiveItem activeItem = super.retrieveActiveItem(libraryItemUuid, orgUuid);
        DbItem item = super.retrieveItem(activeItem);

        String xml = item.getXmlContent();

        LibraryItem ret = QueryDocumentParser.readLibraryItemFromXml(xml);

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

        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        UUID libraryItemUuid = parseUuidFromStr(libraryItem.getUuid());
        String name = libraryItem.getName();
        String description = libraryItem.getDescription();
        UUID folderUuid = parseUuidFromStr(libraryItem.getFolderUuid());

        Query query = libraryItem.getQuery();
        DataSource dataSource = libraryItem.getDataSource();
        Test test = libraryItem.getTest();
        CodeSet codeSet = libraryItem.getCodeSet();
        ListReport listOutput = libraryItem.getListReport();

        LOG.trace("SavingLibraryItem UUID {}, Name {} FolderUuid", libraryItemUuid, name, folderUuid);

        //work out the item type (query, test etc.) from the content passed up
        DefinitionItemType type = null;
        if (query != null) {
            type = DefinitionItemType.Query;
        } else if (dataSource != null) {
            type = DefinitionItemType.Datasource;
        } else if (test != null) {
            type = DefinitionItemType.Test;
        } else if (codeSet != null) {
            type = DefinitionItemType.CodeSet;
        } else if (listOutput != null) {
            type = DefinitionItemType.ListOutput;
        } else {
            throw new BadRequestException("Can't save LibraryItem without some content (e.g. query, test etc.)");
        }

        boolean inserting = libraryItemUuid == null;
        if (inserting) {
            libraryItemUuid = UUID.randomUUID();
            libraryItem.setUuid(libraryItemUuid.toString());
        }

        QueryDocument doc = new QueryDocument();
        doc.getLibraryItem().add(libraryItem);

        super.saveItem(inserting, libraryItemUuid, orgUuid, userUuid, type, name, description, doc, folderUuid);

        //return the UUID of the libraryItem
        LibraryItem ret = new LibraryItem();
        ret.setUuid(libraryItemUuid.toString());

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

        UUID libraryItemUuid = parseUuidFromStr(libraryItem.getUuid());
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        LOG.trace("DeletingLibraryItem UUID {}", libraryItemUuid);

        JsonDeleteResponse ret = deleteItem(libraryItemUuid, orgUuid, userUuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getLibraryItemNamesForReport")
    public Response getLibraryItemNamesForReport(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception {
        UUID reportUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("GettingLibraryItemNamesForReport for UUID {}", reportUuid);

        JsonFolderContentsList ret = new JsonFolderContentsList();

        List<DbActiveItemDependency> dependentItems = DbActiveItemDependency.retrieveForItem(reportUuid);
        for (DbActiveItemDependency dependentItem: dependentItems) {
            UUID itemUuid = dependentItem.getDependentItemUuid();
            DbItem item = DbItem.retrieveForUuidLatestVersion(orgUuid, itemUuid);

            JsonFolderContent content = new JsonFolderContent(item);
            ret.addContent(content);
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }
}
