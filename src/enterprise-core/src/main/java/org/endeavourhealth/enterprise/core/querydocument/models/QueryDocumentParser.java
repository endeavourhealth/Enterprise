package org.endeavourhealth.enterprise.core.querydocument.models;

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
 * Created by Drew on 11/03/2016.
 */
public abstract class QueryDocumentParser {


    public static QueryDocument readFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {

        //parse XML string into DOM
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(is);
        org.w3c.dom.Element varElement = document.getDocumentElement();

        //parse DOM into POJOs
        JAXBContext context = JAXBContext.newInstance(QueryDocument.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<QueryDocument> loader = unmarshaller.unmarshal(varElement, QueryDocument.class);
        return loader.getValue();
    }

    public static String writeToXml(QueryDocument q) {

        StringWriter sw = new StringWriter();

        try {
            JAXBContext carContext = JAXBContext.newInstance(QueryDocument.class);
            Marshaller carMarshaller = carContext.createMarshaller();
            carMarshaller.marshal(q, sw);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return sw.toString();
    }

}
