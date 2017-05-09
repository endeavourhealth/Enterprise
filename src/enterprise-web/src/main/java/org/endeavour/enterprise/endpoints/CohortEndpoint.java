package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.enterprise.core.database.CohortManager;
import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.database.models.data.OrganizationEntity;
import org.endeavourhealth.enterprise.core.database.models.data.CohortPatientsEntity;
import org.endeavourhealth.enterprise.core.database.models.data.CohortResultEntity;
import org.endeavourhealth.enterprise.core.json.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;

@Path("/cohort")
public final class CohortEndpoint extends AbstractItemEndpoint {

	private static final Logger LOG = LoggerFactory.getLogger(CohortEndpoint.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/getOrganisations")
	public Response getOrganisations(@Context SecurityContext sc) throws Exception {
		super.setLogbackMarkers(sc);

		List<Object[]> organisations = OrganizationEntity.getOrganisations();

		List<JsonOrganisation> results = new ArrayList<>();

		for (Object[] orgEntity : organisations) {
			String id = orgEntity[0].toString();
			String name = orgEntity[1].toString();

			JsonOrganisation org = new JsonOrganisation();
			org.setId(id);
			org.setName(name);

			results.add(org);
		}

		clearLogbackMarkers();

		return Response
				.ok()
				.entity(results)
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/getResults")
	public Response getResults(@Context SecurityContext sc, @QueryParam("queryItemUuid") String queryItemUuid, @QueryParam("runDate") String runDate) throws Exception {
		super.setLogbackMarkers(sc);

		List<CohortResultEntity[]> results = CohortResultEntity.getCohortResults(queryItemUuid, runDate);

		clearLogbackMarkers();

		return Response
				.ok()
				.entity(results)
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/getAllResults")
	public Response getAllResults(@Context SecurityContext sc, @QueryParam("queryItemUuid") String queryItemUuid, @QueryParam("runDate") String runDate) throws Exception {
		super.setLogbackMarkers(sc);

		List<CohortResultEntity[]> results = CohortResultEntity.getAllCohortResults(queryItemUuid);

		clearLogbackMarkers();

		return Response
				.ok()
				.entity(results)
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/getPatients")
	public Response getPatients(@Context SecurityContext sc, @QueryParam("type") String type, @QueryParam("queryItemUuid") String queryItemUuid, @QueryParam("runDate") String runDate, @QueryParam("organisationId") Long organisationId) throws Exception {

		List<Object[]> results = null;

		LOG.trace("Called cohort/getPatients", "");

		try {
			super.setLogbackMarkers(sc);

			results = CohortPatientsEntity.getCohortPatients(type, queryItemUuid, runDate, organisationId);

			Object[] header = null;

			if (type.equals("EHR"))
				header = new Object[]{"patient_id", "patient_pseudo_id", "patient_sex", "patient_age_years", "patient_age_months", "patient_age_weeks", "patient_date_of_death", "patient_postcode_prefix", "patient_household_id", "patient_lsoa_code", "patient_msoa_code", "patient_townsend_score", "observation_clinical_effective_date", "observation_snomed_concept_id", "observation_original_code", "observation_original_term", "observation_value", "observation_units", "observation_is_problem", "organisation_name", "organisation_ods_code", "organisation_type", "practitioner_name", "practitioner_role_code", "practitioner_role_description"};
			else
				header = new Object[]{"patient_id", "patient_pseudo_id", "patient_sex", "patient_age_years", "patient_age_months", "patient_age_weeks", "patient_date_of_death", "patient_postcode_prefix", "patient_household_id", "patient_lsoa_code", "patient_msoa_code", "patient_townsend_score", "organisation_name", "organisation_ods_code", "organisation_type"};

			results.add(0, header);

			clearLogbackMarkers();

		} catch (Exception e) {
			LOG.trace("cohort/getPatients", e.getMessage());
		}

		return Response
				.ok()
				.entity(results)
				.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/run")
	public Response run(@Context SecurityContext sc, JsonCohortRun cohortRun) throws Exception {
		super.setLogbackMarkers(sc);

		String userUuid = SecurityUtils.getCurrentUserId(sc).toString();

		ItemEntity item = ItemEntity.retrieveLatestForUUid(cohortRun.getQueryItemUuid());
		String xml = item.getXmlContent();
		LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(xml);

		CohortManager.runCohort(libraryItem, cohortRun, userUuid);

		clearLogbackMarkers();

		return Response
				.ok()
				.entity(null)
				.build();
	}

}
