
package org.endeavourhealth.discovery.core.discoverydefinition;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for test complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="test">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="dataSource" type="{}dataSource"/>
 *           &lt;element name="dataSourceUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="any">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="fieldTest" type="{}fieldTest" maxOccurs="unbounded"/>
 *           &lt;element name="count" type="{}comparison"/>
 *         &lt;/choice>
 *         &lt;element name="linkedTest" type="{}linkedTestType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test", propOrder = {
    "dataSource",
    "dataSourceUuid",
    "any",
    "fieldTest",
    "count",
    "linkedTest"
})
public class Test {

    protected DataSource dataSource;
    protected String dataSourceUuid;
    protected Test.Any any;
    protected List<FieldTest> fieldTest;
    protected Comparison count;
    protected LinkedTestType linkedTest;

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
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Test.Any }
     *     
     */
    public Test.Any getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Test.Any }
     *     
     */
    public void setAny(Test.Any value) {
        this.any = value;
    }

    /**
     * Gets the value of the fieldTest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fieldTest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFieldTest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldTest }
     * 
     * 
     */
    public List<FieldTest> getFieldTest() {
        if (fieldTest == null) {
            fieldTest = new ArrayList<FieldTest>();
        }
        return this.fieldTest;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link Comparison }
     *     
     */
    public Comparison getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link Comparison }
     *     
     */
    public void setCount(Comparison value) {
        this.count = value;
    }

    /**
     * Gets the value of the linkedTest property.
     * 
     * @return
     *     possible object is
     *     {@link LinkedTestType }
     *     
     */
    public LinkedTestType getLinkedTest() {
        return linkedTest;
    }

    /**
     * Sets the value of the linkedTest property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinkedTestType }
     *     
     */
    public void setLinkedTest(LinkedTestType value) {
        this.linkedTest = value;
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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Any {


    }

}
