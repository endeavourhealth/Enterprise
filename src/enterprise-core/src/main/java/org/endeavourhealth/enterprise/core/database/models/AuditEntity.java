package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 14/02/17.
 */
@Entity
@Table(name = "Audit", schema = "enterprise_admin", catalog = "")
public class AuditEntity {
    private String auditUuid;
    private String endUserUuid;
    private Timestamp timeStamp;
    private String organisationUuid;

    @Id
    @Column(name = "AuditUuid", nullable = false, length = 36)
    public String getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(String auditUuid) {
        this.auditUuid = auditUuid;
    }

    @Basic
    @Column(name = "EndUserUuid", nullable = false, length = 36)
    public String getEndUserUuid() {
        return endUserUuid;
    }

    public void setEndUserUuid(String endUserUuid) {
        this.endUserUuid = endUserUuid;
    }

    @Basic
    @Column(name = "TimeStamp", nullable = false)
    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Basic
    @Column(name = "OrganisationUuid", nullable = false, length = 36)
    public String getOrganisationUuid() {
        return organisationUuid;
    }

    public void setOrganisationUuid(String organisationUuid) {
        this.organisationUuid = organisationUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditEntity that = (AuditEntity) o;

        if (auditUuid != null ? !auditUuid.equals(that.auditUuid) : that.auditUuid != null) return false;
        if (endUserUuid != null ? !endUserUuid.equals(that.endUserUuid) : that.endUserUuid != null) return false;
        if (timeStamp != null ? !timeStamp.equals(that.timeStamp) : that.timeStamp != null) return false;
        if (organisationUuid != null ? !organisationUuid.equals(that.organisationUuid) : that.organisationUuid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = auditUuid != null ? auditUuid.hashCode() : 0;
        result = 31 * result + (endUserUuid != null ? endUserUuid.hashCode() : 0);
        result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
        result = 31 * result + (organisationUuid != null ? organisationUuid.hashCode() : 0);
        return result;
    }

    public static AuditEntity factoryNow(String endUserUuid, String organisationUuid) {
        AuditEntity ret = new AuditEntity();
        ret.setAuditUuid(UUID.randomUUID().toString()); //always explicitly set a new UUID as we'll always want to use it
        //ret.setSaveMode(TableSaveMode.INSERT);
        ret.setEndUserUuid(endUserUuid);
        ret.setTimeStamp(Timestamp.from(Instant.now()));
        ret.setOrganisationUuid(organisationUuid);
        return ret;
    }

    public static final String SELECT_QUERY_UUID_LIST =
            "from AuditEntity where auditUuid IN :auditUuid";

    public static final String SELECT_QUERY_FOR_UUID =
            "from AuditEntity where auditUuid = :auditUuid";

    public static final String SELECT_QUERY_FOR_LATEST =
            "from AuditEntity where TimeStamp = "
                    + "(SELECT MAX(TimeStamp)"
                    + " FROM AuditEntity)";

    public static List<AuditEntity> retrieveForActiveItems(List<ActiveItemEntity> activeItems) throws Exception {
        List<String> uuids = new ArrayList<>();
        for (ActiveItemEntity activeItem: activeItems) {
            uuids.add(activeItem.getAuditUuid());
        }
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<AuditEntity> ent = entityManager.createQuery(SELECT_QUERY_UUID_LIST, AuditEntity.class).setParameter("auditUuid", uuids).getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static AuditEntity retrieveForUuid(String auditUuid) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        AuditEntity ent = entityManager.createQuery(SELECT_QUERY_FOR_UUID, AuditEntity.class).setParameter("auditUuid", auditUuid).getSingleResult();

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

    public List<AuditEntity> retrieveAuditsForUuids(List<String> uuids) throws Exception {
        if (uuids.isEmpty()) {
            return new ArrayList<AuditEntity>();
        }

        String where = "from AuditEntity WHERE auditUuid IN :uuids";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<AuditEntity> ent = entityManager.createQuery(where, AuditEntity.class).setParameter("uuids", uuids).getResultList();

        entityManager.close();

        return ent;

    }
}
