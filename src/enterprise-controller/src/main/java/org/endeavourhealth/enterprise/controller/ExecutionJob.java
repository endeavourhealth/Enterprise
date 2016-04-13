package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.controller.configuration.models.Configuration;
import org.endeavourhealth.enterprise.controller.configuration.ConfigurationAPI;
import org.endeavourhealth.enterprise.controller.jobinventory.JobInventory;
import org.endeavourhealth.enterprise.controller.outputfiles.OutputFileApi;
import org.endeavourhealth.enterprise.core.ProcessorState;
import org.endeavourhealth.enterprise.core.database.execution.*;
import org.endeavourhealth.enterprise.core.queuing.controller.ControllerQueueProcessorNodeCompleteMessage;
import org.endeavourhealth.enterprise.core.queuing.controller.ControllerQueueProcessorNodeStartedMessage;
import org.endeavourhealth.enterprise.core.queuing.controller.ControllerQueueWorkItemCompleteMessage;
import org.endeavourhealth.enterprise.enginecore.carerecord.CareRecordDal;
import org.endeavourhealth.enterprise.enginecore.carerecord.SourceStatistics;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseConnectionDetails;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

class ExecutionJob {

    private final UUID executionUuid = UUID.randomUUID();
    private final Instant startDateTime = Instant.now();
    private final Configuration configuration;
    private final static Logger logger = LoggerFactory.getLogger(ExecutionJob.class);
    private final ProcessorNodesQueueWrapper processorNodesQueueWrapper;
    private final WorkerQueueWrapper workerQueueWrapper;
    private final JobProgressTracker jobProgressTracker = new JobProgressTracker();
    private final ExecutionTablesWrapper executionTablesWrapper;
    private final JobInventory jobInventory = new JobInventory();

    private SourceStatistics primaryTableStats;
    private ResultProcessor resultProcessor;
    private OutputFileApi outputFileApi;
    private boolean stopping;

    public ExecutionJob(Configuration configuration) {
        this.configuration = configuration;
        this.processorNodesQueueWrapper = new ProcessorNodesQueueWrapper(configuration, executionUuid);
        this.workerQueueWrapper = new WorkerQueueWrapper(configuration, executionUuid);
        this.executionTablesWrapper = new ExecutionTablesWrapper(executionUuid, startDateTime);
    }

    public UUID getExecutionUuid() {
        return executionUuid;
    }

    public boolean start() throws Exception {
        logger.debug("Starting job: " + executionUuid);

        clearPreviousJobs();
        List<DbRequest> itemRequests = getItemRequests();

        if (itemRequests.isEmpty()) {
            executionTablesWrapper.createJobAsFinished(ExecutionStatus.NoJobRequests);
            logger.debug("No jobs to run.  Job UUID: " + executionUuid);
            return false;
        }

        setPatientStatistics();

        if (primaryTableStats.getRecordCount() == 0) {
            executionTablesWrapper.createJobAsFinished(ExecutionStatus.Failed);
            logger.error("Tried to start execution but zero patients to process.  Job UUID: " + executionUuid);
            return false;
        }

        jobInventory.initialise(itemRequests);
        prepareJobReportParameters();
        resultProcessor = new ResultProcessor(getExecutionUuid(), jobInventory.getJobReportInfoList());
        prepareOutputFiles();
        prepareExecutionTables();
        createAndPopulateWorkerQueue();
        startProcessorNodes();

        return true;
    }

    private void prepareOutputFiles() throws Exception {
        outputFileApi = new OutputFileApi(configuration.getOutputFiles(), jobInventory, getExecutionUuid(), startDateTime);
        outputFileApi.prepareFiles();
    }

    private void prepareJobReportParameters() throws Exception {

        JobReportParameterBuilder parameterBuilder = new JobReportParameterBuilder();
        parameterBuilder.buildParameters(jobInventory.getJobReportInfoList());
    }

    public void stop() {
        try {
            stopping = true;
            executionTablesWrapper.markJobAsFailed();
            logger.debug("Execution Job failed: " + executionUuid.toString());
            stopExecutionNodes();
            purgeWorkerQueue();
        } catch (Exception e) {
            logger.error("Job stopped exception", e);
        }
    }

    private void clearPreviousJobs() throws Exception {
        PreviousJobCleanup.clearPreviousJobs(configuration.getCoreDatabase(), configuration.getMessageQueuing());
    }

    private void setPatientStatistics() throws Exception {
        DatabaseConnectionDetails connectionDetails = ConfigurationAPI.convertConnection(configuration.getCareRecordDatabase());

        SourceStatistics primaryTableStats = CareRecordDal.calculateTableStatistics(connectionDetails);

        if (configuration.getDebugging().getMaximumPatientId() != null && configuration.getDebugging().getMaximumPatientId() > 0)
            primaryTableStats = new SourceStatistics(primaryTableStats.getRecordCount(), primaryTableStats.getMinimumId(), configuration.getDebugging().getMaximumPatientId());

        this.primaryTableStats = primaryTableStats;
        executionTablesWrapper.setPrimaryTableStatistics(primaryTableStats);
    }

    private List<DbRequest> getItemRequests() throws Exception {
        return DbRequest.retrieveAllPending();
    }

    private void prepareExecutionTables() throws Exception {
        executionTablesWrapper.prepareExecutionTables(jobInventory);
    }

    private void createAndPopulateWorkerQueue() throws Exception {
        workerQueueWrapper.createAndPopulate(primaryTableStats, jobProgressTracker);
    }

    private void purgeWorkerQueue() {
        workerQueueWrapper.purge();
    }

    private void startProcessorNodes() throws Exception {
        processorNodesQueueWrapper.startProcessorNodes();
    }

    private void stopExecutionNodes() throws Exception{
        processorNodesQueueWrapper.stopProcessorNodes();
    }

    public synchronized void workerItemComplete(ControllerQueueWorkItemCompleteMessage.WorkItemCompletePayload payload) throws Exception {
        if (!executionUuid.equals(payload.getExecutionUuid()))
            return;

        jobProgressTracker.receivedWorkItemComplete(payload.getStartId());
    }

    private void jobFinishedSuccessfully() {

        if (stopping)
            return;

        try {
            resultProcessor.complete(jobProgressTracker.getAllProcessorNodes());
            outputFileApi.complete();
            executionTablesWrapper.markJobAsSuccessful(configuration.getDebugging().isMarkRequestAsComplete());  //This updates the Request table which will stop the job running again.
            logger.debug("Execution Job finished: " + executionUuid.toString());
        } catch (Exception e) {
            logger.error("Job Finished exception", e);
        }
    }

    public boolean processorNodeComplete(ControllerQueueProcessorNodeCompleteMessage.ProcessorNodeCompletePayload payload) throws Exception {
        if (!executionUuid.equals(payload.getExecutionUuid()))
            return false;

        jobProgressTracker.receivedProcessorNodeCompleteMessage(payload.getProcessorUuid());

        if (jobProgressTracker.isComplete()) {
            jobFinishedSuccessfully();
            return true;
        }

        return false;
    }

    public void processorNodeStarted(ControllerQueueProcessorNodeStartedMessage.ProcessorNodeStartedPayload payload) throws Exception {
        if (!executionUuid.equals(payload.getExecutionUuid()))
            return;

        jobProgressTracker.receivedProcessorNodeStartedMessage(payload.getProcessorUuid());
    }
}
