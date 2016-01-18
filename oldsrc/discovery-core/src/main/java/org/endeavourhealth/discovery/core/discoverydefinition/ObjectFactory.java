
package org.endeavourhealth.discovery.core.discoverydefinition;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.endeavourhealth.discovery.core.discoverydefinition package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DiscoveryDefinition_QNAME = new QName("", "discoveryDefinition");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.endeavourhealth.discovery.core.discoverydefinition
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Report }
     * 
     */
    public Report createReport() {
        return new Report();
    }

    /**
     * Create an instance of {@link ListGroup }
     * 
     */
    public ListGroup createListGroup() {
        return new ListGroup();
    }

    /**
     * Create an instance of {@link Test }
     * 
     */
    public Test createTest() {
        return new Test();
    }

    /**
     * Create an instance of {@link DiscoveryDefinition }
     * 
     */
    public DiscoveryDefinition createDiscoveryDefinition() {
        return new DiscoveryDefinition();
    }

    /**
     * Create an instance of {@link CalculationType }
     * 
     */
    public CalculationType createCalculationType() {
        return new CalculationType();
    }

    /**
     * Create an instance of {@link ValueTo }
     * 
     */
    public ValueTo createValueTo() {
        return new ValueTo();
    }

    /**
     * Create an instance of {@link org.endeavourhealth.discovery.core.discoverydefinition.ListOutput }
     * 
     */
    public org.endeavourhealth.discovery.core.discoverydefinition.ListOutput createListOutput() {
        return new org.endeavourhealth.discovery.core.discoverydefinition.ListOutput();
    }

    /**
     * Create an instance of {@link CodeSetValue }
     * 
     */
    public CodeSetValue createCodeSetValue() {
        return new CodeSetValue();
    }

    /**
     * Create an instance of {@link LibraryItemType }
     * 
     */
    public LibraryItemType createLibraryItemType() {
        return new LibraryItemType();
    }

    /**
     * Create an instance of {@link FieldTest }
     * 
     */
    public FieldTest createFieldTest() {
        return new FieldTest();
    }

    /**
     * Create an instance of {@link ValueRange }
     * 
     */
    public ValueRange createValueRange() {
        return new ValueRange();
    }

    /**
     * Create an instance of {@link Rule }
     * 
     */
    public Rule createRule() {
        return new Rule();
    }

    /**
     * Create an instance of {@link LinkedTestType }
     * 
     */
    public LinkedTestType createLinkedTestType() {
        return new LinkedTestType();
    }

    /**
     * Create an instance of {@link CodeSet }
     * 
     */
    public CodeSet createCodeSet() {
        return new CodeSet();
    }

    /**
     * Create an instance of {@link Value }
     * 
     */
    public Value createValue() {
        return new Value();
    }

    /**
     * Create an instance of {@link ParameterType }
     * 
     */
    public ParameterType createParameterType() {
        return new ParameterType();
    }

    /**
     * Create an instance of {@link Comparison }
     * 
     */
    public Comparison createComparison() {
        return new Comparison();
    }

    /**
     * Create an instance of {@link org.endeavourhealth.discovery.core.discoverydefinition.Query }
     * 
     */
    public org.endeavourhealth.discovery.core.discoverydefinition.Query createQuery() {
        return new org.endeavourhealth.discovery.core.discoverydefinition.Query();
    }

    /**
     * Create an instance of {@link CalculationParameter }
     * 
     */
    public CalculationParameter createCalculationParameter() {
        return new CalculationParameter();
    }

    /**
     * Create an instance of {@link ValueFrom }
     * 
     */
    public ValueFrom createValueFrom() {
        return new ValueFrom();
    }

    /**
     * Create an instance of {@link Folder }
     * 
     */
    public Folder createFolder() {
        return new Folder();
    }

    /**
     * Create an instance of {@link Restriction }
     * 
     */
    public Restriction createRestriction() {
        return new Restriction();
    }

    /**
     * Create an instance of {@link FieldOutput }
     * 
     */
    public FieldOutput createFieldOutput() {
        return new FieldOutput();
    }

    /**
     * Create an instance of {@link ValueEqualTo }
     * 
     */
    public ValueEqualTo createValueEqualTo() {
        return new ValueEqualTo();
    }

    /**
     * Create an instance of {@link DataSource }
     * 
     */
    public DataSource createDataSource() {
        return new DataSource();
    }

    /**
     * Create an instance of {@link Report.Query }
     * 
     */
    public Report.Query createReportQuery() {
        return new Report.Query();
    }

    /**
     * Create an instance of {@link Report.ListOutput }
     * 
     */
    public Report.ListOutput createReportListOutput() {
        return new Report.ListOutput();
    }

    /**
     * Create an instance of {@link ListGroup.Summary }
     * 
     */
    public ListGroup.Summary createListGroupSummary() {
        return new ListGroup.Summary();
    }

    /**
     * Create an instance of {@link ListGroup.Field }
     * 
     */
    public ListGroup.Field createListGroupField() {
        return new ListGroup.Field();
    }

    /**
     * Create an instance of {@link Test.Any }
     * 
     */
    public Test.Any createTestAny() {
        return new Test.Any();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiscoveryDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "discoveryDefinition")
    public JAXBElement<DiscoveryDefinition> createDiscoveryDefinition(DiscoveryDefinition value) {
        return new JAXBElement<DiscoveryDefinition>(_DiscoveryDefinition_QNAME, DiscoveryDefinition.class, null, value);
    }

}
