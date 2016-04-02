package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.querydocument.models.Query;
import org.endeavourhealth.enterprise.core.querydocument.models.Rule;
import org.endeavourhealth.enterprise.core.querydocument.models.RuleAction;
import org.endeavourhealth.enterprise.core.querydocument.models.RuleActionOperator;
import org.endeavourhealth.enterprise.engine.compiled.Node;
import org.endeavourhealth.enterprise.engine.compiled.NodeTraversal;
import org.endeavourhealth.enterprise.engine.compiled.RuleNodeRelationships;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;

import java.util.*;

public class NodeCompiler {
    private NodeTraversal nodeTraversal;
    private RuleNodeRelationships ruleNodeRelationships;

    public void compile(Query query) throws InvalidQueryDocumentException {

        createNodes(query);

        List<Node> startingNodes = createStartingNodes(query.getStartingRules());
        buildNodeTree(query.getRule());

        nodeTraversal = new NodeTraversal(startingNodes);

        List<Node> allNodes = getAllNodes(query.getRule());

        checkForCycles(allNodes);
        checkForUnusedNodes(startingNodes, allNodes);
    }

    private void checkForCycles(List<Node> allNodes) throws InvalidQueryDocumentException {
        if (NodeCycleChecker.Test(allNodes))
            throw new InvalidQueryDocumentException("Cycle detected");
    }

    private void checkForUnusedNodes(List<Node> startingNodes, List<Node> allNodes) throws InvalidQueryDocumentException {

        Set<Node> nodes = UnusedNodeFinder.find(startingNodes, allNodes);

        if (CollectionUtils.isNotEmpty(nodes)) {
            Node firstNode = (Node)nodes.toArray()[0];
            throw new InvalidQueryDocumentException("Rule not used: " + ruleNodeRelationships.getRuleIdFromNode(firstNode));
        }
    }

    private List<Node> getAllNodes(List<Rule> allRules) {
        List<Node> allNodes = new ArrayList<>();

        for (Rule rule: allRules) {
            allNodes.add(ruleNodeRelationships.getNodeFromRule(rule));
        }

        return allNodes;
    }

    private void buildNodeTree(List<Rule> rules) throws InvalidQueryDocumentException {

        for (Rule rule: rules) {
            Node node = ruleNodeRelationships.getNodeFromRule(rule);

            if (rule.getOnPass().getAction() == RuleActionOperator.GOTO_RULES) {
                node.setOnTrueNodes(new ArrayList<>());
                buildNodeAction(rule.getId(), node.getOnTrueNodes(), rule.getOnPass().getRuleId());
            }

            if (rule.getOnFail().getAction() == RuleActionOperator.GOTO_RULES) {
                node.setOnFalseNodes(new ArrayList<>());
                buildNodeAction(rule.getId(), node.getOnFalseNodes(), rule.getOnFail().getRuleId());
            }

            node.buildOnTrueAndFalseNodes();
        }
    }

    private void buildNodeAction(int ruleId, List<Node> nodeList, List<Integer> gotoRuleIds) throws InvalidQueryDocumentException {

        if (CollectionUtils.isEmpty(gotoRuleIds))
            throw new InvalidQueryDocumentException(String.format("Rule %s does not contain any GotoRules", ruleId));

        for (int gotoRuleId: gotoRuleIds) {
            nodeList.add(ruleNodeRelationships.getNodeFromRuleId(gotoRuleId));
        }
    }

    private List<Node> createStartingNodes(Query.StartingRules startingRules) throws InvalidQueryDocumentException {

        if (startingRules == null || CollectionUtils.isEmpty(startingRules.getRuleId()))
            throw new InvalidQueryDocumentException("No starting rules defined");

        List<Node> startingNodes = new ArrayList<>();
        Set<Integer> uniqueNodes = new HashSet<>();

        for (int startingId: startingRules.getRuleId()) {
            if (uniqueNodes.contains(startingId))
                throw new InvalidQueryDocumentException("Multiple starting nodes with same id: " + startingId);

            startingNodes.add(ruleNodeRelationships.getNodeFromRuleId(startingId));
            uniqueNodes.add(startingId);
        }

        return startingNodes;
    }

    private void createNodes(Query query) throws InvalidQueryDocumentException {
        ruleNodeRelationships = new RuleNodeRelationships();

        if (CollectionUtils.isEmpty(query.getRule()))
            throw new InvalidQueryDocumentException("Query contains no rules");

        for (Rule rule: query.getRule()) {

            validateRuleActions(rule);

            Node node = new Node(rule.getOnPass().getAction(), rule.getOnFail().getAction());
            ruleNodeRelationships.add(rule, node);
        }
    }

    private void validateRuleActions(Rule rule) throws InvalidQueryDocumentException {

        if (rule.getOnPass().getAction() == RuleActionOperator.INCLUDE && rule.getOnFail().getAction() == RuleActionOperator.INCLUDE)
            throw new InvalidQueryDocumentException("Both rule actions cannot be Include.  RuleId: " + rule.getId());

        if (rule.getOnPass().getAction() == RuleActionOperator.NO_ACTION && rule.getOnFail().getAction() == RuleActionOperator.NO_ACTION)
            throw new InvalidQueryDocumentException("Both rule actions cannot be NoAction.  RuleId: " + rule.getId());

        validateRuleAction(rule.getId(), rule.getOnPass());
        validateRuleAction(rule.getId(), rule.getOnFail());

        if (rule.getOnPass().getAction() == RuleActionOperator.GOTO_RULES && rule.getOnFail().getAction() == RuleActionOperator.GOTO_RULES) {
            if (CollectionUtils.containsAny(rule.getOnPass().getRuleId(), rule.getOnFail().getRuleId()))
                throw new InvalidQueryDocumentException("Both actions of rule " + rule.getId() + " are going to the same rule");
        }
    }

    private void validateRuleAction(int ruleId, RuleAction action) throws InvalidQueryDocumentException {
        if (action.getAction() == RuleActionOperator.GOTO_RULES) {
            if (CollectionUtils.isEmpty(action.getRuleId()))
                throw new InvalidQueryDocumentException("Rule action is GotoRules but no rule IDs specified.  RuleId: " + ruleId);
        } else {
            if (CollectionUtils.isNotEmpty(action.getRuleId()))
                throw new InvalidQueryDocumentException("Rule action has RuleIds specified but the action is " + action.getAction() + ".  RuleId: " + ruleId);
        }
    }

    public NodeTraversal getNodeTraversal() {
        return nodeTraversal;
    }

    public RuleNodeRelationships getRuleNodeRelationships() {
        return ruleNodeRelationships;
    }
}
