package org.endeavourhealth.enterprise.engine.testhelpers;

import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiler.FieldTestCompiler;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.ICompiledFieldTest;

import java.time.LocalDate;

public class FieldTesterBuilder {
    private Field field;
    private FieldTest fieldTest = new FieldTest();

    public static class FieldAssertion {

        private final ICompiledFieldTest compiledFieldTest;

        public FieldAssertion(FieldTest fieldTest, Field field) throws Exception {
            FieldTestCompiler compiler = new FieldTestCompiler();
            compiledFieldTest = compiler.createCompiledFieldTest(fieldTest, field);
        }

        public void assertTrue(Object value) {
            assert compiledFieldTest.test(value);
        }

        public void assertFalse(Object value) {
            assert !compiledFieldTest.test(value);
        }
    }

    public FieldTesterBuilder setDataType(LogicalDataType logicalDataType) throws Exception {

        field = new EntityMapBuilder()
                .addEntity(logicalDataType)
                .buildField();

        return this;
    }

    public FieldTesterBuilder setFieldTest(ValueFrom value) {
        fieldTest.setValueFrom(value);
        return this;
    }

    public FieldTesterBuilder setFieldTest(ValueTo value) {
        fieldTest.setValueTo(value);
        return this;
    }

    public FieldTesterBuilder setFieldTest(ValueRange value) {
        fieldTest.setValueRange(value);
        return this;
    }

    public FieldTesterBuilder negate() {
        fieldTest.setNegate(true);
        return this;
    }

    public FieldAssertion build() throws Exception {
        return new FieldAssertion(fieldTest, field);
    }
}
