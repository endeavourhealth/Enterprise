package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by darren on 23/03/17.
 */
@Entity
@Table(name = "medication_statement", schema = "enterprise_data_pseudonymised", catalog = "")
public class MedicationStatementEntity {
    private long id;
    private long organizationId;
    private long patientId;
    private long personId;
    private Long encounterId;
    private Long practitionerId;
    private Date clinicalEffectiveDate;
    private Short datePrecisionId;
    private Long dmdId;
    private byte isActive;
    private Date cancellationDate;
    private String dose;
    private Double quantityValue;
    private String quantityUnit;
    private short medicationStatementAuthorisationTypeId;
    private String originalTerm;

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
    @Column(name = "dmd_id", nullable = true)
    public Long getDmdId() {
        return dmdId;
    }

    public void setDmdId(Long dmdId) {
        this.dmdId = dmdId;
    }

    @Basic
    @Column(name = "is_active", nullable = false)
    public byte getIsActive() {
        return isActive;
    }

    public void setIsActive(byte isActive) {
        this.isActive = isActive;
    }

    @Basic
    @Column(name = "cancellation_date", nullable = true)
    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Date cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    @Basic
    @Column(name = "dose", nullable = true, length = 1000)
    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    @Basic
    @Column(name = "quantity_value", nullable = true, precision = 0)
    public Double getQuantityValue() {
        return quantityValue;
    }

    public void setQuantityValue(Double quantityValue) {
        this.quantityValue = quantityValue;
    }

    @Basic
    @Column(name = "quantity_unit", nullable = true, length = 255)
    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    @Basic
    @Column(name = "medication_statement_authorisation_type_id", nullable = false)
    public short getMedicationStatementAuthorisationTypeId() {
        return medicationStatementAuthorisationTypeId;
    }

    public void setMedicationStatementAuthorisationTypeId(short medicationStatementAuthorisationTypeId) {
        this.medicationStatementAuthorisationTypeId = medicationStatementAuthorisationTypeId;
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

        MedicationStatementEntity that = (MedicationStatementEntity) o;

        if (id != that.id) return false;
        if (organizationId != that.organizationId) return false;
        if (patientId != that.patientId) return false;
        if (personId != that.personId) return false;
        if (isActive != that.isActive) return false;
        if (medicationStatementAuthorisationTypeId != that.medicationStatementAuthorisationTypeId) return false;
        if (encounterId != null ? !encounterId.equals(that.encounterId) : that.encounterId != null) return false;
        if (practitionerId != null ? !practitionerId.equals(that.practitionerId) : that.practitionerId != null)
            return false;
        if (clinicalEffectiveDate != null ? !clinicalEffectiveDate.equals(that.clinicalEffectiveDate) : that.clinicalEffectiveDate != null)
            return false;
        if (datePrecisionId != null ? !datePrecisionId.equals(that.datePrecisionId) : that.datePrecisionId != null)
            return false;
        if (dmdId != null ? !dmdId.equals(that.dmdId) : that.dmdId != null) return false;
        if (cancellationDate != null ? !cancellationDate.equals(that.cancellationDate) : that.cancellationDate != null)
            return false;
        if (dose != null ? !dose.equals(that.dose) : that.dose != null) return false;
        if (quantityValue != null ? !quantityValue.equals(that.quantityValue) : that.quantityValue != null)
            return false;
        if (quantityUnit != null ? !quantityUnit.equals(that.quantityUnit) : that.quantityUnit != null) return false;
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
        result = 31 * result + (dmdId != null ? dmdId.hashCode() : 0);
        result = 31 * result + (int) isActive;
        result = 31 * result + (cancellationDate != null ? cancellationDate.hashCode() : 0);
        result = 31 * result + (dose != null ? dose.hashCode() : 0);
        result = 31 * result + (quantityValue != null ? quantityValue.hashCode() : 0);
        result = 31 * result + (quantityUnit != null ? quantityUnit.hashCode() : 0);
        result = 31 * result + (int) medicationStatementAuthorisationTypeId;
        result = 31 * result + (originalTerm != null ? originalTerm.hashCode() : 0);
        return result;
    }
}
