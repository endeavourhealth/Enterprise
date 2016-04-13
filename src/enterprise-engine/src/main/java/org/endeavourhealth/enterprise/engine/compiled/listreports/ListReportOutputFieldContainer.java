package org.endeavourhealth.enterprise.engine.compiled.listreports;

import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;

public class ListReportOutputFieldContainer {
    private final IListReportOutputField listReportOutputField;
    private final int sourceFieldIndex;

    public ListReportOutputFieldContainer(IListReportOutputField listReportOutputField, int sourceFieldIndex) {
        this.listReportOutputField = listReportOutputField;
        this.sourceFieldIndex = sourceFieldIndex;
    }

    public String getResult(ICompiledDataSource dataSource, int rowIndex) {
        return listReportOutputField.getResult(dataSource.getValue(rowIndex, sourceFieldIndex));
    }
}
