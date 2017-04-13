package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 14/02/17.
 */
@Entity
@Table(name = "ActiveItem", schema = "enterprise_admin", catalog = "")
public class ActiveItemEntity {
    private String activeItemUuid;
    private String organisationUuid;
    private String itemUuid;
    private String auditUuid;
    private short itemTypeId;
    private byte isDeleted;

    @Id
    @Column(name = "ActiveItemUuid", nullable = false, length = 36)
    public String getActiveItemUuid() {
        return activeItemUuid;
    }

    public void setActiveItemUuid(String activeItemUuid) {
        this.activeItemUuid = activeItemUuid;
    }

    @Basic
    @Column(name = "OrganisationUuid", nullable = false, length = 36)
    public String getOrganisationUuid() {
        return organisationUuid;
    }

    public void setOrganisationUuid(String organisationUuid) {
        this.organisationUuid = organisationUuid;
    }

    @Basic
    @Column(name = "ItemUuid", nullable = false, length = 36)
    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }

    @Basic
    @Column(name = "AuditUuid", nullable = false, length = 36)
    public String getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(String auditUuid) {
        this.auditUuid = auditUuid;
    }

    @Basic
    @Column(name = "ItemTypeId", nullable = false)
    public short getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(short itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    @Basic
    @Column(name = "IsDeleted", nullable = false)
    public byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiveItemEntity that = (ActiveItemEntity) o;

        if (itemTypeId != that.itemTypeId) return false;
        if (isDeleted != that.isDeleted) return false;
        if (activeItemUuid != null ? !activeItemUuid.equals(that.activeItemUuid) : that.activeItemUuid != null)
            return false;
        if (organisationUuid != null ? !organisationUuid.equals(that.organisationUuid) : that.organisationUuid != null)
            return false;
        if (itemUuid != null ? !itemUuid.equals(that.itemUuid) : that.itemUuid != null) return false;
        if (auditUuid != null ? !auditUuid.equals(that.auditUuid) : that.auditUuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = activeItemUuid != null ? activeItemUuid.hashCode() : 0;
        result = 31 * result + (organisationUuid != null ? organisationUuid.hashCode() : 0);
        result = 31 * result + (itemUuid != null ? itemUuid.hashCode() : 0);
        result = 31 * result + (auditUuid != null ? auditUuid.hashCode() : 0);
        result = 31 * result + (int) itemTypeId;
        result = 31 * result + (int) isDeleted;
        return result;
    }

    public static final String SELECT_QUERY_FOR_UUID =
            "from ActiveItemEntity where itemUuid = :itemUuid";

    public static final String SELECT_QUERY_FOR_COUNT =
            "select count(1) from ActiveItemEntity a, ItemDependencyEntity d"
                    + " WHERE d.dependentItemUuid = :dependentItemUuid"
                    + " AND d.dependencyTypeId = :dependencyTypeId"
                    + " AND a.itemUuid = d.itemUuid"
                    + " AND a.auditUuid = d.auditUuid";

    public static final String SELECT_QUERY_FOR_DEPENDENT =
            "SELECT a from ActiveItemEntity a"
                    + " INNER JOIN ItemDependencyEntity d"
                    + " ON d.dependentItemUuid = :dependentItemUuid"
                    + " AND d.dependencyTypeId = :dependencyTypeId"
                    + " AND d.itemUuid = a.itemUuid"
                    + " AND d.auditUuid = a.auditUuid"
                    + " WHERE a.organisationUuid = :organisationUuid";

    public static ActiveItemEntity retrieveForItemUuid(String itemUuid) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseAdmin();

        ActiveItemEntity ent = entityManager.createQuery(SELECT_QUERY_FOR_UUID, ActiveItemEntity.class).setParameter("itemUuid", itemUuid).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public List<ActiveItemEntity> retrieveActiveItemRecentItems(String endUserUuid, String organisationUuid, int count) throws Exception {
        String where = "SELECT a from ActiveItemEntity a"
                + " INNER JOIN ItemEntity i"
                + " ON i.itemUuid = a.itemUuid"
                + " AND i.auditUuid = a.auditUuid"
                + " AND a.isDeleted = 0"
                + " INNER JOIN AuditEntity ae"
                + " ON ae.auditUuid = i.auditUuid"
                + " AND ae.endUserUuid = :endUserUuid"
                + " WHERE a.itemTypeId NOT IN (" + DefinitionItemType.LibraryFolder.getValue() + ", " + DefinitionItemType.ReportFolder.getValue() + ")"
                + " AND a.organisationUuid = :organisationUuid"
                + " ORDER BY ae.timeStamp DESC";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseAdmin();

        List<ActiveItemEntity> ent = entityManager.createQuery(where, ActiveItemEntity.class)
                .setParameter("endUserUuid", endUserUuid)
                .setParameter("organisationUuid", organisationUuid)
                .setMaxResults(count)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;

    }


    public static int retrieveCountDependencies(String dependentItemUuid, Short dependencyTypeId) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseAdmin();

        Long result = (Long)entityManager.createQuery(SELECT_QUERY_FOR_COUNT)
                .setParameter("dependentItemUuid", dependentItemUuid)
                .setParameter("dependencyTypeId", dependencyTypeId)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return result.intValue();
    }

    public static List<ActiveItemEntity> retrieveDependentItems(String organisationUuid, String dependentItemUuid, Short dependencyTypeId) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseAdmin();

        List<ActiveItemEntity> ent = entityManager.createQuery(SELECT_QUERY_FOR_DEPENDENT, ActiveItemEntity.class)
                .setParameter("dependentItemUuid", dependentItemUuid)
                .setParameter("dependencyTypeId", dependencyTypeId)
                .setParameter("organisationUuid", organisationUuid)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static ActiveItemEntity factoryNew(ItemEntity item, String organisationUuid, Short itemType) {
        String itemUuid = item.getItemUuid();
        String auditUuid = item.getAuditUuid();

        if (itemUuid == null) {
            throw new RuntimeException("Cannot create ActiveItem without first saving Item to DB");
        }

        ActiveItemEntity ret = new ActiveItemEntity();
        ret.setActiveItemUuid(UUID.randomUUID().toString());
        ret.setOrganisationUuid(organisationUuid);
        ret.setItemUuid(itemUuid);
        ret.setAuditUuid(auditUuid);
        ret.setItemTypeId(itemType);

        return ret;
    }

}
