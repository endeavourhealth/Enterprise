
package org.endeavourhealth.enterprise.core.querydocument.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for linkedTestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="linkedTestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="dataSource" type="{}dataSource"/>
 *           &lt;element name="dataSourceUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;element name="parentField" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="childField" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="comparison" type="{}comparison"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "linkedTestType", propOrder = {
    "dataSource",
    "dataSourceUuid",
    "parentField",
    "childField",
    "comparison"
})
public class LinkedTestType {

    protected DataSource dataSource;
    protected String dataSourceUuid;
    @XmlElement(required = true)
    protected String parentField;
    @XmlElement(required = true)
    protected String childField;
    @XmlElement(required = true)
    protected Comparison comparison;

    /**
     * Gets the value of the dataSource property.
     * 
     * @return
     *     possible object is
     *     {@link DataSource }
     *     
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Sets the value of the dataSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSource }
     *     
     */
    public void setDataSource(DataSource value) {
        this.dataSource = value;
    }

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
     * Gets the value of the parentField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentField() {
        return parentField;
    }

    /**
     * Sets the value of the parentField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentField(String value) {
        this.parentField = value;
    }

    /**
     * Gets the value of the childField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildField() {
        return childField;
    }

    /**
     * Sets the value of the childField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildField(String value) {
        this.childField = value;
    }

    /**
     * Gets the value of the comparison property.
     * 
     * @return
     *     possible object is
     *     {@link Comparison }
     *     
     */
    public Comparison getComparison() {
        return comparison;
    }

    /**
     * Sets the value of the comparison property.
     * 
     * @param value
     *     allowed object is
     *     {@link Comparison }
     *     
     */
    public void setComparison(Comparison value) {
        this.comparison = value;
    }

}
