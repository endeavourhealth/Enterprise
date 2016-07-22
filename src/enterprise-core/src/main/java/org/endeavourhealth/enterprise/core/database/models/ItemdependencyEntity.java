package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "itemdependency", schema = "\"Definition\"", catalog = "Endeavour_Enterprise")
@IdClass(ItemdependencyEntityPK.class)
public class ItemdependencyEntity {
    private UUID itemuuid;
    private UUID audituuid;
    private UUID dependentitemuuid;
    private short dependencytypeid;

    @Id
    @Column(name = "itemuuid")
    public UUID getItemuuid() {
        return itemuuid;
    }

    public void setItemuuid(UUID itemuuid) {
        this.itemuuid = itemuuid;
    }

    @Id
    @Column(name = "audituuid")
    public UUID getAudituuid() {
        return audituuid;
    }

    public void setAudituuid(UUID audituuid) {
        this.audituuid = audituuid;
    }

    @Id
    @Column(name = "dependentitemuuid")
    public UUID getDependentitemuuid() {
        return dependentitemuuid;
    }

    public void setDependentitemuuid(UUID dependentitemuuid) {
        this.dependentitemuuid = dependentitemuuid;
    }

    @Basic
    @Column(name = "dependencytypeid")
    public short getDependencytypeid() {
        return dependencytypeid;
    }

    public void setDependencytypeid(short dependencytypeid) {
        this.dependencytypeid = dependencytypeid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemdependencyEntity that = (ItemdependencyEntity) o;

        if (dependencytypeid != that.dependencytypeid) return false;
        if (itemuuid != null ? !itemuuid.equals(that.itemuuid) : that.itemuuid != null) return false;
        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;
        if (dependentitemuuid != null ? !dependentitemuuid.equals(that.dependentitemuuid) : that.dependentitemuuid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemuuid != null ? itemuuid.hashCode() : 0;
        result = 31 * result + (audituuid != null ? audituuid.hashCode() : 0);
        result = 31 * result + (dependentitemuuid != null ? dependentitemuuid.hashCode() : 0);
        result = 31 * result + (int) dependencytypeid;
        return result;
    }

    public int retrieveCountDependencies(UUID dependentitemuuid, Short dependencytypeid) throws Exception {
        String where = "SELECT COUNT(1)"
                + " FROM ItemdependencyEntity d, ActiveitemEntity a"
                + " WHERE d.dependentitemuuid = :dependentitemuuid"
                + " AND d.dependencytypeid = :dependencytypeid"
                + " AND a.itemuuid = d.itemuuid"
                + " AND a.audituuid = d.audituuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        Integer result = (Integer)entityManager.createQuery(where)
                .setParameter("dependentitemuuid", dependentitemuuid)
                .setParameter("dependencytypeid", dependencytypeid)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return result;
    }

    public static List<ItemdependencyEntity> retrieveForActiveItem(ActiveitemEntity activeItem) throws Exception {
        String where = "from ItemdependencyEntity WHERE itemuuid = :itemuuid"
                + " AND audituuid = :audituuid";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemdependencyEntity> ent = entityManager.createQuery(where, ItemdependencyEntity.class)
                .setParameter("itemuuid", activeItem.getItemuuid())
                .setParameter("audituuid", activeItem.getAudituuid())
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemdependencyEntity> retrieveForActiveItemType(ActiveitemEntity activeItem, Short dependencyType) throws Exception {
        String where = "from ItemdependencyEntity WHERE itemuuid = :itemuuid"
                + " AND audituuid = :audituuid"
                + " AND dependencytypeid = :dependencytypeid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemdependencyEntity> ent = entityManager.createQuery(where, ItemdependencyEntity.class)
                .setParameter("itemuuid", activeItem.getItemuuid())
                .setParameter("audituuid", activeItem.getAudituuid())
                .setParameter("dependencytypeid", dependencyType)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemdependencyEntity> retrieveForItemType(UUID itemUuid, UUID auditUuid, Short dependencyType) throws Exception {
        String where = "from ItemdependencyEntity WHERE itemuuid = :itemuuid"
                + " AND audituuid = :audituuid"
                + " AND dependencytypeid = :dependencytypeid";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemdependencyEntity> ent = entityManager.createQuery(where, ItemdependencyEntity.class)
                .setParameter("itemuuid", itemUuid)
                .setParameter("audituuid", auditUuid)
                .setParameter("dependencytypeid", dependencyType)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemdependencyEntity> retrieveForDependentItem(UUID dependentItemUuid) throws Exception {
        String where = "SELECT d from ItemdependencyEntity d INNER JOIN ActiveitemEntity a"
                + " ON a.itemuuid = d.itemuuid"
                + " AND a.audituuid = d.audituuid"
                + " AND a.isdeleted = 'false'"
                + " WHERE dependentitemuuid = :dependentitemuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemdependencyEntity> ent = entityManager.createQuery(where, ItemdependencyEntity.class)
                .setParameter("dependentitemuuid", dependentItemUuid)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemdependencyEntity> retrieveForDependentItemType(UUID dependentitemuuid, Short dependencytypeid) throws Exception {
        String where = "SELECT d from ItemdependencyEntity d INNER JOIN ActiveitemEntity a"
                + " ON a.itemuuid = d.itemuuid"
                + " AND a.audituuid = d.audituuid"
                + " AND a.isdeleted = 'false'"
                + " WHERE dependentitemuuid = :dependentitemuuid"
                + " AND dependencytypeid = :dependencytypeid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemdependencyEntity> ent = entityManager.createQuery(where, ItemdependencyEntity.class)
                .setParameter("dependentitemuuid", dependentitemuuid)
                .setParameter("dependencytypeid", dependencytypeid)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }
}
