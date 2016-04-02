package org.endeavourhealth.enterprise.engine;

import org.endeavourhealth.enterprise.engine.compiled.CompiledQuery;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;
import org.endeavourhealth.enterprise.engine.execution.Request;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Processor {

    private final ExecutionContext executionContext;
    private final List<Request> requests;

    public Processor(List<Request> requests, Map<UUID, CompiledQuery> queryMap) {

        executionContext = new ExecutionContext(queryMap);
        this.requests = requests;
    }

    public void process(DataContainer dataContainer) throws ExecutionException {

        executionContext.setItem(dataContainer);

        for (Request request: requests) {
            try {
                executionContext.setRequestParameters(request.getParameters());
                request.getCompiledReport().execute(executionContext);
            } catch (Exception e) {
                throw new ExecutionException("Request UUID: " + request.getJobReportUuid(), e);
            }
        }
    }
}
