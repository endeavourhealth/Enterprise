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
@Table(name = "audit", schema = "\"Definition\"", catalog = "Endeavour_Enterprise")
public class AuditEntity {
    private UUID audituuid;
    private UUID enduseruuid;
    private Timestamp timestamp;
    private Integer auditversion;
    private UUID organisationuuid;

    @Id
    @Column(name = "audituuid")
    public UUID getAudituuid() {
        return audituuid;
    }

    public void setAudituuid(UUID audituuid) {
        this.audituuid = audituuid;
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
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "auditversion")
    public Integer getAuditversion() {
        return auditversion;
    }

    public void setAuditversion(Integer auditversion) {
        this.auditversion = auditversion;
    }

    @Basic
    @Column(name = "organisationuuid")
    public UUID getOrganisationuuid() {
        return organisationuuid;
    }

    public void setOrganisationuuid(UUID organisationuuid) {
        this.organisationuuid = organisationuuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditEntity that = (AuditEntity) o;

        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;
        if (enduseruuid != null ? !enduseruuid.equals(that.enduseruuid) : that.enduseruuid != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (auditversion != null ? !auditversion.equals(that.auditversion) : that.auditversion != null) return false;
        if (organisationuuid != null ? !organisationuuid.equals(that.organisationuuid) : that.organisationuuid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = audituuid != null ? audituuid.hashCode() : 0;
        result = 31 * result + (enduseruuid != null ? enduseruuid.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (auditversion != null ? auditversion.hashCode() : 0);
        result = 31 * result + (organisationuuid != null ? organisationuuid.hashCode() : 0);
        return result;
    }

    public static AuditEntity factoryNow(UUID endUserUuid, UUID organisationUuid) {
        AuditEntity ret = new AuditEntity();
        ret.setAudituuid(UUID.randomUUID()); //always explicitly set a new UUID as we'll always want to use it
        //ret.setSaveMode(TableSaveMode.INSERT);
        ret.setEnduseruuid(endUserUuid);
        ret.setTimestamp(Timestamp.from(Instant.now()));
        ret.setOrganisationuuid(organisationUuid);
        ret.setAuditversion(1);
        return ret;
    }

    public static final String SELECT_QUERY_UUID_LIST =
            "from AuditEntity where audituuid IN :audituuid";

    public static final String SELECT_QUERY_FOR_UUID =
            "from AuditEntity where audituuid = :audituuid";

    public static final String SELECT_QUERY_FOR_LATEST =
            "from AuditEntity where auditversion = "
                    + "(SELECT MAX(auditversion)"
                    + " FROM AuditEntity)";

    public static List<AuditEntity> retrieveForActiveItems(List<ActiveitemEntity> activeItems) throws Exception {
        List<UUID> uuids = new ArrayList<>();
        for (ActiveitemEntity activeItem: activeItems) {
            uuids.add(activeItem.getAudituuid());
        }
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<AuditEntity> ent = entityManager.createQuery(SELECT_QUERY_UUID_LIST, AuditEntity.class).setParameter("audituuid", uuids).getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static AuditEntity retrieveForUuid(UUID audituuid) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        AuditEntity ent = entityManager.createQuery(SELECT_QUERY_FOR_UUID, AuditEntity.class).setParameter("audituuid", audituuid).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static AuditEntity retrieveLatest() throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        AuditEntity ent = entityManager.createQuery(SELECT_QUERY_FOR_LATEST, AuditEntity.class).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;

    }

    public List<AuditEntity> retrieveAuditsForUuids(List<UUID> uuids) throws Exception {
        if (uuids.isEmpty()) {
            return new ArrayList<AuditEntity>();
        }

        String where = "from AuditEntity WHERE audituuid IN :uuids";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<AuditEntity> ent = entityManager.createQuery(where, AuditEntity.class).setParameter("uuids", uuids).getResultList();

        entityManager.close();

        return ent;

    }
}
