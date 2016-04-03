package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.database.*;

import java.util.List;
import java.util.UUID;

public final class DbJobProcessorResult extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJobProcessorResult.class);

    @DatabaseColumn
    @PrimaryKeyColumn
    private UUID jobUuid = null;
    @DatabaseColumn
    @PrimaryKeyColumn
    private UUID processorUuid = null;
    @DatabaseColumn
    private String resultXml = null;

    public static List<DbJobProcessorResult> retrieveForJob(DbJob job) throws Exception {
        return retrieveForJob(job.getJobUuid());
    }
    public static List<DbJobProcessorResult> retrieveForJob(UUID jobUuid) throws Exception {
        return DatabaseManager.db().retrieveJobProcessorResultsForJob(jobUuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    /**
     * gets/sets
     */
    public UUID getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(UUID jobUuid) {
        this.jobUuid = jobUuid;
    }

    public UUID getProcessorUuid() {
        return processorUuid;
    }

    public void setProcessorUuid(UUID processorUuid) {
        this.processorUuid = processorUuid;
    }

    public String getResultXml() {
        return resultXml;
    }

    public void setResultXml(String resultXml) {
        this.resultXml = resultXml;
    }

}
