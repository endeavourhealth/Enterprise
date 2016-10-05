package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "jobprocessorresult", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
@IdClass(JobprocessorresultEntityPK.class)
public class JobprocessorresultEntity {
    private UUID jobuuid;
    private UUID processoruuid;
    private String resultxml;

    @Id
    @Column(name = "jobuuid")
    public UUID getJobuuid() {
        return jobuuid;
    }

    public void setJobuuid(UUID jobuuid) {
        this.jobuuid = jobuuid;
    }

    @Id
    @Column(name = "processoruuid")
    public UUID getProcessoruuid() {
        return processoruuid;
    }

    public void setProcessoruuid(UUID processoruuid) {
        this.processoruuid = processoruuid;
    }

    @Basic
    @Column(name = "resultxml")
    public String getResultxml() {
        return resultxml;
    }

    public void setResultxml(String resultxml) {
        this.resultxml = resultxml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobprocessorresultEntity that = (JobprocessorresultEntity) o;

        if (jobuuid != null ? !jobuuid.equals(that.jobuuid) : that.jobuuid != null) return false;
        if (processoruuid != null ? !processoruuid.equals(that.processoruuid) : that.processoruuid != null)
            return false;
        if (resultxml != null ? !resultxml.equals(that.resultxml) : that.resultxml != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobuuid != null ? jobuuid.hashCode() : 0;
        result = 31 * result + (processoruuid != null ? processoruuid.hashCode() : 0);
        result = 31 * result + (resultxml != null ? resultxml.hashCode() : 0);
        return result;
    }

    public static List<JobprocessorresultEntity> retrieveForJob(JobEntity job) throws Exception {
        return retrieveForJob(job.getJobuuid());
    }
    public static List<JobprocessorresultEntity> retrieveForJob(UUID jobuuid) throws Exception {
        String where = "FROM JobprocessorresultEntity WHERE jobuuid = :jobuuid";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobprocessorresultEntity> ret = entityManager.createQuery(where, JobprocessorresultEntity.class)
                .setParameter("jobuuid", jobuuid)
                .getResultList();

        entityManager.close();

        return ret;
    }
    public static void deleteAllResults() throws Exception {
        String where = "DELETE FROM JobprocessorresultEntity";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();
        entityManager.createQuery(where).executeUpdate();

        entityManager.close();
    }
}
