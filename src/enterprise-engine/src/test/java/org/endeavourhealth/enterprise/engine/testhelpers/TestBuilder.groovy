package org.endeavourhealth.enterprise.engine.testhelpers

import org.endeavourhealth.enterprise.core.querydocument.models.DataSource
import org.endeavourhealth.enterprise.core.querydocument.models.FieldTest
import org.endeavourhealth.enterprise.core.querydocument.models.Test

class TestBuilder {
    private Test test = new Test();

    public TestBuilder addDataSource(DataSource dataSource) {
        test.dataSource = dataSource;
        return this;
    }

    public TestBuilder addDataSourceUuid(UUID dataSourceUuid) {
        test.dataSourceUuid = dataSourceUuid;
        return this;
    }

    public TestBuilder setIsAny() {
        test.setIsAny(new Test.IsAny());
        return this;
    }

    public Test build() {
        return test;
    }

    public TestBuilder addFieldTest(FieldTest fieldTest) {
        test.fieldTest.add(fieldTest);
        return this;
    }
}
