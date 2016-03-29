package org.endeavourhealth.enterprise.processornode.datasource;

import org.endeavourhealth.enterprise.enginecore.communication.WorkerQueue;
import org.endeavourhealth.enterprise.enginecore.communication.WorkerQueueBatchMessage;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class WorkerQueueProcessor implements AutoCloseable, Runnable {

    public interface IWorkerQueueProcessorRetrievedMessage {
        void workerQueueProcessorRetrievedMessage(WorkerQueueBatchMessage message);
    }

    private final IWorkerQueueProcessorRetrievedMessage callback;
    private final WorkerQueue workerQueue;

    public WorkerQueueProcessor(
            IWorkerQueueProcessorRetrievedMessage callback,
            QueueConnectionProperties queueConnectionProperties,
            String workerQueueName) throws IOException, TimeoutException {

        this.callback = callback;
        workerQueue = new WorkerQueue(queueConnectionProperties, workerQueueName);
    }

    @Override
    public void run() {

        try {
            workerQueue.acknowledgePreviousMessage();
            WorkerQueueBatchMessage message = workerQueue.getNextMessage();
            callback.workerQueueProcessorRetrievedMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (workerQueue != null)
            workerQueue.close();
    }
}