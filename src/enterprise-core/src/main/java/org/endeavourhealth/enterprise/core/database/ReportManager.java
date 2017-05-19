package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.database.models.data.*;
import org.endeavourhealth.enterprise.core.json.JsonCohortRun;
import org.endeavourhealth.enterprise.core.json.JsonOrganisation;
import org.endeavourhealth.enterprise.core.json.JsonReportRun;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportManager {
	private static final Logger LOG = LoggerFactory.getLogger(ReportManager.class);

	public static Timestamp runLater(String userUuid, JsonReportRun reportRun, LibraryItem reportItem) throws Exception {
		Timestamp runDate = new Timestamp(reportRun.getScheduleDateTime().getTime());
		LOG.info("Scheduling report " + reportItem.getName() + " at " + reportRun.getScheduleDateTime().toString());

		String reportRunJson = ObjectMapperPool.getInstance().writeValueAsString(reportRun);

		ReportScheduleEntity reportSchedule = new ReportScheduleEntity()
				.setScheduledAt(runDate)
				.setEndUserUuid(userUuid)
				.setReportItemUuid(reportItem.getUuid())
				.setReportRunParams(reportRunJson);

		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
		entityManager.getTransaction().begin();

		reportSchedule = entityManager.merge(reportSchedule);

		entityManager.getTransaction().commit();
		entityManager.close();

		LOG.info("Report Schedule " + reportSchedule.getReportScheduleId() + " Saved.");

		return runDate;
	}

	public static Timestamp runNow(String userUuid, JsonReportRun reportRun, LibraryItem reportItem) throws Exception {
		LOG.info("Running report " + reportItem.getName());

		Timestamp runDate = new Timestamp(System.currentTimeMillis());

		// Run the baseline cohort if one has been defined
		if (reportRun.getBaselineCohortId() != null)
			runCohort(userUuid, reportRun, reportRun.getBaselineCohortId(), runDate, null);

		for (ReportCohortFeature feature : reportItem.getReport().getCohortFeature()) {
			LOG.info("Running report feature " + feature.getFieldName());
			runCohort(userUuid, reportRun, feature.getCohortFeatureUuid(), runDate, reportRun.getBaselineCohortId());
		}

		saveReport(userUuid, reportRun, reportItem, runDate);

		List<String> featureUuids = reportItem.getReport().getCohortFeature().stream()
				.map(ReportCohortFeature::getCohortFeatureUuid)
				.collect(Collectors.toList());
		List<String> orgIds = reportRun.getOrganisation().stream()
				.map(JsonOrganisation::getId)
				.collect(Collectors.toList());

		loadReportResults(featureUuids, orgIds, runDate);

		return runDate;
	}

	public static List<ReportResultEntity> getReportResultList(String reportItemUuid) {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

		List<ReportResultEntity> reportResultList = entityManager
				.createQuery("SELECT r FROM ReportResultEntity r WHERE r.reportItemUuid = :reportItemUuid")
				.setParameter("reportItemUuid", reportItemUuid)
				.getResultList();

		return reportResultList;
	}

	public static List<Object[]> getReportResults(int reportResultId) {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

		ReportResultEntity reportResult = (ReportResultEntity) entityManager
				.createQuery("SELECT r FROM ReportResultEntity r WHERE r.reportResultId = :reportResultId")
				.setParameter("reportResultId", reportResultId)
				.getSingleResult();

		if (reportResult == null)
			LOG.error("No report found with id " + reportResultId);

		List<ReportResultQueryEntity> reportResultQueries = entityManager
				.createQuery("SELECT q FROM ReportResultQueryEntity q WHERE q.id.reportResultId = :reportResultId")
				.setParameter("reportResultId", reportResultId)
				.getResultList();

		List<ReportResultOrganisationEntity> reportResultOrgs = entityManager
				.createQuery("SELECT o FROM ReportResultOrganisationEntity o WHERE o.id.reportResultId = :reportResultId")
				.setParameter("reportResultId", reportResultId)
				.getResultList();

		List<String> featureUuids = reportResultQueries.stream()
				.map(q -> q.getId().getQueryItemUuid())
				.collect(Collectors.toList());

		List<String> orgIds = reportResultOrgs.stream()
				.map(o -> Long.toString(o.getId().getOrganisationId()))
				.collect(Collectors.toList());

		return loadReportResults(featureUuids, orgIds, reportResult.getRunDate());
	}

	private static List<Object[]> loadReportResults(List<String> cohortFeatureUuids, List<String> orgIds, Timestamp runDate) {
		String select = " SELECT p.pseudoId, p.organizationId ";
		String from = " FROM PatientEntity p ";
		String where = " WHERE p.organizationId IN (" + String.join(",", orgIds) + ")"; // TODO : Population restriction

		for (Integer i = 0; i < cohortFeatureUuids.size(); i++) {
			String alias = "field"+i.toString(); // TODO : feature.getFieldName();
			select += ", " +alias + ".patientId";		// TODO : Select additional data fields
			from += " LEFT OUTER JOIN CohortPatientsEntity " + alias;
			from += " ON " + alias + ".patientId = p.id";
			from += " AND " + alias + ".organisationId = p.organizationId ";
			from += " AND " + alias + ".queryItemUuid = :queryUuid"+i.toString();
			from += " AND " + alias + ".runDate = :runDate"+i.toString();
		}

		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
		Query query = entityManager.createQuery(select + from + where);


		Integer i = 0;
		for (String featureUuid : cohortFeatureUuids) {
			query.setParameter("runDate"+i.toString(), runDate);
			query.setParameter("queryUuid"+i.toString(), featureUuid);
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

		entityManager.close();

		return results;
	}

	private static void runCohort(String userUuid, JsonReportRun reportRun, String featureUuid, Timestamp runDate, String baselineCohortId) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(featureUuid);
		String featureXml = featureEntity.getXmlContent();
		LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

		Optional<Rule> rule = featureItem.getQuery().getRule().stream()
				.filter(ReportManager::isEndRule)
				.filter(ReportManager::hasReportFields)
				.findFirst();

		if (rule.isPresent()) {
			String reportFields = String.join(", ", rule.get().getTest().getRestriction().getField());
			LOG.debug("Cohort   : " + featureItem.getName());
			LOG.debug("End rule : " + rule.get().getDescription());
			LOG.debug("Fields   : " + reportFields);
		}

		JsonCohortRun featureRun = new JsonCohortRun();
		featureRun.setBaselineDate(reportRun.getBaselineDate());
		featureRun.setOrganisation(reportRun.getOrganisation());
		featureRun.setPopulation(reportRun.getPopulation());
		featureRun.setQueryItemUuid(featureUuid);

		CohortManager.runCohort(featureItem, featureRun, userUuid, runDate, baselineCohortId);
	}

	private static boolean isEndRule(Rule rule) {
		if (rule.getOnPass().getAction() == RuleActionOperator.INCLUDE
				&& (rule.getOnPass().getRuleId() == null || rule.getOnPass().getRuleId().size() == 0))
			return true;

		if (rule.getOnFail().getAction() == RuleActionOperator.INCLUDE
				&& (rule.getOnFail().getRuleId() == null || rule.getOnFail().getRuleId().size() == 0))
			return true;

		return false;
	}

	private static boolean hasReportFields(Rule rule) {
		if (rule.getTest() == null)
			return false;

		Restriction restriction = rule.getTest().getRestriction();
		if (restriction == null)
			return false;

		if (restriction.getField() == null || restriction.getField().size() == 0)
			return false;

		return true;
	}

	private static void saveReport(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(reportItem.getUuid());
		LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(featureEntity.getXmlContent());
		libraryItem.getReport().setLastRunDate(runDate.getTime());

		featureEntity.setXmlContent(QueryDocumentSerializer.writeToXml(libraryItem));

		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseAdmin();
		entityManager.getTransaction().begin();
		entityManager.merge(featureEntity);

		saveReportResults(userUuid, reportRun, reportItem, runDate);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private static void saveReportResults(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate) {
		ReportResultEntity reportResult = new ReportResultEntity()
				.setEndUserUuid(userUuid)
				.setReportItemUuid(reportItem.getUuid())
				.setRunDate(runDate);

		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
		entityManager.getTransaction().begin();

		reportResult = entityManager.merge(reportResult);
		LOG.info("Report " + reportResult.getReportResultId() + " Saved.");

		for (ReportCohortFeature feature : reportItem.getReport().getCohortFeature()) {
			ReportResultQueryEntity reportResultQuery = new ReportResultQueryEntity()
					.setId(
							new ReportResultQueryEntityKey()
									.setReportResultId(reportResult.getReportResultId())
									.setQueryItemUuid(feature.getCohortFeatureUuid())
					);

			entityManager.persist(reportResultQuery);
		}

		for (JsonOrganisation organisation : reportRun.getOrganisation()) {
			ReportResultOrganisationEntity reportResultOrganisation = new ReportResultOrganisationEntity()
					.setId(
							new ReportResultOrganisationEntityKey()
									.setReportResultId(reportResult.getReportResultId())
									.setOrganisationId(Long.parseLong(organisation.getId()))
					);

			entityManager.persist(reportResultOrganisation);
		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
