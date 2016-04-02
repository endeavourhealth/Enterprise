package org.endeavourhealth.enterprise.engine.compiled;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.querydocument.models.RuleActionOperator;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private List<Node> onTrueNodes;
    private List<Node> onFalseNodes;
    private List<Node> onTrueAndFalseNodes;
    private final RuleActionOperator onTrueAction;
    private final RuleActionOperator onFalseAction;

    public Node(RuleActionOperator onTrueAction, RuleActionOperator onFalseAction) {
        this.onTrueAction = onTrueAction;
        this.onFalseAction = onFalseAction;
    }

    public List<Node> getOnTrueNodes() {
        return onTrueNodes;
    }

    public List<Node> getOnFalseNodes() {
        return onFalseNodes;
    }

    public RuleActionOperator getOnTrueAction() {
        return onTrueAction;
    }

    public RuleActionOperator getOnFalseAction() {
        return onFalseAction;
    }

    public void setOnTrueNodes(List<Node> onTrueNodes) {
        this.onTrueNodes = onTrueNodes;
    }

    public void setOnFalseNodes(List<Node> onFalseNodes) {
        this.onFalseNodes = onFalseNodes;
    }

    public void buildOnTrueAndFalseNodes() {
        if (CollectionUtils.isNotEmpty(onTrueNodes)) {

            if (onTrueAndFalseNodes == null)
                onTrueAndFalseNodes = new ArrayList<>();

            onTrueAndFalseNodes.addAll(onTrueNodes);
        }

        if (CollectionUtils.isNotEmpty(onFalseNodes)) {

            if (onTrueAndFalseNodes == null)
                onTrueAndFalseNodes = new ArrayList<>();

            onTrueAndFalseNodes.addAll(onFalseNodes);
        }
    }

    public List<Node> getOnTrueAndFalseNodes() {
        return onTrueAndFalseNodes;
    }

}
