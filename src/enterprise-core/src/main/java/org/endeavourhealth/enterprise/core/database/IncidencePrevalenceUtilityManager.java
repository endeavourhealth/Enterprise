package org.endeavourhealth.enterprise.core.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.enterprise.core.json.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class IncidencePrevalenceUtilityManager {

    private boolean includeOrganisationQuery = false;
    private String orgJoin = " join enterprise_admin.incidence_prevalence_organisation_list o " +
        " on o.organisation_id = p.organization_id ";
    private String whereClauses = "";
    private List<Object> whereParams = new ArrayList<>();

    public boolean runPrevIncReport(JsonPrevInc options) throws Exception {

        cleanUpDatabase();
        createTemporaryTables();
        initialiseReportResultTable(options);
        saveReportOptions(options);
        generateWhereClausesFromOptions(options);
        populateOrganisationTable(options);
        populatePatientTable();
        populateClinicalData(options);
        updatePersonData();
        runInitialPopulationQuery();
        //runIncidenceQueries();
        //runPrevalenceQueries();
        //runPopulationQueries();

        return true;
    }

    private void initialiseReportResultTable(JsonPrevInc options) throws Exception {
        UtilityManagerCommon.runScript("delete from enterprise_admin.incidence_prevalence_date_range;");

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
            String insert = "insert into enterprise_admin.incidence_prevalence_date_range (min_date, max_date)\n" +
                "values (" + parameterize(params, beginning) + ", " + parameterize(params, beginning) + ")";

            UtilityManagerCommon.runScript(insert, params);
        }
    }

    private void saveReportOptions(JsonPrevInc options) throws Exception {

        ObjectMapper objMap = new ObjectMapper();


        String script = "insert into enterprise_admin.incidence_prevalence_options (options)\n" +
            "values (?1);";

        List<Object> params = new ArrayList<>();
        params.add(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(options));

        UtilityManagerCommon.runScript(script, params);

    }

    public JsonPrevInc getReportOptions() throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<String> optionsScripts = new ArrayList<>();

        ObjectMapper objMap = new ObjectMapper();

        entityManager.getTransaction().begin();

        Query q = entityManager.createNativeQuery("select options from enterprise_admin.incidence_prevalence_options;");

        JsonPrevInc options = (JsonPrevInc) objMap.readValue(q.getSingleResult().toString(), JsonPrevInc.class);

        entityManager.getTransaction().commit();
        entityManager.close();

        return options;
    }

    private void cleanUpDatabase() throws Exception {

        includeOrganisationQuery = false;
        whereClauses = "";

        List<String> deleteScripts = new ArrayList<>();

        deleteScripts.add("drop table if exists enterprise_admin.incidence_prevalence_raw_data;");
        deleteScripts.add("drop table if exists enterprise_admin.incidence_prevalence_patient_list;");
        deleteScripts.add("drop table if exists enterprise_admin.incidence_prevalence_organisation_list;");
        deleteScripts.add("drop table if exists enterprise_admin.incidence_prevalence_population_list;");
        deleteScripts.add("drop table if exists enterprise_admin.incidence_prevalence_date_range");
        deleteScripts.add("drop table if exists enterprise_admin.incidence_prevalence_options");

        for (String script : deleteScripts) {
            UtilityManagerCommon.runScript(script);
        }
    }

    private void createTemporaryTables() throws Exception {

        List<String> tempTableScripts = new ArrayList<>();

        tempTableScripts.add("create table enterprise_admin.incidence_prevalence_raw_data (\n" +
            "\tperson_id bigint(20) not null,\n" +
            "    patient_gender_id smallint(6) null,\n" +
            "    age_years int(11) null,\n" +
            "    postcode_prefix varchar(20) null,\n" +
            "    clinical_effective_date date null,\n" +
            "    clinical_effective_year int(4) null,\n" +
            "    clinical_effective_month int(4) null,\n" +
            "    lsoa_code varchar(50) null,\n" +
            "    msoa_code varchar(50) null,\n" +
            "    ethnic_code char(1) null,\n" +
            "    organisation_id bigint(20) null,\n" +
            "    date_of_death date null,\n" +
            "    date_of_death_year int(6) null,\n" +
            "    date_of_death_month int(6) null,\n" +
            "    ccg varchar(10) null, \n" +
            "    \n" +
            "    index ix_incidence_prevalence_raw_data_clinical_effective_date (clinical_effective_date),    \n" +
            "    index ix_incidence_prevalence_raw_data_patient_gender_id (patient_gender_id),    \n" +
            "    index ix_incidence_prevalence_raw_data_postcode_prefix (postcode_prefix),\n" +
            "    index ix_incidence_prevalence_raw_data_age_years (age_years),      \n" +
            "    index ix_incidence_prevalence_raw_data_person_id (person_id)      \n" +
            "    \n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.incidence_prevalence_patient_list (\n" +
            "\tpatient_id bigint(20) not null primary key\n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.incidence_prevalence_organisation_list (\n" +
            "\torganisation_id bigint(20) not null primary key\n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.incidence_prevalence_population_list (\n" +
            "\tepisode_id bigint(20) not null,\n" +
            "\tperson_id bigint(20) not null,\n" +
            "    date_registered date null,\n" +
            "    date_registered_year int(4) null,\n" +
            "    date_registered_month int(4) null,\n" +
            "    date_registered_end date null,\n" +
            "    date_registered_end_year int(4) null,\n" +
            "    date_registered_end_month int(4) null,\n" +
            "    patient_gender_id smallint(6) null,\n" +
            "    age_years int(11) null,\n" +
            "    organisation_id bigint(20) null,\n" +
            "    date_of_death date null,\n" +
            "    date_of_death_year int(4) null,\n" +
            "    date_of_death_month int(4) null,\n" +
            "    lsoa_code varchar(50) null,\n" +
            "    msoa_code varchar(50) null,\n" +
            "    ethnic_code char(1) null,\n" +
            "    ccg varchar(10) null, \n" +
            "    postcode_prefix varchar(20) null, \n" +
            "     \n" +
            "\tprimary key (episode_id, person_id),\n" +
            "    index ix_population_list_date_registered_year (date_registered_year),  \n" +
            "    index ix_population_list_date_registered_month (date_registered_month), \n" +
            "    index ix_population_list_patient_gender_id (patient_gender_id),    \n" +
            "    index ix_population_list_date_registered_end_year (date_registered_end_year),\n" +
            "    index ix_population_list_date_registered_end_month (date_registered_end_month),\n" +
            "    index ix_population_list_date_of_death_year (date_of_death_year) ,\n" +
            "    index ix_population_list_date_of_death_month (date_of_death_month),       \n" +
            "    index ix_population_list_msoa_code (msoa_code),       \n" +
            "    index ix_population_list_lsoa_code (lsoa_code)       \n" +
            "    \n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.incidence_prevalence_date_range (\n" +
            " min_date date,\n" +
            " max_date date,\n" +
            " \n" +
            " index ix_incidence_prevalence_date_range_min_date (min_date),\n" +
            " index ix_incidence_prevalence_date_range_max_date (max_date)\n" +
            " \n" +
            ");");

        tempTableScripts.add("create table enterprise_admin.incidence_prevalence_options (\n" +
            " options varchar(5000)\n);");

        for (String script : tempTableScripts) {
            UtilityManagerCommon.runScript(script);
        }
    }

    private void populateOrganisationTable(JsonPrevInc options) throws Exception {

        if (options.getOrganisationGroup() != null && !options.getOrganisationGroup().equals(0)) {
            includeOrganisationQuery = true;
            String script = UtilityManagerCommon.createStandardOrganisationInsert(
                options.getOrganisationGroup(),
                "enterprise_admin.incidence_prevalence_organisation_list");
            UtilityManagerCommon.runScript(script);
        }
    }

    private void generateWhereClausesFromOptions(JsonPrevInc options) throws Exception {

        List<String> whereClauseList = new ArrayList<>();

        if (options.getLsoaCode() != null && options.getLsoaCode().size() > 0)
            whereClauseList.add(" p.lsoa_code in " + parameterize(whereParams, options.getLsoaCode().stream().map(JsonLsoa::getLsoaCode).collect(Collectors.toList())));

        if (options.getMsoaCode() != null && options.getMsoaCode().size() > 0)
            whereClauseList.add(" p.msoa_code in " + parameterize(whereParams, options.getMsoaCode().stream().map(JsonMsoa::getMsoaCode).collect(Collectors.toList())));

        if (options.getEthnicity() != null && options.getEthnicity().size() > 0)
            whereClauseList.add(" p.ethnic_code in " + parameterize(whereParams, options.getEthnicity()));

        if (options.getPostCodePrefix() != null && !options.getPostCodePrefix().equals(""))
            whereClauseList.add(" p.postcode_prefix = " + parameterize(whereParams, options.getPostCodePrefix()));

        if (options.getAgeFrom() != null && !options.getAgeFrom().equals(""))
            whereClauseList.add(" p.age_years >= " + parameterize(whereParams, options.getAgeFrom()));

        if (options.getAgeTo() != null && !options.getAgeTo().equals(""))
            whereClauseList.add(" p.age_years <= " + parameterize(whereParams, options.getAgeTo()));

        if (options.getSex() != null && !options.getSex().equals("-1"))
            whereClauseList.add(" p.patient_gender_id = " + parameterize(whereParams, options.getSex()));

        String allWhereClauses = "";
        String prefix = " where ";
        for (String where : whereClauseList) {
            allWhereClauses += prefix + where;
            prefix = " and ";
        }

        whereClauses = allWhereClauses;
    }

    private void populatePatientTable() throws Exception {

        String script = String.format("insert into enterprise_admin.incidence_prevalence_patient_list (patient_id)\n" +
            "select \n" +
            "\tid \n" +
            "from patient p" +
            "%s" +
            "%s;", includeOrganisationQuery ? orgJoin : "", whereClauses);

        UtilityManagerCommon.runScript(script, whereParams);
    }

    private void runChronicDataScript(JsonPrevInc options) throws Exception {

        String script = "insert into enterprise_admin.incidence_prevalence_raw_data (person_id, clinical_effective_date)\n" +
            "SELECT\n" +
            "\tDISTINCT\n" +
            "    d.person_id, \n" +
            "    min(IFNULL(d.clinical_effective_date, '1000-01-01')) as clinical_effective_date\n" +
            "FROM enterprise_admin.incidence_prevalence_patient_list p \n" +
            "JOIN observation d \n" +
            "\tON p.patient_id = d.patient_id\n" +
            "JOIN enterprise_admin.CodeSet c\n" +
            "    ON c.SnomedConceptId = d.snomed_concept_id\n" +
            "WHERE \n" +
            "    c.ItemUuid = ?1\n" +
            "group by d.person_id;";

        List<Object> params = new ArrayList<>();
        params.add(options.getCodeSet());

        UtilityManagerCommon.runScript(script, params);
    }

    private void runAcuteDataScript(JsonPrevInc options) throws Exception {
        String script = "insert into enterprise_admin.incidence_prevalence_raw_data (person_id, clinical_effective_date)\n" +
            "SELECT\n" +
            "\tDISTINCT\n" +
            "    d.person_id, \n" +
            "    IFNULL(d.clinical_effective_date, '1000-01-01') as clinical_effective_date\n" +
            "FROM enterprise_admin.incidence_prevalence_patient_list p \n" +
            "JOIN observation d \n" +
            "\tON p.patient_id = d.patient_id\n" +
            "JOIN enterprise_admin.CodeSet c\n" +
            "    ON c.SnomedConceptId = d.snomed_concept_id\n" +
            "WHERE \n" +
            "    c.ItemUuid = ?1\n " +
            "    and d.is_review = 0;";

        List<Object> params = new ArrayList<>();
        params.add(options.getCodeSet());

        UtilityManagerCommon.runScript(script, params);
    }

    private void populateClinicalData(JsonPrevInc options) throws Exception {

        if (options.getDiseaseCategory().equals("0")) {
            runChronicDataScript(options);
        } else {
            runAcuteDataScript(options);
        }
    }

    private void updatePersonData() throws Exception {

        String script = "update enterprise_admin.incidence_prevalence_raw_data r\n" +
            "inner join patient p on p.person_id = r.person_id\n" +
            "inner JOIN organization org on org.id = p.organization_id \n" +
            "inner JOIN organization parentOrg on parentOrg.id = org.parent_organization_id \n" +
            "set r.patient_gender_id = p.patient_gender_id,\n" +
            "\tr.age_years = p.age_years,\n" +
            "    r.postcode_prefix = p.postcode_prefix,\n" +
            "    r.clinical_effective_year = YEAR(r.clinical_effective_date),\n" +
            "    r.clinical_effective_month = MONTH(r.clinical_effective_date),\n" +
            "    r.lsoa_code = p.lsoa_code,\n" +
            "    r.msoa_code = p.msoa_code,\n" +
            "    r.ethnic_code =  p.ethnic_code,\n" +
            "    r.organisation_id = p.organization_id,\n" +
            "    r.date_of_death = ifnull(p.date_of_death, '9999-01-01'),\n" +
            "    r.date_of_death_year = year(ifnull(p.date_of_death, '9999-01-01')),\n" +
            "    r.date_of_death_month = month(ifnull(p.date_of_death, '9999-01-01'))," +
            "    r.ccg = parentOrg.ods_code;";

        UtilityManagerCommon.runScript(script);
    }

    private void runInitialPopulationQuery() throws Exception {

        String script = String.format("insert into enterprise_admin.incidence_prevalence_population_list\n" +
            "select \n" +
            "\te.id,\n" +
            "\te.person_id,\n" +
            "    e.date_registered,\n" +
            "    YEAR(e.date_registered),\n" +
            "    MONTH(e.date_registered),\n" +
            "    IFNULL(e.date_registered_end, '9999-12-31'),\n" +
            "    YEAR(IFNULL(e.date_registered_end, '9999-12-31')),\n" +
            "    MONTH(IFNULL(e.date_registered_end, '9999-12-31')),\n" +
            "    p.patient_gender_id,\n" +
            "    p.age_years,\n" +
            "    p.organization_id,\n" +
            "    IFNULL(p.date_of_death, '9999-12-31'),\n" +
            "    YEAR(IFNULL(p.date_of_death, '9999-12-31')),\n" +
            "    MONTH(IFNULL(p.date_of_death, '9999-12-31')),    \n" +
            "    p.lsoa_code, \n " +
            "    p.msoa_code, \n " +
            "    p.ethnic_code, \n " +
            "    parentOrg.ods_code, \n " +
            "    p.postcode_prefix \n" +
            "from episode_of_care e\n" +
            "join patient p \n" +
            "\ton p.id = e.patient_id and e.organization_id = p.organization_id and e.person_id = p.person_id \n" +
            "inner JOIN organization org on org.id = p.organization_id \n" +
            "inner JOIN organization parentOrg on parentOrg.id = org.parent_organization_id \n" +
            "%s" +
            "%s", includeOrganisationQuery ? orgJoin : "", whereClauses);

        UtilityManagerCommon.runScript(script, whereParams);
    }

    public List getIncidenceResults(JsonPrevIncGraph runParams) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
        List<Object> sqlParams = new ArrayList<>();

        String select = "r.min_date, count(distinct d.person_id)";
        String from = " enterprise_admin.incidence_prevalence_date_range r" +
            " left outer join enterprise_admin.incidence_prevalence_raw_data d  " +
            "   on d.clinical_effective_date >= r.min_date and d.clinical_effective_date <= r.max_date";
        List<String> andJoin = getAndJoinClauseForIncidence(runParams, sqlParams);

        String group = "r.min_date";
        String order = "r.min_date";

        // GROUPING
        if (runParams.breakdown != null && !runParams.breakdown.isEmpty()) {
            select += ", IFNULL(" + parameterize(sqlParams, runParams.breakdown) + ", 'Unknown')";
            group += ", IFNULL(" + parameterize(sqlParams, runParams.breakdown) + ", 'Unknown')";
            order = order + ", " + parameterize(sqlParams, runParams.breakdown);
        }

        String sql = " SELECT " + select +
            " FROM " + from;
        if (andJoin.size() > 0)
            sql += " AND " + StringUtils.join(andJoin, "\nAND ");
        sql += " GROUP BY " + group +
            " ORDER BY " + order;

        System.out.println(sql);

        Query q = entityManager.createNativeQuery(sql);

        List resultList = q.getResultList();

        System.out.println(resultList.size() + " rows affected");

        entityManager.close();

        return resultList;
    }

    public List<String> getAndJoinClauseForIncidence(JsonPrevIncGraph runParams, List<Object> sqlParams) {

        List<String> where = new ArrayList<>();

        // FILTERING
        if (runParams.gender != null && runParams.gender.size() > 0)
            where.add("patient_gender_id in " + parameterize(sqlParams, runParams.gender) + "\n");

        if (runParams.ethnicity != null && runParams.ethnicity.size() > 0)
            where.add("ethnic_code in " + parameterize(sqlParams, runParams.ethnicity) + "\n");

        if (runParams.postcode != null && runParams.postcode.size() > 0)
            where.add("postcode_prefix in " + parameterize(sqlParams, runParams.postcode) + "\n");

        if (runParams.lsoa != null && runParams.lsoa.size() > 0)
            where.add("lsoa_code in " + parameterize(sqlParams, runParams.lsoa) + "\n");

        if (runParams.msoa != null && runParams.msoa.size() > 0)
            where.add("msoa_code in " + parameterize(sqlParams, runParams.msoa) + "\n");

        if (runParams.orgs != null && runParams.orgs.size() > 0)
            where.add("organisation_id in " + parameterize(sqlParams, runParams.orgs) + "\n");

        if (runParams.agex10 != null && runParams.agex10.size() > 0) {
            List<String> ageWhere = new ArrayList<>();

            for (int i = 0; i < runParams.agex10.size(); i++) {
                int agex10 = Integer.parseInt(runParams.agex10.get(i)) * 10;
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

    public List getPrevalenceResults(JsonPrevIncGraph runParams) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<Object> sqlParams = new ArrayList<>();

        String select = "r.min_date, count(distinct d.person_id)";
        String from = " enterprise_admin.incidence_prevalence_date_range r" +
            " left outer join enterprise_admin.incidence_prevalence_raw_data d  " +
            "   on d.clinical_effective_date <= r.max_date";
        List<String> joinAnd = getAndJoinClauseForIncidence(runParams, sqlParams);

        String group = "r.min_date";
        String order = "r.min_date";

        joinAnd.add(" d.date_of_death > r.max_date ");

        // GROUPING
        if (runParams.breakdown != null && !runParams.breakdown.isEmpty()) {
            select += ", IFNULL(" + parameterize(sqlParams, runParams.breakdown) + ", 'Unknown')";
            group += ", IFNULL(" + parameterize(sqlParams, runParams.breakdown) + ", 'Unknown')";
            order = order + ", " + parameterize(sqlParams, runParams.breakdown);
        }

        String sql = " SELECT " + select +
            " FROM " + from;
        if (joinAnd.size() > 0)
            sql += " AND " + StringUtils.join(joinAnd, "\nAND ");
        sql += " GROUP BY " + group +
            " ORDER BY " + order;

        System.out.println(sql);

        Query q = entityManager.createNativeQuery(sql);

        List resultList = q.getResultList();

        System.out.println(resultList.size() + " rows affected");

        entityManager.close();

        return resultList;
    }

    public List getPopulationResults(JsonPrevIncGraph runParams) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<Object> sqlParams = new ArrayList<>();

        String select = "r.min_date, count(distinct d.person_id)";
        String from = " enterprise_admin.incidence_prevalence_date_range r" +
            " left outer join enterprise_admin.incidence_prevalence_population_list d  " +
            "   on d.date_of_death > r.max_date " +
            "   and d.date_registered <= r.max_date " +
            "   and d.date_registered_end >= r.min_date";

        List<String> joinAnd = getAndJoinClauseForIncidence(runParams, sqlParams);

        String group = "r.min_date";
        String order = "r.min_date";

        // GROUPING
        if (runParams.breakdown != null && !runParams.breakdown.isEmpty()) {
            select += ", IFNULL(" + parameterize(sqlParams, runParams.breakdown) + ", 'Unknown')";
            group += ", IFNULL(" + parameterize(sqlParams, runParams.breakdown) + ", 'Unknown')";
            order = order + ", " + parameterize(sqlParams, runParams.breakdown);
        }


        String sql = " SELECT " + select +
            " FROM " + from;
        if (joinAnd.size() > 0)
            sql += " AND " + StringUtils.join(joinAnd, "\nAND ");
        sql += " GROUP BY " + group +
            " ORDER BY " + order;

        System.out.println(sql);

        Query q = entityManager.createNativeQuery(sql);

        List resultList = q.getResultList();

        System.out.println(resultList.size() + " rows affected");

        entityManager.close();

        return resultList;
    }

    public List getOrganisationGroups() throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        Query q = entityManager.createNativeQuery("select group_id, group_name " +
            " from enterprise_admin.incidence_prevalence_organisation_group" +
            " order by group_name ASC ;");

        List resultList = q.getResultList();

        entityManager.close();

        return resultList;
    }

    public List getOrganisationsInGroup(Integer groupId) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        String query =
            "select distinct o.ods_code, o.name, o.id from enterprise_admin.incidence_prevalence_organisation_group_lookup l\n" +
                " join organization o on o.ods_code = l.ods_code " +
                " join patient p on p.organization_id = o.id " +
                "where l.group_id = ?1;";
        System.out.println(query);
        Query q = entityManager.createNativeQuery(query);
        q.setParameter(1, groupId);

        List resultList = q.getResultList();

        entityManager.close();

        return resultList;
    }

    public List getAvailableOrganisations() throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        Query q = entityManager.createNativeQuery("select distinct o.name, o.ods_code, ifnull(o.type_code, 'PR') as type_code\n" +
            "from organization o\n" +
            "join patient p on p.organization_id = o.id\n" +
            "union\n" +
            "\n" +
            "select distinct ccg.name, ccg.ods_code, ccg.type_code\n" +
            "from organization o\n" +
            "join patient p on p.organization_id = o.id\n" +
            "join organization ccg on ccg.id = o.parent_organization_id\n" +
            "order by name;");

        List resultList = q.getResultList();

        entityManager.close();

        return resultList;
    }

    public void updateGroup(JsonOrganisationGroup group) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        String query =
            "update enterprise_admin.incidence_prevalence_organisation_group \n" +
                " set group_name = ?1 " +
                "where group_id = ?2;";
        System.out.println(query);
        Query q = entityManager.createNativeQuery(query);

        q.setParameter(1, group.getName());
        q.setParameter(2, group.getId());
        entityManager.getTransaction().begin();
        q.executeUpdate();


        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public Integer saveNewGroup(JsonOrganisationGroup group) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        String query =
            "insert into enterprise_admin.incidence_prevalence_organisation_group (group_name) \n" +
                " values ( ?1 )";
        System.out.println(query);
        Query q = entityManager.createNativeQuery(query);

        q.setParameter(1, group.getName());

        entityManager.getTransaction().begin();
        q.executeUpdate();

        q = entityManager.createNativeQuery("SELECT LAST_INSERT_ID();");

        Integer groupId = Integer.parseInt(q.getSingleResult().toString());

        entityManager.getTransaction().commit();
        entityManager.close();

        return groupId;
    }

    public void deleteOrganisationsInGroup(JsonOrganisationGroup group) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        entityManager.getTransaction().begin();

        String query =
            "delete from enterprise_admin.incidence_prevalence_organisation_group_lookup \n" +
                "where group_id = ?1;";

        System.out.println(query);
        Query q = entityManager.createNativeQuery(query);
        q.setParameter(1, group.getId());
        q.executeUpdate();

        entityManager.getTransaction().commit();

        entityManager.close();

    }

    public void insertGroupOrganisations(JsonOrganisationGroup group) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        String query = "insert into enterprise_admin.incidence_prevalence_organisation_group_lookup (group_id, ods_code)" +
            " values (?1, ?2)";

        System.out.println(query);

        entityManager.getTransaction().begin();
        Query q = entityManager.createNativeQuery(query);

        for (JsonOrganisation org : group.getOrganisations()) {
            q.setParameter(1, group.getId());
            q.setParameter(2, org.getId());
            q.executeUpdate();
        }

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private String parameterize(List<Object> list, Object value) {
        list.add(value);
        return " ?" + list.size() + " ";
    }
}
