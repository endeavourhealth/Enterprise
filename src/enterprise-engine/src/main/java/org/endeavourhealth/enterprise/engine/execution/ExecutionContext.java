package org.endeavourhealth.enterprise.engine.execution;

import com.sun.xml.internal.ws.util.Pool;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.ExecutionException;
import org.endeavourhealth.enterprise.engine.compiled.CompiledQuery;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class ExecutionContext {

    private DataContainer dataContainer;

    private HashSet<UUID> queriesPatientExcluded = new HashSet<>();
    private HashSet<UUID> queriesPatientIncluded = new HashSet<>();

    private Map<UUID, CompiledQuery> queryMap;
    private RequestParameters requestParameters;

    public ExecutionContext(Map<UUID, CompiledQuery> queryMap) {
        this.queryMap = queryMap;
    }

    public boolean getQueryResult(UUID queryUuid) throws Exception {

        if (queriesPatientIncluded.contains(queryUuid))
            return true;
        else if (queriesPatientExcluded.contains(queryUuid))
            return false;
        else {
            CompiledQuery query = queryMap.get(queryUuid);

            if (query == null)
                throw new ExecutionException("Query requested but not in cache: " + queryUuid);

            boolean result = query.isIncluded(this);

            if (result)
                queriesPatientIncluded.add(queryUuid);
            else
                queriesPatientExcluded.add(queryUuid);

            return result;
        }
    }

    public void setItem(DataContainer dataContainer) {
        clearResults();
        this.dataContainer = dataContainer;
        this.requestParameters = null;
    }

    private void clearResults() {
        queriesPatientExcluded.clear();
        queriesPatientIncluded.clear();
    }

    public void setRequestParameters(RequestParameters requestParameters) {
        clearResults();

        if (shouldSetRequestParameters(requestParameters))
            this.requestParameters = requestParameters;
    }

    private boolean shouldSetRequestParameters(RequestParameters requestParameters) {

        if (this.requestParameters == null)
            return true;

        if (this.requestParameters.getBaselineDate().equals(requestParameters.getBaselineDate()))
            return false;

        return true;
    }

    public DataContainer getDataContainer() {
        return dataContainer;
    }
}
