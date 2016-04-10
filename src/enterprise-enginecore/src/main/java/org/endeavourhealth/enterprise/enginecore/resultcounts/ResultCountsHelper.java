package org.endeavourhealth.enterprise.enginecore.resultcounts;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.XmlSerializer;
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class ResultCountsHelper {
    public static ResultCounts deserializeFromString(String xml) throws ParserConfigurationException, IOException, SAXException, JAXBException {

        return XmlSerializer.deserializeFromString(ResultCounts.class, xml, "ResultCounts.xsd");
    }

    public static String serialise(ResultCounts resultCounts) {
        JAXBElement element = new ObjectFactory().createResultCounts(resultCounts);
        return XmlSerializer.serializeToString(element, "ResultCounts.xsd");
    }

    public static int calculateTotal(List<OrganisationResult> organisationResult) {
        if (CollectionUtils.isEmpty(organisationResult))
            return 0;

        int count = 0;

        for (OrganisationResult result: organisationResult) {
            count += result.getResultCount();
        }

        return count;
    }

    public static ResultCounts clone(ResultCounts result) throws Exception {
        String xml = ResultCountsHelper.serialise(result);
        return ResultCountsHelper.deserializeFromString(xml);
    }
}
