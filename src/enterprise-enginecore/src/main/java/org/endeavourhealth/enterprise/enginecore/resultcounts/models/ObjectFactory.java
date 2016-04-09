
package org.endeavourhealth.enterprise.enginecore.resultcounts.models;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.endeavourhealth.enterprise.enginecore.resultcounts.models package. 
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

    private final static QName _ResultCounts_QNAME = new QName("", "resultCounts");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.endeavourhealth.enterprise.enginecore.resultcounts.models
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JobReportType }
     * 
     */
    public JobReportType createJobReportType() {
        return new JobReportType();
    }

    /**
     * Create an instance of {@link ResultCounts }
     * 
     */
    public ResultCounts createResultCounts() {
        return new ResultCounts();
    }

    /**
     * Create an instance of {@link OrganisationResult }
     * 
     */
    public OrganisationResult createOrganisationResult() {
        return new OrganisationResult();
    }

    /**
     * Create an instance of {@link JobReportItemResultType }
     * 
     */
    public JobReportItemResultType createJobReportItemResultType() {
        return new JobReportItemResultType();
    }

    /**
     * Create an instance of {@link JobReportType.OrganisationResults }
     * 
     */
    public JobReportType.OrganisationResults createJobReportTypeOrganisationResults() {
        return new JobReportType.OrganisationResults();
    }

    /**
     * Create an instance of {@link JobReportType.JobReportItemResults }
     * 
     */
    public JobReportType.JobReportItemResults createJobReportTypeJobReportItemResults() {
        return new JobReportType.JobReportItemResults();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultCounts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "resultCounts")
    public JAXBElement<ResultCounts> createResultCounts(ResultCounts value) {
        return new JAXBElement<ResultCounts>(_ResultCounts_QNAME, ResultCounts.class, null, value);
    }

}
