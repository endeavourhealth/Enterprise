package org.endeavourhealth.enterprise.core.querydocument;

import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.ObjectFactory;
import org.endeavourhealth.enterprise.core.querydocument.models.QueryDocument;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public abstract class QueryDocumentSerializer {

    private static ObjectFactory queryDocumentObjectFactory = new ObjectFactory();


    public static LibraryItem readLibraryItemFromItem(DbItem item) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return XmlSerializer.readFromXml(LibraryItem.class, item.getXmlContent());
    }
    public static Report readReportFromItem(DbItem item) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return XmlSerializer.readFromXml(Report.class, item.getXmlContent());
    }
    public static QueryDocument readQueryDocumentFromItem(DbItem item) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return XmlSerializer.readFromXml(QueryDocument.class, item.getXmlContent());
    }


    public static LibraryItem readLibraryItemFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return XmlSerializer.readFromXml(LibraryItem.class, xml);
    }
    public static Report readReportFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return XmlSerializer.readFromXml(Report.class, xml);
    }
    public static QueryDocument readQueryDocumentFromXml(String xml) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        return XmlSerializer.readFromXml(QueryDocument.class, xml);
    }


    public static String writeToXml(QueryDocument q) {
        if (q.getFolder().isEmpty()
                && q.getLibraryItem().isEmpty()
                && q.getReport().size() == 1) {

            Report report = q.getReport().get(0);
            JAXBElement element = queryDocumentObjectFactory.createReport(report);
            return XmlSerializer.writeObjectToXml(element);

        } else if (q.getFolder().isEmpty()
                && q.getLibraryItem().size() == 1
                && q.getReport().isEmpty()) {

            LibraryItem libraryItem = q.getLibraryItem().get(0);
            JAXBElement element = queryDocumentObjectFactory.createLibraryItem(libraryItem);
            return XmlSerializer.writeObjectToXml(element);
        }
        else
        {
            JAXBElement element = queryDocumentObjectFactory.createQueryDocument(q);
            return XmlSerializer.writeObjectToXml(element);
        }
    }



}
