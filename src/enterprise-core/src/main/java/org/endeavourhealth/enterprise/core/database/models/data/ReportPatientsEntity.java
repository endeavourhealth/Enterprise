package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by darren on 29/03/17.
 */
@Entity
@Table(name = "ReportPatients", schema = "enterprise_data_pseudonymised", catalog = "")
public class ReportPatientsEntity {
    private int reportPatientId;
    private Timestamp runDate;
    private String queryItemUuid;
    private long organisationId;
    private long patientId;
    private String pseudoId;

    @Id
    @Column(name = "ReportPatientId", nullable = false)
    public int getReportPatientId() {
        return reportPatientId;
    }

    public void setReportPatientId(int reportPatientId) {
        this.reportPatientId = reportPatientId;
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
    @Column(name = "QueryItemUuid", nullable = false, length = 36)
    public String getQueryItemUuid() {
        return queryItemUuid;
    }

    public void setQueryItemUuid(String queryItemUuid) {
        this.queryItemUuid = queryItemUuid;
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
    @Column(name = "PatientId", nullable = false)
    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    @Basic
    @Column(name = "PseudoId", nullable = true, length = 255)
    public String getPseudoId() {
        return pseudoId;
    }

    public void setPseudoId(String pseudoId) {
        this.pseudoId = pseudoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportPatientsEntity that = (ReportPatientsEntity) o;

        if (reportPatientId != that.reportPatientId) return false;
        if (organisationId != that.organisationId) return false;
        if (patientId != that.patientId) return false;
        if (runDate != null ? !runDate.equals(that.runDate) : that.runDate != null) return false;
        if (queryItemUuid != null ? !queryItemUuid.equals(that.queryItemUuid) : that.queryItemUuid != null)
            return false;
        if (pseudoId != null ? !pseudoId.equals(that.pseudoId) : that.pseudoId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = reportPatientId;
        result = 31 * result + (runDate != null ? runDate.hashCode() : 0);
        result = 31 * result + (queryItemUuid != null ? queryItemUuid.hashCode() : 0);
        result = 31 * result + (int) (organisationId ^ (organisationId >>> 32));
        result = 31 * result + (int) (patientId ^ (patientId >>> 32));
        result = 31 * result + (pseudoId != null ? pseudoId.hashCode() : 0);
        return result;
    }

    public static List<Object[]> getReportPatients(String type, String queryItemUuid, String runDate, Short organisationId) throws Exception {
        String where = "";

        if (type.equals("EHR"))
            where = "select p.id, p.pseudoId, pg.value, p.ageYears, p.ageMonths, p.ageWeeks, p.dateOfDeath, p.postcodePrefix, p.householdId, p.lsoaCode, p.msoaCode, p.townsendScore, "+
                "o.clinicalEffectiveDate, o.snomedConceptId, o.originalCode, o.originalTerm, o.value, o.units, o.isProblem, " +
                "og.name, og.odsCode, og.typeDesc, "+
                "pr.name, pr.roleCode, pr.roleDesc "+
                "from ReportPatientsEntity r "+
                "join PatientEntity p on p.id = r.patientId "+
                "join PatientGenderEntity pg on pg.id = p.patientGenderId "+
                "join ObservationEntity o on o.patientId = r.patientId "+
                "join OrganizationEntity og on og.id = o.organizationId "+
                "join PractitionerEntity pr on pr.id = o.practitionerId "+
                "where queryItemUuid = :queryItemUuid "+
                "and runDate = :runDate and organisationId = :organisationId";
        else
            where = "select p.id, p.pseudoId, pg.value, p.ageYears, p.ageMonths, p.ageWeeks, p.dateOfDeath, p.postcodePrefix, p.householdId, p.lsoaCode, p.msoaCode, p.townsendScore, "+
                    "og.name, og.odsCode, og.typeDesc "+
                    "from ReportPatientsEntity r "+
                    "join PatientEntity p on p.id = r.patientId "+
                    "join PatientGenderEntity pg on pg.id = p.patientGenderId "+
                    "join OrganizationEntity og on og.id = p.organizationId "+
                    "where queryItemUuid = :queryItemUuid "+
                    "and runDate = :runDate and organisationId = :organisationId";

        Timestamp runDate1 = null;

        try{
            runDate1 = new java.sql.Timestamp(Long.parseLong(runDate));
        }catch(Exception e){

        }

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager2();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("queryItemUuid", queryItemUuid)
                .setParameter("runDate", runDate1)
                .setParameter("organisationId", organisationId)
                .getResultList();

        entityManager.close();

        return ent;

    }
}
