package org.endeavourhealth.enterprise.core.requestParameters;

import org.endeavourhealth.enterprise.core.AbstractXmlParser;
import org.endeavourhealth.enterprise.core.entity.database.DbJobReport;
import org.endeavourhealth.enterprise.core.requestParameters.models.ObjectFactory;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by Drew on 19/03/2016.
 */
public abstract class RequestParametersParser extends AbstractXmlParser {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static RequestParameters readFromJobReport(DbJobReport jobReport) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return readFromXml(jobReport.getParameters());
    }
    public static RequestParameters readFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return readFromXml(RequestParameters.class, xml);
    }

    public static String writeToXml(RequestParameters r) {
        JAXBElement element = objectFactory.createRequestParameters(r);
        return writeObjectToXml(element);
    }



}
