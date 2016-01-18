
package org.endeavourhealth.discovery.core.discoverydefinition;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="operator" type="{}ruleOperator"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="test" type="{}test"/>
 *           &lt;element name="libraryItemUuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;element name="onTrue" type="{}ruleAction"/>
 *         &lt;element name="onFalse" type="{}ruleAction"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rule", propOrder = {
    "uuid",
    "operator",
    "testOrLibraryItemUuid",
    "onTrue",
    "onFalse"
})
public class Rule {

    @XmlElement(required = true)
    protected String uuid;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RuleOperator operator;
    @XmlElements({
        @XmlElement(name = "test", type = Test.class),
        @XmlElement(name = "libraryItemUuid", type = String.class)
    })
    protected List<Object> testOrLibraryItemUuid;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RuleAction onTrue;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RuleAction onFalse;

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
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link RuleOperator }
     *     
     */
    public RuleOperator getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleOperator }
     *     
     */
    public void setOperator(RuleOperator value) {
        this.operator = value;
    }

    /**
     * Gets the value of the testOrLibraryItemUuid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the testOrLibraryItemUuid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTestOrLibraryItemUuid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Test }
     * {@link String }
     * 
     * 
     */
    public List<Object> getTestOrLibraryItemUuid() {
        if (testOrLibraryItemUuid == null) {
            testOrLibraryItemUuid = new ArrayList<Object>();
        }
        return this.testOrLibraryItemUuid;
    }

    /**
     * Gets the value of the onTrue property.
     * 
     * @return
     *     possible object is
     *     {@link RuleAction }
     *     
     */
    public RuleAction getOnTrue() {
        return onTrue;
    }

    /**
     * Sets the value of the onTrue property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleAction }
     *     
     */
    public void setOnTrue(RuleAction value) {
        this.onTrue = value;
    }

    /**
     * Gets the value of the onFalse property.
     * 
     * @return
     *     possible object is
     *     {@link RuleAction }
     *     
     */
    public RuleAction getOnFalse() {
        return onFalse;
    }

    /**
     * Sets the value of the onFalse property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleAction }
     *     
     */
    public void setOnFalse(RuleAction value) {
        this.onFalse = value;
    }

}
