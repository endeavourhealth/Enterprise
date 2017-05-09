package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ReportResultOrganisationEntityKey implements Serializable {

	private int reportResultId;
	private long organisationId;

	@Basic
	@Column(name = "ReportResultId", nullable = false)
	public int getReportResultId() {
		return reportResultId;
	}

	public ReportResultOrganisationEntityKey setReportResultId(int reportResultId) {
		this.reportResultId = reportResultId;
		return this;
	}

	@Basic
	@Column(name = "OrganisationId", nullable = false)
	public long getOrganisationId() {
		return organisationId;
	}

	public ReportResultOrganisationEntityKey setOrganisationId(long organisationId) {
		this.organisationId = organisationId;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReportResultOrganisationEntityKey that = (ReportResultOrganisationEntityKey) o;

		if (reportResultId != that.reportResultId) return false;
		if (organisationId != that.organisationId) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = reportResultId;
		result = 31 * result + (int) (organisationId ^ (organisationId >>> 32));
		return result;
	}
}
