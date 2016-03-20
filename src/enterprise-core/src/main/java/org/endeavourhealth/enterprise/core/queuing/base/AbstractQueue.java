package org.endeavourhealth.enterprise.core.queuing.base;

import com.rabbitmq.client.*;
import org.endeavourhealth.enterprise.core.queuing.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractQueue implements AutoCloseable {

    private QueueProperties properties;
    private Connection connection;
    private Channel channel;

    protected void openChannel(QueueProperties properties) throws IOException, TimeoutException {
        this.properties = properties;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(properties.getIpAddress());

        factory.setUsername("andy");
        factory.setPassword("!QAZ1qaz");

        connection = factory.newConnection();
        channel = connection.createChannel();

        boolean durable = true;
        boolean exclusive = false;
        boolean autoDelete = false;
        java.util.Map<String,Object> arguments = null;

        channel.queueDeclare(properties.getQueueName(), durable, exclusive, autoDelete, arguments);
        channel.basicQos(1);  //only allow a single unacknowledged message to be pre-fetched from the server
    }

    protected void readQueueBase(Consumer.DeliveryReceiver receiver) throws IOException {
        Consumer consumer = new Consumer(channel, receiver);

        boolean autoAck = false;
        String consumerTag = channel.basicConsume(properties.getQueueName(), autoAck, consumer);
    }

    protected void addMessageBase(Message coreMessage) throws IOException {
        String exchange = "";
        channel.basicPublish(exchange, properties.getQueueName(), coreMessage.getProperties(), coreMessage.getBodyAsByteArray());
    }

    @Override
    public void close() throws Exception {
        if (channel != null && channel.isOpen()) {
            channel.close();
            channel = null;
        }

        if (connection != null && connection.isOpen()) {
            connection.close();
            connection = null;
        }
    }
}
