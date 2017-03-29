package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;
import org.endeavourhealth.enterprise.core.database.models.data.ReportResultEntity;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darren on 14/02/17.
 */
@Entity
@Table(name = "Item", schema = "enterprise_admin", catalog = "")
@IdClass(ItemEntityPK.class)
public class ItemEntity {
    private String itemUuid;
    private String auditUuid;
    private String xmlContent;
    private String title;
    private String description;
    private byte isDeleted;

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

    @Basic
    @Column(name = "XmlContent", nullable = false, length = -1)
    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    @Basic
    @Column(name = "Title", nullable = false, length = 255)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "Description", nullable = false, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

        ItemEntity that = (ItemEntity) o;

        if (isDeleted != that.isDeleted) return false;
        if (itemUuid != null ? !itemUuid.equals(that.itemUuid) : that.itemUuid != null) return false;
        if (auditUuid != null ? !auditUuid.equals(that.auditUuid) : that.auditUuid != null) return false;
        if (xmlContent != null ? !xmlContent.equals(that.xmlContent) : that.xmlContent != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemUuid != null ? itemUuid.hashCode() : 0;
        result = 31 * result + (auditUuid != null ? auditUuid.hashCode() : 0);
        result = 31 * result + (xmlContent != null ? xmlContent.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) isDeleted;
        return result;
    }

    public static ItemEntity factoryNew(String title, AuditEntity audit) {
        ItemEntity ret = new ItemEntity();
        ret.setAuditUuid(audit.getAuditUuid());
        ret.setTitle(title);
        return ret;
    }

    public static ItemEntity retrieveLatestForUUid(String itemUuid) throws Exception {
        String where = "SELECT e from ItemEntity e INNER JOIN ActiveItemEntity a"
                + " ON a.itemUuid = e.itemUuid"
                + " AND a.auditUuid = e.auditUuid"
                + " WHERE a.itemUuid = :itemUuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ItemEntity ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("itemUuid", itemUuid)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;

    }

    public static ItemEntity retrieveForUuidAndAudit(String itemUuid, String auditUuid) throws Exception {
        String where = "from ItemEntity"
                + " WHERE auditUuid = :auditUuid"
                + " AND itemUuid = :itemUuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ItemEntity ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("auditUuid", auditUuid)
                .setParameter("itemUuid", itemUuid)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static ItemEntity retrieveForActiveItem(ActiveItemEntity activeItem) throws Exception {
        String where = "from ItemEntity"
                + " WHERE auditUuid = :auditUuid"
                + " AND itemUuid = :itemUuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ItemEntity ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("auditUuid", activeItem.getAuditUuid())
                .setParameter("itemUuid", activeItem.getItemUuid())
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }


    public static List<ItemEntity> retrieveDependentItems(String dependentItemUuid, Short dependencyTypeId) throws Exception {
        String where = "SELECT i from ItemEntity i"
                + " INNER JOIN ActiveItemEntity a"
                + " ON a.itemUuid = i.itemUuid"
                + " AND a.auditUuid = i.auditUuid"
                + " AND a.isDeleted = 0"
                + " INNER JOIN ItemDependencyEntity d"
                + " ON d.itemUuid = i.itemUuid"
                + " AND a.auditUuid = i.auditUuid"
                + " AND d.dependentItemUuid = :dependentItemUuid"
                + " AND d.dependencyTypeId = :dependencyTypeId"
                + " INNER JOIN ActiveItemEntity ad"
                + " ON ad.itemUuid = d.itemUuid"
                + " AND ad.auditUuid = d.auditUuid"
                + " AND ad.isDeleted = 0";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("dependentItemUuid", dependentItemUuid)
                .setParameter("dependencyTypeId", dependencyTypeId)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;

    }

    public static List<ItemEntity> retrieveNonDependentItems(String organisationUuid, Short dependencyTypeId, Short itemTypeId) throws Exception {
        String where = "SELECT i from ItemEntity i"
                + " INNER JOIN ActiveItemEntity a"
                + " ON a.itemUuid = i.itemUuid"
                + " AND a.auditUuid = i.auditUuid"
                + " AND a.itemTypeId = :itemTypeId"
                + " AND a.organisationUuid = :organisationUuid"
                + " AND a.isDeleted = 0"
                + " WHERE NOT EXISTS ("
                + "SELECT 1 FROM ItemDependencyEntity d"
                + " WHERE d.itemUuid = i.itemUuid"
                + " AND d.auditUuid = i.auditUuid"
                + " AND d.dependencyTypeId = :dependencyTypeId"
                + ")";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("itemTypeId", itemTypeId)
                .setParameter("organisationUuid", organisationUuid)
                .setParameter("dependencyTypeId", dependencyTypeId)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemEntity> retrieveForActiveItems(List<ActiveItemEntity> activeItems) throws Exception {
        if (activeItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> parameters = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("from ItemEntity WHERE ");

        for (int i=0; i<activeItems.size(); i++) {
            ActiveItemEntity activeItem = activeItems.get(i);
            String itemUuid = activeItem.getItemUuid();
            String auditUuid = activeItem.getAuditUuid();

            if (i > 0){
                sb.append(" OR ");
            }
            sb.append("(itemUuid = '"+itemUuid+"' AND auditUuid = '"+auditUuid+"')");

        }
        String where = sb.toString();

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> ent = entityManager.createQuery(where, ItemEntity.class)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ReportResultEntity> retrieveForReports(List<ActiveItemEntity> activeItems) throws Exception {
        if (activeItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> parameters = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("from ReportResultEntity WHERE ( ");

        for (int i=0; i<activeItems.size(); i++) {
            ActiveItemEntity activeItem = activeItems.get(i);
            String itemUuid = activeItem.getItemUuid();

            if (i > 0){
                sb.append(" OR ");
            }
            sb.append("(queryItemUuid = '"+itemUuid+"')");

        }
        sb.append(" ) and runDate in (select max(runDate) from ReportResultEntity group by queryItemUuid) ");
        String where = sb.toString();

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager2();

        List<ReportResultEntity> ent = entityManager.createQuery(where, ReportResultEntity.class)
                .getResultList();

        entityManager.close();

        return ent;
    }

    public static List<ItemEntity> retrieveLatestForUuids(List<String> itemUuids) throws Exception {
        if (itemUuids.isEmpty()) {
            return new ArrayList<>();
        }

        String where = "SELECT i from ItemEntity i"
                + " INNER JOIN ActiveItemEntity a"
                + " ON a.itemUuid = i.itemUuid"
                + " AND a.auditUuid = i.auditUuid"
                + " AND i.itemUuid IN :itemUuids";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("itemUuids", itemUuids)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }



}
