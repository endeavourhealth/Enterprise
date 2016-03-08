
package org.endeavourhealth.enterprise.core.querydocument;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.endeavourhealth.enterprise.core.querydocument package. 
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

    private final static QName _QueryDocument_QNAME = new QName("", "queryDocument");
    private final static QName _LibraryItem_QNAME = new QName("", "libraryItem");
    private final static QName _RuleTestLibraryItemUUID_QNAME = new QName("", "testLibraryItemUUID");
    private final static QName _RuleQueryLibraryItemUUID_QNAME = new QName("", "queryLibraryItemUUID");
    private final static QName _RuleTest_QNAME = new QName("", "test");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.endeavourhealth.enterprise.core.querydocument
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
     * Create an instance of {@link QueryDocument }
     * 
     */
    public QueryDocument createQueryDocument() {
        return new QueryDocument();
    }

    /**
     * Create an instance of {@link LibraryItemType }
     * 
     */
    public LibraryItemType createLibraryItemType() {
        return new LibraryItemType();
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
     * Create an instance of {@link org.endeavourhealth.enterprise.core.querydocument.ListOutput }
     * 
     */
    public org.endeavourhealth.enterprise.core.querydocument.ListOutput createListOutput() {
        return new org.endeavourhealth.enterprise.core.querydocument.ListOutput();
    }

    /**
     * Create an instance of {@link CodeSetValue }
     * 
     */
    public CodeSetValue createCodeSetValue() {
        return new CodeSetValue();
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
     * Create an instance of {@link org.endeavourhealth.enterprise.core.querydocument.Query }
     * 
     */
    public org.endeavourhealth.enterprise.core.querydocument.Query createQuery() {
        return new org.endeavourhealth.enterprise.core.querydocument.Query();
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
     * Create an instance of {@link Test.IsAny }
     * 
     */
    public Test.IsAny createTestIsAny() {
        return new Test.IsAny();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "queryDocument")
    public JAXBElement<QueryDocument> createQueryDocument(QueryDocument value) {
        return new JAXBElement<QueryDocument>(_QueryDocument_QNAME, QueryDocument.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LibraryItemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "libraryItem")
    public JAXBElement<LibraryItemType> createLibraryItem(LibraryItemType value) {
        return new JAXBElement<LibraryItemType>(_LibraryItem_QNAME, LibraryItemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "testLibraryItemUUID", scope = Rule.class)
    public JAXBElement<String> createRuleTestLibraryItemUUID(String value) {
        return new JAXBElement<String>(_RuleTestLibraryItemUUID_QNAME, String.class, Rule.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "queryLibraryItemUUID", scope = Rule.class)
    public JAXBElement<String> createRuleQueryLibraryItemUUID(String value) {
        return new JAXBElement<String>(_RuleQueryLibraryItemUUID_QNAME, String.class, Rule.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Test }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "test", scope = Rule.class)
    public JAXBElement<Test> createRuleTest(Test value) {
        return new JAXBElement<Test>(_RuleTest_QNAME, Test.class, Rule.class, value);
    }

}
