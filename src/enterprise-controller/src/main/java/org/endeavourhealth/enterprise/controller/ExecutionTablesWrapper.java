package org.endeavourhealth.enterprise.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.controller.jobinventory.*;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.TableSaveMode;
import org.endeavourhealth.enterprise.core.database.definition.DbAudit;
import org.endeavourhealth.enterprise.core.database.execution.*;
import org.endeavourhealth.enterprise.enginecore.carerecord.SourceStatistics;

import java.time.Instant;
import java.util.*;

class ExecutionTablesWrapper {

    private final UUID jobUuid;
    private final Instant startDateTime;
    private DbJob dbJob;
    private SourceStatistics primaryTableStatistics;
    private JobInventory inventory;

    public ExecutionTablesWrapper(UUID jobUuid, Instant startDateTime) {

        this.jobUuid = jobUuid;
        this.startDateTime = startDateTime;
    }

    public void prepareExecutionTables(JobInventory inventory) throws Exception {
        this.inventory = inventory;

        List<DbAbstractTable> toSave = new ArrayList<>();

        dbJob = createJobAsStarted();
        toSave.add(dbJob);

        prepareJobContentTable(inventory, toSave);

        for (JobReportInfo jobReportInfo: inventory.getJobReportInfoList()) {

            DbJobReport jobReport = createJobReport(jobReportInfo, inventory.getItemsAuditUuid(jobReportInfo.getReportUuid()));
            toSave.add(jobReport);

            prepareJobReportItemTable(jobReportInfo, toSave, inventory);
        }

        DatabaseManager.db().writeEntities(toSave);
    }

    private void prepareJobReportItemTable(
            JobReportInfo jobReportInfo,
            List<DbAbstractTable> toSave,
            JobInventory inventory
    ) throws Exception {

        for (JobReportItemInfo jobReportItemInfo: jobReportInfo.getChildren()) {
            addJobReportItem(jobReportInfo.getJobReportUuid(), jobReportItemInfo, null, toSave, inventory);
        }
    }

    private void addJobReportItem(
            UUID jobReportUuid,
            JobReportItemInfo jobReportItemInfo,
            UUID parentUuid,
            List<DbAbstractTable> toSave,
            JobInventory inventory) {

        DbJobReportItem dbJobReportItem = new DbJobReportItem();
        dbJobReportItem.setSaveMode(TableSaveMode.INSERT);

        dbJobReportItem.setAuditUuid(inventory.getItemsAuditUuid(jobReportItemInfo.getLibraryItemUuid()));
        dbJobReportItem.setJobReportUuid(jobReportUuid);
        dbJobReportItem.setParentJobReportItemUuid(parentUuid);
        dbJobReportItem.setItemUuid(jobReportItemInfo.getLibraryItemUuid());
        dbJobReportItem.setJobReportItemUuid(jobReportItemInfo.getJobReportItemUuid());
        toSave.add(dbJobReportItem);

        if (CollectionUtils.isNotEmpty(jobReportItemInfo.getChildren())) {
            for (JobReportItemInfo child: jobReportItemInfo.getChildren()) {
                addJobReportItem(jobReportUuid, child, jobReportItemInfo.getJobReportItemUuid(), toSave, inventory);
            }
        }
    }

    private void prepareJobContentTable(JobInventory jobInventory, List<DbAbstractTable> toSave) {

        for (UUID itemUuid: jobInventory.getAllItemsUsed()) {

            DbJobContent jobContent = new DbJobContent();
            jobContent.setJobUuid(jobUuid);
            jobContent.setItemUuid(itemUuid);
            jobContent.setAuditUuid(jobInventory.getItemsAuditUuid(itemUuid));
            jobContent.setSaveMode(TableSaveMode.INSERT); //because the primary keys have been explicitly set, we need to force insert mode
            toSave.add(jobContent);
        }
    }

    private DbJobReport createJobReport(JobReportInfo jobReportInfo, UUID auditUuid) throws Exception {

        DbJobReport jobReport = new DbJobReport();
        jobReport.setJobReportUuid(jobReportInfo.getJobReportUuid());
        jobReport.setSaveMode(TableSaveMode.INSERT);

        jobReport.setJobUuid(jobUuid);
        jobReport.setReportUuid(jobReportInfo.getReportUuid());
        jobReport.setAuditUuid(auditUuid);
        jobReport.setOrganisationUuid(jobReportInfo.getRequest().getOrganisationUuid());
        jobReport.setEndUserUuid(jobReportInfo.getRequest().getEndUserUuid());
        jobReport.setParameters(jobReportInfo.getParametersAsString());
        jobReport.setStatusId(ExecutionStatus.Executing);

        return jobReport;
    }

    private DbJob createJobAsStarted() throws Exception {
        DbJob job = new DbJob();
        job.setSaveMode(TableSaveMode.INSERT);

        job.setJobUuid(jobUuid);
        job.setStatusId(ExecutionStatus.Executing);
        job.setStartDateTime(startDateTime);
        job.setBaselineAuditUuid(getLatestAuditUuid());
        job.setPatientsInDatabase(primaryTableStatistics.getRecordCount());

        return job;
    }

    private UUID getLatestAuditUuid() throws Exception {
        DbAudit latestAudit = DbAudit.retrieveLatest();

        if (latestAudit == null)
            return null;
        else
            return latestAudit.getAuditUuid();
    }

    public void createJobAsFinished(ExecutionStatus executionStatus) throws Exception {
        DbJob job = new DbJob();
        job.setSaveMode(TableSaveMode.INSERT);

        job.setJobUuid(jobUuid);
        job.setStatusId(executionStatus);
        job.setStartDateTime(startDateTime);
        job.setEndDateTime(Instant.now());
        job.setBaselineAuditUuid(getLatestAuditUuid());

        if (primaryTableStatistics != null)
            job.setPatientsInDatabase(primaryTableStatistics.getRecordCount());

        job.writeToDb();
    }

    public void markJobAsSuccessful(boolean shouldUpdateRequestTable) throws Exception {

        List<DbAbstractTable> toSave = new ArrayList<>();

        dbJob.setSaveMode(TableSaveMode.UPDATE);
        dbJob.markAsFinished(ExecutionStatus.Succeeded);
        dbJob.setEndDateTime(Instant.now());

        toSave.add(dbJob);

        if (shouldUpdateRequestTable)
            updateRequestTable(toSave);

        DatabaseManager.db().writeEntities(toSave);
    }

    private void updateRequestTable(List<DbAbstractTable> toSave) throws Exception {
        for (JobReportInfo jobReportInfo: inventory.getJobReportInfoList()) {

            DbRequest request = DbRequest.retrieveForUuid(jobReportInfo.getRequest().getRequestUuid());  //get it again in case we override some values by accident
            request.setJobUuid(jobUuid);
            request.setSaveMode(TableSaveMode.UPDATE);
            toSave.add(request);
        }
    }

    public void markJobAsFailed() throws Exception {
        dbJob.setSaveMode(TableSaveMode.UPDATE);
        dbJob.markAsFinished(ExecutionStatus.Failed);

        dbJob.writeToDb();
    }

    public void setPrimaryTableStatistics(SourceStatistics primaryTableStatistics) {
        this.primaryTableStatistics = primaryTableStatistics;
    }
}
