package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

public interface ICompiledRule {
    boolean execute(ExecutionContext context);
}
