package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.database.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbJobReportItem extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJobReportItem.class);

    @DatabaseColumn
    @PrimaryKeyColumn
    private UUID jobReportItemUuid = null;
    @DatabaseColumn
    private UUID jobReportUuid = null;
    @DatabaseColumn
    private UUID parentJobReportItemUuid = null;
    @DatabaseColumn
    private UUID itemUuid = null;
    @DatabaseColumn
    private UUID auditUuid = null;
    @DatabaseColumn
    private Integer resultCount = null;


    public static List<DbJobReportItem> retrieveForJobReport(UUID jobReportUuid) throws Exception {
        return DatabaseManager.db().retrieveJobReportItemsForJobReport(jobReportUuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    /**
     * gets/sets
     */
    public UUID getJobReportItemUuid() {
        return jobReportItemUuid;
    }

    public void setJobReportItemUuid(UUID jobReportItemUuid) {
        this.jobReportItemUuid = jobReportItemUuid;
    }

    public UUID getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(UUID auditUuid) {
        this.auditUuid = auditUuid;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public UUID getJobReportUuid() {
        return jobReportUuid;
    }

    public void setJobReportUuid(UUID jobReportUuid) {
        this.jobReportUuid = jobReportUuid;
    }

    public UUID getParentJobReportItemUuid() {
        return parentJobReportItemUuid;
    }

    public void setParentJobReportItemUuid(UUID parentJobReportItemUuid) {
        this.parentJobReportItemUuid = parentJobReportItemUuid;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }
}
