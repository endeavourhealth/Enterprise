package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "jobreportitem", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
public class JobreportitemEntity {
    private UUID jobreportitemuuid;
    private UUID jobreportuuid;
    private UUID parentjobreportitemuuid;
    private UUID itemuuid;
    private UUID audituuid;
    private Integer resultcount;
    private String filelocation;

    @Id
    @Column(name = "jobreportitemuuid")
    public UUID getJobreportitemuuid() {
        return jobreportitemuuid;
    }

    public void setJobreportitemuuid(UUID jobreportitemuuid) {
        this.jobreportitemuuid = jobreportitemuuid;
    }

    @Basic
    @Column(name = "jobreportuuid")
    public UUID getJobreportuuid() {
        return jobreportuuid;
    }

    public void setJobreportuuid(UUID jobreportuuid) {
        this.jobreportuuid = jobreportuuid;
    }

    @Basic
    @Column(name = "parentjobreportitemuuid")
    public UUID getParentjobreportitemuuid() {
        return parentjobreportitemuuid;
    }

    public void setParentjobreportitemuuid(UUID parentjobreportitemuuid) {
        this.parentjobreportitemuuid = parentjobreportitemuuid;
    }

    @Basic
    @Column(name = "itemuuid")
    public UUID getItemuuid() {
        return itemuuid;
    }

    public void setItemuuid(UUID itemuuid) {
        this.itemuuid = itemuuid;
    }

    @Basic
    @Column(name = "audituuid")
    public UUID getAudituuid() {
        return audituuid;
    }

    public void setAudituuid(UUID audituuid) {
        this.audituuid = audituuid;
    }

    @Basic
    @Column(name = "resultcount")
    public Integer getResultcount() {
        return resultcount;
    }

    public void setResultcount(Integer resultcount) {
        this.resultcount = resultcount;
    }

    @Basic
    @Column(name = "filelocation")
    public String getFilelocation() {
        return filelocation;
    }

    public void setFilelocation(String filelocation) {
        this.filelocation = filelocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobreportitemEntity that = (JobreportitemEntity) o;

        if (jobreportitemuuid != null ? !jobreportitemuuid.equals(that.jobreportitemuuid) : that.jobreportitemuuid != null)
            return false;
        if (jobreportuuid != null ? !jobreportuuid.equals(that.jobreportuuid) : that.jobreportuuid != null)
            return false;
        if (parentjobreportitemuuid != null ? !parentjobreportitemuuid.equals(that.parentjobreportitemuuid) : that.parentjobreportitemuuid != null)
            return false;
        if (itemuuid != null ? !itemuuid.equals(that.itemuuid) : that.itemuuid != null) return false;
        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;
        if (resultcount != null ? !resultcount.equals(that.resultcount) : that.resultcount != null) return false;
        if (filelocation != null ? !filelocation.equals(that.filelocation) : that.filelocation != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobreportitemuuid != null ? jobreportitemuuid.hashCode() : 0;
        result = 31 * result + (jobreportuuid != null ? jobreportuuid.hashCode() : 0);
        result = 31 * result + (parentjobreportitemuuid != null ? parentjobreportitemuuid.hashCode() : 0);
        result = 31 * result + (itemuuid != null ? itemuuid.hashCode() : 0);
        result = 31 * result + (audituuid != null ? audituuid.hashCode() : 0);
        result = 31 * result + (resultcount != null ? resultcount.hashCode() : 0);
        result = 31 * result + (filelocation != null ? filelocation.hashCode() : 0);
        return result;
    }

    public static List<JobreportitemEntity> retrieveForJobReport(JobreportEntity jobReport) throws Exception {
        return retrieveForJobReport(jobReport.getJobreportuuid());
    }
    public static List<JobreportitemEntity> retrieveForJobReport(UUID jobreportuuid) throws Exception {
        String where = "FROM JobreportitemEntity WHERE jobreportuuid = :jobreportuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobreportitemEntity> ret = entityManager.createQuery(where, JobreportitemEntity.class)
                .setParameter("jobreportuuid", jobreportuuid)
                .getResultList();

        entityManager.close();

        return ret;
    }
}
