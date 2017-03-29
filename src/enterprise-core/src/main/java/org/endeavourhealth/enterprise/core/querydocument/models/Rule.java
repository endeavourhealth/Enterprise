
package org.endeavourhealth.enterprise.core.querydocument.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rule", propOrder = {
    "description",
    "id",
    "type",
    "test",
    "testLibraryItemUUID",
    "queryLibraryItemUUID",
    "expression",
    "onPass",
    "onFail",
    "layout"
})
public class Rule {

    @XmlElement(required = true)
    protected String description;
    protected int id;
    protected int type;
    protected Test test;
    protected String testLibraryItemUUID;
    protected String queryLibraryItemUUID;
    protected ExpressionType expression;
    @XmlElement(required = true)
    protected RuleAction onPass;
    @XmlElement(required = true)
    protected RuleAction onFail;
    @XmlElement(required = true)
    protected LayoutType layout;


    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public int getId() {
        return id;
    }

    public void setType(int value) {
        this.type = value;
    }

    public int getType() {
        return type;
    }

    public void setId(int value) {
        this.id = value;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test value) {
        this.test = value;
    }

    public String getTestLibraryItemUUID() {
        return testLibraryItemUUID;
    }

    public void setTestLibraryItemUUID(String value) {
        this.testLibraryItemUUID = value;
    }

    public String getQueryLibraryItemUUID() {
        return queryLibraryItemUUID;
    }

    public void setQueryLibraryItemUUID(String value) {
        this.queryLibraryItemUUID = value;
    }

    public ExpressionType getExpression() {
        return expression;
    }

    public void setExpression(ExpressionType value) {
        this.expression = value;
    }

    public RuleAction getOnPass() {
        return onPass;
    }

    public void setOnPass(RuleAction value) {
        this.onPass = value;
    }

    public RuleAction getOnFail() {
        return onFail;
    }

    public void setOnFail(RuleAction value) {
        this.onFail = value;
    }

    public LayoutType getLayout() {
        return layout;
    }

    public void setLayout(LayoutType value) {
        this.layout = value;
    }

}
