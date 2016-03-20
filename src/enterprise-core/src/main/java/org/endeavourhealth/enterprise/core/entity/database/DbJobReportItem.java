package org.endeavourhealth.enterprise.core.entity.database;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 19/03/2016.
 */
public final class DbJobReportItem extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJobReportItem.class, "JobReportItem", "Execution",
            "JobReportItemUuid,JobReportUuid,ItemUuid,ResultCount", "JobReportItemUuid");

    private UUID jobReportUuid = null;
    private UUID itemUuid = null;
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
        builder.add(itemUuid);
        builder.add(resultCount);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        jobReportUuid = reader.readUuid();
        itemUuid = reader.readUuid();
        resultCount = reader.readInt();
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

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
