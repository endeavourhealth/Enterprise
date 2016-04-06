package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.*;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbJobReport extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJobReport.class);

    @DatabaseColumn
    @PrimaryKeyColumn
    private UUID jobReportUuid = null;
    @DatabaseColumn
    private UUID jobUuid = null;
    @DatabaseColumn
    private UUID reportUuid = null;
    @DatabaseColumn
    private UUID auditUuid = null;
    @DatabaseColumn
    private UUID organisationUuid = null;
    @DatabaseColumn
    private UUID endUserUuid = null;
    @DatabaseColumn
    private String parameters = null;
    @DatabaseColumn
    private ExecutionStatus statusId = ExecutionStatus.Executing;
    @DatabaseColumn
    private Integer populationCount = null;

    public static List<DbJobReport> retrieveRecent(UUID organisationUuid, int count) throws Exception {
        return DatabaseManager.db().retrieveJobReports(organisationUuid, count);
    }

    public static List<DbJobReport> retrieveForJob(DbJob job) throws Exception {
        return retrieveForJob(job.getJobUuid());
    }
    public static List<DbJobReport> retrieveForJob(UUID jobUuid) throws Exception {
        return DatabaseManager.db().retrieveJobReportsForJob(jobUuid);
    }
    public static List<DbJobReport> retrieveLatestForActiveItems(UUID organisationUuid, List<DbActiveItem> activeItems) throws Exception {

        //filter activeItems to find UUIDs of just reports
        List<UUID> itemUuids = new ArrayList<>();
        for (DbActiveItem activeItem: activeItems) {
            if (activeItem.getItemTypeId() == DefinitionItemType.Report) {
                itemUuids.add(activeItem.getItemUuid());
            }
        }
        return retrieveLatestForItemUuids(organisationUuid, itemUuids);
    }
    public static List<DbJobReport> retrieveLatestForItemUuids(UUID organisationUuid, List<UUID> itemUuids) throws Exception {
        return DatabaseManager.db().retrieveLatestJobReportsForItemUuids(organisationUuid, itemUuids);
    }
    public static DbJobReport retrieveForJobAndReportAndParameters(UUID jobUuid, UUID reportUuid, String parameters) throws Exception {
        return DatabaseManager.db().retrieveJobReportForJobAndReportAndParameters(jobUuid, reportUuid, parameters);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    /**
     * gets/sets
     */
    public UUID getJobReportUuid() {
        return jobReportUuid;
    }

    public void setJobReportUuid(UUID jobReportUuid) {
        this.jobReportUuid = jobReportUuid;
    }

    public UUID getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(UUID auditUuid) {
        this.auditUuid = auditUuid;
    }

    public UUID getEndUserUuid() {
        return endUserUuid;
    }

    public void setEndUserUuid(UUID endUserUuid) {
        this.endUserUuid = endUserUuid;
    }

    public UUID getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(UUID jobUuid) {
        this.jobUuid = jobUuid;
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

    public UUID getReportUuid() {
        return reportUuid;
    }

    public void setReportUuid(UUID reportUuid) {
        this.reportUuid = reportUuid;
    }

    public ExecutionStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(ExecutionStatus statusId) {
        this.statusId = statusId;
    }

    public Integer getPopulationCount() {
        return populationCount;
    }

    public void setPopulationCount(Integer populationCount) {
        this.populationCount = populationCount;
    }
}
