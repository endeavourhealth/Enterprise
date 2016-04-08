package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

import java.util.UUID;

public class CompiledRuleOfTypeTest implements ICompiledRule {

    private final UUID testUuid;

    public CompiledRuleOfTypeTest(UUID testUuid) {
        this.testUuid = testUuid;
    }

    @Override
    public boolean execute(ExecutionContext context) throws Exception {
        return context.getTestResult(testUuid);
    }
}
