package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.json.JsonCohortRun;
import org.endeavourhealth.enterprise.core.json.JsonReportRun;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.ReportCohortFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class ReportManager {
	private static final Logger LOG = LoggerFactory.getLogger(ReportManager.class);

	public static void run(String userUuid, JsonReportRun reportRun, LibraryItem reportItem) throws Exception {
		LOG.info("Running report " + reportItem.getName());

		Timestamp runDate = new Timestamp(System.currentTimeMillis());
		List<String> orgIds = reportRun.getOrganisation().stream().map(o -> o.getId()).collect(Collectors.toList());

		String select = " SELECT p.pseudoId, p.organizationId ";
		String from = " FROM PatientEntity p ";
		String where = " WHERE p.organizationId IN (" + String.join(",", orgIds) + ")"; // TODO : Population restriction

		Integer i = 0;
		for (ReportCohortFeature feature : reportItem.getReport().getCohortFeature()) {
			runCohort(userUuid, reportRun, feature, runDate);

			String alias = "field"+i.toString(); // TODO : feature.getFieldName();
			select += ", " +alias + ".patientId";		// TODO : Select additional data fields
			from += " LEFT OUTER JOIN CohortPatientsEntity " + alias;
			from += " ON " + alias + ".patientId = p.id";
			from += " AND " + alias + ".organisationId = p.organizationId ";
			from += " AND " + alias + ".queryItemUuid = :queryUuid"+i.toString();
			from += " AND " + alias + ".runDate = :runDate"+i.toString();

			i++;
		}

		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
		Query query = entityManager.createQuery(select + from + where);


		i = 0;
		for (ReportCohortFeature feature : reportItem.getReport().getCohortFeature()) {
			query.setParameter("runDate"+i.toString(), runDate);
			query.setParameter("queryUuid"+i.toString(), feature.getCohortFeatureUuid());
			i++;
		}

		List<Object[]> results = query.getResultList();

//		i = 0;
//		for (Object[] row : results) {
//			String rowData = i.toString();
//			for (Object field : row) {
//				if (field != null)
//					rowData += ", " + field.toString();
//				else
//					rowData += ", [null]";
//			}
//			LOG.info(rowData);
//			i++;
//		}
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

		ResultsManager.runCohort(featureItem, featureRun, userUuid, runDate);
	}
}
