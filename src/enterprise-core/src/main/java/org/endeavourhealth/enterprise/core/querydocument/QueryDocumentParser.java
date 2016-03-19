package org.endeavourhealth.enterprise.core.querydocument;

import org.endeavourhealth.enterprise.core.AbstractParser;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.ObjectFactory;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
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
 * Created by Drew on 11/03/2016.
 */
public abstract class QueryDocumentParser extends AbstractParser {

    private static ObjectFactory queryDocumentObjectFactory = new ObjectFactory();

    public static LibraryItem readLibraryItemFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return readFromXml(LibraryItem.class, xml);
    }
    public static Report readReportFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return readFromXml(Report.class, xml);
    }
    public static QueryDocument readQueryDocumentFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return readFromXml(QueryDocument.class, xml);
    }


    public static String writeToXml(QueryDocument q) {
        if (q.getFolder().isEmpty()
                && q.getLibraryItem().isEmpty()
                && q.getReport().size() == 1) {

            Report report = q.getReport().get(0);
            JAXBElement element = queryDocumentObjectFactory.createReport(report);
            return writeObjectToXml(element);

        } else if (q.getFolder().isEmpty()
                && q.getLibraryItem().size() == 1
                && q.getReport().isEmpty()) {

            LibraryItem libraryItem = q.getLibraryItem().get(0);
            JAXBElement element = queryDocumentObjectFactory.createLibraryItem(libraryItem);
            return writeObjectToXml(element);
        }
        else
        {
            JAXBElement element = queryDocumentObjectFactory.createQueryDocument(q);
            return writeObjectToXml(element);
        }
    }



}
