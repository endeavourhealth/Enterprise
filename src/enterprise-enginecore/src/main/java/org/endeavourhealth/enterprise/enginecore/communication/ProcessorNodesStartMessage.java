package org.endeavourhealth.enterprise.enginecore.communication;

import com.rabbitmq.client.AMQP;
import org.endeavourhealth.enterprise.core.JsonSerializer;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseConnectionDetails;
import org.endeavourhealth.enterprise.core.queuing.Message;

import java.util.UUID;

public class ProcessorNodesStartMessage {

    private Message message;
    private StartMessagePayload payload;
    private static final String type = "start";

    public Message getMessage() {
        return message;
    }

    public StartMessagePayload getPayload() {
        return payload;
    }

    public static class StartMessagePayload {
        private UUID jobUuid;
        private String workerQueueName;
        private DatabaseConnectionDetails coreDatabaseConnectionDetails;
        private DatabaseConnectionDetails careRecordDatabaseConnectionDetails;
        private String controllerQueueName;

        public UUID getJobUuid() {
            return jobUuid;
        }

        public void setJobUuid(UUID jobUuid) {
            this.jobUuid = jobUuid;
        }

        public String getWorkerQueueName() {
            return workerQueueName;
        }

        public void setWorkerQueueName(String workerQueueName) {
            this.workerQueueName = workerQueueName;
        }

        public DatabaseConnectionDetails getCoreDatabaseConnectionDetails() {
            return coreDatabaseConnectionDetails;
        }

        public void setCoreDatabaseConnectionDetails(DatabaseConnectionDetails coreDatabaseConnectionDetails) {
            this.coreDatabaseConnectionDetails = coreDatabaseConnectionDetails;
        }

        public DatabaseConnectionDetails getCareRecordDatabaseConnectionDetails() {
            return careRecordDatabaseConnectionDetails;
        }

        public void setCareRecordDatabaseConnectionDetails(DatabaseConnectionDetails careRecordDatabaseConnectionDetails) {
            this.careRecordDatabaseConnectionDetails = careRecordDatabaseConnectionDetails;
        }

        public String getControllerQueueName() {
            return controllerQueueName;
        }

        public void setControllerQueueName(String controllerQueueName) {
            this.controllerQueueName = controllerQueueName;
        }
    }

    private ProcessorNodesStartMessage(Message message) {
        this.message = message;
        this.payload = JsonSerializer.deserialize(message.getBody(), StartMessagePayload.class);
    }

    public static ProcessorNodesStartMessage CreateFromMessage(Message message) {
        return new ProcessorNodesStartMessage(message);
    }

    public static ProcessorNodesStartMessage CreateAsNew(StartMessagePayload payload) {

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

        AMQP.BasicProperties properties = builder
                .type(type)
                .build();

        String serialisedMessage = JsonSerializer.serialize(payload);

        Message newMessage = new Message(properties, serialisedMessage);

        return new ProcessorNodesStartMessage(newMessage);
    }

    public static boolean isTypeOf(Message message) {
        if (type.equals(message.getProperties().getType()))
            return true;
        else
            return false;
    }
}
