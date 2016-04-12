package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodeQueue;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesStartMessage;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesStopMessage;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseConnectionDetails;
import org.endeavourhealth.enterprise.processornode.configuration.models.Configuration;
import org.endeavourhealth.enterprise.processornode.configuration.ConfigurationAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

class ProcessorNodeMain implements AutoCloseable, ProcessorNodeQueue.IProcessorNodeMessageReceiver {

    private UUID processorNodeUuid = UUID.randomUUID();
    private Configuration configuration;
    private ProcessorNodeQueue queue;
    private final static Logger logger = LoggerFactory.getLogger(ProcessorNodeMain.class);
    private ExecutionController executionController;

    public void start() throws Exception {
        logger.info("Processor node: " + processorNodeUuid.toString());

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


        Path path = Paths.get("H:\\Deletable\\", "Temp", "Test.csv");

        try(FileWriter fw = new FileWriter(path.toString(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println("the text");
            //more code
            out.println("more text");
            //more code
        } catch (IOException e) {
            logger.error("Writing to file: " + e);
        }

//
//        logger.info("Processor " + processorNodeUuid.toString() + " received start message for job " + startMessage.getJobUuid().toString());
//
//        try {
//
//            if (executionController != null) {
//                executionController.shutDown();
//                executionController.close();
//                executionController = null;
//            }
//
//            initialiseDatabaseManager(startMessage.getCoreDatabaseConnectionDetails());
//
//            executionController = new ExecutionController(processorNodeUuid, configuration, startMessage);
//
//            executionController.start();
//        } catch (Exception e) {
//
//            if (executionController != null)
//                executionController.errorOccurred(e);
//
//            logger.error("Processor " + processorNodeUuid.toString(), e);
//        }
    }

    @Override
    public void receiveStopMessage(ProcessorNodesStopMessage.StopMessagePayload stopMessage) {
        logger.info("Processor " + processorNodeUuid.toString() + " received stop message for job " + stopMessage.getJobUuid().toString());

        try {

            if (executionController != null) {

                if (executionController.getJobUuid().equals(stopMessage.getJobUuid())) {
                    logger.info("Processor " + processorNodeUuid.toString() + ".  Stopping job " + stopMessage.getJobUuid().toString());

                    executionController.shutDown();
                    executionController.close();
                    executionController = null;
                }
            }

        } catch (Exception e) {
            logger.error("Processor " + processorNodeUuid.toString(), e);
        }
    }

    private void initialiseDatabaseManager(DatabaseConnectionDetails coreConnectionDetails) {

        DatabaseManager.getInstance().setConnectionProperties(
                coreConnectionDetails.getUrl(),
                coreConnectionDetails.getUsername(),
                coreConnectionDetails.getPassword());
    }

    @Override
    public void close() throws Exception {
        if (queue != null)
            queue.close();

        if (executionController != null)
            executionController.close();
    }
}
