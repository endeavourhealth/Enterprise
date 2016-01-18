package org.endeavourhealth.discovery.core.execution;

import java.util.UUID;

public class ExecutionContentForInsert {
    private final UUID itemUuid;
    private final int auditId;

    public ExecutionContentForInsert(UUID itemUuid, int auditId) {
        this.itemUuid = itemUuid;
        this.auditId = auditId;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public int getAuditId() {
        return auditId;
    }
}
