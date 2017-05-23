package org.endeavourhealth.enterprise.core.database;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportManager {
	private static final Logger LOG = LoggerFactory.getLogger(ReportManager.class);

	public Timestamp runLater(String userUuid, JsonReportRun reportRun, LibraryItem reportItem) throws Exception {
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

	public Timestamp runNow(String userUuid, JsonReportRun reportRun, LibraryItem reportItem) throws Exception {
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

		loadReportResults(featureUuids, orgIds, reportRun, runDate);

		return runDate;
	}

	public List<ReportResultEntity> getReportResultList(String reportItemUuid) {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

		List<ReportResultEntity> reportResultList = entityManager
				.createQuery("SELECT r FROM ReportResultEntity r WHERE r.reportItemUuid = :reportItemUuid")
				.setParameter("reportItemUuid", reportItemUuid)
				.getResultList();

		return reportResultList;
	}

	public List<Object[]> getReportResults(int reportResultId) throws IOException {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

		ReportResultEntity reportResult = (ReportResultEntity) entityManager
				.createQuery("SELECT r FROM ReportResultEntity r WHERE r.reportResultId = :reportResultId")
				.setParameter("reportResultId", reportResultId)
				.getSingleResult();

		if (reportResult == null || reportResult.getReportRunParams() == null) {
			LOG.error("Error loading report id " + reportResultId);
			return null;
		}

		JsonReportRun reportRun = ObjectMapperPool.getInstance().readValue(reportResult.getReportRunParams(), JsonReportRun.class);

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

		return loadReportResults(featureUuids, orgIds, reportRun, reportResult.getRunDate());
	}

	private List<Object[]> loadReportResults(List<String> cohortFeatureUuids, List<String> orgIds, JsonReportRun reportRun, Timestamp runDate) {
		Map<String, List<String>> cohortFields = getCohortFieldMap(cohortFeatureUuids);

		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

		Query query = buildReportResultsLoaderQuery(cohortFeatureUuids, orgIds, reportRun, entityManager, cohortFields);

		setReportResultsLoaderParams(cohortFeatureUuids, reportRun, runDate, query);

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

	private void setReportResultsLoaderParams(List<String> cohortFeatureUuids, JsonReportRun reportRun, Timestamp runDate, Query query) {
		query.setParameter("runDate" , runDate);
		if (reportRun.getBaselineCohortId() != null)
			query.setParameter("baselineCohortId", reportRun.getBaselineCohortId());
		else
			query.setParameter("baseline", reportRun.getBaselineDate());

		Integer i = 0;
		for (String featureUuid : cohortFeatureUuids) {
			query.setParameter("runDate"+i.toString(), runDate);
			query.setParameter("queryUuid"+i.toString(), featureUuid);
			i++;
		}
	}

	private Query buildReportResultsLoaderQuery(List<String> cohortFeatureUuids, List<String> orgIds, JsonReportRun reportRun, EntityManager entityManager, Map<String, List<String>> cohortFieldMap) {
		String select = " SELECT p.pseudoId, p.organizationId ";
		String from = " FROM PatientEntity p ";
		String where = "";

		if (reportRun.getBaselineCohortId() != null) { // Cohort subset
			from += " JOIN CohortPatientsEntity c ON p.id = c.patientId AND p.organizationId = c.organisationId ";
			where = " WHERE c.queryItemUuid = :baselineCohortId " +
					"and c.runDate = :runDate ";
		} else if (reportRun.getPopulation().equals("0")) { // currently registered
			from += " JOIN EpisodeOfCareEntity e on e.patientId = p.id ";
			where = " WHERE p.dateOfDeath IS NULL " +
					"and e.registrationTypeId = 2 " +
					"and e.dateRegistered <= :baseline " +
					"and (e.dateRegisteredEnd > :baseline or e.dateRegisteredEnd IS NULL) " +
					"and p.organizationId IN (" + String.join(",", orgIds) + ") ";
		} else if (reportRun.getPopulation().equals("1")) { // all patients
			from += " JOIN EpisodeOfCareEntity e on e.patientId = p.id ";
			where = " where e.dateRegistered <= :baseline " +
					"and (e.dateRegisteredEnd > :baseline or e.dateRegisteredEnd IS NULL) " +
					"and p.organizationId IN (" + String.join(",", orgIds) + ") ";
		}

		Integer i = 0;
		for (String cohortFeatureUuid : cohortFeatureUuids) {
			String alias = "field"+i.toString(); // TODO : feature.getFieldName();
			List<String> cohortFields = cohortFieldMap.get(cohortFeatureUuid);
			select += getCohortFieldsSelect(alias, cohortFields);
			from += " LEFT OUTER JOIN CohortPatientsEntity " + alias;
			from += " ON " + alias + ".patientId = p.id";
			from += " AND " + alias + ".organisationId = p.organizationId ";
			from += " AND " + alias + ".queryItemUuid = :queryUuid"+i.toString();
			from += " AND " + alias + ".runDate = :runDate"+i.toString();
			from += getCohortFieldsAdditionalJoins(alias, cohortFields);
			i++;
		}

		return entityManager.createQuery(select + from + where);
	}

	private String getCohortFieldsSelect(String alias, List<String> cohortFields) {
		// TODO : Determine additional select db fields based on "KEEP" field tags
		return ", " +alias + ".patientId";
	}

	private String getCohortFieldsAdditionalJoins(String alias, List<String> cohortFields) {
		// TODO : Determine additional join tables based on "KEEP" field tags
		return "";
	}

	private Map<String, List<String>> getCohortFieldMap(List<String> cohortFeatureUuids) {
		Map<String, List<String>> cohortFieldMap = new HashMap<>();

		for (String cohortFeatureUuid : cohortFeatureUuids) {
			try {
				ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(cohortFeatureUuid);
				String featureXml = featureEntity.getXmlContent();
				LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

				Optional<Rule> rule = featureItem.getQuery().getRule().stream()
						.filter(this::isEndRule)
						.filter(this::hasReportFields)
						.findFirst();

				if (rule.isPresent())
					cohortFieldMap.put(cohortFeatureUuid, rule.get().getTest().getRestriction().getField());

			} catch (Exception e) {
				LOG.error("Could not retrieve feature " + cohortFeatureUuid);
			}

		}

		return cohortFieldMap;
	}

	private void runCohort(String userUuid, JsonReportRun reportRun, String featureUuid, Timestamp runDate, String baselineCohortId) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(featureUuid);
		String featureXml = featureEntity.getXmlContent();
		LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

		Optional<Rule> rule = featureItem.getQuery().getRule().stream()
				.filter(this::isEndRule)
				.filter(this::hasReportFields)
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

	private boolean isEndRule(Rule rule) {
		if (rule.getOnPass().getAction() == RuleActionOperator.INCLUDE
				&& (rule.getOnPass().getRuleId() == null || rule.getOnPass().getRuleId().size() == 0))
			return true;

		if (rule.getOnFail().getAction() == RuleActionOperator.INCLUDE
				&& (rule.getOnFail().getRuleId() == null || rule.getOnFail().getRuleId().size() == 0))
			return true;

		return false;
	}

	private boolean hasReportFields(Rule rule) {
		if (rule.getTest() == null)
			return false;

		Restriction restriction = rule.getTest().getRestriction();
		if (restriction == null)
			return false;

		if (restriction.getField() == null || restriction.getField().size() == 0)
			return false;

		return true;
	}

	private void saveReport(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate) throws Exception {
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

	private void saveReportResults(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate) throws JsonProcessingException {
		String reportRunJson = ObjectMapperPool.getInstance().writeValueAsString(reportRun);

		ReportResultEntity reportResult = new ReportResultEntity()
				.setEndUserUuid(userUuid)
				.setReportItemUuid(reportItem.getUuid())
				.setReportRunParams(reportRunJson)
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
