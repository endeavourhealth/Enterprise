package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "jobcontent", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
@IdClass(JobcontentEntityPK.class)
public class JobcontentEntity {
    private UUID jobuuid;
    private UUID itemuuid;
    private UUID audituuid;

    @Id
    @Column(name = "jobuuid")
    public UUID getJobuuid() {
        return jobuuid;
    }

    public void setJobuuid(UUID jobuuid) {
        this.jobuuid = jobuuid;
    }

    @Id
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobcontentEntity that = (JobcontentEntity) o;

        if (jobuuid != null ? !jobuuid.equals(that.jobuuid) : that.jobuuid != null) return false;
        if (itemuuid != null ? !itemuuid.equals(that.itemuuid) : that.itemuuid != null) return false;
        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobuuid != null ? jobuuid.hashCode() : 0;
        result = 31 * result + (itemuuid != null ? itemuuid.hashCode() : 0);
        result = 31 * result + (audituuid != null ? audituuid.hashCode() : 0);
        return result;
    }

    public static List<JobcontentEntity> retrieveForJob(UUID jobuuid) throws Exception {
        String where = "from JobcontentEntity WHERE jobuuid = :jobuuid";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobcontentEntity> ret = entityManager.createQuery(where, JobcontentEntity.class)
                .setParameter("jobuuid", jobuuid)
                .getResultList();

        entityManager.close();

        return ret;
    }
}
