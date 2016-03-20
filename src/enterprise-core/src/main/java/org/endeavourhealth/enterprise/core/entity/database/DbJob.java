package org.endeavourhealth.enterprise.core.entity.database;

import org.endeavourhealth.enterprise.core.entity.ExecutionStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Drew on 19/03/2016.
 */
public final class DbJob extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbJob.class, "Job", "Execution",
            "JobUuid,StatusId,StartDateTime,EndDateTime,PatientsInDatabase", "JobUuid");

    private ExecutionStatus statusId = ExecutionStatus.Executing;
    private Date startDateTime = null;
    private Date endDateTime = null;
    private int patientsInDatabase = -1;

    public static List<DbJob> retrieveRecent(int count) throws Exception {
        return DatabaseManager.db().retrieveRecentJobs(count);
    }
    public static List<DbJob> retrieveForStatus(ExecutionStatus status) throws Exception {
        return DatabaseManager.db().retrieveJobsForStatus(status);
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
    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getPatientsInDatabase() {
        return patientsInDatabase;
    }

    public void setPatientsInDatabase(int patientsInDatabase) {
        this.patientsInDatabase = patientsInDatabase;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ExecutionStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(ExecutionStatus statusId) {
        this.statusId = statusId;
    }
}
