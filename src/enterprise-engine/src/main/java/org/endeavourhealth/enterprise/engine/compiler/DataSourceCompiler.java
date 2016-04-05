package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.querydocument.models.DataSource;
import org.endeavourhealth.enterprise.core.querydocument.models.FieldTest;
import org.endeavourhealth.enterprise.core.querydocument.models.Restriction;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.CompiledEntityDataSource;
import org.endeavourhealth.enterprise.engine.compiled.CompiledRestriction;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.FieldTestFromDataSource;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;


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

        if (dataSource.getRestriction() != null) {
            Restriction restriction = dataSource.getRestriction();

            if (restriction.getCount() < 1)
                throw new InvalidQueryDocumentException("Restriction Count cannot be less than 1");

            int fieldIndex = target.getFieldIndex(restriction.getFieldName());

            CompiledRestriction compiledRestriction = new CompiledRestriction(target, fieldIndex, restriction.getOrderDirection(), restriction.getCount());
            target.setRestriction(compiledRestriction);
        }

        return target;
    }
}
