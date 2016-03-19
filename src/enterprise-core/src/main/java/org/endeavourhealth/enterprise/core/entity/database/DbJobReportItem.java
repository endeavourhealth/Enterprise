package org.endeavourhealth.enterprise.core.entity.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Drew on 19/03/2016.
 */
public final class DbJobReportItem extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbOrganisation.class, "JobReportItem", "Execution",
            "JobReportItemUuid,JobReportUuid,QueryUuid,ResultCount", "JobReportItemUuid");

    private UUID jobReportUuid = null;
    private UUID queryUuid = null;
    private int resultCount = -1;

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(jobReportUuid);
        builder.add(queryUuid);
        builder.add(resultCount);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        jobReportUuid = reader.readUuid();
        queryUuid = reader.readUuid();
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

    public UUID getQueryUuid() {
        return queryUuid;
    }

    public void setQueryUuid(UUID queryUuid) {
        this.queryUuid = queryUuid;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
