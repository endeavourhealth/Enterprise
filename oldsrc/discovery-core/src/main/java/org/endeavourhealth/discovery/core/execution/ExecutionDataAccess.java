package org.endeavourhealth.discovery.core.execution;

import net.sourceforge.jtds.jdbc.JtdsResultSet;
import org.endeavourhealth.discovery.core.database.DatabaseConnectionDetails;
import org.endeavourhealth.discovery.core.database.DatabaseHelper;
import org.endeavourhealth.discovery.core.execution.models.JobItem;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExecutionDataAccess {

    public static void updateStatus(
            DatabaseConnectionDetails connectionDetails,
            ExecutionStatus status,
            UUID jobUuid) throws SQLException, ClassNotFoundException {

        String sql = "update Execution.Job set StatusId = ?, EndDateTime = ? where JobUuid = ?;";

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setByte(1, (byte)status.getValue());
            ps.setTimestamp(2, DatabaseHelper.currentDateTime());
            ps.setString(3, jobUuid.toString());

            ps.execute();
        }
    }

    public static List<UUID> findExecutionHistoryItemsByStatus(
            DatabaseConnectionDetails connectionDetails,
            ExecutionStatus status) throws SQLException, ClassNotFoundException {

        String sql = "select JobUuid from Execution.Job where StatusId = ?;";
        List<UUID> results = new ArrayList<>();

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setByte(1, (byte) status.getValue());

            try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery()) {
                while (rs.next()) {

                    UUID id = UUID.fromString(rs.getString(1));
                    results.add(id);
                }
            }
        }

        return results;
    }

    public static void createExecutionHistory(
            DatabaseConnectionDetails connectionDetails,
            UUID jobUuid) throws SQLException, ClassNotFoundException {

        String sql = "insert into Execution.Job (JobUuid, StatusId, StartDateTime ) values ( ?, ?, ? );";

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, jobUuid.toString());
            ps.setByte(2, (byte) ExecutionStatus.EXECUTING.getValue());
            ps.setTimestamp(3, DatabaseHelper.currentDateTime());

            ps.execute();
        }
    }

    public static void updateExecutionJobWithPatientCount(DatabaseConnectionDetails connectionDetails, UUID jobUuid, int recordCount) throws SQLException, ClassNotFoundException {
        String sql = "update Execution.Job set PatientsInDatabase = ? where JobUuid = ?;";

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setInt(1, recordCount);
            ps.setString(2, jobUuid.toString());
            ps.execute();
        }
    }

    public static List<ItemRequestWithAudit> findItemRequestsWithAudit(DatabaseConnectionDetails connectionDetails) throws SQLException, ClassNotFoundException {
        String sql = "select r.RequestId, r.ReportItemUuid, r.[DateTime], r.UserUuid, a.CurrentAuditId from Execution.Request as r inner join [Definition].ActiveItems as a on a.ItemUuid = r.ItemUuid;";
        List<ItemRequestWithAudit> results = new ArrayList<>();

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                Statement ps = con.createStatement();
        ) {
            try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery(sql)) {
                while (rs.next()) {

                    ItemRequestWithAudit item = new ItemRequestWithAudit(
                            rs.getInt(1),
                            UUID.fromString(rs.getString(2)),
                            new DateTime(rs.getTimestamp(3).getTime()),
                            UUID.fromString(rs.getString(4)),
                            rs.getInt(5));

                    results.add(item);
                }
            }
        }

        return results;
    }

    public static void insertExecutionContent(DatabaseConnectionDetails connectionDetails, UUID jobUuid, List<ExecutionContentForInsert> items) throws SQLException, ClassNotFoundException {
        String sql = "insert into Execution.JobItem (JobUuid, ReportItemUuid, AuditId) values (?, ?, ?);";

        if (items.isEmpty())
            return;

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            con.setAutoCommit(false);

            for (ExecutionContentForInsert contentItem : items)  {
                ps.setString(1, jobUuid.toString());
                ps.setString(2, contentItem.getItemUuid().toString());
                ps.setInt(3, contentItem.getAuditId());

                ps.addBatch();
            }

            ps.executeBatch();

            con.commit();
        }
    }

    public static void updateJobWithAuditId(DatabaseConnectionDetails connectionDetails, UUID jobUuid, int currentAuditId) throws SQLException, ClassNotFoundException {
        String sql = "update Execution.Job set BaselineAuditId = ? where JobUuid = ?;";

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setInt(1, currentAuditId);
            ps.setString(2, jobUuid.toString());
            ps.execute();
        }
    }

    public static List<JobItem> getJobItems(DatabaseConnectionDetails connectionDetails, UUID jobUuid) throws SQLException, ClassNotFoundException {
        String sql = "select j.JobItemId, j.JobUuid, j.ReportItemUuid, j.AuditId, j.UserUuid, j.OrganisationUuid, j.[Parameters] from Execution.JobItem as j where j.JobUuid = ?;";

        List<JobItem> results = new ArrayList<>();

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, jobUuid.toString());

            try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery(sql)) {
                while (rs.next()) {

                    JobItem item = new JobItem();
                    item.setJobItemId(rs.getInt(1));
                    item.setJobUuid(UUID.fromString(rs.getString(2)));
                    item.setReportItemUuid(UUID.fromString(rs.getString(3)));
                    item.setAuditId(rs.getInt(4));
                    item.setUserUuid(UUID.fromString(rs.getString(5)));
                    item.setOrganisationUuid(UUID.fromString(rs.getString(6)));
                    item.setParameters(rs.getString(7));

                    results.add(item);
                }
            }
        }

        return results;
    }

    public static int getJobAuditId(DatabaseConnectionDetails connectionDetails, UUID jobUuid) throws SQLException, ClassNotFoundException, ExecutionException {
        String sql = "select j.BaselineAuditId from Execution.Job as j where j.JobUuid = ?;";

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, jobUuid.toString());

            try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery(sql)) {
                if (!rs.next())
                    throw new ExecutionException("JobUuid not found: " + jobUuid);

                int result = rs.getInt(1);

                if (rs.wasNull())
                    throw new ExecutionException("AuditId not set");

                return result;
            }
        }
    }
}
