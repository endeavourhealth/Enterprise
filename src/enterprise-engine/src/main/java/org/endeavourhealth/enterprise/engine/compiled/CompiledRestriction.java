package org.endeavourhealth.enterprise.engine.compiled;

import org.endeavourhealth.enterprise.core.querydocument.models.OrderDirection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class CompiledRestriction {

    private static class ValueToRowId implements Comparable {
        public final Object value;
        public final int rowId;

        public ValueToRowId(Object value, int rowId) {
            this.value = value;
            this.rowId = rowId;
        }

        @Override
        public int compareTo(Object o) {

            if (value instanceof LocalDate)
                return ((LocalDate)value).compareTo((LocalDate)((ValueToRowId)o).value);
            else if (value instanceof BigDecimal)
                return ((BigDecimal)value).compareTo((BigDecimal)((ValueToRowId)o).value);
            else
                throw new RuntimeException("Restriction type not supported: " + value.getClass().getName());
        }
    }

    private final ICompiledDataSource dataSource;
    private final int fieldId;
    private final OrderDirection orderDirection;
    private final int count;
    private final List<ValueToRowId> temporaryMap = new ArrayList<>();
    private final List<Integer> returnList = new ArrayList<>();

    public CompiledRestriction(ICompiledDataSource dataSource, int fieldId, OrderDirection orderDirection, int count) {
        this.dataSource = dataSource;
        this.fieldId = fieldId;
        this.orderDirection = orderDirection;
        this.count = count;
    }

    public List<Integer> process() {

        temporaryMap.clear();
        returnList.clear();

        populateTemporaryMap();
        sortMap();
        populateReturnList();

        return returnList;
    }

    private void populateReturnList() {
        int countToReturn = count;

        for (int i = 0; i < countToReturn; i++) {
            returnList.add(temporaryMap.get(i).rowId);
        }
    }

    private void sortMap() {
        if (orderDirection == OrderDirection.ASCENDING)
            Collections.sort(temporaryMap);
        else
            Collections.sort(temporaryMap, Collections.reverseOrder());
    }

    private void populateTemporaryMap() {
        for (int rowId: dataSource.getRowIds()) {
            Object value = dataSource.getValue(rowId, fieldId);

            if (value == null)
                continue;;

            ValueToRowId valueToRowId = new ValueToRowId(value, rowId);
            temporaryMap.add(valueToRowId);
        }
    }
}
