package org.endeavourhealth.enterprise.engine.compiled.listreports;

import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.engine.execution.ExecutionContext;
import org.endeavourhealth.enterprise.engine.execution.listreports.FileContentBuilder;

import java.util.List;
import java.util.UUID;

public class FieldBasedGroup implements ICompiledListReportGroup {

    private final UUID dataSourceUuid;
    private final int groupId;
    private final List<ListReportOutputFieldContainer> fields;

    public FieldBasedGroup(UUID dataSourceUuid, int groupId, List<ListReportOutputFieldContainer> fields) {
        this.dataSourceUuid = dataSourceUuid;
        this.groupId = groupId;
        this.fields = fields;
    }

    @Override
    public void execute(ExecutionContext context, UUID jobReportItemUuid) throws Exception {

        ICompiledDataSource dataSourceResult = context.getDataSourceResult(dataSourceUuid);
        FileContentBuilder fileContentBuilder = context.getFileContentBuilder(jobReportItemUuid, groupId);

        if (!dataSourceResult.anyResults())
            return;

        for (int rowIndex: dataSourceResult.getRowIds()) {
            fileContentBuilder.newRow();
            processRow(fileContentBuilder, dataSourceResult, rowIndex);
        }
    }

    private void processRow(FileContentBuilder fileContentBuilder, ICompiledDataSource dataSourceResult, int rowIndex) {

        for (ListReportOutputFieldContainer field: fields) {
            fileContentBuilder.addField(field.getResult(dataSourceResult, rowIndex));
        }
    }
}
