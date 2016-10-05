package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "enduserpwd", schema = "\"Administration\"", catalog = "Endeavour_Enterprise")
public class EnduserpwdEntity {
    private UUID enduserpwduuid;
    private UUID enduseruuid;
    private String pwdhash;
    private Timestamp dtexpired;
    private Integer failedattempts;
    private boolean isonetimeuse;

    @Id
    @Column(name = "enduserpwduuid")
    public UUID getEnduserpwduuid() {
        return enduserpwduuid;
    }

    public void setEnduserpwduuid(UUID enduserpwduuid) {
        this.enduserpwduuid = enduserpwduuid;
    }

    @Basic
    @Column(name = "enduseruuid")
    public UUID getEnduseruuid() {
        return enduseruuid;
    }

    public void setEnduseruuid(UUID enduseruuid) {
        this.enduseruuid = enduseruuid;
    }

    @Basic
    @Column(name = "pwdhash")
    public String getPwdhash() {
        return pwdhash;
    }

    public void setPwdhash(String pwdhash) {
        this.pwdhash = pwdhash;
    }

    @Basic
    @Column(name = "dtexpired")
    public Timestamp getDtexpired() {
        return dtexpired;
    }

    public void setDtexpired(Timestamp dtexpired) {
        this.dtexpired = dtexpired;
    }

    @Basic
    @Column(name = "failedattempts")
    public Integer getFailedattempts() {
        return failedattempts;
    }

    public void setFailedattempts(Integer failedattempts) {
        this.failedattempts = failedattempts;
    }

    @Basic
    @Column(name = "isonetimeuse")
    public boolean getIsonetimeuse() {
        return isonetimeuse;
    }

    public void setIsonetimeuse(boolean isonetimeuse) {
        this.isonetimeuse = isonetimeuse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnduserpwdEntity that = (EnduserpwdEntity) o;

        if (enduserpwduuid != null ? !enduserpwduuid.equals(that.enduserpwduuid) : that.enduserpwduuid != null)
            return false;
        if (enduseruuid != null ? !enduseruuid.equals(that.enduseruuid) : that.enduseruuid != null) return false;
        if (pwdhash != null ? !pwdhash.equals(that.pwdhash) : that.pwdhash != null) return false;
        if (dtexpired != null ? !dtexpired.equals(that.dtexpired) : that.dtexpired != null) return false;
        if (failedattempts != null ? !failedattempts.equals(that.failedattempts) : that.failedattempts != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = enduserpwduuid != null ? enduserpwduuid.hashCode() : 0;
        result = 31 * result + (enduseruuid != null ? enduseruuid.hashCode() : 0);
        result = 31 * result + (pwdhash != null ? pwdhash.hashCode() : 0);
        result = 31 * result + (dtexpired != null ? dtexpired.hashCode() : 0);
        result = 31 * result + (failedattempts != null ? failedattempts.hashCode() : 0);
        return result;
    }

    public static final String SELECT_QUERY =
            "from EnduserpwdEntity where enduseruuid = :enduseruuid and dtexpired IS NULL";

    public static EnduserpwdEntity retrieveEndUserPwdForUserNotExpired(UUID enduseruuid) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        EnduserpwdEntity ent = entityManager.createQuery(SELECT_QUERY, EnduserpwdEntity.class).setParameter("enduseruuid", enduseruuid).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static void writeToDb(EnduserpwdEntity ent) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        EnduserpwdEntity entToSave = entityManager.find(EnduserpwdEntity.class, ent.getEnduserpwduuid());
        entToSave.setDtexpired(ent.getDtexpired());
        entToSave.setFailedattempts(ent.getFailedattempts());
        entToSave.setIsonetimeuse(ent.getIsonetimeuse());
        entToSave.setEnduseruuid(ent.getEnduseruuid());
        entToSave.setPwdhash(ent.getPwdhash());

        entityManager.getTransaction().begin();
        entityManager.persist(entToSave);
        entityManager.getTransaction().commit();

        entityManager.close();
    }
}
