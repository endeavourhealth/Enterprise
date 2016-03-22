package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.ResultReader;
import org.endeavourhealth.enterprise.core.database.TableAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbJobReportItem extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJobReportItem.class,
            "JobReportItemUuid,JobReportUuid,ParentJobReportItemUuid,ItemUuid,AuditUuid,ResultCount", "JobReportItemUuid");

    private UUID jobReportUuid = null;
    private UUID parentJobReportItemUuid = null;
    private UUID itemUuid = null;
    private UUID auditUuid = null;
    private int resultCount = -1;


    public static List<DbJobReportItem> retrieveForJobReport(UUID jobReportUuid) throws Exception {
        return DatabaseManager.db().retrieveJobReportItemsForJobReport(jobReportUuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(jobReportUuid);
        builder.add(parentJobReportItemUuid);
        builder.add(itemUuid);
        builder.add(auditUuid);
        builder.add(resultCount);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        jobReportUuid = reader.readUuid();
        parentJobReportItemUuid = reader.readUuid();
        itemUuid = reader.readUuid();
        auditUuid = reader.readUuid();
        resultCount = reader.readInt();
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

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
