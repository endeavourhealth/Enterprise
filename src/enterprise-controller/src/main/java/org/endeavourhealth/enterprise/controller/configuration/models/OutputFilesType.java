
package org.endeavourhealth.enterprise.controller.configuration.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for outputFilesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="outputFilesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="temporaryFolder" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="rootFolder" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "outputFilesType", propOrder = {
    "temporaryFolder",
    "rootFolder"
})
public class OutputFilesType {

    @XmlElement(required = true)
    protected String temporaryFolder;
    @XmlElement(required = true)
    protected String rootFolder;

    /**
     * Gets the value of the temporaryFolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemporaryFolder() {
        return temporaryFolder;
    }

    /**
     * Sets the value of the temporaryFolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemporaryFolder(String value) {
        this.temporaryFolder = value;
    }

    /**
     * Gets the value of the rootFolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootFolder() {
        return rootFolder;
    }

    /**
     * Sets the value of the rootFolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootFolder(String value) {
        this.rootFolder = value;
    }

}
