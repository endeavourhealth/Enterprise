package org.endeavourhealth.enterprise.core.database;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Calendar;
import java.util.List;

public final class UtilityManagerCommon {

    public static void runScript(String script) throws Exception {
        runScript(script, null);
    }

    public static void runScript(String script, List<Object> params) {
        System.out.println(script);
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        entityManager.getTransaction().begin();
        try {
            Query q = entityManager.createNativeQuery(script);

            if (params != null && !params.isEmpty())
                setQueryParams(q, params);

            int resultCount = q.executeUpdate();

            System.out.println(resultCount + " rows affected");
        } finally {
            entityManager.getTransaction().commit();

            entityManager.close();
        }
    }

    public static void setQueryParams(Query q, List<Object> params) {
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i) instanceof Calendar)
                q.setParameter(i + 1, (Calendar) params.get(i), TemporalType.TIMESTAMP);
            else
                q.setParameter(i + 1, params.get(i));
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
            "join organization o on o.ods_code = l.ods_code\n" +
            "left outer join organization child on child.parent_organization_id = o.id and child.type_code = 'PR'\n" +
            "where l.group_id = %d;", tableName, organisationGroup);


        return query;
    }

    public static List getDistinctValuesForGraphing(String columnName, String rawDataTableName) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();
        List resultList;
        try {
            if (columnName.equals("postcode_prefix") || columnName.equals("encounter_type")) {
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
            case "patient_gender_id":
                return "patient_gender";
            case "lsoa_code":
                return "lsoa_lookup";
            case "msoa_code":
                return "msoa_lookup";
            case "ethnic_code":
                return "ethnicity_lookup";
            case "service_id":
                return "organization";
            case "organisation_id":
                return "organization";
            case "ccg":
                return "organization";
        }

        return "";
    }

    private static String getJoinColumnForDistinctValues(String column) {
        switch (column) {
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
