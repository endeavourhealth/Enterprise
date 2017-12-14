package org.endeavourhealth.enterprise.core.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
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
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportManager {
	private static final Logger LOG = LoggerFactory.getLogger(ReportManager.class);
	private static final String SCHEDULER_USER = "28aa7b57-940d-4b03-9ef8-ae25f5fa2b2f";

	public Timestamp runLater(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, String cohortName) throws Exception {
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

	public static String replaceNull(String input) {
		return input == null ? "" : input;
	}

	public static String replaceDoubleNull(Double input) {
		return input == null ? "" : input.toString();
	}

	public static String formatDate(Date inDate) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(inDate);
	}

	public long runNow(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate, String cohortName) throws Exception {
		LOG.info("Running report " + reportItem.getName());

		List<QueryResult> reportFeatureData = new ArrayList<>();
		List<String> reportOutput = new ArrayList<>();
		List<String> patients = new ArrayList<>();
		reportOutput.add("Cohort: "+cohortName);
		reportOutput.add("");
		reportOutput.add("Patient ID, Organisation ID, Gender, Age, Post Code");

		reportRun.setCohortName(cohortName);

		// Run the baseline cohort if one has been defined
		if (reportRun.getBaselineCohortId() != null) {
			List<Long> reportCohort = new ArrayList<>();
			reportCohort = runCohort(userUuid, reportRun, reportRun.getBaselineCohortId(), runDate, null, false);

			for (Long patientInCohort : reportCohort) {
				patients.add(patientInCohort.toString());
			}
		}

		List<PatientEntity> patientEntities = getPatientDemographics(patients);
		for (PatientEntity patient : patientEntities) {
			String patientDemographics = Long.toString(patient.getId())+","+patient.getOrganizationId()+","+patient.getPatientGenderId()+","+patient.getAgeYears()+","+replaceNull(patient.getPostcodePrefix());
			reportOutput.add(patientDemographics);
		}

		for (ReportCohortFeature feature : reportItem.getReport().getCohortFeature()) {
			reportFeatureData = runCohortFeature(userUuid, reportRun, feature.getCohortFeatureUuid(), runDate, reportRun.getBaselineCohortId(), true);
			Integer r = 0;
			for (String reportRow : reportOutput) {
				String patientId = reportRow.split(",")[0];
				Boolean found = false;
				String row = reportOutput.get(r);
				for (QueryResult featureObservations : reportFeatureData) {
					for (ObservationEntity observationEntity : featureObservations.getObservations()) {
						if (Long.toString(observationEntity.getPatientId()).equals(patientId)) {
							found = true;
							reportOutput.set(r, row + "," + formatDate(observationEntity.getClinicalEffectiveDate()) + " " + replaceDoubleNull(observationEntity.getValue()) + " " + replaceNull(observationEntity.getUnits()));
						}
					}
				}

				if (!found&&r>2)
					reportOutput.set(r, row + ",");
				r++;
			}
			String header = reportOutput.get(2);
			reportOutput.set(2, header + "," + feature.getFieldName());
		}

		long reportResultId = saveReport(userUuid, reportRun, reportItem, runDate, reportOutput);

		List<String> featureUuids = reportItem.getReport().getCohortFeature().stream()
				.map(ReportCohortFeature::getCohortFeatureUuid)
				.collect(Collectors.toList());
		List<String> orgIds = reportRun.getOrganisation().stream()
				.map(JsonOrganisation::getId)
				.collect(Collectors.toList());

		loadReportResults(featureUuids, orgIds, reportRun, runDate);

		return reportResultId;
	}

	public List<PatientEntity> getPatientDemographics(List<String> patients) {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
		String listPatients = "("+StringUtils.join(patients, ',')+")";
		String sql = "SELECT p from PatientEntity p where p.id in "+listPatients+" order by organizationId";

		TypedQuery<PatientEntity> query = entityManager.
				createQuery(sql, PatientEntity.class);

		return query.getResultList();

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
		if (reportRun.getBaselineCohortId() != null) {
			query.setParameter("runDate" , runDate);
			query.setParameter("baselineCohortId", reportRun.getBaselineCohortId());
		}
		else
			query.setParameter("baseline", CohortManager.convertToDate(reportRun.getBaselineDate()));

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

	private List<Long> runCohort(String userUuid, JsonReportRun reportRun, String featureUuid, Timestamp runDate, String baselineCohortId, Boolean report) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(featureUuid);
		String featureXml = featureEntity.getXmlContent();
		LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

		Optional<Rule> rule = featureItem.getQuery().getRule().stream()
				.filter(this::isEndRule)
				.filter(this::hasReportFields)
				.findFirst();

		if (rule.isPresent()) {
			String reportFields = String.join(", ", rule.get().getTest().getRestriction().getField());
		}

		JsonCohortRun featureRun = new JsonCohortRun();
		featureRun.setBaselineDate(reportRun.getBaselineDate());
		featureRun.setOrganisation(reportRun.getOrganisation());
		featureRun.setPopulation(reportRun.getPopulation());
		featureRun.setQueryItemUuid(featureUuid);

		return CohortManager.runCohort(featureItem, featureRun, userUuid, runDate, baselineCohortId, report, true);
	}

	private List<QueryResult> runCohortFeature(String userUuid, JsonReportRun reportRun, String featureUuid, Timestamp runDate, String baselineCohortId, Boolean report) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(featureUuid);
		String featureXml = featureEntity.getXmlContent();
		LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

		Optional<Rule> rule = featureItem.getQuery().getRule().stream()
				.filter(this::isEndRule)
				.filter(this::hasReportFields)
				.findFirst();

		if (rule.isPresent()) {
			String reportFields = String.join(", ", rule.get().getTest().getRestriction().getField());
		}

		JsonCohortRun featureRun = new JsonCohortRun();
		featureRun.setBaselineDate(reportRun.getBaselineDate());
		featureRun.setOrganisation(reportRun.getOrganisation());
		featureRun.setPopulation(reportRun.getPopulation());
		featureRun.setQueryItemUuid(featureUuid);

		return CohortManager.runCohortFeature(featureItem, featureRun, userUuid, runDate, baselineCohortId, report);
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

	private long saveReport(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate, List<String> reportOutput) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(reportItem.getUuid());
		LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(featureEntity.getXmlContent());
		libraryItem.getReport().setLastRunDate(runDate.getTime());

		featureEntity.setXmlContent(QueryDocumentSerializer.writeToXml(libraryItem));

		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseAdmin();
		entityManager.getTransaction().begin();
		entityManager.merge(featureEntity);

		long reportResultId = saveReportResults(userUuid, reportRun, reportItem, runDate, reportOutput);

		entityManager.getTransaction().commit();
		entityManager.close();

		return reportResultId;
	}

	private long saveReportResults(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate, List<String> reportOutput) throws JsonProcessingException {
		String reportRunJson = ObjectMapperPool.getInstance().writeValueAsString(reportRun);
		String reportOutputJson = ObjectMapperPool.getInstance().writeValueAsString(reportOutput);

		ReportResultEntity reportResult = new ReportResultEntity()
				.setEndUserUuid(userUuid)
				.setReportItemUuid(reportItem.getUuid())
				.setReportRunParams(reportRunJson)
				.setRunDate(runDate)
				.setReportOutput(reportOutputJson);

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

		return reportResult.getReportResultId();
	}

	public void runScheduledReports() throws Exception {
		List reportScheduleEntities = getScheduledReports();

		for(Object reportScheduleEntityObject : reportScheduleEntities) {
			ReportScheduleEntity reportScheduleEntity = (ReportScheduleEntity)reportScheduleEntityObject;
			ItemEntity item = ItemEntity.retrieveLatestForUUid(reportScheduleEntity.getReportItemUuid());
			String xml = item.getXmlContent();
			LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(xml);
			JsonReportRun jsonReportRun = ObjectMapperPool.getInstance().readValue(reportScheduleEntity.getReportRunParams(), JsonReportRun.class);
			Timestamp runDate = new Timestamp(System.currentTimeMillis());

			long reportResultId = runNow(SCHEDULER_USER, jsonReportRun, libraryItem, runDate, ""); // TODO: cohort name
			saveScheduledReportResult(reportScheduleEntity.getReportScheduleId(), reportResultId);
		}
	}

	private List getScheduledReports() {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
		List reportScheduleEntities = entityManager.createQuery(
				"SELECT r FROM ReportScheduleEntity r " +
						"WHERE r.scheduledAt < :now " +
						"AND r.reportResultId IS NULL " +
						"ORDER BY r.scheduledAt")
				.setParameter("now", new Date())
				.getResultList();

		entityManager.close();
		return  reportScheduleEntities;
	}

	private void saveScheduledReportResult(long reportScheduleId, long reportResultId) {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
		entityManager.getTransaction().begin();
		entityManager.createQuery(
				"UPDATE ReportScheduleEntity r " +
						"SET r.reportResultId = :reportResultId " +
						"WHERE r.reportScheduleId = :reportScheduleId ")
				.setParameter("reportResultId", reportResultId)
				.setParameter("reportScheduleId", reportScheduleId)
				.executeUpdate();
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
