package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.json.JsonCohortRun;
import org.endeavourhealth.enterprise.core.json.JsonReportRun;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.ReportCohortFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class ReportManager {
	private static final Logger LOG = LoggerFactory.getLogger(ReportManager.class);

	public static void run(String userUuid, JsonReportRun reportRun, LibraryItem reportItem) throws Exception {
		LOG.info("Running report " + reportItem.getName());

		Timestamp runDate = new Timestamp(System.currentTimeMillis());
		List<String> orgIds = reportRun.getOrganisation().stream().map(o -> o.getId()).collect(Collectors.toList());

		String select = " SELECT patient.pseudo_id, patient.organization_id ";
		String from = " FROM patient ";
		String where = " WHERE patient.organization_id IN (" + String.join(",", orgIds) + ")"; // TODO : Population restriction

		Integer i = 0;
		for (ReportCohortFeature feature : reportItem.getReport().getCohortFeature()) {
			runCohort(userUuid, reportRun, feature, runDate);

			String alias = "field"+(i++).toString(); // TODO : feature.getFieldName();
			select += ", " +alias + ".PatientId";		// TODO : Select additional data fields
			from += " LEFT OUTER JOIN ReportPatients " + alias;
			from += " ON " + alias + ".PatientId = patient.id";
			from += " AND " + alias + ".OrganisationId = patient.organization_id ";
			from += " AND " + alias + ".QueryItemUuid = '" + feature.getCohortFeatureUuid() + "'";
			from += " AND " + alias + ".RunDate = '" + runDate.toString() + "'";
		}

		LOG.info(select);
		LOG.info(from);
		LOG.info(where);

	}

	private static void runCohort(String userUuid, JsonReportRun reportRun, ReportCohortFeature feature, Timestamp runDate) throws Exception {
		LOG.info("Running report feature " + feature.getFieldName());
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(feature.getCohortFeatureUuid());
		String featureXml = featureEntity.getXmlContent();
		LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

		JsonCohortRun featureRun = new JsonCohortRun();
		featureRun.setBaselineDate(reportRun.getBaselineDate());
		featureRun.setOrganisation(reportRun.getOrganisation());
		featureRun.setPopulation(reportRun.getPopulation());
		featureRun.setQueryItemUuid(feature.getCohortFeatureUuid());

		ResultsManager.runReport(featureItem, featureRun, userUuid, runDate);
	}
}
