package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by darren on 08/09/2017.
 */
@Entity
@Table(name = "organisation", schema = "data_sharing_manager", catalog = "")
public class OrganisationEntity {
    private String uuid;
    private String name;
    private String alternativeName;
    private String odsCode;
    private String icoCode;
    private String igToolkitStatus;
    private Date dateOfRegistration;
    private String registrationPerson;
    private String evidenceOfRegistration;
    private byte isService;
    private byte type;
    private byte active;
    private byte bulkImported;
    private byte bulkItemUpdated;
    private String bulkConflictedWith;

    @Id
    @Column(name = "uuid", nullable = false, length = 36)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "alternative_name", nullable = true, length = 100)
    public String getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    @Basic
    @Column(name = "ods_code", nullable = true, length = 10)
    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    @Basic
    @Column(name = "ico_code", nullable = true, length = 10)
    public String getIcoCode() {
        return icoCode;
    }

    public void setIcoCode(String icoCode) {
        this.icoCode = icoCode;
    }

    @Basic
    @Column(name = "ig_toolkit_status", nullable = true, length = 10)
    public String getIgToolkitStatus() {
        return igToolkitStatus;
    }

    public void setIgToolkitStatus(String igToolkitStatus) {
        this.igToolkitStatus = igToolkitStatus;
    }

    @Basic
    @Column(name = "date_of_registration", nullable = true)
    public Date getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(Date dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    @Basic
    @Column(name = "registration_person", nullable = true, length = 36)
    public String getRegistrationPerson() {
        return registrationPerson;
    }

    public void setRegistrationPerson(String registrationPerson) {
        this.registrationPerson = registrationPerson;
    }

    @Basic
    @Column(name = "evidence_of_registration", nullable = true, length = 500)
    public String getEvidenceOfRegistration() {
        return evidenceOfRegistration;
    }

    public void setEvidenceOfRegistration(String evidenceOfRegistration) {
        this.evidenceOfRegistration = evidenceOfRegistration;
    }

    @Basic
    @Column(name = "is_service", nullable = false)
    public byte getIsService() {
        return isService;
    }

    public void setIsService(byte isService) {
        this.isService = isService;
    }

    @Basic
    @Column(name = "type", nullable = false)
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Basic
    @Column(name = "active", nullable = false)
    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    @Basic
    @Column(name = "bulk_imported", nullable = false)
    public byte getBulkImported() {
        return bulkImported;
    }

    public void setBulkImported(byte bulkImported) {
        this.bulkImported = bulkImported;
    }

    @Basic
    @Column(name = "bulk_item_updated", nullable = false)
    public byte getBulkItemUpdated() {
        return bulkItemUpdated;
    }

    public void setBulkItemUpdated(byte bulkItemUpdated) {
        this.bulkItemUpdated = bulkItemUpdated;
    }

    @Basic
    @Column(name = "bulk_conflicted_with", nullable = true, length = 36)
    public String getBulkConflictedWith() {
        return bulkConflictedWith;
    }

    public void setBulkConflictedWith(String bulkConflictedWith) {
        this.bulkConflictedWith = bulkConflictedWith;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganisationEntity that = (OrganisationEntity) o;

        if (isService != that.isService) return false;
        if (type != that.type) return false;
        if (active != that.active) return false;
        if (bulkImported != that.bulkImported) return false;
        if (bulkItemUpdated != that.bulkItemUpdated) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (alternativeName != null ? !alternativeName.equals(that.alternativeName) : that.alternativeName != null)
            return false;
        if (odsCode != null ? !odsCode.equals(that.odsCode) : that.odsCode != null) return false;
        if (icoCode != null ? !icoCode.equals(that.icoCode) : that.icoCode != null) return false;
        if (igToolkitStatus != null ? !igToolkitStatus.equals(that.igToolkitStatus) : that.igToolkitStatus != null)
            return false;
        if (dateOfRegistration != null ? !dateOfRegistration.equals(that.dateOfRegistration) : that.dateOfRegistration != null)
            return false;
        if (registrationPerson != null ? !registrationPerson.equals(that.registrationPerson) : that.registrationPerson != null)
            return false;
        if (evidenceOfRegistration != null ? !evidenceOfRegistration.equals(that.evidenceOfRegistration) : that.evidenceOfRegistration != null)
            return false;
        if (bulkConflictedWith != null ? !bulkConflictedWith.equals(that.bulkConflictedWith) : that.bulkConflictedWith != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (alternativeName != null ? alternativeName.hashCode() : 0);
        result = 31 * result + (odsCode != null ? odsCode.hashCode() : 0);
        result = 31 * result + (icoCode != null ? icoCode.hashCode() : 0);
        result = 31 * result + (igToolkitStatus != null ? igToolkitStatus.hashCode() : 0);
        result = 31 * result + (dateOfRegistration != null ? dateOfRegistration.hashCode() : 0);
        result = 31 * result + (registrationPerson != null ? registrationPerson.hashCode() : 0);
        result = 31 * result + (evidenceOfRegistration != null ? evidenceOfRegistration.hashCode() : 0);
        result = 31 * result + (int) isService;
        result = 31 * result + (int) type;
        result = 31 * result + (int) active;
        result = 31 * result + (int) bulkImported;
        result = 31 * result + (int) bulkItemUpdated;
        result = 31 * result + (bulkConflictedWith != null ? bulkConflictedWith.hashCode() : 0);
        return result;
    }
}
