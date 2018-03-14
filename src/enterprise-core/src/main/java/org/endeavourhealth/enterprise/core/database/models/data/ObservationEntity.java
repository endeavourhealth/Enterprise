package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by darren on 23/03/17.
 */
@Entity
@Table(name = "observation", schema = "enterprise_data_pseudonymised", catalog = "")
public class ObservationEntity {
    private long id;
    private long organizationId;
    private long patientId;
    private long personId;
    private Long encounterId;
    private Long practitionerId;
    private Date clinicalEffectiveDate;
    private Short datePrecisionId;
    private Long snomedConceptId;
    private String originalCode;
    private byte isProblem;
    private String originalTerm;
    private Byte isReview;
    private Double resultValue;
    private String resultValueUnits;
    private Date resultDate;
    private String resultText;
    private Long resultConceptId;
    private Date problemEndDate;
    private Long parentObservationId;

    public void setIsReview(byte isReview) {
        this.isReview = isReview;
    }

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "organization_id", nullable = false)
    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    @Basic
    @Column(name = "patient_id", nullable = false)
    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    @Basic
    @Column(name = "person_id", nullable = false)
    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    @Basic
    @Column(name = "encounter_id", nullable = true)
    public Long getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Long encounterId) {
        this.encounterId = encounterId;
    }

    @Basic
    @Column(name = "practitioner_id", nullable = true)
    public Long getPractitionerId() {
        return practitionerId;
    }

    public void setPractitionerId(Long practitionerId) {
        this.practitionerId = practitionerId;
    }

    @Basic
    @Column(name = "clinical_effective_date", nullable = true)
    public Date getClinicalEffectiveDate() {
        return clinicalEffectiveDate;
    }

    public void setClinicalEffectiveDate(Date clinicalEffectiveDate) {
        this.clinicalEffectiveDate = clinicalEffectiveDate;
    }

    @Basic
    @Column(name = "date_precision_id", nullable = true)
    public Short getDatePrecisionId() {
        return datePrecisionId;
    }

    public void setDatePrecisionId(Short datePrecisionId) {
        this.datePrecisionId = datePrecisionId;
    }

    @Basic
    @Column(name = "snomed_concept_id", nullable = true)
    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    @Basic
    @Column(name = "original_code", nullable = true, length = 20)
    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    @Basic
    @Column(name = "is_problem", nullable = false)
    public byte getIsProblem() {
        return isProblem;
    }

    public void setIsProblem(byte isProblem) {
        this.isProblem = isProblem;
    }

    @Basic
    @Column(name = "original_term", nullable = true, length = 1000)
    public String getOriginalTerm() {
        return originalTerm;
    }

    public void setOriginalTerm(String originalTerm) {
        this.originalTerm = originalTerm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObservationEntity that = (ObservationEntity) o;

        if (id != that.id) return false;
        if (organizationId != that.organizationId) return false;
        if (patientId != that.patientId) return false;
        if (personId != that.personId) return false;
        if (isProblem != that.isProblem) return false;
        if (encounterId != null ? !encounterId.equals(that.encounterId) : that.encounterId != null) return false;
        if (practitionerId != null ? !practitionerId.equals(that.practitionerId) : that.practitionerId != null)
            return false;
        if (clinicalEffectiveDate != null ? !clinicalEffectiveDate.equals(that.clinicalEffectiveDate) : that.clinicalEffectiveDate != null)
            return false;
        if (datePrecisionId != null ? !datePrecisionId.equals(that.datePrecisionId) : that.datePrecisionId != null)
            return false;
        if (snomedConceptId != null ? !snomedConceptId.equals(that.snomedConceptId) : that.snomedConceptId != null)
            return false;
        if (originalCode != null ? !originalCode.equals(that.originalCode) : that.originalCode != null) return false;
        if (originalTerm != null ? !originalTerm.equals(that.originalTerm) : that.originalTerm != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (organizationId ^ (organizationId >>> 32));
        result = 31 * result + (int) (patientId ^ (patientId >>> 32));
        result = 31 * result + (int) (personId ^ (personId >>> 32));
        result = 31 * result + (encounterId != null ? encounterId.hashCode() : 0);
        result = 31 * result + (practitionerId != null ? practitionerId.hashCode() : 0);
        result = 31 * result + (clinicalEffectiveDate != null ? clinicalEffectiveDate.hashCode() : 0);
        result = 31 * result + (datePrecisionId != null ? datePrecisionId.hashCode() : 0);
        result = 31 * result + (snomedConceptId != null ? snomedConceptId.hashCode() : 0);
        result = 31 * result + (originalCode != null ? originalCode.hashCode() : 0);
        result = 31 * result + (int) isProblem;
        result = 31 * result + (originalTerm != null ? originalTerm.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "is_review", nullable = false)
    public Byte getIsReview() {
        return isReview;
    }

    public void setIsReview(Byte isReview) {
        this.isReview = isReview;
    }

    @Basic
    @Column(name = "result_value", nullable = true, precision = 0)
    public Double getResultValue() {
        return resultValue;
    }

    public void setResultValue(Double resultValue) {
        this.resultValue = resultValue;
    }

    @Basic
    @Column(name = "result_value_units", nullable = true, length = 50)
    public String getResultValueUnits() {
        return resultValueUnits;
    }

    public void setResultValueUnits(String resultValueUnits) {
        this.resultValueUnits = resultValueUnits;
    }

    @Basic
    @Column(name = "result_date", nullable = true)
    public Date getResultDate() {
        return resultDate;
    }

    public void setResultDate(Date resultDate) {
        this.resultDate = resultDate;
    }

    @Basic
    @Column(name = "result_text", nullable = true, length = -1)
    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    @Basic
    @Column(name = "result_concept_id", nullable = true)
    public Long getResultConceptId() {
        return resultConceptId;
    }

    public void setResultConceptId(Long resultConceptId) {
        this.resultConceptId = resultConceptId;
    }

    @Basic
    @Column(name = "problem_end_date", nullable = true)
    public Date getProblemEndDate() {
        return problemEndDate;
    }

    public void setProblemEndDate(Date problemEndDate) {
        this.problemEndDate = problemEndDate;
    }

    @Basic
    @Column(name = "parent_observation_id", nullable = true)
    public Long getParentObservationId() {
        return parentObservationId;
    }

    public void setParentObservationId(Long parentObservationId) {
        this.parentObservationId = parentObservationId;
    }
}
