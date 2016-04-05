package org.endeavourhealth.enterprise.engine.compiler

import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType
import org.endeavourhealth.enterprise.core.querydocument.models.DataSource
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
    }

    @Test
    void testCompiler_filterFunctionality_success() {

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



//
//    void testCompiler_restrictionDescending_success() {
//        EntityMapWrapper.EntityMap map = Helpers.buildStandardEntityMap();
//        Library library = new Library();
//        CompilerApi compilerAPI = new CompilerApi(map, library);
//
//        Restriction restriction = new Restriction(fieldName: "clinicalDate", orderDirection: OrderDirection.DESCENDING, count: 1);
//
//        UUID dataSourceId = UUID.randomUUID();
//        DataSource dataSource = new DataSource(entity: map.entity.get(0).logicalName, uuid: dataSourceId);
//        dataSource.restriction = restriction;
//
//        ICompiledDataSource compiledDataSource = compilerAPI.compile(dataSource);
//
//        DataContainer dataContainer = Helpers.buildStandardContainer();
//        Helpers.getClinicalDateDataField(dataContainer).add(new LocalDate(2015, 1, 19));
//        Helpers.getClinicalDateDataField(dataContainer).add(new LocalDate(2015, 1, 22));
//        Helpers.getClinicalDateDataField(dataContainer).add(new LocalDate(2015, 1, 20));
//        Helpers.getClinicalDateDataField(dataContainer).add(new LocalDate(2015, 1, 21));
//
//        Helpers.levelFields(dataContainer);
//
//        compiledDataSource.resolve(dataContainer);
//
//        assert compiledDataSource.anyResults();
//        assert compiledDataSource.count() == 1;
//
//        List<Integer> rowIds = compiledDataSource.getRowIds();
//        int[] expected = [1];
//
//        assert rowIds.equals(expected);
//    }
//
//    void testCompiler_restrictionAscending_success() {
//        EntityMapWrapper.EntityMap map = Helpers.buildStandardEntityMap();
//        Library library = new Library();
//        CompilerApi compilerAPI = new CompilerApi(map, library);
//
//        Restriction restriction = new Restriction(fieldName: "clinicalDate", orderDirection: OrderDirection.ASCENDING, count: 2);
//
//        UUID dataSourceId = UUID.randomUUID();
//        DataSource dataSource = new DataSource(entity: map.entity.get(0).logicalName, uuid: dataSourceId);
//        dataSource.restriction = restriction;
//
//        ICompiledDataSource compiledDataSource = compilerAPI.compile(dataSource);
//
//        DataContainer dataContainer = Helpers.buildStandardContainer();
//        Helpers.getClinicalDateDataField(dataContainer).add(new LocalDate(2015, 1, 19));
//        Helpers.getClinicalDateDataField(dataContainer).add(new LocalDate(2015, 1, 22));
//        Helpers.getClinicalDateDataField(dataContainer).add(new LocalDate(2015, 1, 20));
//        Helpers.getClinicalDateDataField(dataContainer).add(new LocalDate(2015, 1, 21));
//
//        Helpers.levelFields(dataContainer);
//
//        compiledDataSource.resolve(dataContainer);
//
//        assert compiledDataSource.anyResults();
//        assert compiledDataSource.count() == 2;
//
//        List<Integer> rowIds = compiledDataSource.getRowIds();
//        int[] expected = [0, 2];
//
//        assert rowIds.equals(expected);
//    }
}
