package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonReportRow {

    private String patientId = null;
    private String organisationId = null;
    private String label = null;
    private String clinicalEffectiveDate = null;
    private String originalTerm = null;
    private String originalCode = null;
    private String snomedConceptId = null;
    private String value = null;
    private String units = null;
    private String patientGenderId = null;
    private String ageYears = null;
    private String ageMonths = null;
    private String ageWeeks = null;
    private String dateOfDeath = null;
    private String postcodePrefix = null;

    public JsonReportRow() {
    }

    /**
     * gets/sets
     */
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getClinicalEffectiveDate() {
        return clinicalEffectiveDate;
    }

    public void setClinicalEffectiveDate(String clinicalEffectiveDate) {
        this.clinicalEffectiveDate = clinicalEffectiveDate;
    }

    public String getOriginalTerm() {
        return originalTerm;
    }

    public void setOriginalTerm(String originalTerm) {
        this.originalTerm = originalTerm;
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    public String getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(String snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getPatientGenderId() {
        return patientGenderId;
    }

    public void setPatientGenderId(String patientGenderId) {
        this.patientGenderId = patientGenderId;
    }

    public String getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(String ageYears) {
        this.ageYears = ageYears;
    }

    public String getAgeMonths() {
        return ageMonths;
    }

    public void setAgeMonths(String ageMonths) {
        this.ageMonths = ageMonths;
    }

    public String getAgeWeeks() {
        return ageWeeks;
    }

    public void setAgeWeeks(String ageWeeks) {
        this.ageWeeks = ageWeeks;
    }

    public String getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(String dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getPostcodePrefix() {
        return postcodePrefix;
    }

    public void setPostcodePrefix(String postcodePrefix) {
        this.postcodePrefix = postcodePrefix;
    }


}
