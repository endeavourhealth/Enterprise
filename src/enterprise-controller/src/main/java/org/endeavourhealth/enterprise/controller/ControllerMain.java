package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.controller.configuration.models.Configuration;
import org.endeavourhealth.enterprise.controller.configuration.ConfigurationAPI;
import org.endeavourhealth.enterprise.core.JsonSerializer;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.enginecore.communication.*;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

class ControllerMain implements AutoCloseable, ControllerQueue.IControllerQueueMessageReceiver {

    private final static Logger logger = LoggerFactory.getLogger(ControllerMain.class);
    private Configuration configuration;
    private ExecutionJob currentJob;
    private ControllerQueue queue;

    public void start() throws Exception {

        loadConfiguration();
        initialiseDatabaseManager();
        initialiseLogback();

        logger.info("Application starting");

        initialiseControllerQueue();

        SchedulerWrapper scheduler = new SchedulerWrapper(configuration);
        scheduler.startDelayed(3);  //Wait 3 second before starting
    }

    private void initialiseLogback() {
        DatabaseManager.getInstance().registerLogbackDbAppender();
    }

    private void initialiseDatabaseManager() {

        DatabaseManager.getInstance().setConnectionProperties(
                configuration.getCoreDatabase().getUrl(),
                configuration.getCoreDatabase().getUsername(),
                configuration.getCoreDatabase().getPassword());
    }

    private void loadConfiguration() throws Exception {
        ConfigurationAPI configApi = new ConfigurationAPI();
        configuration = configApi.loadConfiguration();
    }

    private void initialiseControllerQueue() throws IOException, TimeoutException {
        QueueConnectionProperties queueConnectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());
        queue = new ControllerQueue(queueConnectionProperties, configuration.getMessageQueuing().getControllerQueueName());
        queue.registerReceiver(this);
    }

    public synchronized void requestStartNewJob() throws Exception {
        if (currentJob != null)
            return;

        currentJob = new ExecutionJob(configuration);
        currentJob.start();
    }

    @Override
    public void close() throws Exception {
        if (queue != null)
            queue.close();
    }

    @Override
    public synchronized void receiveWorkerItemCompleteMessage(ControllerQueueWorkItemCompleteMessage.WorkItemCompletePayload payload) {
        logger.debug("Received worker item complete message: " + payload.getExecutionUuid().toString() + " startID: " + payload.getStartId());

        if (!messageIsForCurrentJob(payload.getExecutionUuid()))
            return;

        try {
            currentJob.workerItemComplete(payload);
        } catch (Exception e) {
            logger.error("receiveWorkerItemCompleteMessage failed", e);
            killCurrentJob();
        }
    }

    @Override
    public void receiveExecutionFailedMessage(ControllerQueueExecutionFailedMessage.ExecutionFailedPayload payload) {
        try {
            String text = JsonSerializer.serialize(payload);

            logger.error("Received execution failed message. " + text);

            if (currentJob != null && currentJob.getExecutionUuid().equals(payload.getExecutionUuid()))
                killCurrentJob();

        } catch (Exception e) {
            logger.error("receiveProcessorNodeStartedMessage failed", e);
            killCurrentJob();
        }
    }

    private synchronized void killCurrentJob() {
        if (currentJob != null) {

            logger.debug("Killing current job: " + currentJob.getExecutionUuid());
            currentJob.stop();
            currentJob = null;
        }
    }

    @Override
    public void receiveProcessorNodeStartedMessage(ControllerQueueProcessorNodeStartedMessage.ProcessorNodeStartedPayload payload) {
        try {
            logger.debug("Received processor node started message: " + payload.getExecutionUuid().toString() + " ProcessorId: " + payload.getProcessorUuid());

            if (!messageIsForCurrentJob(payload.getExecutionUuid()))
                return;

            currentJob.processorNodeStarted(payload);
        } catch (Exception e) {
            logger.error("receiveProcessorNodeStartedMessage failed", e);
            killCurrentJob();
        }
    }

    @Override
    public void receiveProcessorNodeCompleteMessage(ControllerQueueProcessorNodeCompleteMessage.ProcessorNodeCompletePayload payload) {
        try {
            logger.debug("Received processor node complete message: " + payload.getExecutionUuid().toString() + " ProcessorId: " + payload.getProcessorUuid());

            if (!messageIsForCurrentJob(payload.getExecutionUuid()))
                return;

            currentJob.processorNodeComplete(payload);
        } catch (Exception e) {
            logger.error("receiveProcessorNodeCompleteMessage failed", e);
            killCurrentJob();
        }
    }

    private boolean messageIsForCurrentJob(UUID executionUuid) {
        if (currentJob == null)
            return false;

        return currentJob.getExecutionUuid().equals(executionUuid);
    }


//    public void requestCancelCurrentJob() {
//        if (currentJob == null)
//            return;
//
//        currentJob.cancel();
//        currentJob = null;
//    }
//
}
