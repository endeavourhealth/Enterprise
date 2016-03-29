package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodeQueue;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesStartMessage;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.processornode.configuration.models.Configuration;
import org.endeavourhealth.enterprise.processornode.configuration.ConfigurationAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

class ProcessorNodeMain implements AutoCloseable, ProcessorNodeQueue.IProcessorNodeMessageReceiver {

    private UUID processorNodeUuid = UUID.randomUUID();
    private Configuration configuration;
    private ProcessorNodeQueue queue;
    private final static Logger logger = LoggerFactory.getLogger(ProcessorNodeMain.class);
    private ExecutionController executionController;

    public void start() throws Exception {

        loadConfiguration();
        registerProcessorNodeQueue();
    }

    private void loadConfiguration() throws Exception {
        ConfigurationAPI configApi = new ConfigurationAPI();
        configuration = configApi.loadConfiguration();
    }

    private void registerProcessorNodeQueue() throws IOException, TimeoutException {
        QueueConnectionProperties connectionProperties = ConfigurationAPI.convertConnection(configuration.getMessageQueuing());
        String queueName = ProcessorNodeQueue.calculateQueueName(configuration.getMessageQueuing().getProcessorNodeQueuePrefix(), processorNodeUuid);
        queue = new ProcessorNodeQueue(connectionProperties, queueName, configuration.getMessageQueuing().getProcessorNodesExchangeName());
        queue.registerReceiver(this);
    }

    @Override
    public void receiveStartMessage(ProcessorNodesStartMessage.StartMessagePayload startMessage) {
        logger.info("Processor " + processorNodeUuid.toString() + " received start message for job " + startMessage.getJobUuid().toString());

        try {

            if (executionController != null) {
                executionController.shutDown();
                executionController.close();
                executionController = null;
            }

            executionController = new ExecutionController(configuration, startMessage);

            executionController.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (queue != null)
            queue.close();

        if (executionController != null)
            executionController.close();
    }
}
