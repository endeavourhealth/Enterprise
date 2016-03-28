
package org.endeavourhealth.enterprise.controller.configuration.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for messageQueuing complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messageQueuing">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ipAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="controllerQueueName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="processorNodesExchangeName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="workerQueuePrefix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageQueuing", propOrder = {
    "ipAddress",
    "username",
    "password",
    "controllerQueueName",
    "processorNodesExchangeName",
    "workerQueuePrefix"
})
public class MessageQueuing {

    @XmlElement(required = true)
    protected String ipAddress;
    @XmlElement(required = true)
    protected String username;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String controllerQueueName;
    @XmlElement(required = true)
    protected String processorNodesExchangeName;
    @XmlElement(required = true)
    protected String workerQueuePrefix;

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the controllerQueueName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllerQueueName() {
        return controllerQueueName;
    }

    /**
     * Sets the value of the controllerQueueName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllerQueueName(String value) {
        this.controllerQueueName = value;
    }

    /**
     * Gets the value of the processorNodesExchangeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessorNodesExchangeName() {
        return processorNodesExchangeName;
    }

    /**
     * Sets the value of the processorNodesExchangeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessorNodesExchangeName(String value) {
        this.processorNodesExchangeName = value;
    }

    /**
     * Gets the value of the workerQueuePrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkerQueuePrefix() {
        return workerQueuePrefix;
    }

    /**
     * Sets the value of the workerQueuePrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkerQueuePrefix(String value) {
        this.workerQueuePrefix = value;
    }

}
