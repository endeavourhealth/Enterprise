package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "request", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
public class RequestEntity {
    private UUID requestuuid;
    private UUID reportuuid;
    private UUID organisationuuid;
    private UUID enduseruuid;
    private Timestamp timestamp;
    private String parameters;
    private UUID jobreportuuid;

    @Id
    @Column(name = "requestuuid")
    public UUID getRequestuuid() {
        return requestuuid;
    }

    public void setRequestuuid(UUID requestuuid) {
        this.requestuuid = requestuuid;
    }

    @Basic
    @Column(name = "reportuuid")
    public UUID getReportuuid() {
        return reportuuid;
    }

    public void setReportuuid(UUID reportuuid) {
        this.reportuuid = reportuuid;
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
    @Column(name = "parameters")
    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Basic
    @Column(name = "jobreportuuid")
    public UUID getJobreportuuid() {
        return jobreportuuid;
    }

    public void setJobreportuuid(UUID jobreportuuid) {
        this.jobreportuuid = jobreportuuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestEntity that = (RequestEntity) o;

        if (requestuuid != null ? !requestuuid.equals(that.requestuuid) : that.requestuuid != null) return false;
        if (reportuuid != null ? !reportuuid.equals(that.reportuuid) : that.reportuuid != null) return false;
        if (organisationuuid != null ? !organisationuuid.equals(that.organisationuuid) : that.organisationuuid != null)
            return false;
        if (enduseruuid != null ? !enduseruuid.equals(that.enduseruuid) : that.enduseruuid != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (jobreportuuid != null ? !jobreportuuid.equals(that.jobreportuuid) : that.jobreportuuid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = requestuuid != null ? requestuuid.hashCode() : 0;
        result = 31 * result + (reportuuid != null ? reportuuid.hashCode() : 0);
        result = 31 * result + (organisationuuid != null ? organisationuuid.hashCode() : 0);
        result = 31 * result + (enduseruuid != null ? enduseruuid.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (jobreportuuid != null ? jobreportuuid.hashCode() : 0);
        return result;
    }

    public static RequestEntity retrieveForUuid(UUID requestuuid) throws Exception {
        String where = "FROM RequestEntity where requestuuid = :requestuuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        RequestEntity ret = entityManager.createQuery(where, RequestEntity.class)
                .setParameter("requestuuid", requestuuid)
                .getSingleResult();

        entityManager.close();

        return ret;
    }
    public static List<RequestEntity> retrievePendingForActiveItems(UUID organisationUuid, List<ActiveitemEntity> activeItems) throws Exception {

        //filter activeItems to find UUIDs of just reports
        List<UUID> itemUuids = new ArrayList<>();
        for (ActiveitemEntity activeItem: activeItems) {
            if (activeItem.getItemtypeid() == DefinitionItemType.Report.getValue()) {
                itemUuids.add(activeItem.getItemuuid());
            }
        }
        return retrievePendingForItemUuids(organisationUuid, itemUuids);
    }

    public static List<RequestEntity> retrievePendingForItemUuids(UUID organisationuuid, List<UUID> itemuuids) throws Exception {
        if (itemuuids.isEmpty()) {
            return new ArrayList<>();
        }

        String where = "FROM RequestEntity WHERE jobreportuuid IS NULL"
                + " AND organisationuuid = :organisationuuid"
                + " AND reportuuid IN :itemuuids";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<RequestEntity> ret = entityManager.createQuery(where, RequestEntity.class)
                .setParameter("organisationuuid", organisationuuid)
                .setParameter("itemuuids", itemuuids)
                .getResultList();

        entityManager.close();

        return ret;

    }

    public static List<RequestEntity> retrieveAllPending() throws Exception {
        String where = "FROM RequestEntity WHERE jobreportuuid IS NULL";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<RequestEntity> ret = entityManager.createQuery(where, RequestEntity.class)
                .getResultList();

        entityManager.close();

        return ret;


    }
    public static List<RequestEntity> retrieveForItem(UUID organisationuuid, UUID reportuuid, int count) throws Exception {
        String where = "FROM RequestEntity WHERE organisationuuid = :organisationuuid"
                + " AND reportuuid = :reportuuid"
                + " ORDER BY timestamp DESC";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<RequestEntity> ret = entityManager.createQuery(where, RequestEntity.class)
                .setParameter("organisationuuid", organisationuuid)
                .setParameter("reportuuid", reportuuid)
                .setMaxResults(count)
                .getResultList();

        entityManager.close();

        return ret;


    }
}
