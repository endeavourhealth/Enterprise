
package org.endeavourhealth.enterprise.core.querydocument.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "restriction", propOrder = {
    "restriction",
    "count"
})
public class Restriction {

    @XmlElement(required = true)
    protected String restriction;
    protected int count;

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

}
