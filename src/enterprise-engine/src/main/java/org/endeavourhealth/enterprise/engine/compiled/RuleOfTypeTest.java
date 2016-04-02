package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

public class RuleOfTypeTest implements ICompiledRule {
    @Override
    public boolean execute(ExecutionContext context) {
        return false;
    }
}
