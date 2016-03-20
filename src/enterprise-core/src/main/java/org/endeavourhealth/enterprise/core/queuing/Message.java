package org.endeavourhealth.enterprise.core.queuing;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.LongString;

import java.util.UUID;

public class Message {
    private AMQP.BasicProperties properties;
    private String body;

    public Message(AMQP.BasicProperties properties, String body) {
        this.properties = properties;
        this.body = body;
    }

    public AMQP.BasicProperties getProperties() {
        return properties;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBodyAsByteArray() {
        if (body != null)
            return body.getBytes();
        else
            return null;
    }

    public String getMandatoryHeaderAsString(String key) {
        Object valueAsObject = properties.getHeaders().get(key);
        LongString valueAsLongString = (LongString)valueAsObject;
        return valueAsLongString.toString();
    }

    public UUID getMandatoryHeaderAsUUID(String key) {
        return UUID.fromString(getMandatoryHeaderAsString(key));
    }
}
