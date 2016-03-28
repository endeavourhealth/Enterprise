
package org.endeavourhealth.enterprise.controller.configuration.models;

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
 *         &lt;element name="executionSchedule" type="{}executionSchedule"/>
 *         &lt;element name="patientBatchSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="coreDatabase" type="{}databaseConnection"/>
 *         &lt;element name="messageQueuing" type="{}messageQueuing"/>
 *         &lt;element name="patientDatabase" type="{}databaseConnection"/>
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
    "executionSchedule",
    "patientBatchSize",
    "coreDatabase",
    "messageQueuing",
    "patientDatabase"
})
public class Configuration {

    protected Debugging debugging;
    @XmlElement(required = true)
    protected ExecutionSchedule executionSchedule;
    protected int patientBatchSize;
    @XmlElement(required = true)
    protected DatabaseConnection coreDatabase;
    @XmlElement(required = true)
    protected MessageQueuing messageQueuing;
    @XmlElement(required = true)
    protected DatabaseConnection patientDatabase;

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
     * Gets the value of the executionSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link ExecutionSchedule }
     *     
     */
    public ExecutionSchedule getExecutionSchedule() {
        return executionSchedule;
    }

    /**
     * Sets the value of the executionSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecutionSchedule }
     *     
     */
    public void setExecutionSchedule(ExecutionSchedule value) {
        this.executionSchedule = value;
    }

    /**
     * Gets the value of the patientBatchSize property.
     * 
     */
    public int getPatientBatchSize() {
        return patientBatchSize;
    }

    /**
     * Sets the value of the patientBatchSize property.
     * 
     */
    public void setPatientBatchSize(int value) {
        this.patientBatchSize = value;
    }

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

    /**
     * Gets the value of the patientDatabase property.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseConnection }
     *     
     */
    public DatabaseConnection getPatientDatabase() {
        return patientDatabase;
    }

    /**
     * Sets the value of the patientDatabase property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseConnection }
     *     
     */
    public void setPatientDatabase(DatabaseConnection value) {
        this.patientDatabase = value;
    }

}
