package org.endeavour.enterprise.endpoints;

import org.endeavour.enterprise.json.JsonDeleteResponse;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbRequest;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        LOG.trace("GettingReport for UUID {}", reportUuid);

        //retrieve the activeItem, so we know the latest version
        DbItem item = DbItem.retrieveForUUid(reportUuid);

        String xml = item.getXmlContent();

        Report ret = QueryDocumentSerializer.readReportFromXml(xml);

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/scheduleReport")
    public Response scheduleReport(@Context SecurityContext sc, RequestParameters requestParameters) throws Exception
    {
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID userUuid = getEndUserUuidFromToken(sc);

        UUID reportUuid = parseUuidFromStr(requestParameters.getReportUuid());
        String parameterXml = RequestParametersSerializer.writeToXml(requestParameters);

        if (reportUuid == null) {
            throw new BadRequestException("Missing report UUID");
        }

        DbRequest request = new DbRequest();
        request.setReportUuid(reportUuid);
        request.setOrganisationUuuid(orgUuid);
        request.setEndUserUuid(userUuid);
        request.setTimeStamp(Instant.now());
        request.setParameters(parameterXml);

        request.writeToDb();

        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReportSchedules")
    public Response getReportSchedules(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception
    {
        UUID orgUuid = getOrganisationUuidFromToken(sc);
        UUID reportUuid = UUID.fromString(uuidStr);

        LOG.trace("getReportSchedules for UUID {}", reportUuid);

        List<RequestParameters> ret = new ArrayList<>();

        List<UUID> v = new ArrayList<>();
        v.add(reportUuid);
        List<DbRequest> requests = DbRequest.retrievePendingForItemUuids(orgUuid, v);
        for (DbRequest request: requests) {
            String xml = request.getParameters();
            RequestParameters requestObj = RequestParametersSerializer.readFromXml(xml);
            ret.add(requestObj);
        }

        return Response
                .ok()
                .entity(ret)
                .build();
    }
}
