package org.endeavourhealth.enterprise.enginecore.communication;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.endeavourhealth.enterprise.core.queuing.ChannelFacade;
import org.endeavourhealth.enterprise.core.queuing.Message;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ControllerQueue implements AutoCloseable {

    private final ChannelFacade channel;
    private final String queueName;
    private final static Logger logger = LoggerFactory.getLogger(ControllerQueue.class);

    public ControllerQueue(
            QueueConnectionProperties connectionProperties,
            String queueName) throws IOException, TimeoutException {

        this.queueName = queueName;
        channel = new ChannelFacade(connectionProperties);
    }

    public void logDebug(String message) {
        logger.debug(message);
    }

    @Override
    public void close() throws Exception {
        if (channel != null)
            channel.close();
    }

    public void registerReceiver(IControllerQueueMessageReceiver receiver) throws IOException {

        final Consumer consumer = new DefaultConsumer(channel.getInternalChannel()) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                Message message = new Message(properties, new String(body));

                if (ControllerQueueWorkItemCompleteMessage.isTypeOf(message)) {
                    ControllerQueueWorkItemCompleteMessage realMessage = ControllerQueueWorkItemCompleteMessage.CreateFromMessage(message);
                    receiver.receiveWorkerItemCompleteMessage(realMessage.getPayload());
                } else {
                    throw new UnsupportedOperationException("Message type not supported: " + properties.getType());
                }

                channel.basicAck(envelope.getDeliveryTag());
            }
        };

        channel.basicConsume(queueName, consumer);
    }

    public void sendMessage(ControllerQueueWorkItemCompleteMessage message) throws IOException {
        channel.basicPublish("", queueName, message.getMessage());
    }

    public interface IControllerQueueMessageReceiver {
        void receiveWorkerItemCompleteMessage(ControllerQueueWorkItemCompleteMessage.WorkItemCompletePayload payload);
    }
}
