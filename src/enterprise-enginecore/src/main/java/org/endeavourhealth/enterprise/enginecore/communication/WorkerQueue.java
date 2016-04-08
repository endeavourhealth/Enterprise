package org.endeavourhealth.enterprise.enginecore.communication;

import com.rabbitmq.client.GetResponse;
import org.endeavourhealth.enterprise.core.queuing.ChannelFacade;
import org.endeavourhealth.enterprise.core.queuing.Message;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class WorkerQueue implements AutoCloseable {

    private final ChannelFacade channel;
    private final String workerQueueName;
    private final static Logger logger = LoggerFactory.getLogger(WorkerQueue.class);

    private Long deliveryTag;

    public WorkerQueue(QueueConnectionProperties connectionProperties, String workerQueueName) throws IOException, TimeoutException {
        channel = new ChannelFacade(connectionProperties);
        this.workerQueueName = workerQueueName;
    }

    public void create() throws IOException {
        channel.queueCreate(workerQueueName, 7L);
    }

    public void sendMessage(WorkerQueueBatchMessage message) throws IOException {
        channel.publishDirectlyToQueue(workerQueueName, message.getMessage());
    }

    public void logDebug(String message) {
        logger.debug(message);
    }

    @Override
    public void close() throws Exception {
        if (channel != null)
            channel.close();
    }

    public static String calculateWorkerQueueName(String prefix, UUID executionUuid) {
        if (!prefix.endsWith("."))
            prefix = prefix + ".";

        return prefix + executionUuid.toString();
    }

    public WorkerQueueBatchMessage getNextMessage() throws IOException {

        if (deliveryTag != null)
            throw new InvalidStateException("DeliveryTag is not null");

        GetResponse response = channel.basicGet(workerQueueName);

        if (response == null)
            return  null;

        //if (response.getEnvelope().isRedeliver())

        deliveryTag = response.getEnvelope().getDeliveryTag();

        Message message = new Message(response.getProps(), new String(response.getBody()));

        if (WorkerQueueBatchMessage.isTypeOf(message)) {
            return WorkerQueueBatchMessage.CreateFromMessage(message);
        } else {
            throw new UnsupportedOperationException("Message type not supported: " + response.getProps().getType());
        }
    }

    public void acknowledgePreviousMessage() throws IOException {
        if (deliveryTag == null)
            return;

        channel.basicAck(deliveryTag);
        deliveryTag = null;
    }
}
