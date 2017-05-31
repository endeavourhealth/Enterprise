package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.enterprise.core.database.ReportManager;
import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.database.models.data.ReportResultEntity;
import org.endeavourhealth.enterprise.core.json.JsonReportRun;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.Query;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.Timestamp;
import java.util.List;

@Path("/report")
public final class ReportEndpoint extends AbstractItemEndpoint {

	private static final Logger LOG = LoggerFactory.getLogger(ReportEndpoint.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/run")
	public Response run(@Context SecurityContext sc, JsonReportRun report) throws Exception {
		super.setLogbackMarkers(sc);

		String userUuid = SecurityUtils.getCurrentUserId(sc).toString();

		ItemEntity item = ItemEntity.retrieveLatestForUUid(report.getReportItemUuid());
		String xml = item.getXmlContent();
		LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(xml);

		Timestamp runDate = null;
		if (report.getScheduled())
			runDate = new ReportManager().runLater(userUuid, report, libraryItem);
		else {
			runDate = new Timestamp(System.currentTimeMillis());
			new ReportManager().runNow(userUuid, report, libraryItem, runDate);
		}

		clearLogbackMarkers();

		return Response
				.ok()
				.entity(runDate.getTime())
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/reportResult")
	public Response reportResult(@Context SecurityContext sc, @QueryParam("reportItemUuid") String reportItemUuid) {
		super.setLogbackMarkers(sc);

		List<ReportResultEntity> reportResultEntityList = new ReportManager().getReportResultList(reportItemUuid);

		for (ReportResultEntity reportResult : reportResultEntityList) {
			LOG.info("Result Id : " + reportResult.getReportResultId());
		}

		clearLogbackMarkers();

		return Response
				.ok()
				.entity(null)
				.build();
	}

}
