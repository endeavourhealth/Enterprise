
package org.endeavourhealth.enterprise.core.querydocument;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
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
 *         &lt;element name="operator" type="{}ruleOperator" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="test" type="{}test"/>
 *           &lt;element name="testLibraryItemUUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="queryLibraryItemUUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "operator",
    "testOrTestLibraryItemUUIDOrQueryLibraryItemUUID",
    "onTrue",
    "onFalse"
})
public class Rule {

    @XmlSchemaType(name = "string")
    protected RuleOperator operator;
    @XmlElementRefs({
        @XmlElementRef(name = "testLibraryItemUUID", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "test", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "queryLibraryItemUUID", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> testOrTestLibraryItemUUIDOrQueryLibraryItemUUID;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RuleAction onTrue;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RuleAction onFalse;

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
     * Gets the value of the testOrTestLibraryItemUUIDOrQueryLibraryItemUUID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the testOrTestLibraryItemUUIDOrQueryLibraryItemUUID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTestOrTestLibraryItemUUIDOrQueryLibraryItemUUID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Test }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getTestOrTestLibraryItemUUIDOrQueryLibraryItemUUID() {
        if (testOrTestLibraryItemUUIDOrQueryLibraryItemUUID == null) {
            testOrTestLibraryItemUUIDOrQueryLibraryItemUUID = new ArrayList<JAXBElement<?>>();
        }
        return this.testOrTestLibraryItemUUIDOrQueryLibraryItemUUID;
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
