
package org.endeavourhealth.enterprise.core.querydocument.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for listReportFieldBasedType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="listReportFieldBasedType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataSourceUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fieldOutput" type="{}fieldOutput" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listReportFieldBasedType", propOrder = {
    "dataSourceUuid",
    "fieldOutput"
})
public class ListReportFieldBasedType {

    @XmlElement(required = true)
    protected String dataSourceUuid;
    @XmlElement(required = true)
    protected List<FieldOutput> fieldOutput;

    /**
     * Gets the value of the dataSourceUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataSourceUuid() {
        return dataSourceUuid;
    }

    /**
     * Sets the value of the dataSourceUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataSourceUuid(String value) {
        this.dataSourceUuid = value;
    }

    /**
     * Gets the value of the fieldOutput property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fieldOutput property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFieldOutput().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldOutput }
     * 
     * 
     */
    public List<FieldOutput> getFieldOutput() {
        if (fieldOutput == null) {
            fieldOutput = new ArrayList<FieldOutput>();
        }
        return this.fieldOutput;
    }

}
