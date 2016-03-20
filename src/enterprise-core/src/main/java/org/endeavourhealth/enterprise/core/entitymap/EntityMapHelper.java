package org.endeavourhealth.enterprise.core.entitymap;

import org.endeavourhealth.enterprise.core.AbstractXmlParser;
import org.endeavourhealth.enterprise.core.Resources;
import org.endeavourhealth.enterprise.core.entitymap.models.Entity;
import org.endeavourhealth.enterprise.core.entitymap.models.EntityMap;

public class EntityMapHelper extends AbstractXmlParser {

    public static EntityMap loadEntityMap() throws Exception {

        String xml = Resources.getResourceAsString("entitymap.xml");
        //"src/discovery-controller/src/main/resources/controller.config";

        return readFromXml(EntityMap.class, xml);
    }
//
//    public static Entity getPrimaryEntity(EntityMap map) {
//        return map.getEntity().get(0);
//    }
}
