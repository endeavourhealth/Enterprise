package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.entity.database.DbActiveItem;
import org.endeavour.enterprise.entity.database.DbItem;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

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
 * Created by Drew on 23/02/2016.
 */
@Path("/report")
public final class ReportEndpoint extends AbstractItemEndpoint
{
    private static final Logger LOG = LoggerFactory.getLogger(ReportEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReport")
    public Response getReport(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception
    {
        UUID reportUuid = UUID.fromString(uuidStr);
        UUID orgUuid = getOrganisationUuidFromToken(sc);

        LOG.trace("GettingReport for UUID {}", reportUuid);

        //retrieve the activeItem, so we know the latest version
        DbActiveItem activeItem = super.retrieveActiveItem(reportUuid, orgUuid, DefinitionItemType.Report);
        DbItem item = super.retrieveItem(activeItem);

        String xml = item.getXmlContent();

        InputStream is = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(is);
        org.w3c.dom.Element varElement = document.getDocumentElement();
        JAXBContext context = JAXBContext.newInstance(Report.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<Report> loader = unmarshaller.unmarshal(varElement, Report.class);
        Report ret = loader.getValue();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveReport")
    public Response saveReport(@Context SecurityContext sc, Report report) throws Exception
    {
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        UUID reportUuid = parseUuidFromStr(report.getUuid());
        String name = report.getName();
        String description = ""; //reportParameters.getDescription(); TODO: save report description
        UUID folderUuid = parseUuidFromStr(report.getFolderUuid());

        LOG.trace("SavingReport UUID {}, Name {} FolderUuid", reportUuid, name, folderUuid);

        StringWriter sw = new StringWriter();

        try {
            JAXBContext carContext = JAXBContext.newInstance(LibraryItem.class);
            Marshaller carMarshaller = carContext.createMarshaller();
            carMarshaller.marshal(report, sw);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        String xml = sw.toString();

        reportUuid = super.saveItem(reportUuid, orgUuid, userUuid, DefinitionItemType.Report, name, description, xml, folderUuid);

        //return the UUID of the query
        Report ret = new Report();
        ret.setUuid(reportUuid.toString());

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteReport")
    public Response deleteReport(@Context SecurityContext sc, Report reportParameters) throws Exception {

        UUID reportUuid = parseUuidFromStr(reportParameters.getUuid());
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        LOG.trace("DeletingReport UUID {}", reportUuid);

        super.deleteItem(reportUuid, orgUuid, userUuid);

        return Response.ok().build();
    }

}
