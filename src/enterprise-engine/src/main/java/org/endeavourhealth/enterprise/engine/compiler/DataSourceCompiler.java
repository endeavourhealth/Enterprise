package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.querydocument.models.DataSource;
import org.endeavourhealth.enterprise.core.querydocument.models.FieldTest;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.CompiledEntityDataSource;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.FieldTestFromDataSource;


public class DataSourceCompiler {
    private final FieldTestCompiler fieldTestCompiler = new FieldTestCompiler();

    public ICompiledDataSource compile(DataSource dataSource, CompilerContext context) throws Exception {

        ICompiledDataSource target;

        if (dataSource.getEntity() != null) {

            int entityIndex = context.getEntityMapWrapper().getEntityIndexByLogicalName(dataSource.getEntity());
            target = new CompiledEntityDataSource(entityIndex, context.getEntityMapWrapper().getEntity(entityIndex));

        } else {
            throw new UnableToCompileExpection("Only Entity is currently supported");
        }

        if (CollectionUtils.isNotEmpty(dataSource.getFilter())) {
            for (FieldTest fieldTest: dataSource.getFilter()) {

                FieldTestFromDataSource filter = fieldTestCompiler.compile(target, fieldTest);
                target.addFilter(filter);
            }
        }

        return target;
    }
}
