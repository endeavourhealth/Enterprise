
package org.endeavourhealth.enterprise.enginecore.resultcounts.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for jobReportType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="jobReportType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jobReportUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="organisationResults">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="organisationResult" type="{}organisationResult" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="jobReportItemResults">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="jobReportItemResult" type="{}jobReportItemResultType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "jobReportType", propOrder = {
    "jobReportUuid",
    "organisationResults",
    "jobReportItemResults"
})
public class JobReportType {

    @XmlElement(required = true)
    protected String jobReportUuid;
    @XmlElement(required = true)
    protected JobReportType.OrganisationResults organisationResults;
    @XmlElement(required = true)
    protected JobReportType.JobReportItemResults jobReportItemResults;

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
     * Gets the value of the organisationResults property.
     * 
     * @return
     *     possible object is
     *     {@link JobReportType.OrganisationResults }
     *     
     */
    public JobReportType.OrganisationResults getOrganisationResults() {
        return organisationResults;
    }

    /**
     * Sets the value of the organisationResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobReportType.OrganisationResults }
     *     
     */
    public void setOrganisationResults(JobReportType.OrganisationResults value) {
        this.organisationResults = value;
    }

    /**
     * Gets the value of the jobReportItemResults property.
     * 
     * @return
     *     possible object is
     *     {@link JobReportType.JobReportItemResults }
     *     
     */
    public JobReportType.JobReportItemResults getJobReportItemResults() {
        return jobReportItemResults;
    }

    /**
     * Sets the value of the jobReportItemResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobReportType.JobReportItemResults }
     *     
     */
    public void setJobReportItemResults(JobReportType.JobReportItemResults value) {
        this.jobReportItemResults = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="jobReportItemResult" type="{}jobReportItemResultType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "jobReportItemResult"
    })
    public static class JobReportItemResults {

        @XmlElement(required = true)
        protected List<JobReportItemResultType> jobReportItemResult;

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
         * {@link JobReportItemResultType }
         * 
         * 
         */
        public List<JobReportItemResultType> getJobReportItemResult() {
            if (jobReportItemResult == null) {
                jobReportItemResult = new ArrayList<JobReportItemResultType>();
            }
            return this.jobReportItemResult;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="organisationResult" type="{}organisationResult" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "organisationResult"
    })
    public static class OrganisationResults {

        protected List<OrganisationResult> organisationResult;

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

    }

}
