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
		try {
			if (inDate==null)
				return "";
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			return formatter.format(inDate);

		} catch (Exception e) {
			return "";
		}

	}

	public long runNow(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate, String cohortName) throws Exception {
		LOG.info("Running report " + reportItem.getName());

		reportRun.setCohortName(cohortName);
		long reportResultId = saveReport(userUuid, reportRun, reportItem, runDate);

		List<QueryResult> reportFeatureData = new ArrayList<>();

		// Run the baseline cohort if one has been defined
		if (reportRun.getBaselineCohortId() != null) {
			List<Long> reportCohort = new ArrayList<>();
			reportCohort = runCohort(userUuid, reportRun, reportRun.getBaselineCohortId(), runDate, null, false);
		}

		for (ReportCohortFeature feature : reportItem.getReport().getCohortFeature()) {
			LOG.info("Running report feature: " + feature.getFieldName());

			reportFeatureData = runCohortFeature(userUuid, reportRun, feature.getCohortFeatureUuid(), runDate, reportRun.getBaselineCohortId(), true);
			for (QueryResult featureObservations : reportFeatureData) {
				for (ObservationEntity observationEntity : featureObservations.getObservations()) {
					saveReportRows(reportResultId, feature.getFieldName(), observationEntity);
				}
			}
		}

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

	private List<Long> runCohort(String userUuid, JsonReportRun reportRun, String featureUuid, Timestamp runDate, String baselineCohortId, Boolean report) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(featureUuid);
		String featureXml = featureEntity.getXmlContent();
		LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

		JsonCohortRun featureRun = new JsonCohortRun();
		featureRun.setBaselineDate(reportRun.getBaselineDate());
		featureRun.setOrganisationGroup(reportRun.getOrganisationGroup());
		featureRun.setPopulation(reportRun.getPopulation());
		featureRun.setQueryItemUuid(featureUuid);

		return CohortManager.runCohort(featureItem, featureRun, userUuid, runDate, baselineCohortId, report, true);
	}

	private List<QueryResult> runCohortFeature(String userUuid, JsonReportRun reportRun, String featureUuid, Timestamp runDate, String baselineCohortId, Boolean report) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(featureUuid);
		String featureXml = featureEntity.getXmlContent();
		LibraryItem featureItem = QueryDocumentSerializer.readLibraryItemFromXml(featureXml);

		JsonCohortRun featureRun = new JsonCohortRun();
		featureRun.setBaselineDate(reportRun.getBaselineDate());
		featureRun.setOrganisationGroup(reportRun.getOrganisationGroup());
		featureRun.setPopulation(reportRun.getPopulation());
		featureRun.setQueryItemUuid(featureUuid);

		return CohortManager.runCohortFeature(featureItem, featureRun, userUuid, runDate, baselineCohortId, report);
	}

	private long saveReport(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate) throws Exception {
		ItemEntity featureEntity = ItemEntity.retrieveLatestForUUid(reportItem.getUuid());
		LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(featureEntity.getXmlContent());
		libraryItem.getReport().setLastRunDate(runDate.getTime());

		featureEntity.setXmlContent(QueryDocumentSerializer.writeToXml(libraryItem));

		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseAdmin();
		entityManager.getTransaction().begin();
		entityManager.merge(featureEntity);

		long reportResultId = saveReportResults(userUuid, reportRun, reportItem, runDate);

		entityManager.getTransaction().commit();
		entityManager.close();

		return reportResultId;
	}

	private long saveReportResults(String userUuid, JsonReportRun reportRun, LibraryItem reportItem, Timestamp runDate) throws Exception, JsonProcessingException {
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

		String organisationGroup = reportRun.getOrganisationGroup();
		List<JsonOrganisation> organisations = CohortManager.getOrganisationsFromGroup(organisationGroup);

		for (JsonOrganisation organisation : organisations) {
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

	private void saveReportRows(long reportResultId, String label, ObservationEntity observation) throws Exception {

		ReportrowEntity reportResult = new ReportrowEntity()
				.setReportResultId(reportResultId)
				.setPatientId(observation.getPatientId())
				.setOrganisationId(observation.getOrganizationId())
				.setLabel(label)
				.setClinicalEffectiveDate(observation.getClinicalEffectiveDate())
				.setOriginalTerm(observation.getOriginalTerm())
				.setOriginalCode(observation.getOriginalCode())
				.setSnomedConceptId(observation.getSnomedConceptId())
				.setValue(observation.getResultValue())
				.setUnits(observation.getResultValueUnits());


		EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
		entityManager.getTransaction().begin();

		entityManager.merge(reportResult);

		entityManager.getTransaction().commit();
		entityManager.close();

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
