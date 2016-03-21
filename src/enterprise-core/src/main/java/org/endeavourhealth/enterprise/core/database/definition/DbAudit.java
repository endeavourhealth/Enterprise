package org.endeavourhealth.enterprise.core.database.definition;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.*;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbAudit extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbAudit.class, "Audit", "Definition",
            "AuditUuid,EndUserUuid,TimeStamp", "AuditUuid");

    private UUID endUserUuid = null;
    private Instant timeStamp = null;

    public DbAudit() {}

    public static DbAudit factoryNow(UUID endUserUuid) {
        DbAudit ret = new DbAudit();
        ret.setPrimaryUuid(UUID.randomUUID());
        ret.setSaveMode(TableSaveMode.INSERT);
        ret.setEndUserUuid(endUserUuid);
        ret.setTimeStamp(Instant.now());
        return ret;
    }

    public static List<DbAudit> retrieveForActiveItems(List<DbActiveItem> activeItems) throws Exception {
        List<UUID> uuids = new ArrayList<>();
        for (DbActiveItem activeItem: activeItems) {
            uuids.add(activeItem.getAuditUuid());
        }
        return retrieveForUuids(uuids);
    }
    public static List<DbAudit> retrieveForUuids(List<UUID> uuids) throws Exception {
        return DatabaseManager.db().retrieveAuditsForUuids(uuids);
    }

    public static DbAudit retrieveForUuid(UUID auditUuid) throws Exception {
        return (DbAudit) DatabaseManager.db().retrieveForPrimaryKeys(adapter, auditUuid);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(endUserUuid);
        builder.add(timeStamp);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        endUserUuid = reader.readUuid();
        timeStamp = reader.readDateTime();
    }

    /**
     * gets/sets
     */
    public UUID getEndUserUuid() {
        return endUserUuid;
    }

    public void setEndUserUuid(UUID endUserUuid) {
        this.endUserUuid = endUserUuid;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }
}
