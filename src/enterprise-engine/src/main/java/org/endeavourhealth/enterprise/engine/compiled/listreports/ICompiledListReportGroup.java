package org.endeavourhealth.enterprise.engine.compiled.listreports;

import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

import java.util.UUID;

public interface ICompiledListReportGroup {
    void execute(ExecutionContext context, UUID jobReportItemUuid) throws Exception;
}
