package org.endeavourhealth.enterprise.enginecore.communication;

import com.rabbitmq.client.AMQP;
import org.endeavourhealth.enterprise.core.JsonSerializer;
import org.endeavourhealth.enterprise.core.queuing.Message;

import java.util.UUID;

public class ProcessorNodesStopMessage {

    private Message message;
    private StopMessagePayload payload;
    private static final String type = "stop";

    public Message getMessage() {
        return message;
    }

    public StopMessagePayload getPayload() {
        return payload;
    }

    public static class StopMessagePayload {
        private UUID jobUuid;

        public UUID getJobUuid() {
            return jobUuid;
        }

        public void setJobUuid(UUID jobUuid) {
            this.jobUuid = jobUuid;
        }
    }

    private ProcessorNodesStopMessage(Message message) {
        this.message = message;
        this.payload = JsonSerializer.deserialize(message.getBody(), StopMessagePayload.class);
    }

    public static ProcessorNodesStopMessage CreateFromMessage(Message message) {
        return new ProcessorNodesStopMessage(message);
    }

    public static ProcessorNodesStopMessage CreateAsNew(StopMessagePayload payload) {

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

        AMQP.BasicProperties properties = builder
                .type(type)
                .build();

        String serialisedMessage = JsonSerializer.serialize(payload);

        Message newMessage = new Message(properties, serialisedMessage);

        return new ProcessorNodesStopMessage(newMessage);
    }

    public static boolean isTypeOf(Message message) {
        if (type.equals(message.getProperties().getType()))
            return true;
        else
            return false;
    }
}
