package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentHelper;
import org.endeavourhealth.enterprise.core.querydocument.models.FieldTest;
import org.endeavourhealth.enterprise.core.querydocument.models.Test;
import org.endeavourhealth.enterprise.engine.compiled.CompiledTestAny;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledTest;
import org.endeavourhealth.enterprise.engine.compiled.CompiledTestWithFields;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.FieldTestFromDataSource;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestCompiler {

    public ICompiledTest compile(Test source, CompilerContext context) throws Exception {

        UUID dataSourceUuid = getDataSourceUuid(source, context);

        if (source.getIsAny() != null)
            return new CompiledTestAny(dataSourceUuid);
        else {

            if (CollectionUtils.isEmpty(source.getFieldTest()))
                throw new InvalidQueryDocumentException("Test contains no fieldtests");

            return createCompiledTestWithFields(source, context, dataSourceUuid);
        }
    }

    private ICompiledTest createCompiledTestWithFields(Test source, CompilerContext context, UUID dataSourceUuid) throws Exception {

        FieldTestCompiler fieldTestCompiler = new FieldTestCompiler();
        ICompiledDataSource dataSource = context.getCompiledLibrary().getCompiledDataSource(dataSourceUuid);

        if (dataSource.canReturnMultipleResults())
            throw new InvalidQueryDocumentException("DataSource must return a single row to use FieldTests");

        List<FieldTestFromDataSource> fieldTestFromDataSources = new ArrayList<>();

        for (FieldTest fieldTest: source.getFieldTest()) {
            FieldTestFromDataSource compiledFieldTest = fieldTestCompiler.compile(dataSource, fieldTest);
            fieldTestFromDataSources.add(compiledFieldTest);
        }

        return new CompiledTestWithFields(dataSourceUuid, fieldTestFromDataSources);
    }

    private UUID getDataSourceUuid(Test source, CompilerContext context) throws Exception {
        UUID dataSourceUuid;

        if (source.getDataSource() != null) {
            DataSourceCompiler dataSourceCompiler = new DataSourceCompiler();
            ICompiledDataSource dataSource = dataSourceCompiler.compile(source.getDataSource(), context);

            dataSourceUuid = UUID.randomUUID();

            context.getCompiledLibrary().addInlineDataSource(dataSourceUuid, dataSource);

        } else {
            dataSourceUuid = QueryDocumentHelper.parseMandatoryUuid(source.getDataSourceUuid());
            context.getCompiledLibrary().checkHasDataSource(dataSourceUuid);
        }

        return dataSourceUuid;
    }
}
