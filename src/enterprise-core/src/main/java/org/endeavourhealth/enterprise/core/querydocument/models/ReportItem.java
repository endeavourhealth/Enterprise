
package org.endeavourhealth.enterprise.core.querydocument.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reportItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reportItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="queryLibraryItemUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="listReportLibraryItemUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
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
@XmlType(name = "reportItem", propOrder = {
    "queryLibraryItemUuid",
    "listReportLibraryItemUuid",
    "parentUuid"
})
public class ReportItem {

    protected String queryLibraryItemUuid;
    protected String listReportLibraryItemUuid;
    protected String parentUuid;

    /**
     * Gets the value of the queryLibraryItemUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryLibraryItemUuid() {
        return queryLibraryItemUuid;
    }

    /**
     * Sets the value of the queryLibraryItemUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryLibraryItemUuid(String value) {
        this.queryLibraryItemUuid = value;
    }

    /**
     * Gets the value of the listReportLibraryItemUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListReportLibraryItemUuid() {
        return listReportLibraryItemUuid;
    }

    /**
     * Sets the value of the listReportLibraryItemUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListReportLibraryItemUuid(String value) {
        this.listReportLibraryItemUuid = value;
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
