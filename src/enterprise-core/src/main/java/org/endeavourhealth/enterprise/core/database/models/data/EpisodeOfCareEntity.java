package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by darren on 23/03/17.
 */
@Entity
@Table(name = "episode_of_care", schema = "enterprise_data_pseudonymised", catalog = "")
public class EpisodeOfCareEntity {
    private long id;
    private long organizationId;
    private long patientId;
    private long personId;
    private Short registrationTypeId;
    private Date dateRegistered;
    private Date dateRegisteredEnd;
    private Long usualGpPractitionerId;

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
    @Column(name = "registration_type_id", nullable = true)
    public Short getRegistrationTypeId() {
        return registrationTypeId;
    }

    public void setRegistrationTypeId(Short registrationTypeId) {
        this.registrationTypeId = registrationTypeId;
    }

    @Basic
    @Column(name = "date_registered", nullable = true)
    public Date getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(Date dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    @Basic
    @Column(name = "date_registered_end", nullable = true)
    public Date getDateRegisteredEnd() {
        return dateRegisteredEnd;
    }

    public void setDateRegisteredEnd(Date dateRegisteredEnd) {
        this.dateRegisteredEnd = dateRegisteredEnd;
    }

    @Basic
    @Column(name = "usual_gp_practitioner_id", nullable = true)
    public Long getUsualGpPractitionerId() {
        return usualGpPractitionerId;
    }

    public void setUsualGpPractitionerId(Long usualGpPractitionerId) {
        this.usualGpPractitionerId = usualGpPractitionerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpisodeOfCareEntity that = (EpisodeOfCareEntity) o;

        if (id != that.id) return false;
        if (organizationId != that.organizationId) return false;
        if (patientId != that.patientId) return false;
        if (personId != that.personId) return false;
        if (registrationTypeId != null ? !registrationTypeId.equals(that.registrationTypeId) : that.registrationTypeId != null)
            return false;
        if (dateRegistered != null ? !dateRegistered.equals(that.dateRegistered) : that.dateRegistered != null)
            return false;
        if (dateRegisteredEnd != null ? !dateRegisteredEnd.equals(that.dateRegisteredEnd) : that.dateRegisteredEnd != null)
            return false;
        if (usualGpPractitionerId != null ? !usualGpPractitionerId.equals(that.usualGpPractitionerId) : that.usualGpPractitionerId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (organizationId ^ (organizationId >>> 32));
        result = 31 * result + (int) (patientId ^ (patientId >>> 32));
        result = 31 * result + (int) (personId ^ (personId >>> 32));
        result = 31 * result + (registrationTypeId != null ? registrationTypeId.hashCode() : 0);
        result = 31 * result + (dateRegistered != null ? dateRegistered.hashCode() : 0);
        result = 31 * result + (dateRegisteredEnd != null ? dateRegisteredEnd.hashCode() : 0);
        result = 31 * result + (usualGpPractitionerId != null ? usualGpPractitionerId.hashCode() : 0);
        return result;
    }
}
