package org.endeavourhealth.enterprise.core.entity.database;

import org.endeavourhealth.enterprise.core.entity.DefinitionItemType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 19/03/2016.
 */
public final class DbRequest extends DbAbstractTable {

    private static final TableAdapter adapter = new TableAdapter(DbOrganisation.class, "Request", "Execution",
            "RequestUuid,ReportItemUuid,OrganisationUuid,EndUserUuid,TimeStamp,Parameters,JobUuid", "RequestUuid");

    private UUID reportUuid = null;
    private UUID organisationUuuid = null;
    private UUID endUserUuid = null;
    private Date timeStamp = null;
    private String parameters = null;
    private UUID jobUuid = null;

    public static List<DbRequest> retrievePendingForActiveItems(UUID organisationUuid, List<DbActiveItem> activeItems) throws Exception {
        List<UUID> itemUuids = new ArrayList<>();
        for (DbActiveItem activeItem: activeItems) {
            if (activeItem.getItemTypeId() == DefinitionItemType.Report) {
                itemUuids.add(activeItem.getItemUuid());
            }
        }
        return retrievePendingForItemUuids(organisationUuid, itemUuids);
    }
    public static List<DbRequest> retrievePendingForItemUuids(UUID organisationUuid, List<UUID> itemUuids) throws Exception {
        return DatabaseManager.db().retrievePendingRequestsForItems(organisationUuid, itemUuids);
    }
    public static List<DbRequest> retrieveAllPending() throws Exception {
        return DatabaseManager.db().retrievePendingRequests();
    }

    @Override
    public TableAdapter getAdapter() {
        return null;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder) {
        builder.add(getPrimaryUuid());
        builder.add(reportUuid);
        builder.add(organisationUuuid);
        builder.add(endUserUuid);
        builder.add(timeStamp);
        builder.add(parameters);
        builder.add(jobUuid);
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException {
        setPrimaryUuid(reader.readUuid());
        reportUuid = reader.readUuid();
        organisationUuuid = reader.readUuid();
        endUserUuid = reader.readUuid();
        timeStamp = reader.readDateTime();
        parameters = reader.readString();
        jobUuid = reader.readUuid();
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

    public UUID getOrganisationUuuid() {
        return organisationUuuid;
    }

    public void setOrganisationUuuid(UUID organisationUuuid) {
        this.organisationUuuid = organisationUuuid;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public UUID getReportUuid() {
        return reportUuid;
    }

    public void setReportUuid(UUID reportUuid) {
        this.reportUuid = reportUuid;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UUID getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(UUID jobUuid) {
        this.jobUuid = jobUuid;
    }
}
