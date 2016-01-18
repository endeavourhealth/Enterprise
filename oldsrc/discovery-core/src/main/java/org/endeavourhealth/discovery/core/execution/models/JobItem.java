package org.endeavourhealth.discovery.core.execution.models;

import java.util.UUID;

public class JobItem {
    private int jobItemId;
    private UUID jobUuid;
    private UUID reportItemUuid;
    private int auditId;
    private UUID userUuid;
    private UUID organisationUuid;
    private String parameters;

    public int getJobItemId() {
        return jobItemId;
    }

    public void setJobItemId(int jobItemId) {
        this.jobItemId = jobItemId;
    }

    public UUID getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(UUID jobUuid) {
        this.jobUuid = jobUuid;
    }

    public UUID getReportItemUuid() {
        return reportItemUuid;
    }

    public void setReportItemUuid(UUID reportItemUuid) {
        this.reportItemUuid = reportItemUuid;
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public UUID getOrganisationUuid() {
        return organisationUuid;
    }

    public void setOrganisationUuid(UUID organisationUuid) {
        this.organisationUuid = organisationUuid;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
