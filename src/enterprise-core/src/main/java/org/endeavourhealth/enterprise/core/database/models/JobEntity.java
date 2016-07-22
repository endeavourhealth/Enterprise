package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "job", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
public class JobEntity {
    private UUID jobuuid;
    private short statusid;
    private Timestamp startdatetime;
    private Timestamp enddatetime;
    private Integer patientsindatabase;
    private UUID baselineaudituuid;

    @Id
    @Column(name = "jobuuid")
    public UUID getJobuuid() {
        return jobuuid;
    }

    public void setJobuuid(UUID jobuuid) {
        this.jobuuid = jobuuid;
    }

    @Basic
    @Column(name = "statusid")
    public short getStatusid() {
        return statusid;
    }

    public void setStatusid(short statusid) {
        this.statusid = statusid;
    }

    @Basic
    @Column(name = "startdatetime")
    public Timestamp getStartdatetime() {
        return startdatetime;
    }

    public void setStartdatetime(Timestamp startdatetime) {
        this.startdatetime = startdatetime;
    }

    @Basic
    @Column(name = "enddatetime")
    public Timestamp getEnddatetime() {
        return enddatetime;
    }

    public void setEnddatetime(Timestamp enddatetime) {
        this.enddatetime = enddatetime;
    }

    @Basic
    @Column(name = "patientsindatabase")
    public Integer getPatientsindatabase() {
        return patientsindatabase;
    }

    public void setPatientsindatabase(Integer patientsindatabase) {
        this.patientsindatabase = patientsindatabase;
    }

    @Basic
    @Column(name = "baselineaudituuid")
    public UUID getBaselineaudituuid() {
        return baselineaudituuid;
    }

    public void setBaselineaudituuid(UUID baselineaudituuid) {
        this.baselineaudituuid = baselineaudituuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobEntity jobEntity = (JobEntity) o;

        if (statusid != jobEntity.statusid) return false;
        if (jobuuid != null ? !jobuuid.equals(jobEntity.jobuuid) : jobEntity.jobuuid != null) return false;
        if (startdatetime != null ? !startdatetime.equals(jobEntity.startdatetime) : jobEntity.startdatetime != null)
            return false;
        if (enddatetime != null ? !enddatetime.equals(jobEntity.enddatetime) : jobEntity.enddatetime != null)
            return false;
        if (patientsindatabase != null ? !patientsindatabase.equals(jobEntity.patientsindatabase) : jobEntity.patientsindatabase != null)
            return false;
        if (baselineaudituuid != null ? !baselineaudituuid.equals(jobEntity.baselineaudituuid) : jobEntity.baselineaudituuid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobuuid != null ? jobuuid.hashCode() : 0;
        result = 31 * result + (int) statusid;
        result = 31 * result + (startdatetime != null ? startdatetime.hashCode() : 0);
        result = 31 * result + (enddatetime != null ? enddatetime.hashCode() : 0);
        result = 31 * result + (patientsindatabase != null ? patientsindatabase.hashCode() : 0);
        result = 31 * result + (baselineaudituuid != null ? baselineaudituuid.hashCode() : 0);
        return result;
    }

    public static List<JobEntity> retrieveForJobReports(List<JobreportEntity> jobReports) throws Exception {
        List<UUID> uuids = new ArrayList<>();
        for (JobreportEntity jobReport: jobReports) {
            uuids.add(jobReport.getJobuuid());
        }
        return retrieveForUuids(uuids);
    }

    public static List<JobEntity> retrieveForUuids(List<UUID> uuids) throws Exception {
        if (uuids.isEmpty()) {
            return new ArrayList<JobEntity>();
        }
        String where = "FROM JobEntity WHERE jobuuid IN :uuids";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobEntity> ret = entityManager.createQuery(where, JobEntity.class)
                .setParameter("uuids", uuids)
                .getResultList();

        entityManager.close();

        return ret;
    }


    public static JobEntity retrieveForUuid(UUID jobuuid) throws Exception {
        String where = "FROM JobEntity WHERE jobuuid = :jobuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        JobEntity ret = entityManager.createQuery(where, JobEntity.class)
                .setParameter("jobuuid", jobuuid)
                .getSingleResult();

        entityManager.close();

        return ret;
    }

    public static List<JobEntity> retrieveRecent(int count) throws Exception {
        String where = "FROM JobEntity ORDER BY startdatetime DESC";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobEntity> ret = entityManager.createQuery(where, JobEntity.class)
                .setMaxResults(count)
                .getResultList();

        entityManager.close();

        return ret;
    }

    public static List<JobEntity> retrieveForStatus(Short statusid) throws Exception {
        String where = "FROM JobEntity WHERE statusid = :statusid";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobEntity> ret = entityManager.createQuery(where, JobEntity.class)
                .setParameter("statusid", statusid)
                .getResultList();

        entityManager.close();

        return ret;
    }

    public List<JobEntity> retrieveJobsForJobReportUuids(List<UUID> uuids) throws Exception {
        if (uuids.isEmpty()) {
            return new ArrayList<JobEntity>();
        }

        String where = "SELECT j FROM JobEntity j INNER JOIN ON JobreportEntity r "
                + " ON r.jobreportuuid IN :uuids"
                + " AND r.jobuuid = j.jobuuid";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobEntity> ret = entityManager.createQuery(where, JobEntity.class)
                .setParameter("uuids", uuids)
                .getResultList();

        entityManager.close();

        return ret;

    }

    public void markAsFinished(Short status) {
        setEnddatetime(Timestamp.from(Instant.now()));
        setStatusid(status);
    }
}
