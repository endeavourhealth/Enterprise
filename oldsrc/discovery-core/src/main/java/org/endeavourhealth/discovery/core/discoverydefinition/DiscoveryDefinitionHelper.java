package org.endeavourhealth.discovery.core.discoverydefinition;

import com.google.gson.Gson;
import org.endeavourhealth.discovery.core.entitymap.Entity;
import org.endeavourhealth.discovery.core.entitymap.EntityMap;
import org.endeavourhealth.discovery.core.utilities.XmlSerializer;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;

public class DiscoveryDefinitionHelper {

    public static DiscoveryDefinition deserialize(String xml) throws JAXBException, SAXException, IOException, URISyntaxException {

        XmlSerializer ser = new XmlSerializer();
        DiscoveryDefinition obj = ser.deserializeFromString(DiscoveryDefinition.class, "DiscoveryDefinition.xsd", xml);
        return obj;
    }

    public static String serialize(DiscoveryDefinition source) throws JAXBException, SAXException, IOException, URISyntaxException {

        XmlSerializer ser = new XmlSerializer();
        String result = ser.serialize(DiscoveryDefinition.class, source);
        return result;
    }
}
