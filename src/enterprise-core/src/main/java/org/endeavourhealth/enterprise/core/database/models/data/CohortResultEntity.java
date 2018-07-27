package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by darren on 30/03/17.
 */
@Entity
@Table(name = "CohortResult", schema = "enterprise_data_pseudonymised", catalog = "")
public class CohortResultEntity {
    private static final Logger LOG = LoggerFactory.getLogger(CohortResultEntity.class);

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


    public static List<OrganizationEntity> getCohortReportOrganisations(String queryItemUuid) throws Exception {
        String where = "SELECT DISTINCT o " +
            "from OrganizationEntity o " +
            "join CohortResultEntity a on a.organisationId = o.id " +
            "where a.queryItemUuid = :queryItemUuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<OrganizationEntity> ent = entityManager.createQuery(where)
            .setParameter("queryItemUuid", queryItemUuid)
            .getResultList();

        entityManager.close();

        return ent;

    }

    public static List getReportData(String queryItemUuid, List<Long> orgIds) {
        String where = "SELECT a.runDate, cast((a.enumeratorCount * 100) /a.denominatorCount as float) as pct, a.organisationId " +
            "from CohortResultEntity a " +
            "where a.queryItemUuid = :queryItemUuid " +
            "and a.organisationId in :orgIds " +
            "order by a.runDate, a.organisationId ";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List ent = entityManager.createQuery(where)
            .setParameter("queryItemUuid", queryItemUuid)
            .setParameter("orgIds", orgIds)
            .getResultList();

        entityManager.close();

        return ent;
    }

    public static String getFrailty(String pseudoId) {

        // setup snomed frailty concepts

        //frailty indexes
        Long cshaFrailtyScale = Long.parseLong("445414007");
        Long fiFrailtyIndex = Long.parseLong("713634000");
        Long fiFrailtyIndex2 = Long.parseLong("713636003"); //Emis now use this Snomed code as of Jul 2018

        //severe frailty
        Long severeFrailty = Long.parseLong("925861000000102");
        Long qFrailtySevereFrailty = Long.parseLong("2010301000006105");
        Long qFrailtySevereFrailtyEstimate = Long.parseLong("2010341000006107"); //QFrailty category - severe frailty (estimate)

        //moderate frailty
        Long moderateFrailty = Long.parseLong("925831000000107");
        Long onFrailtyRegister = Long.parseLong("1839051000006106");
        Long frailPerson = Long.parseLong("910301000006104");
        Long frailty = Long.parseLong("248279007");
        Long frailElderlyPeople1 = Long.parseLong("908431000006104");
        Long frailElderlyPeople2 = Long.parseLong("909011000006109");
        Long qFrailtyModerateFrailty = Long.parseLong("2010291000006109");
        Long qFrailtyModerateFrailtyEstimate = Long.parseLong("2010331000006102"); //QFrailty category - moderate frailty (estimate)

        //mild frailty
        Long mildFrailty = Long.parseLong("925791000000100");
        Long qFrailtyMildFrailty = Long.parseLong("2010281000006106"); //QFrailty category - mild frailty
        Long qFrailtyMildFrailtyEstimate = Long.parseLong("2010321000006100"); //QFrailty category - mild frailty (estimate)

        String severeFrailtyQuery = "SELECT p FROM PatientEntity p " +
                "join ObservationEntity o on o.patientId = p.id " +
                "where p.pseudoId = :pseudoId " +
                "and (" +
                "(o.snomedConceptId = :fiFrailtyIndex and o.resultValue > '0.36') " +
                "or " +
                "(o.snomedConceptId = :fiFrailtyIndex2 and o.resultValue > '0.36') " +
                "or " +
                "(o.snomedConceptId = :cshaFrailtyScale and o.resultValue >= '7') " +
                "or " +
                "(o.snomedConceptId = :severeFrailty or o.snomedConceptId = :qFrailtySevereFrailty or o.snomedConceptId = :qFrailtySevereFrailtyEstimate))";

        String moderateFrailtyQuery = "SELECT p FROM PatientEntity p " +
                "join ObservationEntity o on o.patientId = p.id " +
                "where p.pseudoId = :pseudoId " +
                "and (" +
                "(o.snomedConceptId = :fiFrailtyIndex and o.resultValue > '0.24' and o.resultValue <= '0.36') " +
                "or " +
                "(o.snomedConceptId = :fiFrailtyIndex2 and o.resultValue > '0.24' and o.resultValue <= '0.36') " +
                "or " +
                "(o.snomedConceptId = :cshaFrailtyScale and o.resultValue >= '6' and o.resultValue < '7') " +
                "or " +
                "(o.snomedConceptId = :moderateFrailty or o.snomedConceptId = :onFrailtyRegister or o.snomedConceptId = :frailPerson or o.snomedConceptId = :frailty or o.snomedConceptId = :frailElderlyPeople1 or o.snomedConceptId = :frailElderlyPeople2 or o.snomedConceptId = :qFrailtyModerateFrailty or o.snomedConceptId = :qFrailtyModerateFrailtyEstimate))";

        String mildFrailtyQuery = "SELECT p FROM PatientEntity p " +
                "join ObservationEntity o on o.patientId = p.id " +
                "where p.pseudoId = :pseudoId " +
                "and (" +
                "(o.snomedConceptId = :fiFrailtyIndex and o.resultValue > '0.13' and o.resultValue <= '0.24') " +
                "or " +
                "(o.snomedConceptId = :fiFrailtyIndex2 and o.resultValue > '0.13' and o.resultValue <= '0.24') " +
                "or " +
                "(o.snomedConceptId = :cshaFrailtyScale and o.resultValue >= 5 and o.resultValue < 6) " +
                "or " +
                "(o.snomedConceptId = :mildFrailty or o.snomedConceptId = :qFrailtyMildFrailty or o.snomedConceptId = :qFrailtyMildFrailtyEstimate))";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        Query query = entityManager.createQuery(severeFrailtyQuery)
                .setParameter("pseudoId", pseudoId)
                .setParameter("fiFrailtyIndex", fiFrailtyIndex)
                .setParameter("fiFrailtyIndex2", fiFrailtyIndex2)
                .setParameter("cshaFrailtyScale", cshaFrailtyScale)
                .setParameter("severeFrailty", severeFrailty)
                .setParameter("qFrailtySevereFrailty", qFrailtySevereFrailty)
                .setParameter("qFrailtySevereFrailtyEstimate", qFrailtySevereFrailtyEstimate);
        LOG.debug("Severe SQL = " + query);
        List ent = query.getResultList();

        String frailtyCategory = "NONE";

        if (!ent.isEmpty())
            frailtyCategory = "SEVERE"; // patient has severe frailty
        else {

            query = entityManager.createQuery(moderateFrailtyQuery)
                    .setParameter("pseudoId", pseudoId)
                    .setParameter("fiFrailtyIndex", fiFrailtyIndex)
                    .setParameter("fiFrailtyIndex2", fiFrailtyIndex)
                    .setParameter("cshaFrailtyScale", cshaFrailtyScale)
                    .setParameter("moderateFrailty", moderateFrailty)
                    .setParameter("onFrailtyRegister", onFrailtyRegister)
                    .setParameter("frailPerson", frailPerson)
                    .setParameter("frailty", frailty)
                    .setParameter("frailElderlyPeople1", frailElderlyPeople1)
                    .setParameter("frailElderlyPeople2", frailElderlyPeople2)
                    .setParameter("qFrailtyModerateFrailty", qFrailtyModerateFrailty)
                    .setParameter("qFrailtyModerateFrailtyEstimate", qFrailtyModerateFrailtyEstimate);
            LOG.debug("Moderate SQL = " + query);
            ent = query.getResultList();

            if (!ent.isEmpty())
                frailtyCategory = "MODERATE"; // patient has moderate frailty
            else {
                query = entityManager.createQuery(mildFrailtyQuery)
                        .setParameter("pseudoId", pseudoId)
                        .setParameter("fiFrailtyIndex", fiFrailtyIndex)
                        .setParameter("fiFrailtyIndex2", fiFrailtyIndex)
                        .setParameter("cshaFrailtyScale", cshaFrailtyScale)
                        .setParameter("mildFrailty", mildFrailty)
                        .setParameter("qFrailtyMildFrailty", qFrailtyMildFrailty)
                        .setParameter("qFrailtyMildFrailtyEstimate", qFrailtyMildFrailtyEstimate);
                LOG.debug("Mild SQL = " + mildFrailtyQuery);
                ent = query.getResultList();

                if (!ent.isEmpty())
                    frailtyCategory = "MILD"; // patient has mild frailty
            }
        }

        entityManager.close();

        return frailtyCategory;
    }
}
