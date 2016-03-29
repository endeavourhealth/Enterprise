
package org.endeavourhealth.enterprise.processornode.configuration.models;

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
 *         &lt;element name="debugging" type="{}debugging" minOccurs="0"/>
 *         &lt;element name="executionThreads" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dataItemBufferSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dataItemBufferTriggerSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="messageQueuing" type="{}messageQueuing"/>
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
    "debugging",
    "executionThreads",
    "dataItemBufferSize",
    "dataItemBufferTriggerSize",
    "messageQueuing"
})
public class Configuration {

    protected Debugging debugging;
    protected int executionThreads;
    protected int dataItemBufferSize;
    protected int dataItemBufferTriggerSize;
    @XmlElement(required = true)
    protected MessageQueuing messageQueuing;

    /**
     * Gets the value of the debugging property.
     * 
     * @return
     *     possible object is
     *     {@link Debugging }
     *     
     */
    public Debugging getDebugging() {
        return debugging;
    }

    /**
     * Sets the value of the debugging property.
     * 
     * @param value
     *     allowed object is
     *     {@link Debugging }
     *     
     */
    public void setDebugging(Debugging value) {
        this.debugging = value;
    }

    /**
     * Gets the value of the executionThreads property.
     * 
     */
    public int getExecutionThreads() {
        return executionThreads;
    }

    /**
     * Sets the value of the executionThreads property.
     * 
     */
    public void setExecutionThreads(int value) {
        this.executionThreads = value;
    }

    /**
     * Gets the value of the dataItemBufferSize property.
     * 
     */
    public int getDataItemBufferSize() {
        return dataItemBufferSize;
    }

    /**
     * Sets the value of the dataItemBufferSize property.
     * 
     */
    public void setDataItemBufferSize(int value) {
        this.dataItemBufferSize = value;
    }

    /**
     * Gets the value of the dataItemBufferTriggerSize property.
     * 
     */
    public int getDataItemBufferTriggerSize() {
        return dataItemBufferTriggerSize;
    }

    /**
     * Sets the value of the dataItemBufferTriggerSize property.
     * 
     */
    public void setDataItemBufferTriggerSize(int value) {
        this.dataItemBufferTriggerSize = value;
    }

    /**
     * Gets the value of the messageQueuing property.
     * 
     * @return
     *     possible object is
     *     {@link MessageQueuing }
     *     
     */
    public MessageQueuing getMessageQueuing() {
        return messageQueuing;
    }

    /**
     * Sets the value of the messageQueuing property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageQueuing }
     *     
     */
    public void setMessageQueuing(MessageQueuing value) {
        this.messageQueuing = value;
    }

}
