package org.endeavourhealth.discovery.core.execution;

import org.joda.time.DateTime;

import java.util.UUID;

public class ItemRequestWithAudit {
    private final int itemRequestId;
    private final UUID itemUuid;
    private final DateTime dateTime;
    private final UUID userUuid;
    private final int auditId;

    public ItemRequestWithAudit(int itemRequestId, UUID itemUuid, DateTime dateTime, UUID userUuid, int auditId) {
        this.itemRequestId = itemRequestId;
        this.itemUuid = itemUuid;
        this.dateTime = dateTime;
        this.userUuid = userUuid;
        this.auditId = auditId;
    }

    public int getItemRequestId() {
        return itemRequestId;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public int getAuditId() {
        return auditId;
    }
}
