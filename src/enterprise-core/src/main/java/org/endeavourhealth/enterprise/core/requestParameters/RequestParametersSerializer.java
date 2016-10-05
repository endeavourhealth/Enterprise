package org.endeavourhealth.enterprise.core.requestParameters;

import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.core.database.models.JobreportEntity;
import org.endeavourhealth.enterprise.core.requestParameters.models.ObjectFactory;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public abstract class RequestParametersSerializer {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private static final String XSD = "RequestParameters.xsd";

    public static RequestParameters readFromJobReport(JobreportEntity jobReport) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return readFromXml(jobReport.getParameters());
    }
    public static RequestParameters readFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return XmlSerializer.deserializeFromString(RequestParameters.class, xml, XSD);
    }

    public static String writeToXml(RequestParameters r) {
        JAXBElement element = OBJECT_FACTORY.createRequestParameters(r);
        return XmlSerializer.serializeToString(element, XSD);
    }



}
