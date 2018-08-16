package org.endeavourhealth.enterprise.core.database;

import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.enterprise.core.json.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class HealthcareActivityUtilityManager {

    private boolean includeOrganisationQuery = false;
    private String orgJoin = " join enterprise_admin.healthcare_activity_organisation_list o " +
        " on o.organisation_id = p.organization_id ";
    private boolean includeServiceQuery = false;
    private String serviceJoin = " join enterprise_admin.healthcare_activity_service_list s " +
        " on s.organisation_id = e.service_provider_organization_id ";
    private String whereClauses = "";
    private List<Object> patientListParams = new ArrayList<>();
    private List<Object> incidenceParams = new ArrayList<>();

    public boolean getDenominatorPopulation(JsonHealthcareActivity options) throws Exception {

        cleanUpDatabase();
        createTemporaryTables();
        generateWhereClausesFromOptions(options);
        initialiseReportResultTable(options);
        populateOrganisationTable(options);
        populateServiceTable(options);
        populatePatientList();
        populateRawData(options);
        return true;
    }

    private void cleanUpDatabase() throws Exception {

        includeOrganisationQuery = false;
        includeServiceQuery = false;
        whereClauses = "";
        patientListParams = new ArrayList<>();
        incidenceParams = new ArrayList<>();

        List<String> deleteScripts = new ArrayList<>();

        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_patient_list;");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_organisation_list;");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_raw_data;");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_date_range");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_options");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_service_list");

        for (String script : deleteScripts) {
            UtilityManagerCommon.runScript(script);
        }
    }

    private void createTemporaryTables() throws Exception {

        List<String> tempTableScripts = new ArrayList<>();

        tempTableScripts.add("create table enterprise_admin.healthcare_activity_patient_list (\n" +
            "\tpatient_id bigint(20) not null primary key\n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.healthcare_activity_organisation_list (\n" +
            "\torganisation_id bigint(20) not null primary key\n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.healthcare_activity_service_list (\n" +
            "\torganisation_id bigint(20) not null primary key\n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.healthcare_activity_raw_data (\n" +
            "\tpatient_id bigint(20) not null,\n" +
            "    patient_gender_id smallint(6) null,\n" +
            "    age_years int(11) null,\n" +
            "    postcode_prefix varchar(20) null,\n" +
            "    lsoa_code varchar(50) null,\n" +
            "    msoa_code varchar(50) null,\n" +
            "    ethnic_code char(1) null,\n" +
            "    organisation_id bigint(20) null,\n" +
            "    ccg varchar(10) null,\n" +
            "    encounter_snomed_concept_id bigint(20) null,\n" +
            "    clinical_effective_date date null,\n " +
            "    service_id bigint(20) null,\n " +
            "    encounter_type varchar(250) null, \n " +
            "     \n" +
            "    index ix_healthcare_activity_raw_data_patient_gender_id (patient_gender_id),    \n" +
            "    index ix_healthcare_activity_raw_data_postcode_prefix (postcode_prefix),\n" +
            "    index ix_healthcare_activity_raw_data_age_years (age_years),      \n" +
            "    index ix_healthcare_activity_raw_data_person_id (patient_id) ,      \n" +
            "    index ix_healthcare_activity_raw_data_service_id (service_id) ,      \n" +
            "    index ix_healthcare_activity_raw_data_encounter_type (encounter_type) ,      \n" +
            "    index ix_healthcare_activity_raw_data_clinical_effective_date (clinical_effective_date) ,      \n" +
            "    index ix_healthcare_activity_raw_data_encounter_concept_id (encounter_snomed_concept_id)      \n" +
            "    \n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.healthcare_activity_date_range (\n" +
            " min_date date,\n" +
            " max_date date,\n" +
            " \n" +
            " index ix_incidence_prevalence_date_range_min_date (min_date),\n" +
            " index ix_incidence_prevalence_date_range_max_date (max_date)\n" +
            " \n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.healthcare_activity_options (\n" +
            " options varchar(5000)\n);");

        for (String script : tempTableScripts) {
            UtilityManagerCommon.runScript(script);
        }
    }

    private void initialiseReportResultTable(JsonHealthcareActivity options) throws Exception {
        UtilityManagerCommon.runScript("delete from enterprise_admin.healthcare_activity_date_range;");

        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        Calendar beginning = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        Integer number = Integer.parseInt(options.getTimePeriodNo());

        int precision = Calendar.DAY_OF_YEAR;
        int substractionPrecision = Calendar.YEAR;

        if (options.getTimePeriod().equals("MONTHS")) {
            precision = Calendar.DAY_OF_MONTH;
            substractionPrecision = Calendar.MONTH;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Integer i = 0; i < number; i++) {

            // get the last day of the month/year
            if (options.getDateType().equals("absolute"))
                c.set(precision, c.getActualMaximum(precision));
            end.setTime(c.getTime());


            // get the first day of the month/year
            if (options.getDateType().equals("absolute")) {
                c.set(precision, c.getActualMinimum(precision));
                beginning.setTime(c.getTime());
            } else {
                Calendar b = Calendar.getInstance();
                b.setTime(c.getTime());
                b.add(substractionPrecision, -1);
                //Make sure same date isnt counted twice
                b.add(Calendar.DAY_OF_MONTH, 1);
                beginning.setTime(b.getTime());
            }

            c.add(substractionPrecision, -1);

            List<Object> params = new ArrayList<>();
            String insert = "insert into enterprise_admin.healthcare_activity_date_range (min_date, max_date)\n" +
                "values (" + parameterize(params, beginning) + ", " + parameterize(params, beginning) + ")";

            UtilityManagerCommon.runScript(insert, params);
        }
    }

    private void populateOrganisationTable(JsonHealthcareActivity options) throws Exception {

        List<String> orgScripts = new ArrayList<>();

        if (options.getOrganisationGroup() != null && !options.getOrganisationGroup().equals(0)) {
            includeOrganisationQuery = true;
            orgScripts.add(UtilityManagerCommon.createStandardOrganisationInsert(options.getOrganisationGroup(),
                "enterprise_admin.healthcare_activity_organisation_list"));
        }

        for (String script : orgScripts) {
            UtilityManagerCommon.runScript(script);
        }

    }

    private void populateServiceTable(JsonHealthcareActivity options) throws Exception {

        List<String> orgScripts = new ArrayList<>();

        if (options.getServiceGroupId() != null && !options.getServiceGroupId().equals(0)) {
            includeServiceQuery = true;
            orgScripts.add(UtilityManagerCommon.createStandardOrganisationInsert(options.getOrganisationGroup(),
                "enterprise_admin.healthcare_activity_service_list"));
        }

        for (String script : orgScripts) {
            UtilityManagerCommon.runScript(script);
        }

    }

    private void populatePatientList() {
        String sql = String.format("insert into enterprise_admin.healthcare_activity_patient_list (patient_id)\n" +
            "select \n" +
            "\tid \n" +
            "from patient p \n" +
            "%s \n" +
            "%s;", includeOrganisationQuery ? orgJoin : "", whereClauses);


        UtilityManagerCommon.runScript(sql, patientListParams);
    }

    private void populateRawData(JsonHealthcareActivity options) {
        List<Object> params = new ArrayList<>();

        String script = "insert into enterprise_admin.healthcare_activity_raw_data \n" +
            "(\tpatient_id,\n" +
            "    patient_gender_id,\n" +
            "    age_years,\n" +
            "    postcode_prefix,\n" +
            "    lsoa_code,\n" +
            "    msoa_code,\n" +
            "    ethnic_code,\n" +
            "    organisation_id,\n" +
            "    ccg,\n" +
            "    encounter_snomed_concept_id,\n" +
            "    clinical_effective_date,\n " +
            "    service_id, \n " +
            "    encounter_type)\n " +
            "select \n" +
            "\tpl.patient_id,\n" +
            "    p.patient_gender_id,\n" +
            "    p.age_years,\n" +
            "\tp.postcode_prefix, \n" +
            "    p.lsoa_code, \n" +
            "\tp.msoa_code, \n" +
            "\tp.ethnic_code, \n" +
            "    p.organization_id, \n" +
            "\tparentOrg.ods_code,\n" +
            "    e.snomed_concept_id,    \n" +
            "    e.clinical_effective_date, \n " +
            "    e.service_provider_organization_id, \n " +
            "    c.name \n " +
            "from enterprise_admin.healthcare_activity_patient_list pl \n" +
            "inner join patient p on p.id = pl.patient_id\n" +
            "inner join encounter e FORCE INDEX(ix_encounter_compound) on e.patient_id = p.id\n" +
            "inner JOIN organization org on org.id = p.organization_id \n" +
            "inner JOIN organization parentOrg on parentOrg.id = org.parent_organization_id \n" +
            "join enterprise_admin.expression_concept ec on ec.expression = e.snomed_concept_id\n" +
            "join enterprise_admin.concepts c on c.id = ec.value_concept \n " +
            (includeServiceQuery ? serviceJoin : "") +
            " where e.clinical_effective_date > " + parameterize(params, calculateEffectiveDate(options.getTimePeriodNo(), options.getTimePeriod())) + " ";

        if (options.getEncounterType() != null & options.getEncounterType().size() > 0) {
            script += " and c.id IN " + parameterize(params, options.getEncounterType());
        }

        UtilityManagerCommon.runScript(script, params);
    }

    private Calendar calculateEffectiveDate(String period, String units) {
        int periodVal = 0 - Integer.parseInt(period);
        Calendar effectiveDate = Calendar.getInstance();
        switch (units) {
            case "MONTHS":
                effectiveDate.add(Calendar.MONTH, periodVal);
                break;
            case "WEEKS":
                effectiveDate.add(Calendar.WEEK_OF_YEAR, periodVal);
                break;
            case "YEARS":
                effectiveDate.add(Calendar.YEAR, periodVal);
                break;
            default:
                throw new IllegalArgumentException("Unknown period unit [" + units + "]");
        }

        return effectiveDate;
    }

    private void generateWhereClausesFromOptions(JsonHealthcareActivity options) {

        List<String> whereClauseList = new ArrayList<>();

        if (options.getLsoaCode() != null && options.getLsoaCode().size() > 0)
            whereClauseList.add(" p.lsoa_code in " + parameterize(patientListParams, options.getLsoaCode().stream().map(JsonLsoa::getLsoaCode).collect(Collectors.toList())));

        if (options.getMsoaCode() != null && options.getMsoaCode().size() > 0)
            whereClauseList.add(" p.msoa_code in " + parameterize(patientListParams, options.getMsoaCode().stream().map(JsonMsoa::getMsoaCode).collect(Collectors.toList())));

        if (options.getEthnicity() != null && options.getEthnicity().size() > 0)
            whereClauseList.add(" p.ethnic_code in " + parameterize(patientListParams, options.getEthnicity()));

        if (options.getPostCodePrefix() != null && !options.getPostCodePrefix().equals(""))
            whereClauseList.add(" p.postcode_prefix = " + parameterize(patientListParams, options.getPostCodePrefix()));

        if (options.getAgeFrom() != null && !options.getAgeFrom().equals(""))
            whereClauseList.add(" p.age_years >= " + parameterize(patientListParams, options.getAgeFrom()));

        if (options.getAgeTo() != null && !options.getAgeTo().equals(""))
            whereClauseList.add(" p.age_years <= " + parameterize(patientListParams, options.getAgeTo()));

        if (options.getSex() != null && !options.getSex().equals("-1"))
            whereClauseList.add(" p.patient_gender_id = " + parameterize(patientListParams, options.getSex()));

        if (whereClauseList.size() > 0)
            whereClauses = " where " + String.join(" and ", whereClauseList);
    }

    public List getIncidenceResults(JsonHealthcareActivityGraph params) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        String select = "r.min_date, count(d.patient_id)";
        String from = " enterprise_admin.healthcare_activity_date_range r" +
            " left outer join enterprise_admin.healthcare_activity_raw_data d  " +
            "   on d.clinical_effective_date >= r.min_date and d.clinical_effective_date <= r.max_date";
        List<String> andJoin = getAndJoinClauseForIncidence(params);

        String group = "r.min_date";
        String order = "r.min_date";

        // GROUPING
        if (params.breakdown != null && !params.breakdown.isEmpty()) {
            select += ", IFNULL(" + parameterize(incidenceParams, params.breakdown) + ", 'Unknown')";
            group += ", IFNULL(" + parameterize(incidenceParams, params.breakdown) + ", 'Unknown')";
            order = parameterize(incidenceParams, params.breakdown) + ", " + order;
        }

        String sql = " SELECT " + select +
            " FROM " + from;
        if (andJoin.size() > 0)
            sql += " AND " + StringUtils.join(andJoin, "\nAND ");
        sql += " GROUP BY " + group +
            " ORDER BY " + order;

        System.out.println(sql);

        Query q = entityManager.createNativeQuery(sql);

        UtilityManagerCommon.setQueryParams(q, incidenceParams);

        List resultList = q.getResultList();

        System.out.println(resultList.size() + " rows affected");

        entityManager.close();

        return resultList;
    }

    public List<String> getAndJoinClauseForIncidence(JsonHealthcareActivityGraph params) {

        List<String> where = new ArrayList<>();
        incidenceParams = new ArrayList<>();

        // FILTERING
        if (params.gender != null && params.gender.size() > 0)
            where.add("patient_gender_id in " + parameterize(incidenceParams, params.gender));

        if (params.ethnicity != null && params.ethnicity.size() > 0)
            where.add("ethnic_code in " + parameterize(incidenceParams, params.ethnicity));

        if (params.postcode != null && params.postcode.size() > 0)
            where.add("postcode_prefix in " + parameterize(incidenceParams, params.postcode));

        if (params.lsoa != null && params.lsoa.size() > 0)
            where.add("lsoa_code in " + parameterize(incidenceParams, params.lsoa));

        if (params.msoa != null && params.msoa.size() > 0)
            where.add("msoa_code in " + parameterize(incidenceParams, params.msoa));

        if (params.services != null && params.services.size() > 0)
            where.add("service_id in " + parameterize(incidenceParams, params.services));

        if (params.orgs != null && params.orgs.size() > 0)
            where.add("organisation_id in " + parameterize(incidenceParams, params.orgs));

        if (params.encounterType != null && params.encounterType.size() > 0)
            where.add("encounter_type in " + parameterize(incidenceParams, params.encounterType));

        if (params.agex10 != null && params.agex10.size() > 0) {
            List<String> ageWhere = new ArrayList<>();

            for (int i = 0; i < params.agex10.size(); i++) {
                int agex10 = Integer.parseInt(params.agex10.get(i)) * 10;
                if (agex10 == 0)
                    ageWhere.add("age_years < 10");
                else if (agex10 == 90)
                    ageWhere.add("age_years > 90");
                else
                    ageWhere.add("(age_years >= " + String.valueOf(agex10) + " AND age_years < " + String.valueOf(agex10 + 9) + ")");
            }

            where.add("(" + StringUtils.join(ageWhere, "\nOR ") + ")");
        }

        return where;
    }

    private String parameterize(List<Object> list, Object value) {
        list.add(value);
        return " ?" + list.size() + " ";
    }
}
