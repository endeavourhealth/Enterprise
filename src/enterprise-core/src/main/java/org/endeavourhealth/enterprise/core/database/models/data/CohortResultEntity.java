package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by darren on 30/03/17.
 */
@Entity
@Table(name = "CohortResult", schema = "enterprise_data_pseudonymised", catalog = "")
public class CohortResultEntity {
    private int cohortResultId;
    private String endUserUuid;
    private Timestamp baselineDate;
    private Timestamp runDate;
    private long organisationId;
    private String queryItemUuid;
    private byte populationTypeId;
    private Integer denominatorCount;
    private Integer enumeratorCount;

    @Id
    @Column(name = "CohortResultId", nullable = false)
    public int getCohortResultId() {
        return cohortResultId;
    }

    public void setCohortResultId(int cohortResultId) {
        this.cohortResultId = cohortResultId;
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
    @Column(name = "BaselineDate", nullable = false)
    public Timestamp getBaselineDate() {
        return baselineDate;
    }

    public void setBaselineDate(Timestamp baselineDate) {
        this.baselineDate = baselineDate;
    }

    @Basic
    @Column(name = "RunDate", nullable = true)
    public Timestamp getRunDate() {
        return runDate;
    }

    public void setRunDate(Timestamp runDate) {
        this.runDate = runDate;
    }

    @Basic
    @Column(name = "OrganisationId", nullable = false)
    public long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(long organisationId) {
        this.organisationId = organisationId;
    }

    @Basic
    @Column(name = "QueryItemUuid", nullable = false, length = 36)
    public String getQueryItemUuid() {
        return queryItemUuid;
    }

    public void setQueryItemUuid(String queryItemUuid) {
        this.queryItemUuid = queryItemUuid;
    }

    @Basic
    @Column(name = "PopulationTypeId", nullable = false)
    public byte getPopulationTypeId() {
        return populationTypeId;
    }

    public void setPopulationTypeId(byte populationTypeId) {
        this.populationTypeId = populationTypeId;
    }

    @Basic
    @Column(name = "DenominatorCount", nullable = true)
    public Integer getDenominatorCount() {
        return denominatorCount;
    }

    public void setDenominatorCount(Integer denominatorCount) {
        this.denominatorCount = denominatorCount;
    }

    @Basic
    @Column(name = "EnumeratorCount", nullable = true)
    public Integer getEnumeratorCount() {
        return enumeratorCount;
    }

    public void setEnumeratorCount(Integer enumeratorCount) {
        this.enumeratorCount = enumeratorCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CohortResultEntity that = (CohortResultEntity) o;

        if (cohortResultId != that.cohortResultId) return false;
        if (organisationId != that.organisationId) return false;
        if (populationTypeId != that.populationTypeId) return false;
        if (endUserUuid != null ? !endUserUuid.equals(that.endUserUuid) : that.endUserUuid != null) return false;
        if (baselineDate != null ? !baselineDate.equals(that.baselineDate) : that.baselineDate != null) return false;
        if (runDate != null ? !runDate.equals(that.runDate) : that.runDate != null) return false;
        if (queryItemUuid != null ? !queryItemUuid.equals(that.queryItemUuid) : that.queryItemUuid != null)
            return false;
        if (denominatorCount != null ? !denominatorCount.equals(that.denominatorCount) : that.denominatorCount != null)
            return false;
        if (enumeratorCount != null ? !enumeratorCount.equals(that.enumeratorCount) : that.enumeratorCount != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cohortResultId;
        result = 31 * result + (endUserUuid != null ? endUserUuid.hashCode() : 0);
        result = 31 * result + (baselineDate != null ? baselineDate.hashCode() : 0);
        result = 31 * result + (runDate != null ? runDate.hashCode() : 0);
        result = 31 * result + (int) (organisationId ^ (organisationId >>> 32));
        result = 31 * result + (queryItemUuid != null ? queryItemUuid.hashCode() : 0);
        result = 31 * result + (int) populationTypeId;
        result = 31 * result + (denominatorCount != null ? denominatorCount.hashCode() : 0);
        result = 31 * result + (enumeratorCount != null ? enumeratorCount.hashCode() : 0);
        return result;
    }

    public static List<CohortResultEntity[]> getCohortResults(String queryItemUuid, String runDate) throws Exception {
        String where = "select r " +
                "from CohortResultEntity r where queryItemUuid = :queryItemUuid and runDate = :runDate";

        Timestamp runDate1 = null;

        try{
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //Date parsedDate = dateFormat.parse(runDate);
            runDate1 = new java.sql.Timestamp(Long.parseLong(runDate));
        }catch(Exception e){

        }

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<CohortResultEntity[]> ent = entityManager.createQuery(where)
                .setParameter("queryItemUuid", queryItemUuid)
                .setParameter("runDate", runDate1)
                .getResultList();

        entityManager.close();

        return ent;

    }

    public static List<CohortResultEntity[]> getAllCohortResults(String queryItemUuid) throws Exception {
        String where = "SELECT a from CohortResultEntity a"
                + " where cohortResultId in ( "+
                "SELECT MAX(cohortResultId) "+
                "FROM CohortResultEntity where queryItemUuid = :queryItemUuid "+
                "GROUP BY runDate "+
                ") ORDER BY runDate desc";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<CohortResultEntity[]> ent = entityManager.createQuery(where)
                .setParameter("queryItemUuid", queryItemUuid)
                .getResultList();

        entityManager.close();

        return ent;

    }
}
