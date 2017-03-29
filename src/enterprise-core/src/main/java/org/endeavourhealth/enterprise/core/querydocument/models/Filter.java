
package org.endeavourhealth.enterprise.core.querydocument.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filter", propOrder = {
    "field",
    "valueFrom",
    "valueTo",
    "codeSet",
    "codeSetLibraryItemUuid",
    "valueSet",
    "negate"
})
public class Filter {

    @XmlElement(required = true)
    protected String field;
    protected ValueFrom valueFrom;
    protected ValueTo valueTo;
    protected CodeSet codeSet;
    protected List<String> codeSetLibraryItemUuid;
    protected ValueSet valueSet;
    protected boolean negate;

    public String getField() {
        return field;
    }

    public void setField(String value) {
        this.field = value;
    }

    public ValueFrom getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(ValueFrom value) {
        this.valueFrom = value;
    }

    public ValueTo getValueTo() {
        return valueTo;
    }

    public void setValueTo(ValueTo value) {
        this.valueTo = value;
    }

    public CodeSet getCodeSet() {
        return codeSet;
    }

    public void setCodeSet(CodeSet value) {
        this.codeSet = value;
    }

    public List<String> getCodeSetLibraryItemUuid() {
        if (codeSetLibraryItemUuid == null) {
            codeSetLibraryItemUuid = new ArrayList<String>();
        }
        return this.codeSetLibraryItemUuid;
    }

    public ValueSet getValueSet() {
        return valueSet;
    }

    public void setValueSet(ValueSet value) {
        this.valueSet = value;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean value) {
        this.negate = value;
    }

}
