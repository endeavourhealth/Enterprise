package org.endeavourhealth.enterprise.core.requestParameters;

import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.requestParameters.models.ObjectFactory;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public abstract class RequestParametersSerializer {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static RequestParameters readFromJobReport(DbJobReport jobReport) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return readFromXml(jobReport.getParameters());
    }
    public static RequestParameters readFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return XmlSerializer.readFromXml(RequestParameters.class, xml);
    }

    public static String writeToXml(RequestParameters r) {
        JAXBElement element = objectFactory.createRequestParameters(r);
        return XmlSerializer.writeObjectToXml(element);
    }



}
