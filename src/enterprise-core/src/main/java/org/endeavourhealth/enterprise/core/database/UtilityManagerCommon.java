package org.endeavourhealth.enterprise.core.database;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public final class UtilityManagerCommon {

    public static void runScript(String script) throws Exception {
        System.out.println(script);
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        entityManager.getTransaction().begin();
        try {
            Query q = entityManager.createNativeQuery(script);

            int resultCount = q.executeUpdate();

            System.out.println(resultCount + " rows affected");
        } finally {
            entityManager.getTransaction().commit();

            entityManager.close();
        }
    }

    private static List getDistinctValuesForGraphingNoLookup(String columnName, String rawDataTableName) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        Query q = entityManager.createNativeQuery("SELECT DISTINCT " + columnName + " as id, " +
                " ifnull(" + columnName + ", 'Unknown') " +
                " FROM " + rawDataTableName + " d " +
                " ORDER BY " + columnName +
                " ASC");

        List resultList = q.getResultList();

        entityManager.close();

        return resultList;
    }

    public static String createStandardOrganisationInsert(Integer organisationGroup, String tableName) throws Exception {

        String query = String.format("insert into %s " +
                " select distinct coalesce(child.id, o.id) \n" +
                "from enterprise_admin.incidence_prevalence_organisation_group_lookup l\n" +
                "join enterprise_data_pseudonymised.organization o on o.ods_code = l.ods_code\n" +
                "left outer join enterprise_data_pseudonymised.organization child on child.parent_organization_id = o.id and child.type_code = 'PR'\n" +
                "where l.group_id = %d;", tableName, organisationGroup);


        return query;
    }

    public static List getDistinctValuesForGraphing(String columnName, String rawDataTableName) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
        List resultList;
        try {
            if (columnName.equals("postcode_prefix")) {
                return getDistinctValuesForGraphingNoLookup(columnName, rawDataTableName);
            }

            String joinTable = getJoinTableForDistinctValues(columnName);
            String joinColumn = getJoinColumnForDistinctValues(columnName);
            String lookupColumn = getLookupColumnForDistinctValues(columnName);

            String query = "SELECT DISTINCT d." + columnName + ", " +
                    " ifnull(j." + lookupColumn + ", 'Unknown') " +
                    " FROM " + rawDataTableName + " d " +
                    " join " + joinTable + " j on d." + columnName + " =  j." + joinColumn + " ORDER BY " +
                    " j." + lookupColumn +
                    " ASC";
            Query q = entityManager.createNativeQuery(query);
            System.out.println(query);
            entityManager.getTransaction().begin();
            resultList = q.getResultList();
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return resultList;
    }

    private static String getJoinTableForDistinctValues(String column) {
        switch (column) {
            case "encounter_snomed_concept_id":
                return "enterprise_data_pseudonymised.concept";
            case "patient_gender_id":
                return "enterprise_data_pseudonymised.patient_gender";
            case "lsoa_code":
                return "enterprise_data_pseudonymised.lsoa_lookup";
            case "msoa_code":
                return "enterprise_data_pseudonymised.msoa_lookup";
            case "ethnic_code":
                return "enterprise_data_pseudonymised.ethnicity_lookup";
            case "service_id":
                return "enterprise_data_pseudonymised.organization";
            case "organisation_id":
                return "enterprise_data_pseudonymised.organization";
            case "ccg":
                return "enterprise_data_pseudonymised.organization";
        }

        return "";
    }

    private static String getJoinColumnForDistinctValues(String column) {
        switch (column) {
            case "encounter_snomed_concept_id":
                return "ConceptId";
            case "patient_gender_id":
                return "id";
            case "lsoa_code":
                return "lsoa_code";
            case "msoa_code":
                return "msoa_code";
            case "ethnic_code":
                return "ethnic_code";
            case "service_id":
                return "id";
            case "organisation_id":
                return "id";
            case "ccg":
                return "ods_code";
        }

        return "";
    }

    private static String getLookupColumnForDistinctValues(String column) {
        switch (column) {
            case "encounter_snomed_concept_id":
                return "Definition";
            case "patient_gender_id":
                return "value";
            case "lsoa_code":
                return "lsoa_name";
            case "msoa_code":
                return "msoa_name";
            case "ethnic_code":
                return "ethnic_name";
            case "service_id":
                return "name";
            case "organisation_id":
                return "name";
            case "ccg":
                return "name";
        }

        return "";
    }

}
