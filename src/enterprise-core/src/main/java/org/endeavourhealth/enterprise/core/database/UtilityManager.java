package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.enterprise.core.json.JsonPrevInc;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UtilityManager {

    public boolean runPrevIncReport(JsonPrevInc options) throws Exception {

        cleanUpDatabase();
        initialiseReportResultTable(options);
        createDataTable(options.getCodeSet());
        runIncidenceQueries();
        runPrevalenceQueries();
        runPopulationQueries();

        return true;
    }

    private void initialiseReportResultTable(JsonPrevInc options) throws Exception {
        List<String> initialiseScripts = new ArrayList<>();

        initialiseScripts.add("delete from enterprise_admin.incidence_prevalence_result;");

        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        Date beginning;
        Date end;

        Integer number = Integer.parseInt(options.getTimePeriodNo());

        String insert = "insert into enterprise_admin.incidence_prevalence_result (query_id, query_title, min_date, max_date)\n" +
                "values ('70134d14-8402-11e7-a9c9-0a0027000012', '%s', '%s', '%s')";

        int precision = Calendar.DAY_OF_YEAR;
        int substractionPrecision = Calendar.YEAR;

        if (options.getTimePeriod().equals("MONTHS")) {
            precision = Calendar.DAY_OF_MONTH;
            substractionPrecision = Calendar.MONTH;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Integer i = 0; i < number; i++) {
            // get the first day of the month/year
            c.set(precision, c.getActualMinimum(precision));
            beginning = c.getTime();
            // get the last day of the month/year
            c.set(precision, c.getActualMaximum(precision));
            end = c.getTime();

            initialiseScripts.add(String.format(insert, options.getTitle(), dateFormat.format(beginning).toString(),dateFormat.format(end).toString()));

            c.add(substractionPrecision, -1);
        }

        for (String script : initialiseScripts) {
            System.out.println(script);
            runScript(script);
        }

    }

    private void cleanUpDatabase() throws Exception {

        List<String> deleteScripts = new ArrayList<>();

        deleteScripts.add("drop table if exists enterprise_admin.incidence_prevalence_population;");
        deleteScripts.add("drop table if exists enterprise_admin.incidence_prevalence_data;");

        for (String script : deleteScripts) {
            runScript(script);
        }

    }

    private void createDataTable(String codeSetUuid) throws Exception {

        List<String> dataTableScripts = new ArrayList<>();

        dataTableScripts.add(String.format("CREATE TABLE enterprise_admin.incidence_prevalence_data\n" +
                        "SELECT\n" +
                        "\tDISTINCT\n" +
                        "    d.patient_id, \n" +
                        "    min(coalesce(d.clinical_effective_date, '1000-01-01')) as clinical_effective_date\n" +
                        "FROM enterprise_admin.CodeSet c\n" +
                        "JOIN enterprise_data_pseudonymised.observation d \n" +
                        "    ON c.SnomedConceptId = d.snomed_concept_id\n" +
                        "WHERE \n" +
                        "    c.ItemUuid = '%s'\n" +
                        "group by d.patient_id;",
                codeSetUuid));

        dataTableScripts.add("CREATE INDEX ix_incidence_prevalence_data_clinical_date\n" +
                "ON enterprise_admin.incidence_prevalence_data (clinical_effective_date);");

        dataTableScripts.add("CREATE UNIQUE INDEX ix_incidence_prevalence_data_patient_id\n" +
                "ON enterprise_admin.incidence_prevalence_data (patient_id);");

        for (String script : dataTableScripts) {
            runScript(script);
        }
    }

    private void runIncidenceQueries() throws Exception {

        List<String> incidenceScripts = new ArrayList<>();

        String incidenceQuery = "update enterprise_admin.incidence_prevalence_result res, \n" +
                "(select \n" +
                "\tr.min_date,\n" +
                "    r.max_date,\n" +
                "    count(DISTINCT p.person_id) total\n" +
                "from enterprise_admin.incidence_prevalence_result r\n" +
                "left outer join enterprise_admin.incidence_prevalence_data d \n" +
                "\ton d.clinical_effective_date >= r.min_date and d.clinical_effective_date <= r.max_date \n" +
                "left outer join enterprise_data_pseudonymised.patient p \n" +
                "\ton p.id = d.patient_id\n" +
                "    %s\n" +
                "where r.query_id = '70134d14-8402-11e7-a9c9-0a0027000012'\n" +
                "group by r.min_date, r.max_date) gru\n" +
                "set res.%s = gru.total\n" +
                "where res.min_date = gru.min_date\n" +
                "and res.query_id = '70134d14-8402-11e7-a9c9-0a0027000012';";

        // Male
        incidenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id = 0", "incidence_male"));
        // Female
        incidenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id = 1", "incidence_female"));
        // Other
        incidenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id not in (0, 1)", "incidence_other"));

        for (String script : incidenceScripts) {
            runScript(script);
        }
    }

    private void runPrevalenceQueries() throws Exception {

        List<String> prevalenceScripts = new ArrayList<>();

        String incidenceQuery = "update enterprise_admin.incidence_prevalence_result res, \n" +
                "(select \n" +
                "\tr.min_date,\n" +
                "    r.max_date,\n" +
                "    COUNT(DISTINCT p.person_id) total \n" +
                "from enterprise_admin.incidence_prevalence_result r\n" +
                "left outer join enterprise_admin.incidence_prevalence_data d \n" +
                "\ton d.clinical_effective_date <= r.max_date\n" +
                "left outer join enterprise_data_pseudonymised.patient p \n" +
                "\ton p.id = d.patient_id\n" +
                "\t%s\n" +
                "    AND (p.date_of_death IS NULL OR p.date_of_death > r.max_date)\n" +
                "    where r.query_id = '70134d14-8402-11e7-a9c9-0a0027000012'\n" +
                "group by r.min_date, r.max_date) gru\n" +
                "set res.%s = gru.total\n" +
                "where res.min_date = gru.min_date\n" +
                "and res.query_id = '70134d14-8402-11e7-a9c9-0a0027000012';";

        // Male
        prevalenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id = 0", "prevalence_male"));
        // Female
        prevalenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id = 1", "prevalence_female"));
        // Other
        prevalenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id not in (0, 1)", "prevalence_other"));

        for (String script : prevalenceScripts) {
            runScript(script);
        }
    }

    private void runPopulationQueries() throws Exception {

        List<String> prevalenceScripts = new ArrayList<>();

        String incidenceQuery = "update enterprise_admin.incidence_prevalence_result res, \n" +
                "(select \n" +
                "\tr.min_date,\n" +
                "    r.max_date,\n" +
                "    COUNT(DISTINCT e.person_id) total -- DL changed to count distinct persons\n" +
                "from enterprise_admin.incidence_prevalence_result r\n" +
                "left outer join enterprise_data_pseudonymised.patient p\n" +
                "    on (p.date_of_death IS NULL OR p.date_of_death > r.max_date)\n" +
                "    %s\n" +
                "left outer join enterprise_data_pseudonymised.episode_of_care e \n" +
                "\tON e.person_id = p.person_id and e.organization_id = p.organization_id AND e.patient_id = p.id  \n" +
                "    and e.date_registered <= r.max_date \n" +
                "    and (e.date_registered_end is null or e.date_registered_end >= r.min_date) \n" +
                "where r.query_id = '70134d14-8402-11e7-a9c9-0a0027000012'\n" +
                "group by r.min_date, r.max_date) gru\n" +
                "set res.%s = gru.total\n" +
                "where res.min_date = gru.min_date\n" +
                "and res.query_id = '70134d14-8402-11e7-a9c9-0a0027000012';";

        // Male
        prevalenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id = 0", "population_male"));
        // Female
        prevalenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id = 1", "population_female"));
        // Other
        prevalenceScripts.add(String.format(incidenceQuery, " and p.patient_gender_id not in (0, 1)", "population_other"));

        for (String script : prevalenceScripts) {
            runScript(script);
        }
    }

    private void runScript(String script) throws Exception {

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        entityManager.getTransaction().begin();

        Query q = entityManager.createNativeQuery(script);

        int resultCount = q.executeUpdate();

        System.out.println(resultCount + " rows affected");
        entityManager.getTransaction().commit();

        entityManager.close();

    }

    public List getIncPrevResults() {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        Query q = entityManager.createNativeQuery("SELECT "+
            "min_date, "+
            "incidence_male, incidence_female, incidence_other, "+
            "prevalence_male, prevalence_female, prevalence_other, "+
            "population_male, population_female, population_other, query_title "+
            "FROM enterprise_admin.incidence_prevalence_result ORDER BY min_date ASC");

        List resultList = q.getResultList();

        System.out.println(resultList.size() + " rows affected");

        entityManager.close();

        return resultList;
    }
}
