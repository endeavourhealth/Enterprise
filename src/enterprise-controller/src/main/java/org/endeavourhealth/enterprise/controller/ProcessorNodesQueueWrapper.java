package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.controller.configuration.ConfigurationAPI;
import org.endeavourhealth.enterprise.controller.configuration.models.Configuration;
import org.endeavourhealth.enterprise.core.FtpWrapper;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesExchange;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesStartMessage;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesStopMessage;
import org.endeavourhealth.enterprise.enginecore.communication.WorkerQueue;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseConnectionDetails;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

class ProcessorNodesQueueWrapper {
    private final Configuration configuration;
    private final UUID executionUuid;

    public ProcessorNodesQueueWrapper(Configuration configuration, UUID executionUuid) {
        this.configuration = configuration;
        this.executionUuid = executionUuid;
    }

    public void startProcessorNodes() throws Exception {
        QueueConnectionProperties queueConnectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());
        String workerQueueName = WorkerQueue.calculateWorkerQueueName(configuration.getMessageQueuing().getWorkerQueuePrefix(), executionUuid);
        DatabaseConnectionDetails coreDatabaseConnectionDetails = ConfigurationAPI.convertConnection(configuration.getCoreDatabase());
        DatabaseConnectionDetails careRecordDatabaseConnectionDetails = ConfigurationAPI.convertConnection(configuration.getCareRecordDatabase());

        ProcessorNodesStartMessage.StartMessagePayload payload = new ProcessorNodesStartMessage.StartMessagePayload();
        payload.setJobUuid(executionUuid);
        payload.setWorkerQueueName(workerQueueName);
        payload.setCoreDatabaseConnectionDetails(coreDatabaseConnectionDetails);
        payload.setCareRecordDatabaseConnectionDetails(careRecordDatabaseConnectionDetails);
        payload.setControllerQueueName(configuration.getMessageQueuing().getControllerQueueName());

        Path path = Paths.get(configuration.getOutputFiles().getStreamingFolder(), executionUuid.toString());
        payload.setStreamingFolder(path.toString());

        if (configuration.getOutputFiles().getFtpConnection() != null) {
            FtpWrapper.FtpConnectionDetails ftpConnectionDetails = new FtpWrapper.FtpConnectionDetails(
                    configuration.getOutputFiles().getFtpConnection().getHost(),
                    configuration.getOutputFiles().getFtpConnection().getUsername(),
                    configuration.getOutputFiles().getFtpConnection().getPassword());

            payload.setFtpConnectionDetails(ftpConnectionDetails);
        }

        ProcessorNodesStartMessage message = ProcessorNodesStartMessage.CreateAsNew(payload);

        ProcessorNodesExchange exchange = new ProcessorNodesExchange(queueConnectionProperties, configuration.getMessageQueuing().getProcessorNodesExchangeName());
        exchange.sendMessage(message);
    }

    public void stopProcessorNodes() throws Exception {
        QueueConnectionProperties queueConnectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());

        ProcessorNodesStopMessage.StopMessagePayload payload = new ProcessorNodesStopMessage.StopMessagePayload();
        payload.setJobUuid(executionUuid);

        ProcessorNodesStopMessage message = ProcessorNodesStopMessage.CreateAsNew(payload);

        ProcessorNodesExchange exchange = new ProcessorNodesExchange(queueConnectionProperties, configuration.getMessageQueuing().getProcessorNodesExchangeName());
        exchange.sendMessage(message);
    }
}
