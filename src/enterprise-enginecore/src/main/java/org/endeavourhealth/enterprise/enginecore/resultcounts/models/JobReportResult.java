
package org.endeavourhealth.enterprise.enginecore.resultcounts.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for jobReportResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="jobReportResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jobReportUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="organisationResult" type="{}organisationResult" maxOccurs="unbounded"/>
 *         &lt;element name="jobReportItemResult" type="{}jobReportItemResult" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "jobReportResult", propOrder = {
    "jobReportUuid",
    "organisationResult",
    "jobReportItemResult"
})
public class JobReportResult {

    @XmlElement(required = true)
    protected String jobReportUuid;
    @XmlElement(required = true)
    protected List<OrganisationResult> organisationResult;
    @XmlElement(required = true)
    protected List<JobReportItemResult> jobReportItemResult;

    /**
     * Gets the value of the jobReportUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobReportUuid() {
        return jobReportUuid;
    }

    /**
     * Sets the value of the jobReportUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobReportUuid(String value) {
        this.jobReportUuid = value;
    }

    /**
     * Gets the value of the organisationResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the organisationResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrganisationResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrganisationResult }
     * 
     * 
     */
    public List<OrganisationResult> getOrganisationResult() {
        if (organisationResult == null) {
            organisationResult = new ArrayList<OrganisationResult>();
        }
        return this.organisationResult;
    }

    /**
     * Gets the value of the jobReportItemResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the jobReportItemResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJobReportItemResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JobReportItemResult }
     * 
     * 
     */
    public List<JobReportItemResult> getJobReportItemResult() {
        if (jobReportItemResult == null) {
            jobReportItemResult = new ArrayList<JobReportItemResult>();
        }
        return this.jobReportItemResult;
    }

}
