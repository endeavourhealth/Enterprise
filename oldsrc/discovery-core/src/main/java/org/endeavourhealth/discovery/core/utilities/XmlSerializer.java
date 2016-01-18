package org.endeavourhealth.discovery.core.utilities;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;

public class XmlSerializer {

    public <T> String serialize(Class<T> type, T source) throws JAXBException, IOException {

        try (StringWriter writer = new StringWriter()) {

            String packageName = type.getPackage().getName();
            JAXBContext context = JAXBContext.newInstance(packageName);
            Marshaller m = context.createMarshaller();
            m.marshal(source, writer);

            String result = writer.toString();
            return result;
        }
    }

    public <T> T deserializeFromResource(
            Class<T> type,
            String xsdName,
            String xmlName
            ) throws JAXBException, SAXException, IOException, URISyntaxException {

        Unmarshaller unmarshaller = getUnmarshaller(type, xsdName);
        JAXBElement<T> rootElement = (JAXBElement<T>) unmarshaller.unmarshal(new File(xmlName));

        T result = rootElement.getValue();
        return result;
    }

    public <T> T deserializeFromString(
            Class<T> type,
            String xsdName,
            String xml
    ) throws JAXBException, SAXException, IOException, URISyntaxException {

        Unmarshaller unmarshaller = getUnmarshaller(type, xsdName);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes())) {

            JAXBElement<T> rootElement = (JAXBElement<T>) unmarshaller.unmarshal(stream);
            T result = rootElement.getValue();
            return result;
        }
    }

    private <T> Unmarshaller getUnmarshaller(Class<T> type, String xsdName) throws JAXBException, SAXException {
        String packageName = type.getPackage().getName();
        JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL url = getClass().getClassLoader().getResource(xsdName);
        Schema schema = sf.newSchema(url);

        unmarshaller.setSchema(schema);
        return unmarshaller;
    }
}
