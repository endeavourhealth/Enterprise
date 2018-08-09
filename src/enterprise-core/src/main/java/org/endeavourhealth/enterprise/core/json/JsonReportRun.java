package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonReportRun {

	private String organisationGroup = null;
	private String population = null;
	private String cohortName = null;
	private String baselineCohortId = null;
	private String baselineDate = null;
	private String reportItemUuid = null;
	private Boolean scheduled = false;
	private Date scheduleDateTime = null;

	public JsonReportRun() {
	}

	/**
	 * gets/sets
	 */
	public String getOrganisationGroup() {
		return organisationGroup;
	}

	public void setOrganisationGroup(String organisationGroup) {
		this.organisationGroup = organisationGroup;
	}

	public String getPopulation() {
		return population;
	}

	public void setPopulation(String population) {
		this.population = population;
	}

	public String getBaselineDate() {
		return baselineDate;
	}

	public void setBaselineDate(String baselineDate) {
		this.baselineDate = baselineDate;
	}

	public String getCohortName() {
		return cohortName;
	}

	public void setCohortName(String cohortName) {
		this.cohortName = cohortName;
	}

	public String getReportItemUuid() {
		return reportItemUuid;
	}

	public void setReportItemUuid(String queryItemUuid) {
		this.reportItemUuid = queryItemUuid;
	}


	public Date getScheduleDateTime() {
		return scheduleDateTime;
	}

	public void setScheduleDateTime(Date scheduleDateTime) {
		this.scheduleDateTime = scheduleDateTime;
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	public void setScheduled(Boolean scheduled) {
		this.scheduled = scheduled;
	}

	public String getBaselineCohortId() {
		return baselineCohortId;
	}
}
