package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.core.entitymap.EntityMapHelper;
import org.endeavourhealth.enterprise.engine.EngineApi;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.enginecore.communication.ControllerQueue;
import org.endeavourhealth.enterprise.enginecore.communication.ControllerQueueWorkItemCompleteMessage;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesStartMessage;
import org.endeavourhealth.enterprise.enginecore.communication.WorkerQueueBatchMessage;
import org.endeavourhealth.enterprise.core.entitymap.models.EntityMap;
import org.endeavourhealth.enterprise.engine.ExecutionException;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.engine.Processor;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;
import org.endeavourhealth.enterprise.processornode.configuration.models.Configuration;
import org.endeavourhealth.enterprise.processornode.configuration.ConfigurationAPI;
import org.endeavourhealth.enterprise.processornode.datasource.DataSourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

class ExecutionController implements DataSourceController.DataSourceControllerNotification, AutoCloseable {

    private final static Logger logger = LoggerFactory.getLogger(ExecutionController.class);

    private final ProcessorNodesStartMessage.StartMessagePayload startMessage;
    private final Configuration configuration;
    private final List<ThreadProcessorPair> threadProcessors = new ArrayList<>();
    private final DataSourceController dataSourceController;
    private final ControllerQueue controllerQueue;
    private final EntityMapWrapper.EntityMap entityMap;

    private EngineApi engineApi;

    public ExecutionController(Configuration configuration, ProcessorNodesStartMessage.StartMessagePayload startMessage) throws Exception {

        if (configuration.getExecutionThreads() < 1)
            throw new IllegalArgumentException("NumberOfThreads must be 1 or higher");

        this.startMessage = startMessage;
        this.configuration = configuration;
        this.entityMap = getEntityMap();

        QueueConnectionProperties queueConnectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());
        controllerQueue = new ControllerQueue(queueConnectionProperties, startMessage.getControllerQueueName());

        dataSourceController = new DataSourceController(
                this,
                configuration.getMessageQueuing(),
                configuration.getDataItemBufferSize(),
                configuration.getDataItemBufferTriggerSize(),
                startMessage.getWorkerQueueName(),
                entityMap,
                startMessage.getCareRecordDatabaseConnectionDetails());
    }

    private EntityMapWrapper.EntityMap getEntityMap() throws Exception {
        EntityMap realEntityMap = EntityMapHelper.loadEntityMap();
        return new EntityMapWrapper.EntityMap(realEntityMap);
    }

    public void shutDown() throws InterruptedException {
        dataSourceController.shutdown();

        for (ThreadProcessorPair pair : threadProcessors) {
            pair.getThread().join();
        }
    }

    public void start() throws ExecutionException, SQLException, ClassNotFoundException, UnableToCompileExpection {

        engineApi = new EngineApi(
                entityMap);

        for (int i = 0; i < configuration.getExecutionThreads(); i++) {

            Processor engineProcessor = engineApi.createProcessor();
            EngineProcessorWrapper engineProcessorWrapper = new EngineProcessorWrapper(engineProcessor, dataSourceController);

            Thread thread = new Thread(engineProcessorWrapper);
            //thread.setUncaughtExceptionHandler();

            ThreadProcessorPair pair = new ThreadProcessorPair(thread, engineProcessorWrapper);
            threadProcessors.add(pair);

            pair.getThread().start();
        }
    }

    @Override
    public void workerQueueItemProcessed(WorkerQueueBatchMessage.BatchMessagePayload payload) throws IOException {

        ControllerQueueWorkItemCompleteMessage message = ControllerQueueWorkItemCompleteMessage.CreateAsNew(
                startMessage.getJobUuid(),
                payload.getMinimumId()
        );

        controllerQueue.sendMessage(message);

        logger.debug("Worker queue item processed.  MinimumId: " + payload.getMinimumId() + "  MaximumId: " + payload.getMaximumId());
    }

    @Override
    public void noWorkerQueueItemsLeft() {

        logger.debug("No worker queue items remaining");

        //shutDown();
    }

    @Override
    public void close() throws Exception {
        if (dataSourceController != null)
            dataSourceController.close();

        if (controllerQueue != null)
            controllerQueue.close();
    }
}
