package org.endeavourhealth.discovery.core.targetdocument

import org.endeavourhealth.discovery.core.discoverydefinition.DataSource
import org.endeavourhealth.discovery.core.discoverydefinition.DiscoveryDefinition
import org.endeavourhealth.discovery.core.discoverydefinition.DiscoveryDefinitionHelper
import org.endeavourhealth.discovery.core.discoverydefinition.Query

class EnquiryDocumentHelperTest extends GroovyTestCase {

    void testSerialiseAndDeserialise_validDocument_reverseWorks() {

        def original = new DiscoveryDefinition();
        original.dataSource = new ArrayList<Query>();
        original.dataSource.add(new DataSource(entity: "test"));

        String xml = DiscoveryDefinitionHelper.serialise(original);

        DiscoveryDefinition newDocument = DiscoveryDefinitionHelper.deserialise(xml);

        assert newDocument.dataSource[0].entity == "test";
    }

}
