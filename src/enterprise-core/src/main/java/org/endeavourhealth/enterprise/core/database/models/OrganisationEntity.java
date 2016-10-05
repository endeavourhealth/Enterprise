package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "organisation", schema = "\"Administration\"", catalog = "Endeavour_Enterprise")
public class OrganisationEntity {
    private UUID organisationuuid;
    private String name;
    private String nationalid;

    @Id
    @Column(name = "organisationuuid")
    public UUID getOrganisationuuid() {
        return organisationuuid;
    }

    public void setOrganisationuuid(UUID organisationuuid) {
        this.organisationuuid = organisationuuid;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "nationalid")
    public String getNationalid() {
        return nationalid;
    }

    public void setNationalid(String nationalid) {
        this.nationalid = nationalid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganisationEntity that = (OrganisationEntity) o;

        if (organisationuuid != null ? !organisationuuid.equals(that.organisationuuid) : that.organisationuuid != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (nationalid != null ? !nationalid.equals(that.nationalid) : that.nationalid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = organisationuuid != null ? organisationuuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (nationalid != null ? nationalid.hashCode() : 0);
        return result;
    }

    public static final String SELECT_QUERY_ALL =
            "from OrganisationEntity";

    public static final String SELECT_QUERY_FOR_UUID =
            "from OrganisationEntity where organisationuuid = :organisationuuid";

    public static final String SELECT_QUERY_FOR_NAME_NATIONALID =
            "from OrganisationEntity where name = :name and nationalid = :nationalid";

    public static List<OrganisationEntity> retrieveForAll() {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<OrganisationEntity> ent = entityManager.createQuery(SELECT_QUERY_ALL, OrganisationEntity.class).getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static OrganisationEntity retrieveForUuid(UUID organisationuuid) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        OrganisationEntity ent = entityManager.createQuery(SELECT_QUERY_FOR_UUID, OrganisationEntity.class).setParameter("organisationuuid", organisationuuid).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static OrganisationEntity retrieveOrganisationForNameNationalId(String name, String nationalid) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        OrganisationEntity ent = entityManager.createQuery(SELECT_QUERY_FOR_NAME_NATIONALID, OrganisationEntity.class)
                .setParameter("name", name)
                .setParameter("nationalid", nationalid)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }
}
