package org.endeavourhealth.enterprise.engine.compiler

import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType
import org.endeavourhealth.enterprise.core.querydocument.models.*
import org.endeavourhealth.enterprise.engine.testhelpers.FieldTesterBuilder
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException
import org.junit.Test

import java.time.LocalDate

class FieldCompilerTest {

    @Test
    void fieldCompiler_greaterThanDate_success() {

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
            .setDataType(LogicalDataType.DATE)
            .setFieldTest(from)
            .build();

        assertion.assertFalse(LocalDate.of(2015, 01, 19));
        assertion.assertFalse(LocalDate.of(2015, 01, 20));
        assertion.assertTrue(LocalDate.of(2015, 01, 21));
        assertion.assertFalse(null);
    }

    @Test
    void fieldCompiler_greaterThanOrEqualToDate_success() {

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.DATE)
                .setFieldTest(from)
                .build();

        assertion.assertFalse(LocalDate.of(2015, 01, 19));
        assertion.assertTrue(LocalDate.of(2015, 01, 20));
        assertion.assertTrue(LocalDate.of(2015, 01, 21));
        assertion.assertFalse(null);
    }

    @Test
    void fieldCompiler_lessThanDate_success() {

        ValueTo to = new ValueTo(operator: ValueToOperator.LESS_THAN, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.DATE)
                .setFieldTest(to)
                .build();

        assertion.assertTrue(LocalDate.of(2015, 01, 19));
        assertion.assertFalse(LocalDate.of(2015, 01, 20));
        assertion.assertFalse(LocalDate.of(2015, 01, 21));
        assertion.assertFalse(null);
    }

    @Test
    void fieldCompiler_lessThanEqualToDate_success() {

        ValueTo to = new ValueTo(operator: ValueToOperator.LESS_THAN_OR_EQUAL_TO, constant: "2015-01-20", absoluteUnit: ValueAbsoluteUnit.DATE);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.DATE)
                .setFieldTest(to)
                .build();

        assertion.assertTrue(LocalDate.of(2015, 01, 19));
        assertion.assertTrue(LocalDate.of(2015, 01, 20));
        assertion.assertFalse(LocalDate.of(2015, 01, 21));
        assertion.assertFalse(null);
    }

    @Test
    void fieldCompiler_RangeDate_success() {

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "2015-01-20");
        ValueTo to = new ValueTo(operator: ValueToOperator.LESS_THAN_OR_EQUAL_TO, constant: "2015-01-25");
        ValueRange range = new ValueRange(valueFrom: from, valueTo: to);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.DATE)
                .setFieldTest(range)
                .build();

        assertion.assertFalse(LocalDate.of(2015, 01, 19));
        assertion.assertTrue(LocalDate.of(2015, 01, 20));
        assertion.assertTrue(LocalDate.of(2015, 01, 22));
        assertion.assertTrue(LocalDate.of(2015, 01, 25));
        assertion.assertFalse(LocalDate.of(2015, 01, 28));
        assertion.assertFalse(null);
    }
//
//    @Ignore("Too hard to calculate")
//    @Test(expected = InvalidQueryDocumentException.class)
//    void fieldCompiler_rangeSameDate_exception() {
//
//        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "2015-01-20");
//        ValueTo to = new ValueTo(operator: ValueToOperator.LESS_THAN_OR_EQUAL_TO, constant: "2015-01-20");
//        ValueRange range = new ValueRange(valueFrom: from, valueTo: to);
//
//        new FieldTesterBuilder()
//                .setDataType(LogicalDataType.DATE)
//                .setFieldTest(range)
//                .build();
//    }
//
//    @Ignore("Too hard to calculate")
//    @Test(expected = InvalidQueryDocumentException.class)
//    void fieldCompiler_rangeBackwardDate_exception() {
//
//        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "2015-01-20");
//        ValueTo to = new ValueTo(operator: ValueToOperator.LESS_THAN_OR_EQUAL_TO, constant: "2015-01-19");
//        ValueRange range = new ValueRange(valueFrom: from, valueTo: to);
//
//        new FieldTesterBuilder()
//                .setDataType(LogicalDataType.DATE)
//                .setFieldTest(range)
//                .build();
//    }

    @Test
    void fieldCompiler_greaterThanDecimal_success() {

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN, constant: "50.5", absoluteUnit: ValueAbsoluteUnit.NUMERIC);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.FLOAT)
                .setFieldTest(from)
                .build();

        assertion.assertFalse(50.4);
        assertion.assertFalse(50.5);
        assertion.assertTrue(50.6);
        assertion.assertFalse(null);
    }

    @Test
    void fieldCompiler_greaterThanOrEqualToDecimal_success() {

        ValueFrom from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "50.5", absoluteUnit: ValueAbsoluteUnit.NUMERIC);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.FLOAT)
                .setFieldTest(from)
                .build();

        assertion.assertFalse(50.4);
        assertion.assertTrue(50.5);
        assertion.assertTrue(50.6);
        assertion.assertFalse(null);
    }

    @Test
    void fieldCompiler_lessThanDecimal_success() {

        def to = new ValueTo(operator: ValueToOperator.LESS_THAN, constant: "50.5", absoluteUnit: ValueAbsoluteUnit.NUMERIC);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.FLOAT)
                .setFieldTest(to)
                .build();

        assertion.assertTrue(50.4);
        assertion.assertFalse(50.5);
        assertion.assertFalse(50.6);
        assertion.assertFalse(null);
    }

    @Test
    void fieldCompiler_lessThanOrEqualToDecimal_success() {

        def to = new ValueTo(operator: ValueToOperator.LESS_THAN_OR_EQUAL_TO, constant: "50.5", absoluteUnit: ValueAbsoluteUnit.NUMERIC);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.FLOAT)
                .setFieldTest(to)
                .build();

        assertion.assertTrue(50.4);
        assertion.assertTrue(50.5);
        assertion.assertFalse(50.6);
        assertion.assertFalse(null);
    }

    @Test
    void fieldCompiler_rangeDecimal_success() {

        def from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "50.5", absoluteUnit: ValueAbsoluteUnit.NUMERIC);
        def to = new ValueTo(operator: ValueToOperator.LESS_THAN_OR_EQUAL_TO, constant: "50.7", absoluteUnit: ValueAbsoluteUnit.NUMERIC);
        ValueRange range = new ValueRange(valueFrom: from, valueTo: to);

        FieldTesterBuilder.FieldAssertion assertion = new FieldTesterBuilder()
                .setDataType(LogicalDataType.FLOAT)
                .setFieldTest(range)
                .build();

        assertion.assertFalse(50.4);
        assertion.assertTrue(50.5);
        assertion.assertTrue(50.6);
        assertion.assertTrue(50.7);
        assertion.assertFalse(50.8);
        assertion.assertFalse(null);
    }

    @Test(expected = InvalidQueryDocumentException.class)
    void fieldCompiler_rangeSameDecimal_exception() {

        def from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "50.5", absoluteUnit: ValueAbsoluteUnit.NUMERIC);
        def to = new ValueTo(operator: ValueToOperator.LESS_THAN_OR_EQUAL_TO, constant: "50.5", absoluteUnit: ValueAbsoluteUnit.NUMERIC);
        ValueRange range = new ValueRange(valueFrom: from, valueTo: to);

        new FieldTesterBuilder()
                .setDataType(LogicalDataType.FLOAT)
                .setFieldTest(range)
                .build();
    }

    @Test(expected = InvalidQueryDocumentException.class)
    void fieldCompiler_rangeBackwardsDecimal_exception() {

        def from = new ValueFrom(operator: ValueFromOperator.GREATER_THAN_OR_EQUAL_TO, constant: "50.8", absoluteUnit: ValueAbsoluteUnit.NUMERIC);
        def to = new ValueTo(operator: ValueToOperator.LESS_THAN_OR_EQUAL_TO, constant: "50.5", absoluteUnit: ValueAbsoluteUnit.NUMERIC);
        ValueRange range = new ValueRange(valueFrom: from, valueTo: to);

        new FieldTesterBuilder()
                .setDataType(LogicalDataType.FLOAT)
                .setFieldTest(range)
                .build();
    }
}
