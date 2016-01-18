
package org.endeavourhealth.discovery.core.discoverydefinition;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for listGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="listGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="heading" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="summary">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                     &lt;choice>
 *                       &lt;element name="dataSourceUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                       &lt;element name="testUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                     &lt;/choice>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="field">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="dataSourceUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                     &lt;element name="fieldOutput" type="{}fieldOutput" maxOccurs="unbounded"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listGroup", propOrder = {
    "heading",
    "summary",
    "field"
})
public class ListGroup {

    protected String heading;
    protected ListGroup.Summary summary;
    protected ListGroup.Field field;

    /**
     * Gets the value of the heading property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeading() {
        return heading;
    }

    /**
     * Sets the value of the heading property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeading(String value) {
        this.heading = value;
    }

    /**
     * Gets the value of the summary property.
     * 
     * @return
     *     possible object is
     *     {@link ListGroup.Summary }
     *     
     */
    public ListGroup.Summary getSummary() {
        return summary;
    }

    /**
     * Sets the value of the summary property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListGroup.Summary }
     *     
     */
    public void setSummary(ListGroup.Summary value) {
        this.summary = value;
    }

    /**
     * Gets the value of the field property.
     * 
     * @return
     *     possible object is
     *     {@link ListGroup.Field }
     *     
     */
    public ListGroup.Field getField() {
        return field;
    }

    /**
     * Sets the value of the field property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListGroup.Field }
     *     
     */
    public void setField(ListGroup.Field value) {
        this.field = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
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
    @XmlType(name = "", propOrder = {
        "dataSourceUuid",
        "fieldOutput"
    })
    public static class Field {

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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;choice>
     *           &lt;element name="dataSourceUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *           &lt;element name="testUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;/choice>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "type",
        "dataSourceUuid",
        "testUuid"
    })
    public static class Summary {

        @XmlElement(required = true)
        protected Object type;
        protected String dataSourceUuid;
        protected String testUuid;

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setType(Object value) {
            this.type = value;
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
         * Gets the value of the testUuid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTestUuid() {
            return testUuid;
        }

        /**
         * Sets the value of the testUuid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTestUuid(String value) {
            this.testUuid = value;
        }

    }

}
