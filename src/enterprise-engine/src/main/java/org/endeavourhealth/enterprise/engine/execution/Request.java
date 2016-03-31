package org.endeavourhealth.enterprise.engine.execution;

import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;

public class Request {
    private final CompiledReport compiledReport;
    private final RequestParameters parameters;

    public Request(CompiledReport compiledReport, RequestParameters parameters) {

        this.compiledReport = compiledReport;
        this.parameters = parameters;
    }

    public CompiledReport getCompiledReport() {
        return compiledReport;
    }

    public RequestParameters getParameters() {
        return parameters;
    }
}
