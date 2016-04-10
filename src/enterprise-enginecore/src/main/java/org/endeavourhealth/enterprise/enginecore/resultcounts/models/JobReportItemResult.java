
package org.endeavourhealth.enterprise.enginecore.resultcounts.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for jobReportItemResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="jobReportItemResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="organisationResult" type="{}organisationResult" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="jobReportItemUuid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "jobReportItemResult", propOrder = {
    "organisationResult"
})
public class JobReportItemResult {

    @XmlElement(required = true)
    protected List<OrganisationResult> organisationResult;
    @XmlAttribute(name = "jobReportItemUuid", required = true)
    protected String jobReportItemUuid;

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
     * Gets the value of the jobReportItemUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobReportItemUuid() {
        return jobReportItemUuid;
    }

    /**
     * Sets the value of the jobReportItemUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobReportItemUuid(String value) {
        this.jobReportItemUuid = value;
    }

}
