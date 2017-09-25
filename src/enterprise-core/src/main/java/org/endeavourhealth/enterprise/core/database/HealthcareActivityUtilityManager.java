package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.enterprise.core.json.JsonHealthcareActivity;
import org.endeavourhealth.enterprise.core.json.JsonLsoa;
import org.endeavourhealth.enterprise.core.json.JsonMsoa;
import org.endeavourhealth.enterprise.core.json.JsonPrevInc;

import java.util.ArrayList;
import java.util.List;

public class HealthcareActivityUtilityManager {

    private boolean includeOrganisationQuery = false;
    private String orgJoin = " join enterprise_admin.healthcare_activity_organisation_list o " +
            " on o.organisation_id = p.organization_id ";
    private String whereClauses = "";

    public boolean getDenominatorPopulation(JsonHealthcareActivity options) throws Exception {

        cleanUpDatabase();
        createTemporaryTables();
        generateWhereClausesFromOptions(options);
        populateOrganisationList(options);
        populatePatientList();
        populateRawData(options);
        return true;
    }

    private void cleanUpDatabase() throws Exception {

        includeOrganisationQuery = false;
        whereClauses = "";

        List<String> deleteScripts = new ArrayList<>();

        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_patient_list;");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_organisation_list;");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_raw_data;");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_date_range");
        deleteScripts.add("drop table if exists enterprise_admin.healthcare_activity_options");

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
                "     \n" +
                "    index ix_healthcare_activity_raw_data_patient_gender_id (patient_gender_id),    \n" +
                "    index ix_healthcare_activity_raw_data_postcode_prefix (postcode_prefix),\n" +
                "    index ix_healthcare_activity_raw_data_age_years (age_years),      \n" +
                "    index ix_healthcare_activity_raw_data_person_id (patient_id) ,      \n" +
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

    private void populateOrganisationList(JsonHealthcareActivity options) throws Exception {

        List<String> organisationScripts = new ArrayList<>();

        organisationScripts.add(String.format("insert into enterprise_admin.healthcare_activity_organisation_list  \n" +
                "select coalesce(child.id, o.id) \n" +
                "from enterprise_admin.incidence_prevalence_organisation_group_lookup l\n" +
                "join enterprise_data_pseudonymised.organization o on o.ods_code = l.ods_code\n" +
                "left outer join enterprise_data_pseudonymised.organization child \n" +
                "\ton child.parent_organization_id = o.id and child.type_code = 'PR' \n" +
                "where l.group_id = %d;", options.getOrganisationGroup()));


        for (String script : organisationScripts) {
            UtilityManagerCommon.runScript(script);
        }
    }

    private void populatePatientList() throws Exception {

        List<String> patientScripts = new ArrayList<>();

        patientScripts.add("insert into enterprise_admin.healthcare_activity_patient_list (patient_id)\n" +
                "select \n" +
                "\tid \n" +
                "from enterprise_data_pseudonymised.patient p \n" +
                "join enterprise_admin.healthcare_activity_organisation_list o  on o.organisation_id = p.organization_id;");


        for (String script : patientScripts) {
            UtilityManagerCommon.runScript(script);
        }
    }

    private void populateRawData(JsonHealthcareActivity options) throws Exception {

        List<String> organisationScripts = new ArrayList<>();

        organisationScripts.add(String.format("insert into enterprise_admin.healthcare_activity_raw_data \n" +
                "(\tpatient_id,\n" +
                "    patient_gender_id,\n" +
                "    age_years,\n" +
                "    postcode_prefix,\n" +
                "    lsoa_code,\n" +
                "    msoa_code,\n" +
                "    ethnic_code,\n" +
                "    organisation_id,\n" +
                "    ccg,\n" +
                "    encounter_snomed_concept_id)\n" +
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
                "    e.snomed_concept_id    \n" +
                "from enterprise_admin.healthcare_activity_patient_list pl \n" +
                "inner join enterprise_data_pseudonymised.patient p on p.id = pl.patient_id\n" +
                "inner join enterprise_data_pseudonymised.encounter e on e.patient_id = p.id\n" +
                "inner JOIN enterprise_data_pseudonymised.organization org on org.id = p.organization_id \n" +
                "inner JOIN enterprise_data_pseudonymised.organization parentOrg on parentOrg.id = org.parent_organization_id \n" +
                " join enterprise_admin.healthcare_activity_organisation_list o  on o.organisation_id = p.organization_id\n" +
                " where e.clinical_effective_date > date_add(current_date, INTERVAL -%s %s);",
                options.getTimePeriodNo(), options.getTimePeriod().replaceFirst("S", "")));


        for (String script : organisationScripts) {
            UtilityManagerCommon.runScript(script);
        }
    }

    private void generateWhereClausesFromOptions(JsonHealthcareActivity options) throws Exception {

        List<String> whereClauseList = new ArrayList<>();


        if (options.getLsoaCode() != null && options.getLsoaCode().size() > 0) {
            String lsoaCodes = "";
            for (JsonLsoa lsoa : options.getLsoaCode()) {
                lsoaCodes += "'" + lsoa.getLsoaCode() + "',";
            }
            lsoaCodes =  lsoaCodes.substring(0, lsoaCodes.length() - 1);
            whereClauseList.add(" p.lsoa_code in (" + lsoaCodes + ")");
        }

        if (options.getMsoaCode() != null && options.getMsoaCode().size() > 0) {
            String msoaCodes = "";
            for (JsonMsoa msoa : options.getMsoaCode()) {
                msoaCodes += "'" + msoa.getMsoaCode() + "',";
            }
            msoaCodes =  msoaCodes.substring(0, msoaCodes.length() - 1);
            whereClauseList.add(" p.msoa_code in (" + msoaCodes + ")");
        }

        if (options.getEthnicity() != null && options.getEthnicity().size() > 0) {
            String ethnicityCodes = "";
            for (String ethnicity : options.getEthnicity()) {
                ethnicityCodes += "'" + ethnicity + "',";
            }
            ethnicityCodes =  ethnicityCodes.substring(0, ethnicityCodes.length() - 1);
            whereClauseList.add(" p.ethnic_code in (" + ethnicityCodes + ")");
        }

        if (options.getPostCodePrefix() != null && !options.getPostCodePrefix().equals("")) {

            whereClauseList.add(" p.postcode_prefix = '" + options.getPostCodePrefix() + "'");
        }

        if (options.getAgeFrom() != null && !options.getAgeFrom().equals("")) {
            whereClauseList.add(" p.age_years >= " + options.getAgeFrom());
        }

        if (options.getAgeTo() != null && !options.getAgeTo().equals("")) {
            whereClauseList.add(" p.age_years <= " + options.getAgeTo());
        }

        if (options.getSex() != null && !options.getSex().equals("-1")) {
            whereClauseList.add(" p.patient_gender_id = " + options.getSex());
        }

        if (options.getServiceGroupId() != null && !options.getServiceGroupId().equals("")) {
            //Add service group in there
        }

        String allWhereClauses = "";
        String prefix = " where ";
        for (String where : whereClauseList) {
            allWhereClauses += prefix + where;
            prefix = " and ";
        }

        whereClauses = allWhereClauses;
    }
}
