package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.TableAdapter;

import java.util.UUID;

public final class JobProcessorResult extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(JobProcessorResult.class);

    private UUID jobUuid = null;
    private UUID processorUuid = null;
    private String resultXml = null;


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
