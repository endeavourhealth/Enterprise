package org.endeavourhealth.enterprise.engine.compiler

import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType
import org.endeavourhealth.enterprise.core.querydocument.models.DataSource
import org.endeavourhealth.enterprise.core.querydocument.models.FieldTest
import org.endeavourhealth.enterprise.core.querydocument.models.OrderDirection
import org.endeavourhealth.enterprise.core.querydocument.models.ValueAbsoluteUnit
import org.endeavourhealth.enterprise.core.querydocument.models.ValueFrom
import org.endeavourhealth.enterprise.core.querydocument.models.ValueFromOperator
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection
import org.endeavourhealth.enterprise.engine.compiled.CompiledLibrary
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext
import org.endeavourhealth.enterprise.engine.testhelpers.CompiledLibraryBuilder
import org.endeavourhealth.enterprise.engine.testhelpers.DataContainerBuilder
import org.endeavourhealth.enterprise.engine.testhelpers.DataSourceBuilder
import org.endeavourhealth.enterprise.engine.testhelpers.EntityMapBuilder
import org.endeavourhealth.enterprise.engine.testhelpers.FieldTestBuilder
import org.endeavourhealth.enterprise.engine.testhelpers.TestBuilder
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper
import org.junit.Test

import java.time.LocalDate


class TestCompilerTest {

    private static class TestAssertion {

        private final ExecutionContext executionContext;
        private final UUID testUuid;

        public TestAssertion(
                EntityMapWrapper.EntityMap entityMap,
                org.endeavourhealth.enterprise.core.querydocument.models.Test test) {

            testUuid = UUID.randomUUID();

            CompiledLibrary compiledLibrary = new CompiledLibraryBuilder(entityMap)
                .addTest(test, testUuid)
                .build();

            executionContext = new ExecutionContext(compiledLibrary);
        }

        public TestAssertion(
                EntityMapWrapper.EntityMap entityMap,
                org.endeavourhealth.enterprise.core.querydocument.models.Test test,
                DataSource dataSource,
                UUID dataSourceUuid) {

            testUuid = UUID.randomUUID();

            CompiledLibrary compiledLibrary = new CompiledLibraryBuilder(entityMap)
                    .addTest(test, testUuid)
                    .addDataSource(dataSource, dataSourceUuid)
                    .build();

            executionContext = new ExecutionContext(compiledLibrary);
        }

        public void assertTrue(DataContainer dataContainer) {
            executionContext.setItem(dataContainer);
            assert executionContext.getTestResult(testUuid);;
        }

        public void assertFalse(DataContainer dataContainer) {
            executionContext.setItem(dataContainer);
            assert !executionContext.getTestResult(testUuid);;
        }
    }

    @Test
    void testCompiler_inlineAndIsAny_success() {

        EntityMapWrapper.EntityMap entityMap = new EntityMapBuilder()
                .addEntity(LogicalDataType.UUID, LogicalDataType.DATE)
                .build();

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        DataSource dataSource = new DataSourceBuilder(entityMap)
                .setEntity(0)
                .addFilter(1, from)
                .build();

        org.endeavourhealth.enterprise.core.querydocument.models.Test test = new TestBuilder()
                .addDataSource(dataSource)
                .setIsAny()
                .build();

        TestAssertion testAssertion = new TestAssertion(entityMap, test);

        DataContainer dataContainer = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 22))
                .build();

        testAssertion.assertTrue(dataContainer);

        dataContainer = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 19))
                .build();

        testAssertion.assertFalse(dataContainer);
    }

    @Test
    void testCompiler_referenceAndIsAny_success() {

        EntityMapWrapper.EntityMap entityMap = new EntityMapBuilder()
                .addEntity(LogicalDataType.UUID, LogicalDataType.DATE)
                .build();

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        DataSource dataSource = new DataSourceBuilder(entityMap)
                .setEntity(0)
                .addFilter(1, from)
                .build();

        UUID dataSourceUuid = UUID.randomUUID();

        org.endeavourhealth.enterprise.core.querydocument.models.Test test = new TestBuilder()
                .addDataSourceUuid(dataSourceUuid)
                .setIsAny()
                .build();

        TestAssertion testAssertion = new TestAssertion(entityMap, test, dataSource, dataSourceUuid);

        DataContainer dataContainer = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 22))
                .build();

        testAssertion.assertTrue(dataContainer);

        dataContainer = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 19))
                .build();

        testAssertion.assertFalse(dataContainer);
    }

    @Test
    void testCompiler_fieldTests_success() {

        EntityMapWrapper.EntityMap entityMap = new EntityMapBuilder()
                .addEntity(LogicalDataType.UUID, LogicalDataType.DATE)
                .build();

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        DataSource dataSource = new DataSourceBuilder(entityMap)
                .setEntity(0)
                .addFilter(1, from)
                .setRestriction(1, OrderDirection.DESCENDING, 1)
                .build();

        ValueFrom fieldTestFrom = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "2015-01-25", absoluteUnit: ValueAbsoluteUnit.DATE);

        FieldTest fieldTest = new FieldTestBuilder(entityMap)
                .setField(0, 1)
                .setFieldTest(fieldTestFrom)
                .build();

        org.endeavourhealth.enterprise.core.querydocument.models.Test test = new TestBuilder()
                .addDataSource(dataSource)
                .addFieldTest(fieldTest)
                .build();

        TestAssertion testAssertion = new TestAssertion(entityMap, test);

        DataContainer dataContainer = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 19))
                .build();

        testAssertion.assertFalse(dataContainer);

        dataContainer = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 22))
                .build();

        testAssertion.assertFalse(dataContainer);

        dataContainer = new DataContainerBuilder(entityMap)
                .addRow(UUID.randomUUID(), LocalDate.of(2015, 1, 27))
                .build();

        testAssertion.assertTrue(dataContainer);
    }


    @Test(expected = UnableToCompileExpection.class)
    void testCompiler_fieldTestsWithMultipleRows_exception() {

        EntityMapWrapper.EntityMap entityMap = new EntityMapBuilder()
                .addEntity(LogicalDataType.UUID, LogicalDataType.DATE)
                .build();

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        DataSource dataSource = new DataSourceBuilder(entityMap)
                .setEntity(0)
                .addFilter(1, from)  //No restriction
                .build();

        ValueFrom fieldTestFrom = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "2015-01-25", absoluteUnit: ValueAbsoluteUnit.DATE);

        FieldTest fieldTest = new FieldTestBuilder(entityMap)
                .setField(0, 1)
                .setFieldTest(fieldTestFrom)
                .build();

        org.endeavourhealth.enterprise.core.querydocument.models.Test test = new TestBuilder()
                .addDataSource(dataSource)
                .addFieldTest(fieldTest)
                .build();

        new TestAssertion(entityMap, test);
    }
}
