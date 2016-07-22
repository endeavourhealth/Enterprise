package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "activeitem", schema = "\"Definition\"", catalog = "Endeavour_Enterprise")
public class ActiveitemEntity {
    private UUID activeitemuuid;
    private UUID organisationuuid;
    private UUID itemuuid;
    private UUID audituuid;
    private short itemtypeid;
    private boolean isdeleted;

    @Id
    @Column(name = "activeitemuuid")
    public UUID getActiveitemuuid() {
        return activeitemuuid;
    }

    public void setActiveitemuuid(UUID activeitemuuid) {
        this.activeitemuuid = activeitemuuid;
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
    @Column(name = "itemuuid")
    public UUID getItemuuid() {
        return itemuuid;
    }

    public void setItemuuid(UUID itemuuid) {
        this.itemuuid = itemuuid;
    }

    @Basic
    @Column(name = "audituuid")
    public UUID getAudituuid() {
        return audituuid;
    }

    public void setAudituuid(UUID audituuid) {
        this.audituuid = audituuid;
    }

    @Basic
    @Column(name = "itemtypeid")
    public short getItemtypeid() {
        return itemtypeid;
    }

    public void setItemtypeid(short itemtypeid) {
        this.itemtypeid = itemtypeid;
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

        ActiveitemEntity that = (ActiveitemEntity) o;

        if (itemtypeid != that.itemtypeid) return false;
        if (activeitemuuid != null ? !activeitemuuid.equals(that.activeitemuuid) : that.activeitemuuid != null)
            return false;
        if (organisationuuid != null ? !organisationuuid.equals(that.organisationuuid) : that.organisationuuid != null)
            return false;
        if (itemuuid != null ? !itemuuid.equals(that.itemuuid) : that.itemuuid != null) return false;
        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = activeitemuuid != null ? activeitemuuid.hashCode() : 0;
        result = 31 * result + (organisationuuid != null ? organisationuuid.hashCode() : 0);
        result = 31 * result + (itemuuid != null ? itemuuid.hashCode() : 0);
        result = 31 * result + (audituuid != null ? audituuid.hashCode() : 0);
        result = 31 * result + (int) itemtypeid;
        return result;
    }

    public static final String SELECT_QUERY_FOR_UUID =
            "from ActiveitemEntity where itemuuid = :itemuuid";

    public static final String SELECT_QUERY_FOR_COUNT =
                    "select count(1) from ActiveitemEntity a, ItemdependencyEntity d"
                    + " WHERE d.dependentitemuuid = :dependentitemuuid"
                    + " AND d.dependencytypeid = :dependencytypeid"
                    + " AND a.itemuuid = d.itemuuid"
                    + " AND a.audituuid = d.audituuid";

    public static final String SELECT_QUERY_FOR_DEPENDENT =
                    "SELECT a from ActiveitemEntity a"
                    + " INNER JOIN ItemdependencyEntity d"
                    + " ON d.dependentitemuuid = :dependentitemuuid"
                    + " AND d.dependencytypeid = :dependencytypeid"
                    + " AND d.itemuuid = a.itemuuid"
                    + " AND d.audituuid = a.audituuid"
                    + " WHERE a.organisationuuid = :organisationuuid";

    public static ActiveitemEntity retrieveForItemUuid(UUID itemUuid) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ActiveitemEntity ent = entityManager.createQuery(SELECT_QUERY_FOR_UUID, ActiveitemEntity.class).setParameter("itemuuid", itemUuid).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public List<ActiveitemEntity> retrieveActiveItemRecentItems(UUID enduseruuid, UUID organisationUuid, int count) throws Exception {
        String where = "SELECT a from ActiveitemEntity a"
                + " INNER JOIN ItemEntity i"
                + " ON i.itemuuid = a.itemuuid"
                + " AND i.audituuid = a.audituuid"
                + " AND a.isdeleted = 'false'"
                + " INNER JOIN AuditEntity ae"
                + " ON ae.audituuid = i.audituuid"
                + " AND ae.enduseruuid = :enduseruuid"
                + " WHERE a.itemtypeid NOT IN (" + DefinitionItemType.LibraryFolder.getValue() + ", " + DefinitionItemType.ReportFolder.getValue() + ")"
                + " AND a.organisationuuid = :organisationUuid"
                + " ORDER BY ae.timestamp DESC";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ActiveitemEntity> ent = entityManager.createQuery(where, ActiveitemEntity.class)
                .setParameter("enduseruuid", enduseruuid)
                .setParameter("organisationUuid", organisationUuid)
                .setMaxResults(count)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;

    }


    public static int retrieveCountDependencies(UUID dependentitemuuid, Short dependencytypeid) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        Long result = (Long)entityManager.createQuery(SELECT_QUERY_FOR_COUNT)
                .setParameter("dependentitemuuid", dependentitemuuid)
                .setParameter("dependencytypeid", dependencytypeid)
                .getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return result.intValue();
    }

    public static List<ActiveitemEntity> retrieveDependentItems(UUID organisationuuid, UUID dependentitemuuid, Short dependencytypeid) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<ActiveitemEntity> ent = entityManager.createQuery(SELECT_QUERY_FOR_DEPENDENT, ActiveitemEntity.class)
                .setParameter("dependentitemuuid", dependentitemuuid)
                .setParameter("dependencytypeid", dependencytypeid)
                .setParameter("organisationuuid", organisationuuid)
                .getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static ActiveitemEntity factoryNew(ItemEntity item, UUID organisationUuid, Short itemType) {
        UUID itemUuid = item.getItemuuid();
        UUID auditUuid = item.getAudituuid();

        if (itemUuid == null) {
            throw new RuntimeException("Cannot create ActiveItem without first saving Item to DB");
        }

        ActiveitemEntity ret = new ActiveitemEntity();
        ret.setOrganisationuuid(organisationUuid);
        ret.setItemuuid(itemUuid);
        ret.setAudituuid(auditUuid);
        ret.setItemtypeid(itemType);

        return ret;
    }

}
