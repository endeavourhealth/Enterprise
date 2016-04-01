
package org.endeavourhealth.enterprise.enginecore.resultcounts.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for organisationResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="organisationResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="odsCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="resultCount" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organisationResult")
public class OrganisationResult {

    @XmlAttribute(name = "odsCode", required = true)
    protected String odsCode;
    @XmlAttribute(name = "resultCount", required = true)
    protected String resultCount;

    /**
     * Gets the value of the odsCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOdsCode() {
        return odsCode;
    }

    /**
     * Sets the value of the odsCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOdsCode(String value) {
        this.odsCode = value;
    }

    /**
     * Gets the value of the resultCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCount() {
        return resultCount;
    }

    /**
     * Sets the value of the resultCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultCount(String value) {
        this.resultCount = value;
    }

}
