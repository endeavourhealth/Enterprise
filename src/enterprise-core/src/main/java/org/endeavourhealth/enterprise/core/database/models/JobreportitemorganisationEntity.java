package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "jobreportitemorganisation", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
@IdClass(JobreportitemorganisationEntityPK.class)
public class JobreportitemorganisationEntity {
    private UUID jobreportitemuuid;
    private String organisationodscode;
    private Integer resultcount;

    @Id
    @Column(name = "jobreportitemuuid")
    public UUID getJobreportitemuuid() {
        return jobreportitemuuid;
    }

    public void setJobreportitemuuid(UUID jobreportitemuuid) {
        this.jobreportitemuuid = jobreportitemuuid;
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
    @Column(name = "resultcount")
    public Integer getResultcount() {
        return resultcount;
    }

    public void setResultcount(Integer resultcount) {
        this.resultcount = resultcount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobreportitemorganisationEntity that = (JobreportitemorganisationEntity) o;

        if (jobreportitemuuid != null ? !jobreportitemuuid.equals(that.jobreportitemuuid) : that.jobreportitemuuid != null)
            return false;
        if (organisationodscode != null ? !organisationodscode.equals(that.organisationodscode) : that.organisationodscode != null)
            return false;
        if (resultcount != null ? !resultcount.equals(that.resultcount) : that.resultcount != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobreportitemuuid != null ? jobreportitemuuid.hashCode() : 0;
        result = 31 * result + (organisationodscode != null ? organisationodscode.hashCode() : 0);
        result = 31 * result + (resultcount != null ? resultcount.hashCode() : 0);
        return result;
    }

    public static JobreportitemorganisationEntity retrieveForJobReportItemAndOdsCode(JobreportitemEntity jobReportItem, String odsCode) throws Exception {
        return retrieveForJobReportItemAndOdsCode(jobReportItem.getJobreportitemuuid(), odsCode);
    }
    public static JobreportitemorganisationEntity retrieveForJobReportItemAndOdsCode(UUID jobreportitemuuid, String organisationodscode) throws Exception {
        String where = "FROM JobreportitemorganisationEntity WHERE jobreportitemuuid = :jobreportitemuuid"
                + " AND organisationodscode = :organisationodscode";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        JobreportitemorganisationEntity ret = entityManager.createQuery(where, JobreportitemorganisationEntity.class)
                .setParameter("jobreportitemuuid", jobreportitemuuid)
                .setParameter("organisationodscode", organisationodscode)
                .getSingleResult();

        entityManager.close();

        return ret;
    }
}
