package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.controller.configuration.models.Configuration;
import org.endeavourhealth.enterprise.controller.configuration.ConfigurationAPI;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.enginecore.communication.ControllerQueue;
import org.endeavourhealth.enterprise.enginecore.communication.ControllerQueueWorkItemCompleteMessage;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class ControllerMain implements AutoCloseable, ControllerQueue.IControllerQueueMessageReceiver {

    private final static Logger logger = LoggerFactory.getLogger(ControllerMain.class);
    private SchedulerWrapper scheduler;
    private Configuration configuration;
    private ExecutionJob currentJob;
    private ControllerQueue queue;

    public void start() throws Exception {

        loadConfiguration();
        initialiseDatabaseManager();
        initialiseLogback();

        logger.info("Application starting");

        initialiseControllerQueue();

        scheduler = new SchedulerWrapper(configuration);
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

    }

    @Override
    public synchronized void receiveWorkerItemCompleteMessage(ControllerQueueWorkItemCompleteMessage.WorkItemCompletePayload payload) {
        logger.debug("Received worker item complete message: " + payload.getExecutionUuid().toString() + " startID: " + payload.getStartId());

        if (currentJob == null)
            return;

        if (!currentJob.getExecutionUuid().equals(payload.getExecutionUuid()))
            return;

        currentJob.workerItemComplete(payload);
    }

//    public void requestCancelCurrentJob() {
//        if (currentJob == null)
//            return;
//
//        currentJob.cancel();
//        currentJob = null;
//    }
//
//    public workerQueueItemComplete() {
//        if (currentJob == null)
//            return;
//    }
}
