package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "ReportResult", schema = "enterprise_data_pseudonymised", catalog = "")
public class ReportResultEntity {
    private long reportResultId;
    private String endUserUuid;
    private String reportItemUuid;
    private Timestamp runDate;
    private String reportRunParams;
    private String reportOutput;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportResultId", nullable = false)
    public long getReportResultId() {
        return reportResultId;
    }

    public ReportResultEntity setReportResultId(long reportResultId) {
        this.reportResultId = reportResultId;
        return this;
    }

    @Basic
    @Column(name = "EndUserUuid", nullable = false, length = 36)
    public String getEndUserUuid() {
        return endUserUuid;
    }

    public ReportResultEntity setEndUserUuid(String endUserUuid) {
        this.endUserUuid = endUserUuid;
        return this;
    }

    @Basic
    @Column(name = "RunDate", nullable = true)
    public Timestamp getRunDate() {
        return runDate;
    }

    public ReportResultEntity setRunDate(Timestamp runDate) {
        this.runDate = runDate;
        return this;
    }

    @Basic
    @Column(name = "ReportItemUuid", nullable = false, length = 36)
    public String getReportItemUuid() {
        return reportItemUuid;
    }

    public ReportResultEntity setReportItemUuid(String reportItemUuid) {
        this.reportItemUuid = reportItemUuid;
        return this;
    }

    @Basic
    @Column(name = "ReportRunParams", nullable = false)
    public String getReportRunParams() {
        return reportRunParams;
    }

    public ReportResultEntity setReportRunParams(String reportRunParams) {
        this.reportRunParams = reportRunParams;
        return this;
    }

    @Basic
    @Column(name = "ReportOutput", nullable = false)
    public String getReportOutput() {
        return reportOutput;
    }

    public ReportResultEntity setReportOutput(String reportOutput) {
        this.reportOutput = reportOutput;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportResultEntity that = (ReportResultEntity) o;

        if (reportResultId != that.reportResultId) return false;
        if (endUserUuid != null ? !endUserUuid.equals(that.endUserUuid) : that.endUserUuid != null) return false;
        if (runDate != null ? !runDate.equals(that.runDate) : that.runDate != null) return false;
        if (reportItemUuid != null ? !reportItemUuid.equals(that.reportItemUuid) : that.reportItemUuid != null)
            return false;
        if (reportRunParams != null ? !reportRunParams.equals(that.reportRunParams) : that.reportRunParams != null) return false;
        if (reportOutput != null ? !reportOutput.equals(that.reportOutput) : that.reportOutput != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (reportResultId ^ (reportResultId >>> 32));
        result = 31 * result + (endUserUuid != null ? endUserUuid.hashCode() : 0);
        result = 31 * result + (runDate != null ? runDate.hashCode() : 0);
        result = 31 * result + (reportItemUuid != null ? reportItemUuid.hashCode() : 0);
        result = 31 * result + (reportRunParams != null ? reportRunParams.hashCode() : 0);
        result = 31 * result + (reportOutput != null ? reportOutput.hashCode() : 0);
        return result;
    }

    public static List<ReportResultEntity[]> getAllReportResults(String reportItemUuid) throws Exception {
        String where = "SELECT a.reportItemUuid,a.runDate,a.reportRunParams from ReportResultEntity a"
                + " where reportResultId in ( "+
                "SELECT MAX(reportResultId) "+
                "FROM ReportResultEntity where reportItemUuid = :reportItemUuid "+
                "GROUP BY runDate "+
                ") ORDER BY runDate desc";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<ReportResultEntity[]> ent = entityManager.createQuery(where)
                .setParameter("reportItemUuid", reportItemUuid)
                .getResultList();

        entityManager.close();

        return ent;

    }

    public static List<ReportResultEntity[]> getReportResults(String reportItemUuid, String runDate) throws Exception {
        String where = "select r " +
                "from ReportResultEntity r where reportItemUuid = :reportItemUuid and runDate = :runDate";

        Timestamp runDate1 = null;

        try{
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //Date parsedDate = dateFormat.parse(runDate);
            runDate1 = new java.sql.Timestamp(Long.parseLong(runDate));
        }catch(Exception e){

        }

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<ReportResultEntity[]> ent = entityManager.createQuery(where)
                .setParameter("reportItemUuid", reportItemUuid)
                .setParameter("runDate", runDate1)
                .getResultList();

        entityManager.close();

        return ent;

    }
}