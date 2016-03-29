
package org.endeavourhealth.enterprise.controller.configuration.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for debugging complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="debugging">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="startImmediately" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="maximumPatientId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "debugging", propOrder = {
    "startImmediately",
    "maximumPatientId"
})
public class Debugging {

    protected Boolean startImmediately;
    protected Integer maximumPatientId;

    /**
     * Gets the value of the startImmediately property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isStartImmediately() {
        return startImmediately;
    }

    /**
     * Sets the value of the startImmediately property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStartImmediately(Boolean value) {
        this.startImmediately = value;
    }

    /**
     * Gets the value of the maximumPatientId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaximumPatientId() {
        return maximumPatientId;
    }

    /**
     * Sets the value of the maximumPatientId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaximumPatientId(Integer value) {
        this.maximumPatientId = value;
    }

}
