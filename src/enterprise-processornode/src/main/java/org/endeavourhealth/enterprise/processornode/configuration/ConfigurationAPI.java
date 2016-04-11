package org.endeavourhealth.enterprise.processornode.configuration;

import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.processornode.configuration.models.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationAPI {

    public Configuration loadConfiguration() throws Exception {

        String configLocation = System.getProperty("config.file");

        Configuration configuration;

        if (configLocation == null)
            configuration = XmlSerializer.deserializeFromResource(Configuration.class, "processornode.config", "ProcessorNode.xsd");
        else {
            Path file = Paths.get(configLocation);
            configuration = XmlSerializer.deserializeFromFile(Configuration.class, file, "ProcessorNode.xsd");
        }

        if (configuration.getDebugging() == null)
            configuration.setDebugging(new Debugging());

        return configuration;
    }

    public static QueueConnectionProperties convertConnection(MessageQueuing configuration) {
        return new QueueConnectionProperties(
                configuration.getIpAddress(),
                configuration.getUsername(),
                configuration.getPassword());
    }
}
