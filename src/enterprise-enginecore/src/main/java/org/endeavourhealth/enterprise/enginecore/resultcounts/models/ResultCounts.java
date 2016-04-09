
package org.endeavourhealth.enterprise.enginecore.resultcounts.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultCounts complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultCounts">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jobReport" type="{}jobReportType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultCounts", propOrder = {
    "jobReport"
})
public class ResultCounts {

    @XmlElement(required = true)
    protected JobReportType jobReport;

    /**
     * Gets the value of the jobReport property.
     * 
     * @return
     *     possible object is
     *     {@link JobReportType }
     *     
     */
    public JobReportType getJobReport() {
        return jobReport;
    }

    /**
     * Sets the value of the jobReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobReportType }
     *     
     */
    public void setJobReport(JobReportType value) {
        this.jobReport = value;
    }

}
