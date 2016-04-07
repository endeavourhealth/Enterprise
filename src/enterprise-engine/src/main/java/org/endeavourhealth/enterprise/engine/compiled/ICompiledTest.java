package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.engine.ExecutionException;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

public interface ICompiledTest {
    boolean passesTest(ExecutionContext context) throws Exception;
}
