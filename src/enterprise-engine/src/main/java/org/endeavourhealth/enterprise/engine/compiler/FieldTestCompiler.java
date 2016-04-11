package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.core.entitymap.models.DataValueType;
import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.core.terminology.TerminologyService;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.*;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldTestCompiler {

    public FieldTestFromDataSource compile(ICompiledDataSource dataSource, FieldTest source) throws Exception {

        int fieldIndex = dataSource.getFieldIndex(source.getField());
        Field field = dataSource.getField(fieldIndex);

        ICompiledFieldTest compiledFieldTest = createCompiledFieldTest(source, field);
        return new FieldTestFromDataSource(compiledFieldTest, fieldIndex);
    }

    public ICompiledFieldTest createCompiledFieldTest(FieldTest valueFilter, Field field) throws UnableToCompileExpection, InvalidQueryDocumentException {

        if (field.getLogicalDataType() == LogicalDataType.DATE) {

            if (valueFilter.getValueFrom() != null) {

                return createGreaterThanDate(valueFilter.getValueFrom());

            } else if (valueFilter.getValueTo() != null) {

                return createLessThanDate(valueFilter.getValueTo());

            } else if (valueFilter.getValueRange() != null) {

                GreaterThanDate from = createGreaterThanDate(valueFilter.getValueRange().getValueFrom());
                LessThanDate to = createLessThanDate(valueFilter.getValueRange().getValueTo());

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
                    throw new InvalidQueryDocumentException("From value cannot be equal to To value");

                if (from.getComparisonValue().compareTo(to.getComparisonValue()) > 0)
                    throw new InvalidQueryDocumentException("From value cannot be greater than To value");

                return new RangeDecimal(from, to);
            }
        } else if (field.getLogicalDataType() == LogicalDataType.DATA_VALUES) {

            if (valueFilter.getValueSet() == null)
                throw new InvalidQueryDocumentException("ValueSet not found for field of type DataValues");

            return createValueSet(field.getDataValues(), valueFilter.getValueSet());

        } else if (field.getLogicalDataType() == LogicalDataType.CODE) {

            if (valueFilter.getCodeSet() == null)
                throw new InvalidQueryDocumentException("CodeSet not found for field of type CodeFieldTest");

            return createCodeSet(valueFilter.getCodeSet());
        }
//        } else if (field.getLogicalDataType() == LogicalDataType.) {
//
//            if (valueFilter.getCodeSets() != null) {
//
//                return new CodeFieldTest(valueFilter.getCodeSets().get(0).getCodeSetValues().get(0).getCode());
//            }
//        }

        throw new UnableToCompileExpection("Could not build field filter.  Logical type: " + field.getLogicalDataType());
    }

    private ICompiledFieldTest createCodeSet(CodeSet codeSet) throws UnableToCompileExpection {

        HashSet<String> conceptStrings = TerminologyService.enumerateConcepts(codeSet);

        Set<Long> concepts = new HashSet<>();

        for (String value: conceptStrings) {
            Long item;

            try {
                item = Long.parseLong(value);
            } catch (Exception e) {
                throw new UnableToCompileExpection("Could not convert code to Long: " + value);
            }

            concepts.add(item);
        }

        return new CodeFieldTest(concepts);
    }

    private ValueSetString createValueSet(List<DataValueType> dataValueList, ValueSet valueSet) throws InvalidQueryDocumentException {

        Set<String> values = new HashSet<>(valueSet.getValue().size());

        for (String logicalValue : valueSet.getValue()) {
            DataValueType matchingDataValue = findDataValueType(dataValueList, logicalValue);

            values.add(matchingDataValue.getPhysicalValue());
        }

        return new ValueSetString(values);
    }

    private DataValueType findDataValueType(List<DataValueType> dataValueList, String logicalValue) throws InvalidQueryDocumentException {
        for (DataValueType dataValueType: dataValueList) {
            if (logicalValue.equals(dataValueType.getLogicalValue()))
                return dataValueType;
        }

        throw new InvalidQueryDocumentException("Could not find logical value: " + logicalValue);
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
