package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;

/**
 * Created by darren on 14/02/17.
 */
@Entity
@Table(name = "ItemDependency", schema = "enterprise_admin", catalog = "")
@IdClass(ItemDependencyEntityPK.class)
public class ItemDependencyEntity {
    private String itemUuid;
    private String auditUuid;
    private String dependentItemUuid;
    private short dependencyTypeId;

    @Id
    @Column(name = "ItemUuid", nullable = false, length = 36)
    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }

    @Id
    @Column(name = "AuditUuid", nullable = false, length = 36)
    public String getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(String auditUuid) {
        this.auditUuid = auditUuid;
    }

    @Id
    @Column(name = "DependentItemUuid", nullable = false, length = 36)
    public String getDependentItemUuid() {
        return dependentItemUuid;
    }

    public void setDependentItemUuid(String dependentItemUuid) {
        this.dependentItemUuid = dependentItemUuid;
    }

    @Basic
    @Column(name = "DependencyTypeId", nullable = false)
    public short getDependencyTypeId() {
        return dependencyTypeId;
    }

    public void setDependencyTypeId(short dependencyTypeId) {
        this.dependencyTypeId = dependencyTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemDependencyEntity that = (ItemDependencyEntity) o;

        if (dependencyTypeId != that.dependencyTypeId) return false;
        if (itemUuid != null ? !itemUuid.equals(that.itemUuid) : that.itemUuid != null) return false;
        if (auditUuid != null ? !auditUuid.equals(that.auditUuid) : that.auditUuid != null) return false;
        if (dependentItemUuid != null ? !dependentItemUuid.equals(that.dependentItemUuid) : that.dependentItemUuid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemUuid != null ? itemUuid.hashCode() : 0;
        result = 31 * result + (auditUuid != null ? auditUuid.hashCode() : 0);
        result = 31 * result + (dependentItemUuid != null ? dependentItemUuid.hashCode() : 0);
        result = 31 * result + (int) dependencyTypeId;
        return result;
    }

    public int retrieveCountDependencies(String dependentItemUuid, Short dependencyTypeId) throws Exception {
        String where = "SELECT COUNT(1)"
                + " FROM ItemDependencyEntity d, ActiveItemEntity a"
                + " WHERE d.dependentItemUuid = :dependentItemUuid"
                + " AND d.dependencyTypeId = :dependencyTypeId"
                + " AND a.itemUuid = d.itemUuid"
                + " AND a.auditUuid = d.auditUuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        Integer result = (Integer)entityManager.createQuery(where)
                .setParameter("dependentItemUuid", dependentItemUuid)
                .setParameter("dependencyTypeId", dependencyTypeId)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return result;
    }

    public static List<ItemDependencyEntity> retrieveForActiveItem(ActiveItemEntity activeItem) throws Exception {
        String where = "from ItemDependencyEntity WHERE itemUuid = :itemUuid"
                + " AND auditUuid = :auditUuid";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemDependencyEntity> ent = entityManager.createQuery(where, ItemDependencyEntity.class)
                .setParameter("itemUuid", activeItem.getItemUuid())
                .setParameter("auditUuid", activeItem.getAuditUuid())
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemDependencyEntity> retrieveForActiveItemType(ActiveItemEntity activeItem, Short dependencyType) throws Exception {
        String where = "from ItemDependencyEntity WHERE itemUuid = :itemUuid"
                + " AND auditUuid = :auditUuid"
                + " AND dependencyTypeId = :dependencyTypeId";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemDependencyEntity> ent = entityManager.createQuery(where, ItemDependencyEntity.class)
                .setParameter("itemUuid", activeItem.getItemUuid())
                .setParameter("auditUuid", activeItem.getAuditUuid())
                .setParameter("dependencyTypeId", dependencyType)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemDependencyEntity> retrieveForItemType(String itemUuid, String auditUuid, Short dependencyType) throws Exception {
        String where = "from ItemDependencyEntity WHERE itemUuid = :itemUuid"
                + " AND auditUuid = :auditUuid"
                + " AND dependencyTypeId = :dependencyTypeId";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemDependencyEntity> ent = entityManager.createQuery(where, ItemDependencyEntity.class)
                .setParameter("itemUuid", itemUuid)
                .setParameter("auditUuid", auditUuid)
                .setParameter("dependencyTypeId", dependencyType)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemDependencyEntity> retrieveForDependentItem(String dependentItemUuid) throws Exception {
        String where = "SELECT d from ItemDependencyEntity d INNER JOIN ActiveItemEntity a"
                + " ON a.itemUuid = d.itemUuid"
                + " AND a.auditUuid = d.auditUuid"
                + " AND a.isDeleted = 0"
                + " WHERE dependentItemUuid = :dependentItemUuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemDependencyEntity> ent = entityManager.createQuery(where, ItemDependencyEntity.class)
                .setParameter("dependentItemUuid", dependentItemUuid)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemDependencyEntity> retrieveForDependentItemType(String dependentItemUuid, Short dependencyTypeId) throws Exception {
        String where = "SELECT d from ItemDependencyEntity d INNER JOIN ActiveItemEntity a"
                + " ON a.itemUuid = d.itemUuid"
                + " AND a.auditUuid = d.auditUuid"
                + " AND a.isDeleted = 0"
                + " WHERE dependentItemUuid = :dependentItemUuid"
                + " AND dependencyTypeId = :dependencyTypeId";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemDependencyEntity> ent = entityManager.createQuery(where, ItemDependencyEntity.class)
                .setParameter("dependentItemUuid", dependentItemUuid)
                .setParameter("dependencyTypeId", dependencyTypeId)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }
}
