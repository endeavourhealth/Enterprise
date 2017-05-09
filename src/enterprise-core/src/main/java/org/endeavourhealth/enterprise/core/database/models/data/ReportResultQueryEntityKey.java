package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ReportResultQueryEntityKey implements Serializable {
	private int reportResultId;
	private String queryItemUuid;

	@Basic
	@Column(name = "ReportResultId", nullable = false)
	public int getReportResultId() {
		return reportResultId;
	}

	public ReportResultQueryEntityKey setReportResultId(int reportResultId) {
		this.reportResultId = reportResultId;
		return this;
	}

	@Basic
	@Column(name = "QueryItemUuid", nullable = false, length = 36)
	public String getQueryItemUuid() {
		return queryItemUuid;
	}

	public ReportResultQueryEntityKey setQueryItemUuid(String queryItemUuid) {
		this.queryItemUuid = queryItemUuid;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReportResultQueryEntityKey that = (ReportResultQueryEntityKey) o;

		if (reportResultId != that.reportResultId) return false;
		if (queryItemUuid != null ? !queryItemUuid.equals(that.queryItemUuid) : that.queryItemUuid != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = reportResultId;
		result = 31 * result + (queryItemUuid != null ? queryItemUuid.hashCode() : 0);
		return result;
	}
}
