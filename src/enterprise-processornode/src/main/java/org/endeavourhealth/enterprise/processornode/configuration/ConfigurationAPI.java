package org.endeavourhealth.enterprise.processornode.configuration;

import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;
import org.endeavourhealth.enterprise.processornode.configuration.models.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;

public class ConfigurationAPI {

    public Configuration loadConfiguration() throws Exception {

        Configuration config = XmlSerializer.deserializeFromResource(Configuration.class, "processornode.config", "ProcessorNode.xsd");

        if (config.getDebugging() == null)
            config.setDebugging(new Debugging());

        return config;
    }

    public static QueueConnectionProperties convertConnection(MessageQueuing configuration) {
        QueueConnectionProperties connectionProperties = new QueueConnectionProperties(
                configuration.getIpAddress(),
                configuration.getUsername(),
                configuration.getPassword());

        return connectionProperties;
    }
}
