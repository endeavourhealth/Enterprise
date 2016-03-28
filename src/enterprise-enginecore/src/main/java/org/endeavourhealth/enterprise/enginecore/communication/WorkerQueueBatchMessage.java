package org.endeavourhealth.enterprise.enginecore.communication;

import com.rabbitmq.client.AMQP;
import org.endeavourhealth.enterprise.core.JsonSerializer;
import org.endeavourhealth.enterprise.core.queuing.Message;

import java.util.UUID;

public class WorkerQueueBatchMessage {

    private Message message;
    private BatchMessagePayload payload;
    private static final String type = "batch";

    public Message getMessage() {
        return message;
    }

    public BatchMessagePayload getPayload() {
        return payload;
    }

    public static class BatchMessagePayload {
        private UUID executionUuid;
        private long minimumId;
        private long maximumId;

        public UUID getExecutionUuid() {
            return executionUuid;
        }

        public void setExecutionUuid(UUID executionUuid) {
            this.executionUuid = executionUuid;
        }

        public long getMinimumId() {
            return minimumId;
        }

        public void setMinimumId(long minimumId) {
            this.minimumId = minimumId;
        }

        public long getMaximumId() {
            return maximumId;
        }

        public void setMaximumId(long maximumId) {
            this.maximumId = maximumId;
        }
    }

    public static boolean isTypeOf(Message message) {
        if (type.equals(message.getProperties().getType()))
            return true;
        else
            return false;
    }

    private WorkerQueueBatchMessage(Message message) {
        this.message = message;
        this.payload = JsonSerializer.deserialize(message.getBody(), BatchMessagePayload.class);
    }

    public static WorkerQueueBatchMessage CreateFromMessage(Message message) {
        return new WorkerQueueBatchMessage(message);
    }

    public static WorkerQueueBatchMessage CreateAsNew(
            UUID executionUuid,
            long minimumId,
            long maximumId) {

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

        AMQP.BasicProperties properties = builder
                .type(type)
                .build();

        BatchMessagePayload payload = new BatchMessagePayload();
        payload.setExecutionUuid(executionUuid);
        payload.setMinimumId(minimumId);
        payload.setMaximumId(maximumId);

        String serialisedMessage = JsonSerializer.serialize(payload);

        Message newMessage = new Message(properties, serialisedMessage);

        return new WorkerQueueBatchMessage(newMessage);
    }
}
