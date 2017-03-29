
package org.endeavourhealth.enterprise.core.querydocument.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "codeSetValue", propOrder = {
    "code",
    "term",
    "dataType",
    "parentType",
    "baseType",
    "present",
    "valueFrom",
    "valueTo",
    "units",
    "includeChildren",
    "exclusion"
})
public class CodeSetValue {

    @XmlElement(required = true)
    protected String code;
    protected String term;
    protected String dataType;
    protected String parentType;
    protected String baseType;
    protected String present;
    protected String valueFrom;
    protected String valueTo;
    protected String units;
    protected boolean includeChildren;
    protected List<CodeSetValue> exclusion;

    public void setCode(String value) {
        this.code = value;
    }

    public String getCode() {
        return code;
    }

    public void setTerm(String value) {
        this.term = value;
    }

    public String getTerm() {
        return term;
    }

    public void setDataType(String value) {
        this.dataType = value;
    }

    public String getDataType() {
        return dataType;
    }

    public void setParentType(String value) {
        this.parentType = value;
    }

    public String getParentType() {
        return parentType;
    }

    public void setBaseType(String value) {
        this.baseType = value;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setPresent(String value) {
        this.present = value;
    }

    public String getPresent() {
        return present;
    }

    public void setValueFrom(String value) {
        this.valueFrom = value;
    }

    public String getValueFrom() {
        return valueFrom;
    }

    public void setValueTo(String value) {
        this.valueTo = value;
    }

    public String getValueTo() {
        return valueTo;
    }

    public void setUnits(String value) {
        this.units = value;
    }

    public String getUnits() {
        return units;
    }

    public boolean isIncludeChildren() {
        return includeChildren;
    }

    public void setIncludeChildren(boolean value) {
        this.includeChildren = value;
    }

    public List<CodeSetValue> getExclusion() {
        if (exclusion == null) {
            exclusion = new ArrayList<CodeSetValue>();
        }
        return this.exclusion;
    }

}
