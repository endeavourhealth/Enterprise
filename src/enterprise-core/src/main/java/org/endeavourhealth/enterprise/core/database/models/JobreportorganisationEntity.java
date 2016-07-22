package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "jobreportorganisation", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
@IdClass(JobreportorganisationEntityPK.class)
public class JobreportorganisationEntity {
    private UUID jobreportuuid;
    private String organisationodscode;
    private Integer populationcount;

    @Id
    @Column(name = "jobreportuuid")
    public UUID getJobreportuuid() {
        return jobreportuuid;
    }

    public void setJobreportuuid(UUID jobreportuuid) {
        this.jobreportuuid = jobreportuuid;
    }

    @Id
    @Column(name = "organisationodscode")
    public String getOrganisationodscode() {
        return organisationodscode;
    }

    public void setOrganisationodscode(String organisationodscode) {
        this.organisationodscode = organisationodscode;
    }

    @Basic
    @Column(name = "populationcount")
    public Integer getPopulationcount() {
        return populationcount;
    }

    public void setPopulationcount(Integer populationcount) {
        this.populationcount = populationcount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobreportorganisationEntity that = (JobreportorganisationEntity) o;

        if (jobreportuuid != null ? !jobreportuuid.equals(that.jobreportuuid) : that.jobreportuuid != null)
            return false;
        if (organisationodscode != null ? !organisationodscode.equals(that.organisationodscode) : that.organisationodscode != null)
            return false;
        if (populationcount != null ? !populationcount.equals(that.populationcount) : that.populationcount != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobreportuuid != null ? jobreportuuid.hashCode() : 0;
        result = 31 * result + (organisationodscode != null ? organisationodscode.hashCode() : 0);
        result = 31 * result + (populationcount != null ? populationcount.hashCode() : 0);
        return result;
    }

    public static JobreportorganisationEntity retrieveForJobReportAndOdsCode(JobreportEntity jobReport, String odsCode) throws Exception {
        return retrieveForJobReportAndOdsCode(jobReport.getJobreportuuid(), odsCode);
    }
    public static JobreportorganisationEntity retrieveForJobReportAndOdsCode(UUID jobreportuuid, String organisationodscode) throws Exception {
        String where = "FROM JobreportorganisationEntity WHERE jobreportuuid = :jobreportuuid"
                + " AND organisationodscode = :organisationodscode";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        JobreportorganisationEntity ret = entityManager.createQuery(where, JobreportorganisationEntity.class)
                .setParameter("jobreportuuid", jobreportuuid)
                .setParameter("organisationodscode", organisationodscode)
                .getSingleResult();

        entityManager.close();

        return ret;

    }
}
