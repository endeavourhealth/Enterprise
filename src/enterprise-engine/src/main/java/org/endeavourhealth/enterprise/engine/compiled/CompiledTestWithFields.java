package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.engine.ExecutionException;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.FieldTestFromDataSource;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;

import java.util.List;
import java.util.UUID;

public class CompiledTestWithFields implements ICompiledTest {
    private final UUID dataSourceUuid;
    private final List<FieldTestFromDataSource> fieldTestFromDataSources;

    public CompiledTestWithFields(UUID dataSourceUuid, List<FieldTestFromDataSource> fieldTestFromDataSources) {

        this.dataSourceUuid = dataSourceUuid;
        this.fieldTestFromDataSources = fieldTestFromDataSources;
    }

    @Override
    public boolean passesTest(ExecutionContext context) throws Exception {

        ICompiledDataSource dataSource = context.getDataSourceResult(dataSourceUuid);

        if (!dataSource.anyResults())
            return false;

        if (dataSource.getRowIds().size() > 1)
            throw new ExecutionException("DataSource returned more than 1 row");

        int rowId = dataSource.getRowIds().get(0);

        for (FieldTestFromDataSource fieldTest: fieldTestFromDataSources) {
            if (!fieldTest.test(dataSource, rowId))
                return false;
        }

        return true;
    }
}
