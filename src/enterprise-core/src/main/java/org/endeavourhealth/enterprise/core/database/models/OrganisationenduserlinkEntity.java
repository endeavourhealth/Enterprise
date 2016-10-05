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
@Table(name = "organisationenduserlink", schema = "\"Administration\"", catalog = "Endeavour_Enterprise")
public class OrganisationenduserlinkEntity {
    private UUID organisationenduserlinkuuid;
    private UUID organisationuuid;
    private UUID enduseruuid;
    private boolean isadmin;
    private Timestamp dtexpired;

    @Id
    @Column(name = "organisationenduserlinkuuid")
    public UUID getOrganisationenduserlinkuuid() {
        return organisationenduserlinkuuid;
    }

    public void setOrganisationenduserlinkuuid(UUID organisationenduserlinkuuid) {
        this.organisationenduserlinkuuid = organisationenduserlinkuuid;
    }

    @Basic
    @Column(name = "organisationuuid")
    public UUID getOrganisationuuid() {
        return organisationuuid;
    }

    public void setOrganisationuuid(UUID organisationuuid) {
        this.organisationuuid = organisationuuid;
    }

    @Basic
    @Column(name = "enduseruuid")
    public UUID getEnduseruuid() {
        return enduseruuid;
    }

    public void setEnduseruuid(UUID enduseruuid) {
        this.enduseruuid = enduseruuid;
    }

    public boolean getIsadmin() {
        return isadmin;
    }

    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }

    @Basic
    @Column(name = "dtexpired")
    public Timestamp getDtexpired() {
        return dtexpired;
    }

    public void setDtexpired(Timestamp dtexpired) {
        this.dtexpired = dtexpired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganisationenduserlinkEntity that = (OrganisationenduserlinkEntity) o;

        if (organisationenduserlinkuuid != null ? !organisationenduserlinkuuid.equals(that.organisationenduserlinkuuid) : that.organisationenduserlinkuuid != null)
            return false;
        if (organisationuuid != null ? !organisationuuid.equals(that.organisationuuid) : that.organisationuuid != null)
            return false;
        if (enduseruuid != null ? !enduseruuid.equals(that.enduseruuid) : that.enduseruuid != null) return false;
        if (dtexpired != null ? !dtexpired.equals(that.dtexpired) : that.dtexpired != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = organisationenduserlinkuuid != null ? organisationenduserlinkuuid.hashCode() : 0;
        result = 31 * result + (organisationuuid != null ? organisationuuid.hashCode() : 0);
        result = 31 * result + (enduseruuid != null ? enduseruuid.hashCode() : 0);
        result = 31 * result + (dtexpired != null ? dtexpired.hashCode() : 0);
        return result;
    }

    public static final String SELECT_QUERY_ALL =
            "from OrganisationenduserlinkEntity where enduseruuid = :enduseruuid and dtexpired IS NULL";

    public static final String SELECT_QUERY_ORG_USER =
            "from OrganisationenduserlinkEntity where organisationuuid = :organisationuuid and enduseruuid = :enduseruuid and dtexpired IS NULL";

    public static final String SELECT_QUERY_ORG =
            "from OrganisationenduserlinkEntity where organisationuuid = :organisationuuid and dtexpired IS NULL";

    public static List<OrganisationenduserlinkEntity> retrieveForEndUserNotExpired(UUID enduseruuid) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<OrganisationenduserlinkEntity> ent = entityManager.createQuery(SELECT_QUERY_ALL, OrganisationenduserlinkEntity.class).setParameter("enduseruuid", enduseruuid).getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static OrganisationenduserlinkEntity retrieveForOrganisationEndUserNotExpired(UUID organisationuuid, UUID enduseruuid) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        OrganisationenduserlinkEntity ent = entityManager.
                createQuery(SELECT_QUERY_ORG_USER, OrganisationenduserlinkEntity.class)
                .setParameter("organisationuuid", organisationuuid)
                .setParameter("enduseruuid", enduseruuid)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<OrganisationenduserlinkEntity> retrieveForOrganisationNotExpired(UUID organisationuuid) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<OrganisationenduserlinkEntity> ent = entityManager.
                createQuery(SELECT_QUERY_ORG, OrganisationenduserlinkEntity.class)
                .setParameter("organisationuuid", organisationuuid)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }




}
