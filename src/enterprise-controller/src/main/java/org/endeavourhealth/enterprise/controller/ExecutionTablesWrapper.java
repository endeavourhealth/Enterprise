package org.endeavourhealth.enterprise.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.controller.jobinventory.*;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.TableSaveMode;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.enginecore.carerecord.SourceStatistics;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

class ExecutionTablesWrapper {

    private final UUID jobUuid;
    private final Instant startDateTime;
    private JobEntity dbJob;
    private SourceStatistics primaryTableStatistics;
    private JobInventory inventory;

    public ExecutionTablesWrapper(UUID jobUuid, Instant startDateTime) {

        this.jobUuid = jobUuid;
        this.startDateTime = startDateTime;
    }

    public void prepareExecutionTables(JobInventory inventory) throws Exception {
        this.inventory = inventory;

        /*List<Object> toSave = new ArrayList<>();

        dbJob = createJobAsStarted();
        toSave.add(dbJob);

        prepareJobContentTable(inventory, toSave);

        for (JobReportInfo jobReportInfo: inventory.getJobReportInfoList()) {

            DbJobReport jobReport = createJobReport(jobReportInfo, inventory.getItemsAuditUuid(jobReportInfo.getReportUuid()));
            toSave.add(jobReport);

            prepareJobReportItemTable(jobReportInfo, toSave, inventory);
        }

        DatabaseManager.db().writeEntities(toSave);*/
    }

    private void prepareJobReportItemTable(
            JobReportInfo jobReportInfo,
            List<Object> toSave,
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
            List<Object> toSave,
            JobInventory inventory) {

        JobreportitemEntity dbJobReportItem = new JobreportitemEntity();

        dbJobReportItem.setAudituuid(inventory.getItemsAuditUuid(jobReportItemInfo.getLibraryItemUuid()));
        dbJobReportItem.setJobreportuuid(jobReportUuid);
        dbJobReportItem.setJobreportitemuuid(parentUuid);
        dbJobReportItem.setItemuuid(jobReportItemInfo.getLibraryItemUuid());
        dbJobReportItem.setJobreportitemuuid(jobReportItemInfo.getJobReportItemUuid());
        toSave.add(dbJobReportItem);

        if (CollectionUtils.isNotEmpty(jobReportItemInfo.getChildren())) {
            for (JobReportItemInfo child: jobReportItemInfo.getChildren()) {
                addJobReportItem(jobReportUuid, child, jobReportItemInfo.getJobReportItemUuid(), toSave, inventory);
            }
        }
    }

    private void prepareJobContentTable(JobInventory jobInventory, List<Object> toSave) {

        for (UUID itemUuid: jobInventory.getAllItemsUsed()) {

            JobcontentEntity jobContent = new JobcontentEntity();
            jobContent.setJobuuid(jobUuid);
            jobContent.setItemuuid(itemUuid);
            jobContent.setAudituuid(jobInventory.getItemsAuditUuid(itemUuid));
            toSave.add(jobContent);
        }
    }

    private JobreportEntity createJobReport(JobReportInfo jobReportInfo, UUID auditUuid) throws Exception {

        JobreportEntity jobReport = new JobreportEntity();
        jobReport.setJobreportuuid(jobReportInfo.getJobReportUuid());

        jobReport.setJobuuid(jobUuid);
        jobReport.setReportuuid(jobReportInfo.getReportUuid());
        jobReport.setAudituuid(auditUuid);
        jobReport.setOrganisationuuid(jobReportInfo.getRequest().getOrganisationuuid());
        jobReport.setEnduseruuid(jobReportInfo.getRequest().getEnduseruuid());
        jobReport.setParameters(jobReportInfo.getParametersAsString());
        jobReport.setStatusid((short)ExecutionStatus.Executing.getValue());

        return jobReport;
    }

    private JobEntity createJobAsStarted() throws Exception {
        JobEntity job = new JobEntity();

        job.setJobuuid(jobUuid);
        job.setStatusid((short)ExecutionStatus.Executing.getValue());
        job.setStartdatetime(Timestamp.from(startDateTime));
        job.setBaselineaudituuid(getLatestAuditUuid());
        //job.setPatientsindatabase(primaryTableStatistics.getRecordCount());

        return job;
    }

    private UUID getLatestAuditUuid() throws Exception {
        AuditEntity latestAudit = AuditEntity.retrieveLatest();

        if (latestAudit == null)
            return null;
        else
            return latestAudit.getAudituuid();
    }

    public void createJobAsFinished(ExecutionStatus executionStatus) throws Exception {
        JobEntity job = new JobEntity();

        job.setJobuuid(jobUuid);
        job.setStatusid((short)executionStatus.getValue());
        job.setStartdatetime(Timestamp.from(startDateTime));
        job.setEnddatetime(Timestamp.from(Instant.now()));
        job.setBaselineaudituuid(getLatestAuditUuid());

        //if (primaryTableStatistics != null)
            //job.setPatientsInDatabase(primaryTableStatistics.getRecordCount());

        //job.writeToDb(); TODO
    }

    public void markJobAsSuccessful(boolean shouldUpdateRequestTable) throws Exception {

        List<Object> toSave = new ArrayList<>();

        dbJob.markAsFinished((short)ExecutionStatus.Succeeded.getValue());
        dbJob.setEnddatetime(Timestamp.from(Instant.now()));

        toSave.add(dbJob);

        if (shouldUpdateRequestTable)
            updateRequestTable(toSave);

        //DatabaseManager.db().writeEntities(toSave); TODO
    }

    private void updateRequestTable(List<Object> toSave) throws Exception {
        for (JobReportInfo jobReportInfo: inventory.getJobReportInfoList()) {

            RequestEntity request = RequestEntity.retrieveForUuid(jobReportInfo.getRequest().getRequestuuid());  //get it again in case we override some values by accident
            request.setJobreportuuid(jobReportInfo.getJobReportUuid());
            toSave.add(request);
        }
    }

    public void markJobAsFailed() throws Exception {
        dbJob.markAsFinished((short)ExecutionStatus.Failed.getValue());

        //dbJob.writeToDb(); TODO
    }

    public void setPrimaryTableStatistics(SourceStatistics primaryTableStatistics) {
        this.primaryTableStatistics = primaryTableStatistics;
    }
}
