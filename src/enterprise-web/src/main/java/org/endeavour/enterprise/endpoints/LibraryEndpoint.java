package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
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

        InputStream is = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(is);
        org.w3c.dom.Element varElement = document.getDocumentElement();
        JAXBContext context = JAXBContext.newInstance(LibraryItem.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<LibraryItem> loader = unmarshaller.unmarshal(varElement, LibraryItem.class);
        LibraryItem ret = loader.getValue();

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
        ListOutput listOutput = libraryItem.getListOutput();

        LOG.trace("SavingLibraryItem UUID {}, Name {} FolderUuid", libraryItemUuid, name, folderUuid);

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

        StringWriter sw = new StringWriter();

        try {
            JAXBContext carContext = JAXBContext.newInstance(LibraryItem.class);
            Marshaller carMarshaller = carContext.createMarshaller();
            carMarshaller.marshal(libraryItem, sw);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        String xml = sw.toString();

        //validate the XML against the schema
/*        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsd));
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));*/


        libraryItemUuid = super.saveItem(libraryItemUuid, orgUuid, userUuid, type, name, description, xml, folderUuid);

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

        super.deleteItem(libraryItemUuid, orgUuid, userUuid);

        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/validateDeleteLibraryItem")
    public Response validateDeleteLibraryItem(@Context SecurityContext sc, LibraryItem libraryItem) throws Exception {

        UUID libraryItemUuid = parseUuidFromStr(libraryItem.getUuid());
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        LOG.trace("ValidatingDeletingLibraryItem UUID {}", libraryItemUuid);

        throw new NotImplementedException();

        //return Response.ok().build();
    }
}
