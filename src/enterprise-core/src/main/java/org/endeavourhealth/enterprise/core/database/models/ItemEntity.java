package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "item", schema = "\"Definition\"", catalog = "Endeavour_Enterprise")
@IdClass(ItemEntityPK.class)
public class ItemEntity {
    private UUID itemuuid;
    private UUID audituuid;
    private String xmlcontent;
    private String title;
    private String description;
    private boolean isdeleted;

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

    @Basic
    @Column(name = "xmlcontent")
    public String getXmlcontent() {
        return xmlcontent;
    }

    public void setXmlcontent(String xmlcontent) {
        this.xmlcontent = xmlcontent;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(boolean isdeleted) {
        this.isdeleted = isdeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemEntity that = (ItemEntity) o;

        if (itemuuid != null ? !itemuuid.equals(that.itemuuid) : that.itemuuid != null) return false;
        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;
        if (xmlcontent != null ? !xmlcontent.equals(that.xmlcontent) : that.xmlcontent != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemuuid != null ? itemuuid.hashCode() : 0;
        result = 31 * result + (audituuid != null ? audituuid.hashCode() : 0);
        result = 31 * result + (xmlcontent != null ? xmlcontent.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public static ItemEntity factoryNew(String title, AuditEntity audit) {
        ItemEntity ret = new ItemEntity();
        ret.setAudituuid(audit.getAudituuid());
        ret.setTitle(title);
        return ret;
    }

    public static ItemEntity retrieveLatestForUUid(UUID itemuuid) throws Exception {
        String where = "SELECT e from ItemEntity e INNER JOIN ActiveitemEntity a"
                + " ON a.itemuuid = e.itemuuid"
                + " AND a.audituuid = e.audituuid"
                + " WHERE a.itemuuid = :itemuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ItemEntity ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("itemuuid", itemuuid)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;

    }

    public static ItemEntity retrieveForUuidAndAudit(UUID itemuuid, UUID audituuid) throws Exception {
        String where = "from ItemEntity"
                + " WHERE audituuid = :audituuid"
                + " AND itemuuid = :itemuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ItemEntity ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("audituuid", audituuid)
                .setParameter("itemuuid", itemuuid)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static ItemEntity retrieveForActiveItem(ActiveitemEntity activeItem) throws Exception {
        String where = "from ItemEntity"
                + " WHERE audituuid = :audituuid"
                + " AND itemuuid = :itemuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ItemEntity ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("audituuid", activeItem.getAudituuid())
                .setParameter("itemuuid", activeItem.getItemuuid())
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }


    public static List<ItemEntity> retrieveDependentItems(UUID dependentitemuuid, Short dependencytypeid) throws Exception {
        String where = "SELECT i from ItemEntity i"
                + " INNER JOIN ActiveitemEntity a"
                + " ON a.itemuuid = i.itemuuid"
                + " AND a.audituuid = i.audituuid"
                + " AND a.isdeleted = 'false'"
                + " INNER JOIN ItemdependencyEntity d"
                + " ON d.itemuuid = i.itemuuid"
                + " AND a.audituuid = i.audituuid"
                + " AND d.dependentitemuuid = :dependentitemuuid"
                + " AND d.dependencytypeid = :dependencytypeid"
                + " INNER JOIN ActiveitemEntity ad"
                + " ON ad.itemuuid = d.itemuuid"
                + " AND ad.audituuid = d.audituuid"
                + " AND ad.isdeleted = 'false'";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("dependentitemuuid", dependentitemuuid)
                .setParameter("dependencytypeid", dependencytypeid)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;

    }

    public static List<ItemEntity> retrieveNonDependentItems(UUID organisationuuid, Short dependencytypeid, Short itemtypeid) throws Exception {
        String where = "SELECT i from ItemEntity i"
                + " INNER JOIN ActiveitemEntity a"
                + " ON a.itemuuid = i.itemuuid"
                + " AND a.audituuid = i.audituuid"
                + " AND a.itemtypeid = :itemtypeid"
                + " AND a.organisationuuid = :organisationuuid"
                + " AND a.isdeleted = 'false'"
                + " WHERE NOT EXISTS ("
                + "SELECT 1 FROM ItemdependencyEntity d"
                + " WHERE d.itemuuid = i.itemuuid"
                + " AND d.audituuid = i.audituuid"
                + " AND d.dependencytypeid = :dependencytypeid"
                + ")";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("itemtypeid", itemtypeid)
                .setParameter("organisationuuid", organisationuuid)
                .setParameter("dependencytypeid", dependencytypeid)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemEntity> retrieveForActiveItems(List<ActiveitemEntity> activeItems) throws Exception {
        if (activeItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> parameters = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("from ItemEntity WHERE ");

        for (int i=0; i<activeItems.size(); i++) {
            ActiveitemEntity activeItem = activeItems.get(i);
            UUID itemUuid = activeItem.getItemuuid();
            UUID auditUuid = activeItem.getAudituuid();

            if (i > 0){
                sb.append(" OR ");
            }
            sb.append("(itemuuid = '"+itemUuid+"' AND audituuid = '"+auditUuid+"')");

        }
        String where = sb.toString();

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> ent = entityManager.createQuery(where, ItemEntity.class)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<ItemEntity> retrieveLatestForUuids(List<UUID> itemUuids) throws Exception {
        if (itemUuids.isEmpty()) {
            return new ArrayList<>();
        }

        String where = "SELECT i from ItemEntity i"
                + " INNER JOIN ActiveitemEntity a"
                + " ON a.itemuuid = i.itemuuid"
                + " AND a.audituuid = i.audituuid"
                + " AND i.itemuuid IN :itemUuids";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> ent = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("itemUuids", itemUuids)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<LibraryItem> retrieveLibraryItemsForJob(UUID jobuuid) throws Exception {

        String where = "SELECT i from ItemEntity i"
                + " INNER JOIN JobcontentEntity c"
                + " ON c.itemuuid = i.itemuuid"
                + " AND c.audituuid = i.audituuid"
                + " AND c.jobuuid = :jobuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ItemEntity> sourceItems = entityManager.createQuery(where, ItemEntity.class)
                .setParameter("jobuuid", jobuuid)
                .getResultList();

        entityManager.close();

        List<LibraryItem> libraryItems = new ArrayList<>();

        for (ItemEntity item: sourceItems) {

            UUID itemUuid = item.getItemuuid();
            String xml = item.getXmlcontent();
            LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(xml);

            UUID libraryItemUuid = UUID.fromString(libraryItem.getUuid());

            if (!itemUuid.equals(libraryItemUuid))
                throw new Exception("Database item UUID does not match LibraryItem content: " + itemUuid);

            libraryItems.add(libraryItem);
        }

        return libraryItems;
    }
}
