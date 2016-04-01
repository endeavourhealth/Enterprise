package org.endeavourhealth.enterprise.enginecore.resultcounts;

import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ObjectFactory;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ResultCounts;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ResultCountsHelper {
    public static ResultCounts deserializeFromString(String xml) throws ParserConfigurationException, IOException, SAXException, JAXBException {

        return XmlSerializer.deserializeFromString(ResultCounts.class, xml, "ResultCounts.xsd");
    }

    public static String serialise(ResultCounts resultCounts) {
        JAXBElement element = new ObjectFactory().createResultCounts(resultCounts);
        return XmlSerializer.serializeToString(element, "ResultCounts.xsd");
    }
}
