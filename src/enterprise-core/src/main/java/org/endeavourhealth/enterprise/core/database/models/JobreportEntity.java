package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "jobreport", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
public class JobreportEntity {
    private UUID jobreportuuid;
    private UUID jobuuid;
    private UUID reportuuid;
    private UUID audituuid;
    private UUID organisationuuid;
    private UUID enduseruuid;
    private String parameters;
    private short statusid;
    private Integer populationcount;

    @Id
    @Column(name = "jobreportuuid")
    public UUID getJobreportuuid() {
        return jobreportuuid;
    }

    public void setJobreportuuid(UUID jobreportuuid) {
        this.jobreportuuid = jobreportuuid;
    }

    @Basic
    @Column(name = "jobuuid")
    public UUID getJobuuid() {
        return jobuuid;
    }

    public void setJobuuid(UUID jobuuid) {
        this.jobuuid = jobuuid;
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
    @Column(name = "audituuid")
    public UUID getAudituuid() {
        return audituuid;
    }

    public void setAudituuid(UUID audituuid) {
        this.audituuid = audituuid;
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
    @Column(name = "parameters")
    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Basic
    @Column(name = "statusid")
    public short getStatusid() {
        return statusid;
    }

    public void setStatusid(short statusid) {
        this.statusid = statusid;
    }

    @Basic
    @Column(name = "populationcount")
    public Integer getPopulationcount() {
        return populationcount;
    }

    public void setPopulationcount(Integer populationcount) {
        this.populationcount = populationcount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobreportEntity that = (JobreportEntity) o;

        if (statusid != that.statusid) return false;
        if (jobreportuuid != null ? !jobreportuuid.equals(that.jobreportuuid) : that.jobreportuuid != null)
            return false;
        if (jobuuid != null ? !jobuuid.equals(that.jobuuid) : that.jobuuid != null) return false;
        if (reportuuid != null ? !reportuuid.equals(that.reportuuid) : that.reportuuid != null) return false;
        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;
        if (organisationuuid != null ? !organisationuuid.equals(that.organisationuuid) : that.organisationuuid != null)
            return false;
        if (enduseruuid != null ? !enduseruuid.equals(that.enduseruuid) : that.enduseruuid != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (populationcount != null ? !populationcount.equals(that.populationcount) : that.populationcount != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobreportuuid != null ? jobreportuuid.hashCode() : 0;
        result = 31 * result + (jobuuid != null ? jobuuid.hashCode() : 0);
        result = 31 * result + (reportuuid != null ? reportuuid.hashCode() : 0);
        result = 31 * result + (audituuid != null ? audituuid.hashCode() : 0);
        result = 31 * result + (organisationuuid != null ? organisationuuid.hashCode() : 0);
        result = 31 * result + (enduseruuid != null ? enduseruuid.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (int) statusid;
        result = 31 * result + (populationcount != null ? populationcount.hashCode() : 0);
        return result;
    }

    public static List<JobreportEntity> retrieveRecent(UUID organisationuuid, int count) throws Exception {
        String where = "SELECT j FROM JobreportEntity j INNER JOIN JobEntity a"
                + " ON a.jobuuid = j.jobuuid"
                + " WHERE j.organisationuuid = :organisationuuid"
                + " ORDER BY a.startdatetime DESC";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobreportEntity> ret = entityManager.createQuery(where, JobreportEntity.class)
                .setParameter("organisationuuid", organisationuuid)
                .setMaxResults(count)
                .getResultList();

        entityManager.close();

        return ret;
    }

    public static List<JobreportEntity> retrieveForJob(JobEntity job) throws Exception {
        return retrieveForJob(job.getJobuuid());
    }

    public static List<JobreportEntity> retrieveForJob(UUID jobuuid) throws Exception {
        String where = "FROM JobreportEntity WHERE jobuuid = :jobuuid";
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobreportEntity> ret = entityManager.createQuery(where, JobreportEntity.class)
                .setParameter("jobuuid", jobuuid)
                .getResultList();

        entityManager.close();

        return ret;
    }
    public static List<JobreportEntity> retrieveLatestForActiveItems(UUID organisationUuid, List<ActiveitemEntity> activeItems) throws Exception {

        //filter activeItems to find UUIDs of just reports
        List<UUID> itemUuids = new ArrayList<>();
        for (ActiveitemEntity activeItem: activeItems) {
            if (activeItem.getItemtypeid() == DefinitionItemType.Report.getValue()) {
                itemUuids.add(activeItem.getItemuuid());
            }
        }
        return retrieveLatestForItemUuids(organisationUuid, itemUuids);
    }
    public static List<JobreportEntity> retrieveLatestForItemUuids(UUID organisationuuid, List<UUID> itemuuids) throws Exception {
        if (itemuuids.isEmpty()) {
            return new ArrayList<JobreportEntity>();
        }

        String where = "SELECT je FROM JobreportEntity je INNER JOIN JobEntity j"
                + " ON je.jobuuid = j.jobuuid"
                + " WHERE organisationuuid = :organisationuuid"
                + " AND reportuuid IN :itemuuids"
                + " AND NOT EXISTS (SELECT 1 FROM JobreportEntity laterJobReport, JobEntity laterJob"
                + " WHERE laterJobReport.jobreportuuid = laterJob.jobuuid"
                + " AND laterJob.jobuuid = laterJobReport.jobuuid"
                + " AND laterJob.startdatetime > j.startdatetime)";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobreportEntity> ret = entityManager.createQuery(where, JobreportEntity.class)
                .setParameter("organisationuuid", organisationuuid)
                .setParameter("itemuuids", itemuuids)
                .getResultList();

        entityManager.close();

        return ret;

    }

    public static JobreportEntity retrieveForJobAndReportAndParameters(UUID jobuuid, UUID reportuuid, String parameters) throws Exception {
        String where = "FROM JobreportEntity WHERE jobuuid = :jobuuid"
                + " AND reportuuid = :reportuuid"
                + " AND parameters = :parameters";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        JobreportEntity ret = entityManager.createQuery(where, JobreportEntity.class)
                .setParameter("jobuuid", jobuuid)
                .setParameter("reportuuid", reportuuid)
                .setParameter("parameters", parameters)
                .getSingleResult();

        entityManager.close();

        return ret;

    }

    public static List<JobreportEntity> retrieveForRequests(List<RequestEntity> requests) throws Exception {
        List<UUID> uuids = new ArrayList<>();
        for (RequestEntity request: requests) {
            if (request.getJobreportuuid() != null) {
                uuids.add(request.getJobreportuuid());
            }
        }
        if (uuids.isEmpty()) {
            return new ArrayList<JobreportEntity>();
        }

        String where = "FROM JobreportEntity WHERE jobreportuuid IN :uuids";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<JobreportEntity> ret = entityManager.createQuery(where, JobreportEntity.class)
                .setParameter("uuids", uuids)
                .getResultList();

        entityManager.close();

        return ret;

    }

    public static JobreportEntity retrieveForUuid(UUID uuid) throws Exception {
        String where = "FROM JobreportEntity WHERE jobreportuuid IN :uuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        JobreportEntity ret = entityManager.createQuery(where, JobreportEntity.class)
                .setParameter("uuid", uuid)
                .getSingleResult();

        entityManager.close();

        return ret;
    }
}
