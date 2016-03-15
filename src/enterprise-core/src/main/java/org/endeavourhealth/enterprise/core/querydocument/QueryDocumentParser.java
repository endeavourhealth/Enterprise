package org.endeavourhealth.enterprise.core.querydocument;

import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by Drew on 11/03/2016.
 */
public abstract class QueryDocumentParser {


    public static <T> T readFromXml(Class cls, String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {

        //parse XML string into DOM
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(is);
        org.w3c.dom.Element varElement = document.getDocumentElement();

        String name = varElement.getNodeName();
        if (name.equals("Report")) {
            return readObjectFromXml(Report.class, document);
        } else if (name.equals("LibraryItem")) {
            return readObjectFromXml(LibraryItem.class, document);
        } else if (name.equals("QueryDocument")) {
             return readObjectFromXml(QueryDocument.class, document);
        } else {
            throw new RuntimeException("Unexpected root node " + name);
        }
    }

    public static <T> T readObjectFromXml(Class cls, Document doc) throws ParserConfigurationException, JAXBException, IOException, SAXException {

        //parse DOM into POJOs
        JAXBContext context = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<T> loader = unmarshaller.unmarshal(doc, cls);
        return loader.getValue();
    }

    public static String writeToXml(QueryDocument q) {
        if (q.getFolder().isEmpty()
                && q.getLibraryItem().isEmpty()
                && q.getReport().size() == 1) {

            Report report = q.getReport().get(0);
            return writeObjectToXml(report);

        } else if (q.getFolder().isEmpty()
                && q.getLibraryItem().size() == 1
                && q.getReport().isEmpty()) {

            LibraryItem libraryItem = q.getLibraryItem().get(0);
            return writeObjectToXml(libraryItem);
        }
        else
        {
            return writeObjectToXml(q);
        }
    }

    private static <T> String writeObjectToXml(T obj) {
        StringWriter sw = new StringWriter();
        Class cls = obj.getClass();

        try {
            JAXBContext context = JAXBContext.newInstance(cls);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); //just makes output easier to read
            marshaller.marshal(new JAXBElement<T>(new QName(cls.getSimpleName()), cls, obj), sw);

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

/*        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsd));
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));*/

        String ret = sw.toString();



        return ret;
    }


}
