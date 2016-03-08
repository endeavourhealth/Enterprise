
package org.endeavourhealth.enterprise.core.querydocument;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ruleOperator.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ruleOperator">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="and"/>
 *     &lt;enumeration value="or"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ruleOperator")
@XmlEnum
public enum RuleOperator {

    @XmlEnumValue("and")
    AND("and"),
    @XmlEnumValue("or")
    OR("or");
    private final String value;

    RuleOperator(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RuleOperator fromValue(String v) {
        for (RuleOperator c: RuleOperator.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
