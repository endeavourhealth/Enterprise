
package org.endeavourhealth.enterprise.core.querydocument.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "restriction", propOrder = {
    "restriction",
    "count",
    "prefix",
    "field"
})
public class Restriction {

    @XmlElement(required = true)
    protected String restriction;
    protected int count;
    protected String prefix;
    protected List<String> field;

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String value) {
        this.restriction = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int value) {
        this.count = value;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String value) {
        this.prefix = value;
    }

    public List<String> getField() {
        if (field == null) {
            field = new ArrayList<String>();
        }
        return this.field;
    }

}
