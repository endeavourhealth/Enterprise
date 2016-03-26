package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.database.DatabaseColumn;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.PrimaryKeyColumn;
import org.endeavourhealth.enterprise.core.database.TableAdapter;

import java.util.UUID;

public final class DbJobContent extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJobContent.class);

    @DatabaseColumn
    @PrimaryKeyColumn
    private UUID jobUuid = null;
    @PrimaryKeyColumn
    @DatabaseColumn
    private UUID itemUuid = null;
    @DatabaseColumn
    private UUID auditUuid = null;

    @Override
    public TableAdapter getAdapter() {
        return adapter;
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

    public UUID getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(UUID jobUuid) {
        this.jobUuid = jobUuid;
    }
}
