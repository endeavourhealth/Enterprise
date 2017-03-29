
package org.endeavourhealth.enterprise.core.querydocument.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "value", propOrder = {
    "constant",
    "testRuleId",
    "absoluteUnit",
    "relativeUnit"
})
@XmlSeeAlso({
    ValueTo.class,
    ValueFrom.class
})
public class Value {

    protected String constant;
    @XmlSchemaType(name = "string")
    protected ValueAbsoluteUnit absoluteUnit;
    @XmlSchemaType(name = "string")
    protected ValueRelativeUnit relativeUnit;

    protected String testRuleId;

    public String getConstant() {
        return constant;
    }

    public void setConstant(String value) {
        this.constant = value;
    }

    public String getTestRuleId() {
        return testRuleId;
    }

    public void setTestRuleId(String value) {
        this.testRuleId = value;
    }

    public ValueAbsoluteUnit getAbsoluteUnit() {
        return absoluteUnit;
    }

    public void setAbsoluteUnit(ValueAbsoluteUnit value) {
        this.absoluteUnit = value;
    }

    public ValueRelativeUnit getRelativeUnit() {
        return relativeUnit;
    }

    public void setRelativeUnit(ValueRelativeUnit value) {
        this.relativeUnit = value;
    }

}
