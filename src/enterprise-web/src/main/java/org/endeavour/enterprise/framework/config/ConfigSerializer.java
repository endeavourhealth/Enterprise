package org.endeavour.enterprise.framework.config;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.endeavour.enterprise.framework.config.models.Config;
import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Drew on 22/03/2016.
 */
public abstract class ConfigSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigSerializer.class);
    private static final String XSD = "Config.xsd";
    private static final String XML = "Config.xml";

    private static Config config = null;

    public static Config getConfig() {
        if (config == null) {
            try {
                config = deserializeConfig();
            } catch (Exception ex) {
                LOG.error("Error reading config", ex);
            }
        }
        return config;
    }

    private static Config deserializeConfig() throws Exception {

//        URL url = Config.class.getClassLoader().getResource(XML);
//        String xml = Resources.toString(url, Charsets.UTF_8);
        //String xml = com.google.common.io.Resources.toString(url, Charsets.UTF_8);

        return XmlSerializer.deserializeFromResource(Config.class, XML, XSD);
    }
}
