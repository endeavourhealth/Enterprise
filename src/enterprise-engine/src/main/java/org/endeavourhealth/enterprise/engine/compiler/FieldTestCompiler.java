package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FieldTestCompiler {

    public FieldTestFromDataSource compile(ICompiledDataSource dataSource, FieldTest source) throws Exception {

        int fieldIndex = dataSource.getFieldIndex(source.getField());
        Field field = dataSource.getField(fieldIndex);

        ICompiledFieldTest compiledFieldTest = createCompiledFieldTest(source, field);
        return new FieldTestFromDataSource(compiledFieldTest, fieldIndex);
    }

    public ICompiledFieldTest createCompiledFieldTest(FieldTest valueFilter, Field field) throws UnableToCompileExpection {

        if (field.getLogicalDataType() == LogicalDataType.DATE) {

            if (valueFilter.getValueFrom() != null) {

                return createGreaterThanDate(valueFilter.getValueFrom());

            } else if (valueFilter.getValueTo() != null) {

                return createLessThanDate(valueFilter.getValueTo());

            } else if (valueFilter.getValueRange() != null) {

                GreaterThanDate from = createGreaterThanDate(valueFilter.getValueRange().getValueFrom());
                LessThanDate to = createLessThanDate(valueFilter.getValueRange().getValueTo());

                if (from.getComparisonValue().isEqual(to.getComparisonValue()))
                    throw new UnableToCompileExpection("From value cannot be equal to To value");

                if (from.getComparisonValue().isAfter(to.getComparisonValue()))
                    throw new UnableToCompileExpection("From value cannot be greater than To value");

                return new RangeDate(from, to);
            }

        } else if (field.getLogicalDataType() == LogicalDataType.FLOAT) {

            if (valueFilter.getValueTo() != null) {
                return createLessThanDecimal(valueFilter.getValueTo());

            } else  if (valueFilter.getValueFrom() != null) {
                return createMoreThanDecimal(valueFilter.getValueFrom());

            } else if (valueFilter.getValueRange() != null) {
                GreaterThanDecimal from = createMoreThanDecimal(valueFilter.getValueRange().getValueFrom());
                LessThanDecimal to = createLessThanDecimal(valueFilter.getValueRange().getValueTo());

                if (from.getComparisonValue().equals(to.getComparisonValue()))
                    throw new UnableToCompileExpection("From value cannot be equal to To value");

                if (from.getComparisonValue().equals(to.getComparisonValue()))
                    throw new UnableToCompileExpection("From value cannot be greater than To value");

                return new RangeDecimal(from, to);
            }
        }
//        } else if (field.getLogicalDataType() == LogicalDataType.) {
//
//            if (valueFilter.getCodeSets() != null) {
//
//                return new Code(valueFilter.getCodeSets().get(0).getCodeSetValues().get(0).getCode());
//            }
//        }

        throw new UnableToCompileExpection("Could not build field filter");
    }

    private LessThanDecimal createLessThanDecimal(ValueTo value) {
        BigDecimal decimal = new BigDecimal(value.getConstant());

        if (value.getOperator() == ValueToOperator.LESS_THAN)
            return new LessThanDecimal(decimal, false);
        else
            return new LessThanDecimal(decimal, true);
    }

    private GreaterThanDecimal createMoreThanDecimal(ValueFrom value) {
        BigDecimal decimal = new BigDecimal(value.getConstant());

        if (value.getOperator() == ValueFromOperator.GREATER_THAN)
            return new GreaterThanDecimal(decimal, false);
        else
            return new GreaterThanDecimal(decimal, true);
    }


    private LessThanDate createLessThanDate(ValueTo to) {
        LocalDate date = parseDate(to.getConstant());

        if (to.getOperator() == ValueToOperator.LESS_THAN_OR_EQUAL_TO)
            date = date.plusDays(1);

        return new LessThanDate(date);
    }

    private GreaterThanDate createGreaterThanDate(ValueFrom from) {
        LocalDate date = parseDate(from.getConstant());

        if (from.getOperator() == ValueFromOperator.GREATER_THAN_OR_EQUAL_TO)
            date = date.minusDays(1);

        return new GreaterThanDate(date);
    }

    private LocalDate parseDate(String input) {
        return LocalDate.parse(input);
    }
}
