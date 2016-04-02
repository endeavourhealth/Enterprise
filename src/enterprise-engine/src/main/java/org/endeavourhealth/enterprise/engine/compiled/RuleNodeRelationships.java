package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.core.querydocument.models.Rule;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;
import org.junit.validator.PublicClassValidator;

import java.util.HashMap;

public class RuleNodeRelationships {

    private final HashMap<Rule, Node> ruleToNodeMap = new HashMap<>();
    private final HashMap<Node, Rule> nodeToRuleMap = new HashMap<>();
    private final HashMap<Integer, Node> ruleIdToNodeMap = new HashMap<>();
    private final HashMap<Node, Integer> nodeToRuleIdMap = new HashMap<>();

    public void add(Rule rule, Node node) throws InvalidQueryDocumentException {
        ruleToNodeMap.put(rule, node);
        nodeToRuleMap.put(node, rule);

        if (ruleIdToNodeMap.containsKey(rule.getId()))
            throw new InvalidQueryDocumentException("Multiple rules have the same id:" + rule.getId());

        ruleIdToNodeMap.put(rule.getId(), node);
        nodeToRuleIdMap.put(node, rule.getId());
    }

    public Rule getRuleFromNode(Node node) {
        return nodeToRuleMap.get(node);
    }

    public Node getNodeFromRule(Rule rule) {
        return ruleToNodeMap.get(rule);
    }

    public Node getNodeFromRuleId(int ruleId) throws InvalidQueryDocumentException {
        if (!ruleIdToNodeMap.containsKey(ruleId))
            throw new InvalidQueryDocumentException("RuleId not found: " + ruleId);

        return ruleIdToNodeMap.get(ruleId);
    }

    public int getRuleIdFromNode(Node node) {
        return nodeToRuleIdMap.get(node);
    }
}
