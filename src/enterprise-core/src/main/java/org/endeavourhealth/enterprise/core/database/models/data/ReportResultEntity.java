package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ReportResult", schema = "enterprise_data_pseudonymised", catalog = "")
public class ReportResultEntity {
	private int reportResultId;
	private String endUserUuid;
	private String reportItemUuid;
	private Timestamp runDate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ReportResultId", nullable = false)
	public int getReportResultId() {
		return reportResultId;
	}

	public ReportResultEntity setReportResultId(int reportResultId) {
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

		return true;
	}

	@Override
	public int hashCode() {
		int result = reportResultId;
		result = 31 * result + (endUserUuid != null ? endUserUuid.hashCode() : 0);
		result = 31 * result + (runDate != null ? runDate.hashCode() : 0);
		result = 31 * result + (reportItemUuid != null ? reportItemUuid.hashCode() : 0);
		return result;
	}
}