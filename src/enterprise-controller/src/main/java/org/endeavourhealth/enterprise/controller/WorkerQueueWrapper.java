package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.controller.configuration.ConfigurationAPI;
import org.endeavourhealth.enterprise.controller.configuration.models.Configuration;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.enginecore.carerecord.SourceStatistics;
import org.endeavourhealth.enterprise.enginecore.communication.WorkerQueue;
import org.endeavourhealth.enterprise.enginecore.communication.WorkerQueueBatchMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

class WorkerQueueWrapper {
    private final Configuration configuration;
    private final UUID jobUuid;
    private String workerQueueName;

    private final static Logger logger = LoggerFactory.getLogger(WorkerQueueWrapper.class);

    WorkerQueueWrapper(Configuration configuration, UUID jobUuid) {

        this.configuration = configuration;
        this.jobUuid = jobUuid;
    }

    public void createAndPopulate(SourceStatistics primaryTableStats, JobProgressTracker jobProgressTracker) throws Exception {

        QueueConnectionProperties connectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());
        long minimumId = primaryTableStats.getMinimumId();
        long maximumId;
        workerQueueName = WorkerQueue.calculateWorkerQueueName(configuration.getMessageQueuing().getWorkerQueuePrefix(), jobUuid);

        try (WorkerQueue queue = new WorkerQueue(connectionProperties, workerQueueName)) {

            queue.create();

            while (true) {

                maximumId = minimumId + configuration.getPatientBatchSize() - 1;

                if (maximumId > primaryTableStats.getMaximumId())
                    maximumId = primaryTableStats.getMaximumId();

                WorkerQueueBatchMessage message = WorkerQueueBatchMessage.CreateAsNew(
                        jobUuid,
                        minimumId,
                        maximumId);

                queue.sendMessage(message);
                jobProgressTracker.registerWorkerItemStartId(minimumId);

                if (maximumId >= primaryTableStats.getMaximumId())
                    break;

                minimumId = maximumId + 1;
            }

            String message = String.format("Added IDs %s to %s in %s batches to Worker queue", primaryTableStats.getMinimumId(), primaryTableStats.getMaximumId(), jobProgressTracker.getTotalNumberOfBatches());
            queue.logDebug(message);
        }
    }

    public void purge() {

        QueueConnectionProperties connectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());

        try (WorkerQueue queue = new WorkerQueue(connectionProperties, workerQueueName)) {
            queue.purge();
        } catch (Exception e) {
            logger.error("Error while purging worker queue: " + workerQueueName, e);
        }
    }
}
