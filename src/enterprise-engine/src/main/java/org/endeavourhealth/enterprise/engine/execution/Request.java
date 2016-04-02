package org.endeavourhealth.enterprise.engine.execution;

import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;

import java.util.UUID;

public class Request {
    private final UUID jobReportUuid;
    private final CompiledReport compiledReport;
    private final RequestParameters parameters;

    public Request(
            UUID jobReportUuid,
            CompiledReport compiledReport,
            RequestParameters parameters) {
        this.jobReportUuid = jobReportUuid;

        this.compiledReport = compiledReport;
        this.parameters = parameters;
    }

    public CompiledReport getCompiledReport() {
        return compiledReport;
    }

    public RequestParameters getParameters() {
        return parameters;
    }

    public UUID getJobReportUuid() {
        return jobReportUuid;
    }
}
