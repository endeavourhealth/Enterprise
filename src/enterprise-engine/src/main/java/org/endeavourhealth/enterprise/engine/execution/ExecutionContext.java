package org.endeavourhealth.enterprise.engine.execution;

import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.ExecutionException;
import org.endeavourhealth.enterprise.engine.compiled.CompiledLibrary;
import org.endeavourhealth.enterprise.engine.compiled.CompiledQuery;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledTest;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExecutionContext {

    private DataContainer dataContainer;

    private final CompiledLibrary compiledLibrary;

    private final Set<UUID> queriesPatientIncluded = new HashSet<>();
    private final Set<UUID> queriesPatientExcluded = new HashSet<>();

    private final Set<UUID> testsPatientIncluded = new HashSet<>();
    private final Set<UUID> testsPatientExcluded = new HashSet<>();

    private final Set<UUID> resolvedDataSources = new HashSet<>();
    private RequestParameters requestParameters;

    public ExecutionContext(CompiledLibrary compiledLibrary) {

        this.compiledLibrary = compiledLibrary;
    }

    public boolean getQueryResult(UUID queryUuid) throws Exception {

        if (queriesPatientIncluded.contains(queryUuid))
            return true;
        else if (queriesPatientExcluded.contains(queryUuid))
            return false;
        else {
            CompiledQuery query = compiledLibrary.getCompiledQuery(queryUuid);

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

    public boolean getTestResult(UUID testUuid) throws Exception {

        if (testsPatientIncluded.contains(testUuid))
            return true;
        else if (testsPatientExcluded.contains(testUuid))
            return false;
        else {
            ICompiledTest test = compiledLibrary.getCompiledTest(testUuid);

            if (test == null)
                throw new ExecutionException("Test requested but not in cache: " + testUuid);

            boolean result = test.passesTest(this);

            if (result)
                testsPatientIncluded.add(testUuid);
            else
                testsPatientExcluded.add(testUuid);

            return result;
        }
    }

    public ICompiledDataSource getDataSourceResult(UUID dataSourceUuid) throws Exception {

        ICompiledDataSource dataSource = compiledLibrary.getCompiledDataSource(dataSourceUuid);

        if (dataSource == null)
            throw new ExecutionException("DataSource requested but not in cache: " + dataSourceUuid);

        if (!resolvedDataSources.contains(dataSourceUuid)) {
            dataSource.resolve(this);
            resolvedDataSources.add(dataSourceUuid);
        }

        return dataSource;
    }

    public void setItem(DataContainer dataContainer) {
        clearResults();
        this.dataContainer = dataContainer;
        this.requestParameters = null;
    }

    private void clearResults() {
        queriesPatientExcluded.clear();
        queriesPatientIncluded.clear();
        testsPatientIncluded.clear();
        testsPatientIncluded.clear();
        resolvedDataSources.clear();
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
