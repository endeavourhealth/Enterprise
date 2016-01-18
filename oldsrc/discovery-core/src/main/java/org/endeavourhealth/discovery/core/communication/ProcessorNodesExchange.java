package org.endeavourhealth.discovery.core.communication;

import org.endeavourhealth.discovery.core.utilities.queuing.ChannelFacade;
import org.endeavourhealth.discovery.core.utilities.queuing.ConnectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorNodesExchange {

    private final ConnectionProperties connectionProperties;
    private final String processorNodesExchangeName;
    private final static Logger logger = LoggerFactory.getLogger(ProcessorNodesExchange.class);

    public ProcessorNodesExchange(ConnectionProperties connectionProperties, String processorNodesExchangeName) {
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
