package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.enterprise.core.database.ResultsManager;
import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.json.JsonCohortRun;
import org.endeavourhealth.enterprise.core.json.JsonReportRun;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.ReportCohortFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

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

		LOG.info("Running report " + libraryItem.getName());

		for (ReportCohortFeature feature : libraryItem.getReport().getCohortFeature()) {
			LOG.info("Running report feature " + feature.getFieldName());
			ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(feature.getCohortFeatureUuid());
			String featureXml = featureEntity.getXmlContent();
			LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

			JsonCohortRun featureRun = new JsonCohortRun();
			featureRun.setBaselineDate(report.getBaselineDate());
			featureRun.setOrganisation(report.getOrganisation());
			featureRun.setPopulation(report.getPopulation());
			featureRun.setQueryItemUuid(feature.getCohortFeatureUuid());

			ResultsManager.runReport(featureItem, featureRun, userUuid);
		}

		clearLogbackMarkers();

		return Response
				.ok()
				.entity(null)
				.build();
	}

}
