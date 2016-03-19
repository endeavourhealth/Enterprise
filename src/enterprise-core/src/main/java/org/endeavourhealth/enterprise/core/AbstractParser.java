package org.endeavourhealth.enterprise.core;

import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
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
public abstract class AbstractParser {

    protected static <T> T readFromXml(Class cls, String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {

        //parse XML string into DOM
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(is);
        org.w3c.dom.Element varElement = document.getDocumentElement();

        String name = varElement.getNodeName();
        if (name.equalsIgnoreCase("report")) {
            return readObjectFromXml(Report.class, document);
        } else if (name.equalsIgnoreCase("libraryItem")) {
            return readObjectFromXml(LibraryItem.class, document);
        } else if (name.equalsIgnoreCase("queryDocument")) {
            return readObjectFromXml(QueryDocument.class, document);
        } else {
            throw new RuntimeException("Unexpected root node " + name);
        }
    }

    protected static <T> T readObjectFromXml(Class cls, Document doc) throws ParserConfigurationException, JAXBException, IOException, SAXException {

        //parse DOM into POJOs
        JAXBContext context = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<T> loader = unmarshaller.unmarshal(doc, cls);
        return loader.getValue();
    }

    protected static String writeObjectToXml(JAXBElement element) {
        StringWriter sw = new StringWriter();
        Class cls = element.getValue().getClass();

        try {
            JAXBContext context = JAXBContext.newInstance(cls);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); //just makes output easier to read
            marshaller.marshal(element, sw);

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        //TODO: validate XML
/*        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsd));
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));*/

        String ret = sw.toString();



        return ret;
    }

}
