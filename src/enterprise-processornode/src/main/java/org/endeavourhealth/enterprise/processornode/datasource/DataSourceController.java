package org.endeavourhealth.enterprise.processornode.datasource;

import org.endeavourhealth.enterprise.enginecore.carerecord.CareRecordDal;
import org.endeavourhealth.enterprise.enginecore.communication.WorkerQueueBatchMessage;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseConnectionDetails;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainerPool;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;
import org.endeavourhealth.enterprise.processornode.ShortLivedThreadWrapper;
import org.endeavourhealth.enterprise.processornode.configuration.ConfigurationAPI;
import org.endeavourhealth.enterprise.processornode.configuration.models.MessageQueuing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;


public class DataSourceController implements AutoCloseable, IDataSourceFromEngine, WorkerQueueProcessor.IWorkerQueueProcessorRetrievedMessage {

    private final static Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    private WorkerQueueBatchMessage.BatchMessagePayload currentWorkerQueueBatch;
    private final ShortLivedThreadWrapper<WorkerQueueProcessor> workerQueueProcessorWrapper;
    private boolean shutdown;

    private final DataSourceBuffer dataSourceBuffer;
    private final Set<Long> dataItemsToProcess = Collections.newSetFromMap(new ConcurrentHashMap<Long, Boolean>());
    private DataSourceControllerNotification notification;
    private DataContainerPool dataContainerPool;

    public interface DataSourceControllerNotification {
        void workerQueueItemProcessed(WorkerQueueBatchMessage.BatchMessagePayload payload) throws IOException;
        void noWorkerQueueItemsLeft();
    }

    public DataSourceController(
            DataSourceControllerNotification notification,
            MessageQueuing messageQueuingConfiguration,
            int dataItemBufferSize,
            int dataItemBufferTriggerSize,
            String workerQueueName,
            EntityMapWrapper.EntityMap entityMap,
            DatabaseConnectionDetails careRecordConnectionDetails)
            throws IOException, TimeoutException, InterruptedException {

        this.notification = notification;
        this.dataContainerPool = new DataContainerPool(dataItemBufferSize, entityMap);

        CareRecordDal careRecordDal = new CareRecordDal(careRecordConnectionDetails, dataContainerPool, entityMap);

        QueueConnectionProperties queueConnectionProperties = ConfigurationAPI.convertConnection(messageQueuingConfiguration);

        dataSourceBuffer = new DataSourceBuffer(careRecordDal, dataItemBufferSize, dataItemBufferTriggerSize, dataItemsToProcess);

        WorkerQueueProcessor workerQueueProcessor = new WorkerQueueProcessor(this, queueConnectionProperties, workerQueueName);
        workerQueueProcessorWrapper = new ShortLivedThreadWrapper<>(workerQueueProcessor);

        workerQueueProcessorWrapper.lock();
        startWorkerQueueProcessingThread();
    }

    @Override
    public DataContainer getNextDataContainer() throws InterruptedException {

        if (shutdown)
            return null;

        return dataSourceBuffer.poll();
    }

    @Override
    public void dataContainerProcessed(DataContainer dataContainer) throws IOException {
        logger.trace("Patient processed.  Key: " + dataContainer.getId() + "  Thread: " + Thread.currentThread().getId());

        boolean wasRemoved = dataItemsToProcess.remove(dataContainer.getId());

        if (!wasRemoved)
            logger.debug("Item not removed: " + dataContainer.getId());

        dataContainerPool.recycle(dataContainer);

        if (!dataItemsToProcess.isEmpty())
            return;

        if (shutdown)
            return;

        if (!workerQueueProcessorWrapper.tryLock())
            return;  //Another thread is already processing this

        notification.workerQueueItemProcessed(currentWorkerQueueBatch);
        startWorkerQueueProcessingThread();
    }

    public void startWorkerQueueProcessingThread() {

        logger.trace("Starting worker queue processing thread");
        workerQueueProcessorWrapper.start();
    }

    @Override
    public void workerQueueProcessorRetrievedMessage(WorkerQueueBatchMessage message) {

        if (message == null) {
            notification.noWorkerQueueItemsLeft();
        } else {

            currentWorkerQueueBatch = message.getPayload();
            logger.debug("Worker queue item received.  MinimumId: " + currentWorkerQueueBatch.getMinimumId() + "  MaximumId: " + currentWorkerQueueBatch.getMaximumId());

            populateDataItemsToProcess(currentWorkerQueueBatch.getMinimumId(), currentWorkerQueueBatch.getMaximumId());
            dataSourceBuffer.setParameters(currentWorkerQueueBatch.getMinimumId(), currentWorkerQueueBatch.getMaximumId());
        }

        workerQueueProcessorWrapper.unlock();
        logger.trace("Stopping worker queue processing thread");
    }

    private void populateDataItemsToProcess(long minimumId, long maximumId) {
        if (dataItemsToProcess.size() != 0)
            throw new IllegalStateException("DataItemsToProcess should be empty");

        for (long i = minimumId; i <= maximumId; i++) {
            dataItemsToProcess.add(i);
        }
    }

    @Override
    public void close() throws Exception {
        if (workerQueueProcessorWrapper != null && workerQueueProcessorWrapper.getItem() != null)
            workerQueueProcessorWrapper.getItem().close();
    }

    public void shutdown() throws InterruptedException {
        shutdown = true;

        dataSourceBuffer.shutdown();
        workerQueueProcessorWrapper.shutdown();
    }
}
