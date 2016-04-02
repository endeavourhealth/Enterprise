package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.engine.compiled.Node;

import java.util.List;

class NodeCycleChecker {

    public static boolean Test(List<Node> allNodes) {

        for (Node node: allNodes) {
            if (visitChildNodes(node, node))
                return true;
        }

        return false;
    }

    private static boolean visitChildNodes(Node activeNode, Node nodeToFind) {
        return visitChildNodes(activeNode.getOnTrueAndFalseNodes(), nodeToFind);
    }

    private static boolean visitChildNodes(List<Node> childNodes, Node nodeToFind) {
        if (childNodes == null)
            return false;

        for (Node node: childNodes) {
            if (node == nodeToFind)
                return true;
            else if (visitChildNodes(node, nodeToFind))
                return true;
        }

        return false;
    }
}
