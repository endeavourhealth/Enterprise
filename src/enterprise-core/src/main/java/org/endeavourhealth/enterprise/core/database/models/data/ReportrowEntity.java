package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by darren on 06/02/2018.
 */
@Entity
@Table(name = "reportrow", schema = "enterprise_data_pseudonymised", catalog = "")
public class ReportrowEntity {
    private long reportRowId;
    private long reportResultId;
    private long patientId;
    private long organisationId;
    private String label;
    private Date clinicalEffectiveDate;
    private String originalTerm;
    private String originalCode;
    private Long snomedConceptId;
    private Double value;
    private String units;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportRowId", nullable = false)
    public long getReportRowId() {
        return reportRowId;
    }

    public ReportrowEntity setReportRowId(long reportRowId) {
        this.reportRowId = reportRowId;
        return this;
    }

    @Basic
    @Column(name = "ReportResultId", nullable = false)
    public long getReportResultId() {
        return reportResultId;
    }

    public ReportrowEntity setReportResultId(long reportResultId) {
        this.reportResultId = reportResultId;
        return this;
    }

    @Basic
    @Column(name = "PatientId", nullable = false)
    public long getPatientId() {
        return patientId;
    }

    public ReportrowEntity setPatientId(long patientId) {
        this.patientId = patientId;
        return this;
    }

    @Basic
    @Column(name = "OrganisationId", nullable = false)
    public long getOrganisationId() {
        return organisationId;
    }

    public ReportrowEntity setOrganisationId(long organisationId) {
        this.organisationId = organisationId;
        return this;
    }

    @Basic
    @Column(name = "Label", nullable = true, length = 250)
    public String getLabel() {
        return label;
    }

    public ReportrowEntity setLabel(String label) {
        this.label = label;
        return this;
    }

    @Basic
    @Column(name = "ClinicalEffectiveDate", nullable = true)
    public Date getClinicalEffectiveDate() {
        return clinicalEffectiveDate;
    }

    public ReportrowEntity setClinicalEffectiveDate(Date clinicalEffectiveDate) {
        this.clinicalEffectiveDate = clinicalEffectiveDate;
        return this;
    }

    @Basic
    @Column(name = "OriginalTerm", nullable = true, length = 1000)
    public String getOriginalTerm() {
        return originalTerm;
    }

    public ReportrowEntity setOriginalTerm(String originalTerm) {
        this.originalTerm = originalTerm;
        return this;
    }

    @Basic
    @Column(name = "OriginalCode", nullable = true, length = 20)
    public String getOriginalCode() {
        return originalCode;
    }

    public ReportrowEntity setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
        return this;
    }

    @Basic
    @Column(name = "SnomedConceptId", nullable = true)
    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public ReportrowEntity setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
        return this;
    }

    @Basic
    @Column(name = "Value", nullable = true, precision = 0)
    public Double getValue() {
        return value;
    }

    public ReportrowEntity setValue(Double value) {
        this.value = value;
        return this;
    }

    @Basic
    @Column(name = "Units", nullable = true, length = 50)
    public String getUnits() {
        return units;
    }

    public ReportrowEntity setUnits(String units) {
        this.units = units;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportrowEntity that = (ReportrowEntity) o;

        if (reportRowId != that.reportRowId) return false;
        if (reportResultId != that.reportResultId) return false;
        if (patientId != that.patientId) return false;
        if (organisationId != that.organisationId) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (clinicalEffectiveDate != null ? !clinicalEffectiveDate.equals(that.clinicalEffectiveDate) : that.clinicalEffectiveDate != null)
            return false;
        if (originalTerm != null ? !originalTerm.equals(that.originalTerm) : that.originalTerm != null) return false;
        if (originalCode != null ? !originalCode.equals(that.originalCode) : that.originalCode != null) return false;
        if (snomedConceptId != null ? !snomedConceptId.equals(that.snomedConceptId) : that.snomedConceptId != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (units != null ? !units.equals(that.units) : that.units != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (reportRowId ^ (reportRowId >>> 32));
        result = (int) (reportResultId ^ (reportResultId >>> 32));
        result = 31 * result + (int) (patientId ^ (patientId >>> 32));
        result = 31 * result + (int) (organisationId ^ (organisationId >>> 32));
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (clinicalEffectiveDate != null ? clinicalEffectiveDate.hashCode() : 0);
        result = 31 * result + (originalTerm != null ? originalTerm.hashCode() : 0);
        result = 31 * result + (originalCode != null ? originalCode.hashCode() : 0);
        result = 31 * result + (snomedConceptId != null ? snomedConceptId.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (units != null ? units.hashCode() : 0);
        return result;
    }
}
