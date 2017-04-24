package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by darren on 23/03/17.
 */
@Entity
@Table(name = "patient", schema = "enterprise_data_pseudonymised", catalog = "")
@IdClass(PatientEntityPK.class)
public class PatientEntity {
    private long id;
    private long organizationId;
    private long personId;
    private short patientGenderId;
    private String pseudoId;
    private Integer ageYears;
    private Integer ageMonths;
    private Integer ageWeeks;
    private Date dateOfDeath;
    private String postcodePrefix;
    private Long householdId;
    private String lsoaCode;
    private String msoaCode;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Id
    @Column(name = "organization_id", nullable = false)
    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
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
    @Column(name = "patient_gender_id", nullable = false)
    public short getPatientGenderId() {
        return patientGenderId;
    }

    public void setPatientGenderId(short patientGenderId) {
        this.patientGenderId = patientGenderId;
    }

    @Basic
    @Column(name = "pseudo_id", nullable = true, length = 255)
    public String getPseudoId() {
        return pseudoId;
    }

    public void setPseudoId(String pseudoId) {
        this.pseudoId = pseudoId;
    }

    @Basic
    @Column(name = "age_years", nullable = true)
    public Integer getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(Integer ageYears) {
        this.ageYears = ageYears;
    }

    @Basic
    @Column(name = "age_months", nullable = true)
    public Integer getAgeMonths() {
        return ageMonths;
    }

    public void setAgeMonths(Integer ageMonths) {
        this.ageMonths = ageMonths;
    }

    @Basic
    @Column(name = "age_weeks", nullable = true)
    public Integer getAgeWeeks() {
        return ageWeeks;
    }

    public void setAgeWeeks(Integer ageWeeks) {
        this.ageWeeks = ageWeeks;
    }

    @Basic
    @Column(name = "date_of_death", nullable = true)
    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    @Basic
    @Column(name = "postcode_prefix", nullable = true, length = 20)
    public String getPostcodePrefix() {
        return postcodePrefix;
    }

    public void setPostcodePrefix(String postcodePrefix) {
        this.postcodePrefix = postcodePrefix;
    }

    @Basic
    @Column(name = "household_id", nullable = true)
    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    @Basic
    @Column(name = "lsoa_code", nullable = true, length = 50)
    public String getLsoaCode() {
        return lsoaCode;
    }

    public void setLsoaCode(String lsoaCode) {
        this.lsoaCode = lsoaCode;
    }

    @Basic
    @Column(name = "msoa_code", nullable = true, length = 50)
    public String getMsoaCode() {
        return msoaCode;
    }

    public void setMsoaCode(String msoaCode) {
        this.msoaCode = msoaCode;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientEntity that = (PatientEntity) o;

        if (id != that.id) return false;
        if (organizationId != that.organizationId) return false;
        if (personId != that.personId) return false;
        if (patientGenderId != that.patientGenderId) return false;
        if (pseudoId != null ? !pseudoId.equals(that.pseudoId) : that.pseudoId != null) return false;
        if (ageYears != null ? !ageYears.equals(that.ageYears) : that.ageYears != null) return false;
        if (ageMonths != null ? !ageMonths.equals(that.ageMonths) : that.ageMonths != null) return false;
        if (ageWeeks != null ? !ageWeeks.equals(that.ageWeeks) : that.ageWeeks != null) return false;
        if (dateOfDeath != null ? !dateOfDeath.equals(that.dateOfDeath) : that.dateOfDeath != null) return false;
        if (postcodePrefix != null ? !postcodePrefix.equals(that.postcodePrefix) : that.postcodePrefix != null)
            return false;
        if (householdId != null ? !householdId.equals(that.householdId) : that.householdId != null) return false;
        if (lsoaCode != null ? !lsoaCode.equals(that.lsoaCode) : that.lsoaCode != null) return false;
        if (msoaCode != null ? !msoaCode.equals(that.msoaCode) : that.msoaCode != null) return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (organizationId ^ (organizationId >>> 32));
        result = 31 * result + (int) (personId ^ (personId >>> 32));
        result = 31 * result + (int) patientGenderId;
        result = 31 * result + (pseudoId != null ? pseudoId.hashCode() : 0);
        result = 31 * result + (ageYears != null ? ageYears.hashCode() : 0);
        result = 31 * result + (ageMonths != null ? ageMonths.hashCode() : 0);
        result = 31 * result + (ageWeeks != null ? ageWeeks.hashCode() : 0);
        result = 31 * result + (dateOfDeath != null ? dateOfDeath.hashCode() : 0);
        result = 31 * result + (postcodePrefix != null ? postcodePrefix.hashCode() : 0);
        result = 31 * result + (householdId != null ? householdId.hashCode() : 0);
        result = 31 * result + (lsoaCode != null ? lsoaCode.hashCode() : 0);
        result = 31 * result + (msoaCode != null ? msoaCode.hashCode() : 0);

        return result;
    }
}
