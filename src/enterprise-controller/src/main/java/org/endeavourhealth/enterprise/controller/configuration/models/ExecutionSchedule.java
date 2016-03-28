
package org.endeavourhealth.enterprise.controller.configuration.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for executionSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="executionSchedule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="startTimeInHours" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "executionSchedule", propOrder = {
    "startTimeInHours"
})
public class ExecutionSchedule {

    protected int startTimeInHours;

    /**
     * Gets the value of the startTimeInHours property.
     * 
     */
    public int getStartTimeInHours() {
        return startTimeInHours;
    }

    /**
     * Sets the value of the startTimeInHours property.
     * 
     */
    public void setStartTimeInHours(int value) {
        this.startTimeInHours = value;
    }

}
