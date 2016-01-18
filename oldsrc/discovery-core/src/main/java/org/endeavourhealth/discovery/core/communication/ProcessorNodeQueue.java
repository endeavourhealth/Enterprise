package org.endeavourhealth.discovery.core.communication;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.endeavourhealth.discovery.core.execution.ExecutionException;
import org.endeavourhealth.discovery.core.utilities.queuing.ChannelFacade;
import org.endeavourhealth.discovery.core.utilities.queuing.ConnectionProperties;
import org.endeavourhealth.discovery.core.utilities.queuing.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ProcessorNodeQueue implements AutoCloseable {

    private final ChannelFacade channel;
    private final String queueName;
    private final static Logger logger = LoggerFactory.getLogger(ProcessorNodeQueue.class);

    public ProcessorNodeQueue(
            ConnectionProperties connectionProperties,
            String queueName,
            String exchangeName) throws IOException, TimeoutException {

        this.queueName = queueName;

        channel = new ChannelFacade(connectionProperties);
        channel.queueCreate(queueName, null);
        channel.queueBind(queueName, exchangeName);
    }

    public void logDebug(String message) {
        logger.debug(message);
    }

    @Override
    public void close() throws Exception {
        if (channel != null)
            channel.close();
    }

    public static String calculateQueueName(String prefix, UUID processorNodeUuid) {
        if (!prefix.endsWith("."))
            prefix = prefix + ".";

        return prefix + processorNodeUuid.toString();
    }

    public void registerReceiver(IProcessorNodeMessageReceiver receiver) throws IOException {

        final Consumer consumer = new DefaultConsumer(channel.getInternalChannel()) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                Message message = new Message(properties, new String(body));

                if (ProcessorNodesStartMessage.isTypeOf(message)) {
                    ProcessorNodesStartMessage startMessage = ProcessorNodesStartMessage.CreateFromMessage(message);
                    receiver.receiveStartMessage(startMessage.getPayload());
                } else {
                    throw new UnsupportedOperationException("Message type not supported: " + properties.getType());
                }

                channel.basicAck(envelope.getDeliveryTag());
            }
        };

        channel.basicConsume(queueName, consumer);
    }

    public interface IProcessorNodeMessageReceiver {
        void receiveStartMessage(ProcessorNodesStartMessage.StartMessagePayload startMessage);
    }
}
