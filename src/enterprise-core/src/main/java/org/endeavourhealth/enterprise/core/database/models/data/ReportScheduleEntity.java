package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ReportSchedule", schema = "enterprise_data_pseudonymised", catalog = "")
public class ReportScheduleEntity {
	private long reportScheduleId;
	private Timestamp scheduledAt;
	private String endUserUuid;
	private String reportItemUuid;
	private String reportRunParams;
	private Long reportResultId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ReportScheduleId", nullable = false)
	public long getReportScheduleId() {
		return reportScheduleId;
	}

	public ReportScheduleEntity setReportScheduleId(long reportScheduleId) {
		this.reportScheduleId = reportScheduleId;
		return this;
	}


	@Basic
	@Column(name = "ScheduledAt", nullable = false)
	public Timestamp getScheduledAt() {
		return scheduledAt;
	}

	public ReportScheduleEntity setScheduledAt(Timestamp scheduledAt) {
		this.scheduledAt = scheduledAt;
		return this;
	}

	@Basic
	@Column(name = "EndUserUuid", nullable = false, length = 36)
	public String getEndUserUuid() {
		return endUserUuid;
	}

	public ReportScheduleEntity setEndUserUuid(String endUserUuid) {
		this.endUserUuid = endUserUuid;
		return this;
	}

	@Basic
	@Column(name = "ReportItemUuid", nullable = false, length = 36)
	public String getReportItemUuid() {
		return reportItemUuid;
	}

	public ReportScheduleEntity setReportItemUuid(String reportItemUuid) {
		this.reportItemUuid = reportItemUuid;
		return this;
	}

	@Basic
	@Column(name = "ReportRunParams", nullable = false)
	public String getReportRunParams() {
		return reportRunParams;
	}

	public ReportScheduleEntity setReportRunParams(String reportRunParams) {
		this.reportRunParams = reportRunParams;
		return this;
	}


	@Basic
	@Column(name = "ReportResultId", nullable = true)
	public Long getReportResultId() {
		return reportResultId;
	}

	public ReportScheduleEntity setReportResultId(Long reportResultId) {
		this.reportResultId = reportResultId;
		return this;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReportScheduleEntity that = (ReportScheduleEntity) o;

		if (reportScheduleId != that.reportScheduleId) return false;
		if (scheduledAt != that.scheduledAt) return false;
		if (endUserUuid != null ? !endUserUuid.equals(that.endUserUuid) : that.endUserUuid != null) return false;
		if (reportItemUuid != null ? !reportItemUuid.equals(that.reportItemUuid) : that.reportItemUuid != null) return false;
		if (!reportRunParams.equals(that.reportRunParams)) return false;
		if (reportResultId != null ? !reportResultId.equals(that.reportResultId) : that.reportResultId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result =  (int) (reportScheduleId ^ (reportScheduleId >>> 32));
		result = 31 * result + (scheduledAt != null ? scheduledAt.hashCode() : 0);
		result = 31 * result + (endUserUuid != null ? endUserUuid.hashCode() : 0);
		result = 31 * result + (reportItemUuid != null ? reportItemUuid.hashCode() : 0);
		result = 31 * result + (reportRunParams != null ? reportRunParams.hashCode() : 0);
		result = 31 * result + (int) (reportResultId ^ (reportResultId >>> 32));
		return result;
	}
}