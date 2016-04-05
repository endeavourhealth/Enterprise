package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.FieldTestFromDataSource;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapException;

import java.util.List;

public interface ICompiledDataSource {

    //Execution
    void resolve(ExecutionContext executionContext);
    boolean anyResults();
    List<Integer> getRowIds();
    Object getValue(Integer rowId, Integer fieldId);

    //Building
    void addFilter(FieldTestFromDataSource filter);
    int getFieldIndex(String logicalName) throws EntityMapException;
    Field getField(int fieldIndex);
    void setRestriction(CompiledRestriction restriction);
}

