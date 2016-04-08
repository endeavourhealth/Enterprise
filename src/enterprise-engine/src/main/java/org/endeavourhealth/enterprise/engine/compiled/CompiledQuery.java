package org.endeavourhealth.enterprise.engine.compiled;

import com.sun.istack.internal.Nullable;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

import java.util.HashMap;
import java.util.UUID;

public class CompiledQuery implements INodeExecutor {

    private final NodeTraversal nodeTraversal;
    private final HashMap<Node, ICompiledRule> nodeToRuleMap;
    private final UUID parentUuid;
    private ExecutionContext context;

    public CompiledQuery(
            NodeTraversal nodeTraversal,
            HashMap<Node, ICompiledRule> nodeToRuleMap,
            @Nullable UUID parentUuid) {

        this.nodeTraversal = nodeTraversal;
        this.nodeToRuleMap = nodeToRuleMap;
        this.parentUuid = parentUuid;
    }

    public boolean isIncluded(ExecutionContext context) throws Exception {

        if (parentUuid != null) {
            if (!context.getQueryResult(parentUuid))
                return false;
        }

        this.context = context;
        return nodeTraversal.execute();
    }

    @Override
    public boolean execute(Node node) throws Exception {

        ICompiledRule rule = nodeToRuleMap.get(node);

        return rule.execute(context);
    }
}
