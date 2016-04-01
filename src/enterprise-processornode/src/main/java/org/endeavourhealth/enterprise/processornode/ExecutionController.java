package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbJobContent;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.entitymap.EntityMapHelper;
import org.endeavourhealth.enterprise.engine.EngineApi;
import org.endeavourhealth.enterprise.enginecore.LibraryItem;
import org.endeavourhealth.enterprise.enginecore.Library;
import org.endeavourhealth.enterprise.enginecore.carerecord.CareRecordDal;
import org.endeavourhealth.enterprise.enginecore.communication.*;
import org.endeavourhealth.enterprise.core.entitymap.models.EntityMap;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainerPool;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ResultCounts;
import org.endeavourhealth.enterprise.processornode.configuration.models.Configuration;
import org.endeavourhealth.enterprise.processornode.configuration.ConfigurationAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

class ExecutionController implements ProcessorThreadPoolExecutor.IBatchComplete, AutoCloseable {

    private final static Logger logger = LoggerFactory.getLogger(ExecutionController.class);

    private final ProcessorNodesStartMessage.StartMessagePayload startMessage;
    private final Configuration configuration;
    private final ControllerQueue controllerQueue;
    private final WorkerQueue workerQueue;

    private ProcessorThreadPoolExecutor executor;
    private WorkerQueueBatchMessage currentWorkerQueueBatch;
    private EngineApi engineApi;

    public ExecutionController(Configuration configuration, ProcessorNodesStartMessage.StartMessagePayload startMessage) throws Exception {

        if (configuration.getExecutionThreads() < 1)
            throw new IllegalArgumentException("NumberOfThreads must be 1 or higher");

        this.startMessage = startMessage;
        this.configuration = configuration;
        this.controllerQueue = createControllerQueue();
        this.workerQueue = createWorkerQueue();
    }

    private WorkerQueue createWorkerQueue() throws IOException, TimeoutException {
        QueueConnectionProperties queueConnectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());
        return new WorkerQueue(queueConnectionProperties, startMessage.getWorkerQueueName());
    }

    private ControllerQueue createControllerQueue() throws IOException, TimeoutException {
        QueueConnectionProperties queueConnectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());
        return new ControllerQueue(queueConnectionProperties, startMessage.getControllerQueueName());
    }

    public void shutDown() throws InterruptedException {
        executor.shutdownNow();
    }

    public void start() throws Exception {
        EntityMapWrapper.EntityMap entityMap = getEntityMap();

        this.engineApi = createEngineApi(entityMap);

        createProcessorThreadPoolExecutor(entityMap);

        if (!requestNextWorkerQueueMessage())
            workComplete();
    }

    private EngineApi createEngineApi(EntityMapWrapper.EntityMap entityMap) throws Exception {

        List<DbJobContent> contentList = DbJobContent.retrieveForJob(startMessage.getJobUuid());
        Library library = new Library();

        for (DbJobContent dbJobContent : contentList) {
            DbItem item = DbItem.retrieveForUuidAndAudit(dbJobContent.getItemUuid(), dbJobContent.getAuditUuid());
            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(item.getItemUuid());
            LibraryItem libraryItem = new LibraryItem(item.getTitle(), item.getItemUuid(), activeItem.getItemTypeId(), item.getXmlContent());
            library.put(libraryItem);
        }

        List<DbJobReport> jobReports = DbJobReport.retrieveForJob(startMessage.getJobUuid());

        EngineApi engineApi = new EngineApi(entityMap, library, jobReports);
        engineApi.initialise();
        return engineApi;
    }

    private void createProcessorThreadPoolExecutor(EntityMapWrapper.EntityMap entityMap) {
        EngineProcessorPool engineProcessorPool = new EngineProcessorPool(engineApi, configuration.getExecutionThreads());
        DataContainerPool dataContainerPool = new DataContainerPool(configuration.getDataItemBufferSize(), entityMap);
        CareRecordDal careRecordDal = new CareRecordDal(startMessage.getCareRecordDatabaseConnectionDetails(), dataContainerPool, entityMap);
        DataSourceRetriever dataSourceRetriever = new DataSourceRetriever(configuration.getDataItemBufferSize(), careRecordDal);
        ExecutionContext executionContext = new ExecutionContext(engineProcessorPool, dataContainerPool);

        executor = new ProcessorThreadPoolExecutor(
                configuration.getExecutionThreads(),
                configuration.getDataItemBufferTriggerSize(),
                dataSourceRetriever,
                this,
                executionContext);
    }

    private EntityMapWrapper.EntityMap getEntityMap() throws Exception {
        EntityMap realEntityMap = EntityMapHelper.loadEntityMap();
        return new EntityMapWrapper.EntityMap(realEntityMap);
    }

    private boolean requestNextWorkerQueueMessage() throws Exception {

        currentWorkerQueueBatch = workerQueue.getNextMessage();

        if (currentWorkerQueueBatch != null) {
            executor.setNextBatch(currentWorkerQueueBatch.getPayload().getMinimumId(), currentWorkerQueueBatch.getPayload().getMaximumId());
            return true;
        } else {
            logger.trace("No worker queue items remaining");
            return false;
        }
    }

    @Override
    public void batchComplete() {

        try {

            WorkerQueueBatchMessage.BatchMessagePayload batchMessagePayload = currentWorkerQueueBatch.getPayload();

            ControllerQueueWorkItemCompleteMessage message = ControllerQueueWorkItemCompleteMessage.CreateAsNew(
                    startMessage.getJobUuid(),
                    batchMessagePayload.getMinimumId()
            );

            controllerQueue.sendMessage(message);
            workerQueue.acknowledgePreviousMessage();

            logger.debug("Worker queue item processed.  MinimumId: " + batchMessagePayload.getMinimumId() + "  MaximumId: " + batchMessagePayload.getMaximumId());

            if (!requestNextWorkerQueueMessage())
                workComplete();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void workComplete() {

        ResultCounts results = engineApi.getResults();

        logger.debug("Work complete");

    }

    @Override
    public void close() throws Exception {

        if (controllerQueue != null)
            controllerQueue.close();

        if (workerQueue != null)
            workerQueue.close();

        if (executor != null && !executor.isTerminating())
            executor.shutdownNow();
    }
}
