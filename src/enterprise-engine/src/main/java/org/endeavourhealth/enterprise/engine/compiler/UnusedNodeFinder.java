package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.engine.compiled.Node;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

class UnusedNodeFinder {

    public static Set<Node> find(List<Node> startingNodes, List<Node> allNodes) {
        Set<Node> unusedNodes = new HashSet<>();
        unusedNodes.addAll(allNodes);

        visitNodes(startingNodes, unusedNodes);

        return unusedNodes;
    }

    private static void visitNodes(List<Node> nodes, Set<Node> unusedNodes) {
        if (CollectionUtils.isEmpty(nodes))
            return;

        for (Node node: nodes) {
            unusedNodes.remove(node);
            visitNodes(node.getOnTrueAndFalseNodes(), unusedNodes);
        }
    }
}
