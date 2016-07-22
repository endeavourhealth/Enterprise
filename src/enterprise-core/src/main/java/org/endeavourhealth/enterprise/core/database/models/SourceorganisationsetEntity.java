package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 16/07/16.
 */
@Entity
@Table(name = "sourceorganisationset", schema = "\"Lookups\"", catalog = "Endeavour_Enterprise")
public class SourceorganisationsetEntity {
    private UUID sourceorganisationsetuuid;
    private UUID organisationuuid;
    private String name;
    private String odscodes;

    @Id
    @Column(name = "sourceorganisationsetuuid")
    public UUID getSourceorganisationsetuuid() {
        return sourceorganisationsetuuid;
    }

    public void setSourceorganisationsetuuid(UUID sourceorganisationsetuuid) {
        this.sourceorganisationsetuuid = sourceorganisationsetuuid;
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "odscodes")
    public String getOdscodes() {
        return odscodes;
    }

    public void setOdscodes(String odscodes) {
        this.odscodes = odscodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceorganisationsetEntity that = (SourceorganisationsetEntity) o;

        if (sourceorganisationsetuuid != null ? !sourceorganisationsetuuid.equals(that.sourceorganisationsetuuid) : that.sourceorganisationsetuuid != null)
            return false;
        if (organisationuuid != null ? !organisationuuid.equals(that.organisationuuid) : that.organisationuuid != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (odscodes != null ? !odscodes.equals(that.odscodes) : that.odscodes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sourceorganisationsetuuid != null ? sourceorganisationsetuuid.hashCode() : 0;
        result = 31 * result + (organisationuuid != null ? organisationuuid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (odscodes != null ? odscodes.hashCode() : 0);
        return result;
    }

    public static List<SourceorganisationsetEntity> retrieveAllSets(UUID organisationuuid) throws Exception {
        String where = "FROM SourceorganisationsetEntity WHERE organisationuuid = :organisationuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<SourceorganisationsetEntity> ret = entityManager.createQuery(where, SourceorganisationsetEntity.class)
                .setParameter("organisationuuid", organisationuuid)
                .getResultList();

        entityManager.close();

        return ret;
    }

    public static List<SourceorganisationsetEntity> retrieveSets(UUID organisationuuid, String searchTerm) throws Exception {
        String where = "FROM SourceorganisationsetEntity WHERE organisationuuid = :organisationuuid"
                + " AND name LIKE :searchTerm";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<SourceorganisationsetEntity> ret = entityManager.createQuery(where, SourceorganisationsetEntity.class)
                .setParameter("organisationuuid", organisationuuid)
                .setParameter("searchTerm", searchTerm)
                .getResultList();

        entityManager.close();

        return ret;

    }

    public static SourceorganisationsetEntity retrieveSetForUuid(UUID sourceorganisationsetuuid) throws Exception {
        String where = "FROM SourceorganisationsetEntity where sourceorganisationsetuuid = :sourceorganisationsetuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        SourceorganisationsetEntity ret = entityManager.createQuery(where, SourceorganisationsetEntity.class)
                .setParameter("sourceorganisationsetuuid", sourceorganisationsetuuid)
                .getSingleResult();

        entityManager.close();

        return ret;

    }

}
