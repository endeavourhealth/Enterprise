package org.endeavourhealth.enterprise.engine.compiler

import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType
import org.endeavourhealth.enterprise.core.querydocument.models.DataSource
import org.endeavourhealth.enterprise.core.querydocument.models.OrderDirection
import org.endeavourhealth.enterprise.core.querydocument.models.ValueAbsoluteUnit
import org.endeavourhealth.enterprise.core.querydocument.models.ValueFrom
import org.endeavourhealth.enterprise.core.querydocument.models.ValueFromOperator
import org.endeavourhealth.enterprise.engine.compiled.CompiledQuery
import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext
import org.endeavourhealth.enterprise.engine.testhelpers.DataContainerBuilder
import org.endeavourhealth.enterprise.engine.testhelpers.DataSourceBuilder
import org.endeavourhealth.enterprise.engine.testhelpers.EntityMapBuilder
import org.endeavourhealth.enterprise.enginecore.Library
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper
import org.junit.Test

import java.time.LocalDate

import static org.junit.Assert.*;

class DataSourceCompilerTest {

    private static class DataSourceAssertion {
        private final ICompiledDataSource compiledDataSource;

        public DataSourceAssertion(EntityMapWrapper.EntityMap entityMap, DataSource dataSource, DataContainer dataContainer) {

            Library library = new Library();
            CompilerContext compilerContext = new CompilerContext(entityMap, library)

            DataSourceCompiler dataSourceCompiler = new DataSourceCompiler();
            compiledDataSource = dataSourceCompiler.compile(dataSource, compilerContext);

            Map<UUID, CompiledQuery> compiledQueryMap = new HashMap<>();
            ExecutionContext executionContext = new ExecutionContext(compiledQueryMap);
            executionContext.setItem(dataContainer);

            compiledDataSource.resolve(executionContext);
        }

        public DataSourceAssertion assertAnyResults() {
            assert compiledDataSource.anyResults();
            return this;
        }

        public DataSourceAssertion assertRows(List<Integer> rowIndexes) {
            Set<Integer> expectedSet = new HashSet<>();
            expectedSet.addAll(rowIndexes);

            List<Integer> rowIds = compiledDataSource.getRowIds();
            Set<Integer> actualSet = new HashSet<>(rowIds);

            assert actualSet.equals(expectedSet);
            return this;
        }

        public DataSourceAssertion assertRowInOrder(List<Integer> rowIndexes) {

            assert rowIndexes.equals(compiledDataSource.getRowIds());
//
//            assert rowIndexes.size() == compiledDataSource.getRowIds().size();
//
//            for (int i = 0; i < rowIndexes.size(); i++) {
//                if (rowIndexes.get(i).)
//            }

            return this;
        }
    }

    @Test
    void testDataSource_filterFunctionality_success() {

        EntityMapWrapper.EntityMap entityMap = new EntityMapBuilder()
                .addEntity(LogicalDataType.UUID, LogicalDataType.DATE)
                .build();

        DataContainerBuilder dataContainerBuilder = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 19))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 20))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 21))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 22));

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(entityMap)
            .setEntity(0)
            .addFilter(1, from)

        new DataSourceAssertion(entityMap, dataSourceBuilder.build(), dataContainerBuilder.build())
            .assertAnyResults()
            .assertRows([2, 3])
    }

    @Test
    void testDataSource_restrictionDescending_success() {

        EntityMapWrapper.EntityMap entityMap = new EntityMapBuilder()
                .addEntity(LogicalDataType.UUID, LogicalDataType.DATE)
                .build();

        DataContainerBuilder dataContainerBuilder = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 19))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 20))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 21))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 22));

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(entityMap)
                .setEntity(0)
                .addFilter(1, from)
                .setRestriction(1, OrderDirection.DESCENDING, 2);

        new DataSourceAssertion(entityMap, dataSourceBuilder.build(), dataContainerBuilder.build())
                .assertAnyResults()
                .assertRowInOrder([3, 2])
    }


    @Test
    void testDataSource_restrictionAscending_success() {

        EntityMapWrapper.EntityMap entityMap = new EntityMapBuilder()
                .addEntity(LogicalDataType.UUID, LogicalDataType.DATE)
                .build();

        DataContainerBuilder dataContainerBuilder = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 19))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 20))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 21))
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 22));

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(entityMap)
                .setEntity(0)
                .addFilter(1, from)
                .setRestriction(1, OrderDirection.ASCENDING, 2);

        new DataSourceAssertion(entityMap, dataSourceBuilder.build(), dataContainerBuilder.build())
                .assertAnyResults()
                .assertRowInOrder([1, 2])
    }
}
