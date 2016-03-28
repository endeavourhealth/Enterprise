package org.endeavourhealth.enterprise.enginecore.communication;

import org.endeavourhealth.enterprise.core.queuing.ChannelFacade;
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

        try (ChannelFacade channel = new ChannelFacade(connectionProperties)) {

            channel.basicPublish(processorNodesExchangeName, "", message.getMessage());
            logger.debug("Start message sent");
        }
    }
}
