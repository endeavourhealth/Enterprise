package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.enterprise.core.database.models.data.PatientEntity;
import org.endeavourhealth.enterprise.core.database.models.data.ReportPatientsEntity;
import org.endeavourhealth.enterprise.core.database.models.data.ReportResultEntity;
import org.endeavourhealth.enterprise.core.json.JsonOrganisation;
import org.endeavourhealth.enterprise.core.json.JsonReportRun;
import org.endeavourhealth.enterprise.core.querydocument.models.*;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ResultsManager {

    public static void saveReportPatients(ReportPatientsEntity result) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager2();

        entityManager.getTransaction().begin();

        entityManager.merge(result);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveReportResult(ReportResultEntity result) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager2();

        entityManager.getTransaction().begin();

        entityManager.merge(result);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static Timestamp convertToDate(String date) {
        Timestamp timeStamp = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);
            timeStamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e){

        }

        return timeStamp;

    }

    public static void runReport(LibraryItem libraryItem, JsonReportRun report, String userUuid) throws Exception {
        for (Rule rule: libraryItem.getQuery().getRule()) {
            Integer id = rule.getId();
            Integer type = rule.getType();
            RuleActionOperator onPassAction = rule.getOnPass().getAction();
            RuleActionOperator onFailAction = rule.getOnFail().getAction();
            if (onPassAction.equals(RuleActionOperator.GOTO_RULES)) {
                List<Integer> nextPassRuleIds = rule.getOnPass().getRuleId();
            }
            if (onFailAction.equals(RuleActionOperator.GOTO_RULES)) {
                List<Integer> nextFailRuleIds = rule.getOnFail().getRuleId();
            }
            List<Filter> filters = rule.getTest().getFilter();
            runReportRule(filters, report, userUuid);
        }
    }

    public static void runReportRule(List<Filter> filters, JsonReportRun report, String userUuid) throws Exception {

        String sqlWhere = "";
        String dataTable = "";
        String patientJoinField = "";

        for (Filter filter: filters) {
            String field = filter.getField();

            if (field.equals("CONCEPT")) {
                List<CodeSetValue> codeSetValues = filter.getCodeSet().getCodeSetValue();
                Integer c = 0;
                for (CodeSetValue codeSetValue : codeSetValues) {
                    c++;
                    String code = codeSetValue.getCode();
                    String term = codeSetValue.getTerm();
                    String parentType = codeSetValue.getParentType();
                    String baseType = codeSetValue.getBaseType();
                    String valueFrom = codeSetValue.getValueFrom();
                    String valueTo = codeSetValue.getValueTo();
                    Boolean includeChildren = codeSetValue.isIncludeChildren();

                    switch (baseType) {
                        case "Patient":
                            patientJoinField = "id";
                            dataTable = "PatientEntity";
                            if ((parentType.equals("Sex") && term.equals("Male")) ||
                                    (term.equals("Sex") && valueFrom.equals("Male"))) {
                                sqlWhere += " and d.patientGenderId = '0'";
                            } else if ((parentType.equals("Sex") && term.equals("Female")) ||
                                    (term.equals("Sex") && valueFrom.equals("Female"))) {
                                sqlWhere += " and d.patientGenderId = '1'";
                            } else if (term.equals("Post Code Prefix")) {
                                sqlWhere += " and d.postcodePrefix like '" + valueFrom + "%'";
                            } else if (term.equals("Age Years")) {
                                if (!valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.ageYears between '" + valueFrom + "' and '" + valueTo + "'";
                                else if (!valueFrom.equals("") && valueTo.equals(""))
                                    sqlWhere += " and d.ageYears >= '" + valueFrom + "'";
                                else if (valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.ageYears <= '" + valueTo + "'";

                            } else if (term.equals("Age Months")) {
                                if (!valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.ageMonths between '" + valueFrom + "' and '" + valueTo + "'";
                                else if (!valueFrom.equals("") && valueTo.equals(""))
                                    sqlWhere += " and d.ageMonths >= '" + valueFrom + "'";
                                else if (valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.ageMonths <= '" + valueTo + "'";
                            } else if (term.equals("Age Weeks")) {
                                if (!valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.ageWeeks between '" + valueFrom + "' and '" + valueTo + "'";
                                else if (!valueFrom.equals("") && valueTo.equals(""))
                                    sqlWhere += " and d.ageWeeks >= '" + valueFrom + "'";
                                else if (valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.ageWeeks <= '" + valueTo + "'";
                            } else if (term.equals("LSOA Code")) {
                                sqlWhere += " and d.lsoaCode like '" + valueFrom + "%'";
                            } else if (term.equals("MSOA Code")) {
                                sqlWhere += " and d.msoaCode like '" + valueFrom + "%'";
                            } else if (term.equals("Townsend Score")) {
                                if (!valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.townsendScore between '" + valueFrom + "' and '" + valueTo + "'";
                                else if (!valueFrom.equals("") && valueTo.equals(""))
                                    sqlWhere += " and d.townsendScore >= '" + valueFrom + "'";
                                else if (valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.townsendScore <= '" + valueTo + "'";
                            } else if (term.equals("Date of Death")) {
                                if (!valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.dateOfDeath between '" + valueFrom + "' and '" + valueTo + "'";
                                else if (!valueFrom.equals("") && valueTo.equals(""))
                                    sqlWhere += " and d.dateOfDeath >= '" + valueFrom + "'";
                                else if (valueFrom.equals("") && !valueTo.equals(""))
                                    sqlWhere += " and d.dateOfDeath <= '" + valueTo + "'";
                            }
                            break;
                        case "Observation":
                            patientJoinField = "patientId";
                            dataTable = "ObservationEntity";
                            String pref = " or";
                            if (c==1)
                                pref = "";

                            if (valueFrom.equals("") && valueTo.equals(""))
                                sqlWhere += pref+" d.snomedConceptId = '" + code + "'";
                            if (!valueFrom.equals("") && valueTo.equals(""))
                                sqlWhere += pref+" (d.snomedConceptId = '" + code + "' and d.value >= '"+valueFrom+"')";
                            if (valueFrom.equals("") && !valueTo.equals(""))
                                sqlWhere += pref+" (d.snomedConceptId = '" + code + "' and d.value <= '"+valueTo+"')";
                            if (!valueFrom.equals("") && !valueTo.equals(""))
                                sqlWhere += pref+" (d.snomedConceptId = '" + code + "' and d.value >= '"+valueFrom+"' and d.value <= '"+valueTo+"')";
                            break;
                        case "Medication Statement":
                            patientJoinField = "patientId";
                            dataTable = "MedicationStatementEntity";
                            sqlWhere += " or d.dmdId = '" + code + "'";
                            break;
                        case "Medication Order":
                            patientJoinField = "patientId";
                            dataTable = "MedicationOrderEntity";
                            sqlWhere += " or d.dmdId = '" + code + "'";
                            break;
                        case "Allergy":
                            patientJoinField = "patientId";
                            dataTable = "AllergyIntoleranceEntity";
                            sqlWhere += " or d.snomedConceptId = '" + code + "'";
                            break;
                        case "Referral":
                            patientJoinField = "patientId";
                            dataTable = "ReferralRequestEntity";
                            sqlWhere += " or d.snomedConceptId = '" + code + "'";
                            break;
                        case "Encounter":
                            patientJoinField = "patientId";
                            dataTable = "EncounterEntity";
                            sqlWhere += " or d.snomedConceptId = '" + code + "'";
                            break;
                    }
                }
                if (dataTable.equals("AllergyIntoleranceEntity")||
                        dataTable.equals("ReferralRequestEntity")||
                        dataTable.equals("EncounterEntity")) {
                    sqlWhere = sqlWhere.replaceFirst("or d.snomedConceptId","and (d.snomedConceptId");
                    sqlWhere += ")";
                } else if (dataTable.equals("ObservationEntity")) {
                    sqlWhere = "and ("+sqlWhere+")";
                } else if (dataTable.equals("MedicationStatementEntity")||
                        dataTable.equals("MedicationOrderEntity")||
                        dataTable.equals("ReferralRequestEntity")) {
                    sqlWhere = sqlWhere.replaceFirst("or d.dmdId","and (d.dmdId");
                    sqlWhere += ")";
                }
            } else if (field.equals("EFFECTIVE_DATE")) {
                if (filter.getValueFrom()!=null) {
                    String dateFrom = filter.getValueFrom().getConstant();
                    if (filter.getValueFrom().getRelativeUnit()!=null) {
                        String relativeUnit = filter.getValueFrom().getRelativeUnit().value();
                        Timestamp baselineDate = convertToDate(report.getBaselineDate());
                        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calDate = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
                        dateFormat.setCalendar(calDate);
                        calDate.setTime(baselineDate);

                        switch (relativeUnit) {
                            case "day":
                                calDate.add(Calendar.DATE, Integer.parseInt(dateFrom));
                                break;
                            case "week":
                                calDate.add(Calendar.DATE, Integer.parseInt(dateFrom)*7);
                                break;
                            case "month":
                                calDate.add(Calendar.MONTH, Integer.parseInt(dateFrom));
                                break;
                            case "year":
                                calDate.add(Calendar.YEAR, Integer.parseInt(dateFrom));
                                break;
                        }
                        dateFrom = dateFormat.format(calDate.getTime());
                    }
                    sqlWhere += " and d.clinicalEffectiveDate >= '"+dateFrom+"'";
                } else if (filter.getValueTo()!=null) {
                    String dateTo = filter.getValueTo().getConstant();
                    if (filter.getValueTo().getRelativeUnit()!=null) {
                        String relativeUnit = filter.getValueTo().getRelativeUnit().value();
                        Timestamp baselineDate = convertToDate(report.getBaselineDate());
                        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calDate = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
                        dateFormat.setCalendar(calDate);
                        calDate.setTime(baselineDate);

                        switch (relativeUnit) {
                            case "day":
                                calDate.add(Calendar.DATE, Integer.parseInt(dateTo));
                                break;
                            case "week":
                                calDate.add(Calendar.DATE, Integer.parseInt(dateTo)*7);
                                break;
                            case "month":
                                calDate.add(Calendar.MONTH, Integer.parseInt(dateTo));
                                break;
                            case "year":
                                calDate.add(Calendar.YEAR, Integer.parseInt(dateTo));
                                break;
                        }
                        dateTo = dateFormat.format(calDate.getTime());
                    }
                    sqlWhere += " and d.clinicalEffectiveDate <= '"+dateTo+"'";
                }
            } else if (field.equals("OBSERVATION_PROBLEM")) {
                sqlWhere += " and d.isProblem = '1'";
            } else if (field.equals("MEDICATION_STATUS")) {
                sqlWhere += " and d.isActive = '1'";
            } else if (field.equals("MEDICATION_TYPE")) {
                for (String value: filter.getValueSet().getValue()) {
                    if (value.equals("ACUTE"))
                        sqlWhere += " or d.medicationStatementAuthorisationTypeId = '0'";
                    else if (value.equals("REPEAT"))
                        sqlWhere += " or d.medicationStatementAuthorisationTypeId = '1'";
                    else if (value.equals("REPEAT_DISPENSING"))
                        sqlWhere += " or d.medicationStatementAuthorisationTypeId = '2'";
                    else if (value.equals("AUTOMATIC"))
                        sqlWhere += " or d.medicationStatementAuthorisationTypeId = '3'";
                }
                sqlWhere = sqlWhere.replaceFirst("or d.medicationStatementAuthorisationTypeId","and (d.medicationStatementAuthorisationTypeId");
                sqlWhere += ")";
            }

        }

        // Run the report for each Organisation specified

        List<JsonOrganisation> organisations = report.getOrganisation();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (JsonOrganisation org: organisations) {
            Timestamp baselineDate = convertToDate(report.getBaselineDate());

            ReportResultEntity reportResult = new ReportResultEntity();
            reportResult.setEndUserUuid(userUuid);
            reportResult.setBaselineDate(baselineDate);
            reportResult.setRunDate(now);
            reportResult.setOrganisationId(Short.parseShort(org.getId()));
            reportResult.setQueryItemUuid(report.getQueryItemUuid());
            reportResult.setPopulationTypeId(Byte.parseByte(report.getPopulation()));

            String patientWhere = "";

            if (report.getPopulation().equals("0")) { // currently registered
                patientWhere = "select distinct p " +
                        "from PatientEntity p JOIN EpisodeOfCareEntity e on e.patientId = p.id " +
                        "JOIN " + dataTable + " d on d." + patientJoinField + " = p.id " +
                        "where p.dateOfDeath IS NULL and p.organizationId = :organizationId " +
                        "and e.registrationTypeId = 2 " +
                        "and e.dateRegistered <= :baseline " +
                        "and (e.dateRegisteredEnd > :baseline or e.dateRegisteredEnd IS NULL) " +sqlWhere;
            }
            else if (report.getPopulation().equals("1")) { // all patients
                patientWhere = "select distinct p " +
                        "from PatientEntity p JOIN EpisodeOfCareEntity e on e.patientId = p.id " +
                        "JOIN " + dataTable + " d on d." + patientJoinField + " = p.id " +
                        "where p.organizationId = :organizationId " +
                        "and e.dateRegistered <= :baseline " +
                        "and (e.dateRegisteredEnd > :baseline or e.dateRegisteredEnd IS NULL) " +sqlWhere;
            }

            EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager2();

            List<PatientEntity> patients = entityManager.
                    createQuery(patientWhere, PatientEntity.class)
                    .setParameter("organizationId", Long.parseLong(org.getId()))
                    .setParameter("baseline", baselineDate)
                    .getResultList();

            for (PatientEntity patient: patients) {
                Long id = patient.getId();
                String pseudoId = patient.getPseudoId();

                ReportPatientsEntity reportPatientsEntity = new ReportPatientsEntity();
                reportPatientsEntity.setRunDate(now);
                reportPatientsEntity.setQueryItemUuid(report.getQueryItemUuid());
                reportPatientsEntity.setOrganisationId(Short.parseShort(org.getId()));
                reportPatientsEntity.setPatientId(id);
                reportPatientsEntity.setPseudoId(pseudoId);

                saveReportPatients(reportPatientsEntity);
            }

            reportResult.setEnumeratorCount(patients.size());

            // calculate denominator count

            String where = "";

            if (report.getPopulation().equals("0")) // currently registered
                where = "select count(1) " +
                        "from PatientEntity p JOIN EpisodeOfCareEntity e on e.patientId = p.id "+
                        "where p.dateOfDeath IS NULL and p.organizationId = :organizationId "+
                        "and e.registrationTypeId = 2 "+
                        "and e.dateRegistered <= :baseline "+
                        "and (e.dateRegisteredEnd > :baseline or e.dateRegisteredEnd IS NULL)";
            else if (report.getPopulation().equals("1")) // all patients
                where = "select count(1) " +
                        "from PatientEntity p JOIN EpisodeOfCareEntity e on e.patientId = p.id "+
                        "where p.organizationId = :organizationId "+
                        "and e.dateRegistered <= :baseline "+
                        "and (e.dateRegisteredEnd > :baseline or e.dateRegisteredEnd IS NULL)";

            Long denominatorCount = (Long)entityManager.createQuery(where)
                    .setParameter("organizationId", Long.parseLong(org.getId()))
                    .setParameter("baseline", baselineDate)
                    .getSingleResult();

            reportResult.setDenominatorCount(denominatorCount.intValue());

            saveReportResult(reportResult);

            entityManager.close();
        }

    }

}
