
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
 *         &lt;element name="ftpConnection" type="{}ftpConnection" minOccurs="0"/>
 *         &lt;element name="streamingFolder" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="workingFolder" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="targetFolder" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "ftpConnection",
    "streamingFolder",
    "workingFolder",
    "targetFolder"
})
public class OutputFilesType {

    protected FtpConnection ftpConnection;
    @XmlElement(required = true)
    protected String streamingFolder;
    @XmlElement(required = true)
    protected String workingFolder;
    @XmlElement(required = true)
    protected String targetFolder;

    /**
     * Gets the value of the ftpConnection property.
     * 
     * @return
     *     possible object is
     *     {@link FtpConnection }
     *     
     */
    public FtpConnection getFtpConnection() {
        return ftpConnection;
    }

    /**
     * Sets the value of the ftpConnection property.
     * 
     * @param value
     *     allowed object is
     *     {@link FtpConnection }
     *     
     */
    public void setFtpConnection(FtpConnection value) {
        this.ftpConnection = value;
    }

    /**
     * Gets the value of the streamingFolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreamingFolder() {
        return streamingFolder;
    }

    /**
     * Sets the value of the streamingFolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreamingFolder(String value) {
        this.streamingFolder = value;
    }

    /**
     * Gets the value of the workingFolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkingFolder() {
        return workingFolder;
    }

    /**
     * Sets the value of the workingFolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkingFolder(String value) {
        this.workingFolder = value;
    }

    /**
     * Gets the value of the targetFolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetFolder() {
        return targetFolder;
    }

    /**
     * Sets the value of the targetFolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetFolder(String value) {
        this.targetFolder = value;
    }

}
