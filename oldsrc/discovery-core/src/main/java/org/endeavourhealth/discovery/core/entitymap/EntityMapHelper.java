package org.endeavourhealth.discovery.core.entitymap;

import com.google.gson.Gson;
import org.endeavourhealth.discovery.core.utilities.XmlSerializer;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;

public class EntityMapHelper {
    public static String serialise(EntityMap source) {
        Gson gson = new Gson();
        String result = gson.toJson(source);
        return result;
    }

    public static EntityMap deserialise(String json) {
        Gson gson = new Gson();
        EntityMap result = gson.fromJson(json, EntityMap.class);
        return result;
    }

    public static EntityMap loadEntityMap() throws JAXBException, SAXException, IOException, URISyntaxException {

        XmlSerializer ser = new XmlSerializer();
        EntityMap map = ser.deserializeFromResource(EntityMap.class, "EntityMap.xsd", "src/discovery-core/src/main/resources/EntityMap.xml");
        return map;
    }

    public static Entity getPrimaryEntity(EntityMap map) {
        return map.getEntity().get(0);
    }
}
