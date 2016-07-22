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
@Table(name = "enduseremailinvite", schema = "\"Administration\"", catalog = "Endeavour_Enterprise")
public class EnduseremailinviteEntity {
    private UUID enduseremailinviteuuid;
    private UUID enduseruuid;
    private String uniquetoken;
    private Timestamp dtcompleted;

    @Id
    @Column(name = "enduseremailinviteuuid")
    public UUID getEnduseremailinviteuuid() {
        return enduseremailinviteuuid;
    }

    public void setEnduseremailinviteuuid(UUID enduseremailinviteuuid) {
        this.enduseremailinviteuuid = enduseremailinviteuuid;
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
    @Column(name = "uniquetoken")
    public String getUniquetoken() {
        return uniquetoken;
    }

    public void setUniquetoken(String uniquetoken) {
        this.uniquetoken = uniquetoken;
    }

    @Basic
    @Column(name = "dtcompleted")
    public Timestamp getDtcompleted() {
        return dtcompleted;
    }

    public void setDtcompleted(Timestamp dtcompleted) {
        this.dtcompleted = dtcompleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnduseremailinviteEntity that = (EnduseremailinviteEntity) o;

        if (enduseremailinviteuuid != null ? !enduseremailinviteuuid.equals(that.enduseremailinviteuuid) : that.enduseremailinviteuuid != null)
            return false;
        if (enduseruuid != null ? !enduseruuid.equals(that.enduseruuid) : that.enduseruuid != null) return false;
        if (uniquetoken != null ? !uniquetoken.equals(that.uniquetoken) : that.uniquetoken != null) return false;
        if (dtcompleted != null ? !dtcompleted.equals(that.dtcompleted) : that.dtcompleted != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = enduseremailinviteuuid != null ? enduseremailinviteuuid.hashCode() : 0;
        result = 31 * result + (enduseruuid != null ? enduseruuid.hashCode() : 0);
        result = 31 * result + (uniquetoken != null ? uniquetoken.hashCode() : 0);
        result = 31 * result + (dtcompleted != null ? dtcompleted.hashCode() : 0);
        return result;
    }

    public static final String SELECT_QUERY_EMAIL =
            "from EnduseremailinviteEntity where enduseruuid = :enduseruuid and dtcompleted IS NULL";

    public static final String SELECT_QUERY_TOKEN =
            "from EnduseremailinviteEntity where uniquetoken = :uniquetoken";

    public static List<EnduseremailinviteEntity> retrieveForEndUserNotCompleted(UUID enduseruuid) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<EnduseremailinviteEntity> ent = entityManager.createQuery(SELECT_QUERY_EMAIL, EnduseremailinviteEntity.class).setParameter("enduseruuid", enduseruuid).getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static EnduseremailinviteEntity retrieveForToken(String uniquetoken) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        EnduseremailinviteEntity ent = entityManager.createQuery(SELECT_QUERY_TOKEN, EnduseremailinviteEntity.class).setParameter("uniquetoken", uniquetoken).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }
}
