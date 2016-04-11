package org.endeavourhealth.enterprise.controller.configuration;

import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseConnectionDetails;
import org.endeavourhealth.enterprise.controller.configuration.models.*;
import org.endeavourhealth.enterprise.core.queuing.QueueConnectionProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationAPI {

    public Configuration loadConfiguration() throws Exception {

        Configuration config = getConfiguration();

        if (config.getDebugging() == null)
            config.setDebugging(new Debugging());

        Debugging debugging = config.getDebugging();

        if (debugging.isStartImmediately() == null)
            debugging.setStartImmediately(false);

        if (debugging.isMarkRequestAsComplete() == null)
            debugging.setMarkRequestAsComplete(true);

        return config;
    }

    public static DatabaseConnectionDetails convertConnection(DatabaseConnection source) {
        return new DatabaseConnectionDetails(
                source.getUrl(),
                source.getUsername(),
                source.getPassword());
    }

    public static QueueConnectionProperties convertConnection(MessageQueuing configuration) {
        return new QueueConnectionProperties(
                configuration.getIpAddress(),
                configuration.getUsername(),
                configuration.getPassword());
    }

    private static Configuration getConfiguration() throws Exception {
        //"src/discovery-controller/src/main/resources/controller.config";

        String configLocation = System.getProperty("config.file");

        Configuration configuration;

        if (configLocation == null)
            configuration = XmlSerializer.deserializeFromResource(Configuration.class, "controller.config", "Controller.xsd");
        else {
            Path file = Paths.get(configLocation);
            configuration = XmlSerializer.deserializeFromFile(Configuration.class, file, "Controller.xsd");
        }

        return configuration;
    }
}
