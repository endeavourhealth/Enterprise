package org.endeavourhealth.enterprise.core.database.execution;

import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.ResultReader;
import org.endeavourhealth.enterprise.core.database.TableAdapter;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbJob extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJob.class,
            "JobUuid,StatusId,StartDateTime,EndDateTime,PatientsInDatabase", "JobUuid");

    private ExecutionStatus statusId = ExecutionStatus.Executing;
    private Instant startDateTime = null;
    private Instant endDateTime = null;
    private int patientsInDatabase = -1;

    public static List<DbJob> retrieveForJobReports(List<DbJobReport> jobReports) throws Exception {
        List<UUID> uuids = new ArrayList<>();
        for (DbJobReport jobReport: jobReports) {
            uuids.add(jobReport.getJobUuid());
        }
        return retrieveForUuids(uuids);
    }
    public static List<DbJob> retrieveForUuids(List<UUID> uuids) throws Exception {
        return DatabaseManager.db().retrieveJobsForUuids(uuids);
    }


    public static DbJob retrieveForUuid(UUID jobUuid) throws Exception {
        return (DbJob)DatabaseManager.db().retrieveForPrimaryKeys(adapter, jobUuid);
    }

    public static List<DbJob> retrieveRecent(int count) throws Exception {
        return DatabaseManager.db().retrieveRecentJobs(count);
    }

    public static List<DbJob> retrieveForStatus(ExecutionStatus status) throws Exception {
        return DatabaseManager.db().retrieveJobsForStatus(status);
    }

    public void markAsFinished(ExecutionStatus status) {
        setEndDateTime(Instant.now());
        setStatusId(status);
    }

    @Override
    public TableAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(statusId);
        builder.add(startDateTime);
        builder.add(endDateTime);
        builder.add(patientsInDatabase);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        statusId = ExecutionStatus.get(reader.readInt());
        startDateTime = reader.readDateTime();
        endDateTime = reader.readDateTime();
        patientsInDatabase = reader.readInt();
    }

    /**
     * gets/sets
     */
    public Instant getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Instant endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getPatientsInDatabase() {
        return patientsInDatabase;
    }

    public void setPatientsInDatabase(int patientsInDatabase) {
        this.patientsInDatabase = patientsInDatabase;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ExecutionStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(ExecutionStatus statusId) {
        this.statusId = statusId;
    }
}
