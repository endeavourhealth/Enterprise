package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.core.querydocument.models.RuleActionOperator;

import java.util.HashSet;
import java.util.List;

public class NodeTraversal {

    private final HashSet<Node> marked = new HashSet<>();
    private final List<Node> startingNodes;
    private INodeExecutor nodeExecutor;

    public NodeTraversal(List<Node> startingNodes) {
        this.startingNodes = startingNodes;
    }

    public void initialise(INodeExecutor nodeExecutor) {
        this.nodeExecutor = nodeExecutor;
    }

    public boolean execute() throws Exception {
        marked.clear();

        for (Node node: startingNodes) {
            if (depthFirstTraversal(node))
                return true;
        }

        return false;
    }

    /*
    * @return True when patient included
    */
    private boolean depthFirstTraversal(Node node) throws Exception {
        marked.add(node);

        if (nodeExecutor.execute(node))
            return processAction(node.getOnTrueAction(), node.getOnTrueNodes());
        else
            return processAction(node.getOnFalseAction(), node.getOnFalseNodes());
    }

    private boolean processAction(RuleActionOperator operator, List<Node> ruleActionNodes) throws Exception {
        switch (operator) {
            case INCLUDE:
                return true;
            case NO_ACTION:
                return false;
            case GOTO_RULES:
                for (Node childNode: ruleActionNodes) {
                    if (!marked.contains(childNode)) {
                        if (depthFirstTraversal(childNode))
                            return true;
                    }
                }

                return false;

            default:
                throw new Exception("Unsupported action: " + operator);
        }
    }
}
