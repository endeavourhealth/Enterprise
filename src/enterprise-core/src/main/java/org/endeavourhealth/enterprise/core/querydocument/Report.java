
package org.endeavourhealth.enterprise.core.querydocument;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for report complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="report">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="folderUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="query" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="parentUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="listOutput" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="parentUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "report", propOrder = {
    "uuid",
    "name",
    "folderUuid",
    "query",
    "listOutput"
})
public class Report {

    @XmlElement(required = true)
    protected String uuid;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String folderUuid;
    protected List<Report.Query> query;
    protected List<Report.ListOutput> listOutput;

    /**
     * Gets the value of the uuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the folderUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * Sets the value of the folderUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderUuid(String value) {
        this.folderUuid = value;
    }

    /**
     * Gets the value of the query property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the query property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuery().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Report.Query }
     * 
     * 
     */
    public List<Report.Query> getQuery() {
        if (query == null) {
            query = new ArrayList<Report.Query>();
        }
        return this.query;
    }

    /**
     * Gets the value of the listOutput property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listOutput property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListOutput().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Report.ListOutput }
     * 
     * 
     */
    public List<Report.ListOutput> getListOutput() {
        if (listOutput == null) {
            listOutput = new ArrayList<Report.ListOutput>();
        }
        return this.listOutput;
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
     *         &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="parentUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "uuid",
        "parentUuid"
    })
    public static class ListOutput {

        @XmlElement(required = true)
        protected String uuid;
        protected String parentUuid;

        /**
         * Gets the value of the uuid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUuid() {
            return uuid;
        }

        /**
         * Sets the value of the uuid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUuid(String value) {
            this.uuid = value;
        }

        /**
         * Gets the value of the parentUuid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParentUuid() {
            return parentUuid;
        }

        /**
         * Sets the value of the parentUuid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParentUuid(String value) {
            this.parentUuid = value;
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
     *         &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="parentUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "uuid",
        "parentUuid"
    })
    public static class Query {

        @XmlElement(required = true)
        protected String uuid;
        protected String parentUuid;

        /**
         * Gets the value of the uuid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUuid() {
            return uuid;
        }

        /**
         * Sets the value of the uuid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUuid(String value) {
            this.uuid = value;
        }

        /**
         * Gets the value of the parentUuid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParentUuid() {
            return parentUuid;
        }

        /**
         * Sets the value of the parentUuid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParentUuid(String value) {
            this.parentUuid = value;
        }

    }

}
