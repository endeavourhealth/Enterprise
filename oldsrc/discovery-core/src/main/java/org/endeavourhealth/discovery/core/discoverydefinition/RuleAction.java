
package org.endeavourhealth.discovery.core.discoverydefinition;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ruleAction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ruleAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="include"/>
 *     &lt;enumeration value="exclude"/>
 *     &lt;enumeration value="nextRule"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ruleAction")
@XmlEnum
public enum RuleAction {

    @XmlEnumValue("include")
    INCLUDE("include"),
    @XmlEnumValue("exclude")
    EXCLUDE("exclude"),
    @XmlEnumValue("nextRule")
    NEXT_RULE("nextRule");
    private final String value;

    RuleAction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RuleAction fromValue(String v) {
        for (RuleAction c: RuleAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
