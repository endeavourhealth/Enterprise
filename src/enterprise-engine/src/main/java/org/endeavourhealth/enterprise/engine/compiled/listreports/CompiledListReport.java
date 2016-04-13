package org.endeavourhealth.enterprise.engine.compiled.listreports;

import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

import java.util.List;
import java.util.UUID;

public class CompiledListReport {

    private List<ICompiledListReportGroup> groups;

    public CompiledListReport(List<ICompiledListReportGroup> groups) {
        this.groups = groups;
    }

    public void execute(ExecutionContext context, UUID jobReportItemUuid) throws Exception {

        for (ICompiledListReportGroup group: groups) {
            group.execute(context, jobReportItemUuid);
        }
    }
}
