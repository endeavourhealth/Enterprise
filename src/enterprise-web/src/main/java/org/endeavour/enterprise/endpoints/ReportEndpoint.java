package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.model.json.JsonDeleteResponse;
import org.endeavourhealth.enterprise.core.entity.DefinitionItemType;
import org.endeavourhealth.enterprise.core.entity.database.DbActiveItem;
import org.endeavourhealth.enterprise.core.entity.database.DbItem;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentParser;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
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

        Report ret = QueryDocumentParser.readFromXml(Report.class, xml);

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
        String description = report.getDescription();
        UUID folderUuid = parseUuidFromStr(report.getFolderUuid());

        LOG.trace("SavingReport UUID {}, Name {} FolderUuid", reportUuid, name, folderUuid);

        boolean inserting = reportUuid == null;
        if (inserting) {
            reportUuid = UUID.randomUUID();
            report.setUuid(reportUuid.toString());
        }

        QueryDocument doc = new QueryDocument();
        doc.getReport().add(report);

        super.saveItem(inserting, reportUuid, orgUuid, userUuid, DefinitionItemType.Report, name, description, doc, folderUuid);

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

        JsonDeleteResponse ret = deleteItem(reportUuid, orgUuid, userUuid);

        return Response
                .ok()
                .entity(ret)
                .build();
    }

}
