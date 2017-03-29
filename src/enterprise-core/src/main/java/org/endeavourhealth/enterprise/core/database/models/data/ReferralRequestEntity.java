package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

/**
 * Created by darren on 23/03/17.
 */
@Entity
@javax.persistence.Table(name = "referral_request", schema = "enterprise_data_pseudonymised", catalog = "")
public class ReferralRequestEntity {
    private long id;

    @Id
    @javax.persistence.Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long organizationId;

    @Basic
    @javax.persistence.Column(name = "organization_id", nullable = false)
    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    private long patientId;

    @Basic
    @javax.persistence.Column(name = "patient_id", nullable = false)
    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    private long personId;

    @Basic
    @javax.persistence.Column(name = "person_id", nullable = false)
    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    private Long encounterId;

    @Basic
    @javax.persistence.Column(name = "encounter_id", nullable = true)
    public Long getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Long encounterId) {
        this.encounterId = encounterId;
    }

    private Long practitionerId;

    @Basic
    @javax.persistence.Column(name = "practitioner_id", nullable = true)
    public Long getPractitionerId() {
        return practitionerId;
    }

    public void setPractitionerId(Long practitionerId) {
        this.practitionerId = practitionerId;
    }

    private Date clinicalEffectiveDate;

    @Basic
    @javax.persistence.Column(name = "clinical_effective_date", nullable = true)
    public Date getClinicalEffectiveDate() {
        return clinicalEffectiveDate;
    }

    public void setClinicalEffectiveDate(Date clinicalEffectiveDate) {
        this.clinicalEffectiveDate = clinicalEffectiveDate;
    }

    private Short datePrecisionId;

    @Basic
    @javax.persistence.Column(name = "date_precision_id", nullable = true)
    public Short getDatePrecisionId() {
        return datePrecisionId;
    }

    public void setDatePrecisionId(Short datePrecisionId) {
        this.datePrecisionId = datePrecisionId;
    }

    private Long snomedConceptId;

    @Basic
    @javax.persistence.Column(name = "snomed_concept_id", nullable = true)
    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    private Long requesterOrganizationId;

    @Basic
    @javax.persistence.Column(name = "requester_organization_id", nullable = true)
    public Long getRequesterOrganizationId() {
        return requesterOrganizationId;
    }

    public void setRequesterOrganizationId(Long requesterOrganizationId) {
        this.requesterOrganizationId = requesterOrganizationId;
    }

    private Long recipientOrganizationId;

    @Basic
    @javax.persistence.Column(name = "recipient_organization_id", nullable = true)
    public Long getRecipientOrganizationId() {
        return recipientOrganizationId;
    }

    public void setRecipientOrganizationId(Long recipientOrganizationId) {
        this.recipientOrganizationId = recipientOrganizationId;
    }

    private Short priorityId;

    @Basic
    @javax.persistence.Column(name = "priority_id", nullable = true)
    public Short getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Short priorityId) {
        this.priorityId = priorityId;
    }

    private Short typeId;

    @Basic
    @javax.persistence.Column(name = "type_id", nullable = true)
    public Short getTypeId() {
        return typeId;
    }

    public void setTypeId(Short typeId) {
        this.typeId = typeId;
    }

    private String mode;

    @Basic
    @javax.persistence.Column(name = "mode", nullable = true, length = 50)
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    private Byte outgoingReferral;

    @Basic
    @javax.persistence.Column(name = "outgoing_referral", nullable = true)
    public Byte getOutgoingReferral() {
        return outgoingReferral;
    }

    public void setOutgoingReferral(Byte outgoingReferral) {
        this.outgoingReferral = outgoingReferral;
    }

    private String originalCode;

    @Basic
    @javax.persistence.Column(name = "original_code", nullable = true, length = 20)
    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    private String originalTerm;

    @Basic
    @javax.persistence.Column(name = "original_term", nullable = true, length = 1000)
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

        ReferralRequestEntity that = (ReferralRequestEntity) o;

        if (id != that.id) return false;
        if (organizationId != that.organizationId) return false;
        if (patientId != that.patientId) return false;
        if (personId != that.personId) return false;
        if (encounterId != null ? !encounterId.equals(that.encounterId) : that.encounterId != null) return false;
        if (practitionerId != null ? !practitionerId.equals(that.practitionerId) : that.practitionerId != null)
            return false;
        if (clinicalEffectiveDate != null ? !clinicalEffectiveDate.equals(that.clinicalEffectiveDate) : that.clinicalEffectiveDate != null)
            return false;
        if (datePrecisionId != null ? !datePrecisionId.equals(that.datePrecisionId) : that.datePrecisionId != null)
            return false;
        if (snomedConceptId != null ? !snomedConceptId.equals(that.snomedConceptId) : that.snomedConceptId != null)
            return false;
        if (requesterOrganizationId != null ? !requesterOrganizationId.equals(that.requesterOrganizationId) : that.requesterOrganizationId != null)
            return false;
        if (recipientOrganizationId != null ? !recipientOrganizationId.equals(that.recipientOrganizationId) : that.recipientOrganizationId != null)
            return false;
        if (priorityId != null ? !priorityId.equals(that.priorityId) : that.priorityId != null) return false;
        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;
        if (mode != null ? !mode.equals(that.mode) : that.mode != null) return false;
        if (outgoingReferral != null ? !outgoingReferral.equals(that.outgoingReferral) : that.outgoingReferral != null)
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
        result = 31 * result + (requesterOrganizationId != null ? requesterOrganizationId.hashCode() : 0);
        result = 31 * result + (recipientOrganizationId != null ? recipientOrganizationId.hashCode() : 0);
        result = 31 * result + (priorityId != null ? priorityId.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        result = 31 * result + (outgoingReferral != null ? outgoingReferral.hashCode() : 0);
        result = 31 * result + (originalCode != null ? originalCode.hashCode() : 0);
        result = 31 * result + (originalTerm != null ? originalTerm.hashCode() : 0);
        return result;
    }
}
