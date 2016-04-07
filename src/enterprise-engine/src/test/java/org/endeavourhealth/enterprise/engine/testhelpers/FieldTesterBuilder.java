package org.endeavourhealth.enterprise.engine.testhelpers;

import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiler.FieldCompilerTest;
import org.endeavourhealth.enterprise.engine.compiler.FieldTestCompiler;
import org.endeavourhealth.enterprise.engine.compiled.fieldTests.ICompiledFieldTest;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.time.LocalDate;

public class FieldTesterBuilder {
    private Field field;
    private FieldTestBuilder fieldTestBuilder;

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

        EntityMapWrapper.EntityMap entityMap = new EntityMapBuilder()
                .addEntity(logicalDataType)
                .build();

        field = entityMap.getEntities().get(0).getField(0);

        fieldTestBuilder = new FieldTestBuilder(entityMap)
            .setField(0, 0);

        return this;
    }

    public FieldTesterBuilder setFieldTest(ValueFrom value) {
        fieldTestBuilder.setFieldTest(value);
        return this;
    }

    public FieldTesterBuilder setFieldTest(ValueTo value) {
        fieldTestBuilder.setFieldTest(value);
        return this;
    }

    public FieldTesterBuilder setFieldTest(ValueRange value) {
        fieldTestBuilder.setFieldTest(value);
        return this;
    }

    public FieldTesterBuilder negate() {
        fieldTestBuilder.negate();
        return this;
    }

    public FieldAssertion build() throws Exception {
        return new FieldAssertion(fieldTestBuilder.build(), field);
    }
}
