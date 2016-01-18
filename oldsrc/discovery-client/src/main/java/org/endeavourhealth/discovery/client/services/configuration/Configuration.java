
package org.endeavourhealth.discovery.client.services.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for configuration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configuration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="coreDatabase" type="{}databaseConnection"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configuration", propOrder = {
    "coreDatabase"
})
public class Configuration {

    @XmlElement(required = true)
    protected DatabaseConnection coreDatabase;

    /**
     * Gets the value of the coreDatabase property.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseConnection }
     *     
     */
    public DatabaseConnection getCoreDatabase() {
        return coreDatabase;
    }

    /**
     * Sets the value of the coreDatabase property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseConnection }
     *     
     */
    public void setCoreDatabase(DatabaseConnection value) {
        this.coreDatabase = value;
    }

}
