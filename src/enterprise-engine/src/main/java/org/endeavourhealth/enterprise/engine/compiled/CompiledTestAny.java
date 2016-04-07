package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

import java.util.UUID;

public class CompiledTestAny implements ICompiledTest {

    private UUID dataSourceUuid;

    public CompiledTestAny(UUID dataSourceUuid) {
        this.dataSourceUuid = dataSourceUuid;
    }

    @Override
    public boolean passesTest(ExecutionContext context) throws Exception {

        ICompiledDataSource dataSource = context.getDataSourceResult(dataSourceUuid);
        return dataSource.anyResults();
    }
}
