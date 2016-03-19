package org.endeavourhealth.enterprise.core.requestParameters;

import org.endeavourhealth.enterprise.core.AbstractParser;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;
import org.endeavourhealth.enterprise.core.requestParameters.models.ObjectFactory;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by Drew on 19/03/2016.
 */
public abstract class RequestParametersParser extends AbstractParser {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static RequestParameters readFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return readFromXml(RequestParameters.class, xml);
    }

    public static String writeToXml(RequestParameters r) {
        JAXBElement element = objectFactory.createRequestParameters(r);
        return writeObjectToXml(element);
    }



}
