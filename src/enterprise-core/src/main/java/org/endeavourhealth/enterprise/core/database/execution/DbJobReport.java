package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.ResultReader;
import org.endeavourhealth.enterprise.core.database.TableAdapter;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbJobReport extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJobReport.class, "JobReport", "Execution",
            "JobReportUuid,JobUuid,ReportUuid,AuditUuid,OrganisationUuid,EndUserUuid,Parameters,StatusId", "JobReportUuid");

    private UUID jobUuid = null;
    private UUID reportUuid = null;
    private UUID auditUuid = null;
    private UUID organisationUuid = null;
    private UUID endUserUuid = null;
    private String parameters = null;
    private ExecutionStatus statusId = ExecutionStatus.Executing;

    public static List<DbJobReport> retrieveRecent(UUID organisationUuid, int count) throws Exception {
        return DatabaseManager.db().retrieveJobReports(organisationUuid, count);
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

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(jobUuid);
        builder.add(reportUuid);
        builder.add(auditUuid);
        builder.add(organisationUuid);
        builder.add(endUserUuid);
        builder.add(parameters);
        builder.add(statusId);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        jobUuid = reader.readUuid();
        reportUuid = reader.readUuid();
        auditUuid = reader.readUuid();
        organisationUuid = reader.readUuid();
        endUserUuid = reader.readUuid();
        parameters = reader.readString();
        statusId = ExecutionStatus.get(reader.readInt());
    }

    /**
     * gets/sets
     */
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
}
