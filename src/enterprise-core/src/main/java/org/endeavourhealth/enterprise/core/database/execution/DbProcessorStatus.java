package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.ProcessorState;
import org.endeavourhealth.enterprise.core.database.*;

public final class DbProcessorStatus extends DbAbstractTable {
    private static TableAdapter adapter = new TableAdapter(DbProcessorStatus.class);

    @DatabaseColumn
    private ProcessorState stateId = null;

    public DbProcessorStatus() {}

    public static DbProcessorStatus retrieveCurrentStatus() throws Exception {
        return DatabaseManager.db().retrieveCurrentProcessorStatus();
    }
    public static void setCurrentStatus(ProcessorState state) throws Exception {
        DatabaseManager.db().deleteCurrentProcessorStatus();

        DbProcessorStatus status = new DbProcessorStatus();
        status.setStateId(state);
        status.setSaveMode(TableSaveMode.INSERT);
        status.writeToDb();
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    /**
     * gets/sets
     * @return
     */
    public ProcessorState getStateId() {
        return stateId;
    }

    public void setStateId(ProcessorState stateId) {
        this.stateId = stateId;
    }
}
