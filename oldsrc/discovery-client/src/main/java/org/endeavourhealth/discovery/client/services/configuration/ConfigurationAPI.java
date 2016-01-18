package org.endeavourhealth.discovery.client.services.configuration;

import org.endeavourhealth.discovery.core.database.DatabaseConnectionDetails;
import org.endeavourhealth.discovery.core.utilities.XmlSerializer;
import org.endeavourhealth.discovery.core.utilities.queuing.ConnectionProperties;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;

public class ConfigurationAPI {

    private static Configuration instance;
    private static DatabaseConnectionDetails coreConnectionDetails;

    public static void initialise(String fullName) throws JAXBException, SAXException, IOException, URISyntaxException {

        XmlSerializer ser = new XmlSerializer();
        Configuration config = ser.deserializeFromResource(Configuration.class, "client.xsd", fullName);
        instance = config;
        coreConnectionDetails = convertConnection(config.getCoreDatabase());
    }

    public static Configuration getInstance() {
        return instance;
    }

    private static DatabaseConnectionDetails convertConnection(DatabaseConnection source) {
        return new DatabaseConnectionDetails(
                source.getIpAddress(),
                source.getPort(),
                source.getDatabaseName(),
                source.getUsername(),
                source.getPassword());
    }

    public static DatabaseConnectionDetails getCoreConnectionDetails() {
        return coreConnectionDetails;
    }
}
