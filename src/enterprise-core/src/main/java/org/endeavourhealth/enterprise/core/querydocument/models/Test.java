
package org.endeavourhealth.enterprise.core.querydocument.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test", propOrder = {
    "testRuleId",
    "filter",
    "restriction"
})
public class Test {

    protected String testRuleId;
    protected List<Filter> filter;
    protected Restriction restriction;

    public String getTestRuleId() {
        return testRuleId;
    }

    public void setTestRuleId(String value) {
        this.testRuleId = value;
    }

    public List<Filter> getFilter() {
        if (filter == null) {
            filter = new ArrayList<Filter>();
        }
        return this.filter;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction value) {
        this.restriction = value;
    }

}
