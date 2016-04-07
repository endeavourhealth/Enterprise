package org.endeavourhealth.enterprise.engine.compiled;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.FieldTestFromDataSource;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataEntity;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapException;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.util.*;

public class CompiledEntityDataSource implements ICompiledDataSource {

    private final int entityId;
    private final EntityMapWrapper.Entity entity;
    private final List<Integer> finalRows = new ArrayList<>();
    private List<FieldTestFromDataSource> filters;
    private CompiledRestriction compiledRestriction;

    private DataEntity dataEntity;

    public CompiledEntityDataSource(
            int entityId,
            EntityMapWrapper.Entity entity) {

        this.entityId = entityId;
        this.entity = entity;
    }

    @Override
    public void addFilter(FieldTestFromDataSource filter) {
        if (filters == null)
            filters = new ArrayList<>();

        filters.add(filter);
    }

    @Override
    public int getFieldIndex(String logicalName) throws EntityMapException {
        return entity.getFieldIndex(logicalName);
    }

    @Override
    public Field getField(int fieldIndex) {
        return entity.getField(fieldIndex);
    }

    @Override
    public void setRestriction(CompiledRestriction restriction) {
        this.compiledRestriction = restriction;
    }

    @Override
    public boolean canReturnMultipleResults() {
        if (compiledRestriction != null)
            return compiledRestriction.canReturnMultipleResults();
        else
            return true;
    }

    @Override
    public void resolve(ExecutionContext executionContext) {

        finalRows.clear();
        dataEntity = executionContext.getDataContainer().getDataEntities().get(entityId);

        processFilters();
        processRestriction();

        //finally have a list of row ids.
    }

    @Override
    public boolean anyResults() {
        return !finalRows.isEmpty();
    }

    @Override
    public List<Integer> getRowIds() {
        return finalRows;
    }

    @Override
    public Object getValue(Integer rowId, Integer fieldId) {
        return dataEntity.getFields().get(fieldId).get(rowId);
    }

    private void processFilters() {

        if (CollectionUtils.isEmpty(filters))
            return;

        for (int rowId = 0; rowId < dataEntity.getSize(); rowId++) {
            for (int f = 0; f < filters.size(); f++) {
                if (filters.get(f).test(dataEntity, rowId))
                    finalRows.add(rowId);
            }
        }
    }

    private void processRestriction() {
        if (compiledRestriction == null)
            return;

        List<Integer> orderedIds = compiledRestriction.process();
        finalRows.clear();
        finalRows.addAll(orderedIds);
    }
}