package org.endeavourhealth.enterprise.enginecore.communication;

import org.endeavourhealth.enterprise.core.queuing.ChannelFacade;
import org.endeavourhealth.enterprise.core.queuing.Message;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorNodesExchange {

    private final QueueConnectionProperties connectionProperties;
    private final String processorNodesExchangeName;
    private final static Logger logger = LoggerFactory.getLogger(ProcessorNodesExchange.class);

    public ProcessorNodesExchange(QueueConnectionProperties connectionProperties, String processorNodesExchangeName) {
        this.connectionProperties = connectionProperties;
        this.processorNodesExchangeName = processorNodesExchangeName;
    }

    public void sendMessage(ProcessorNodesStartMessage message) throws Exception {
        internalSendMessage(message.getMessage());
        logger.debug("Start message sent");
    }

    public void sendMessage(ProcessorNodesStopMessage message) throws Exception {
        internalSendMessage(message.getMessage());
        logger.debug("Stop message sent");
    }

    private void internalSendMessage(Message message) throws Exception {
        try (ChannelFacade channel = new ChannelFacade(connectionProperties)) {
            channel.publishToExchange(processorNodesExchangeName, message);
        }
    }
}
