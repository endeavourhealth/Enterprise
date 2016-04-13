package org.endeavourhealth.enterprise.engine.compiler;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.engine.compiled.listreports.*;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListReportCompiler {
    private ListReportFieldCompiler fieldCompiler = new ListReportFieldCompiler();

    public CompiledListReport compile(CompilerContext compilerContext, ListReport listReport) throws Exception {

        if (CollectionUtils.isEmpty(listReport.getGroup()))
            throw new InvalidQueryDocumentException("Empty ListReport groups");

        List<ICompiledListReportGroup> compiledGroups = new ArrayList<>();

        for (int i = 0; i < listReport.getGroup().size(); i++) {
            ICompiledListReportGroup compiledGroup = compileGroup(compilerContext, i, listReport.getGroup().get(i));
            compiledGroups.add(compiledGroup);
        }

        return new CompiledListReport(compiledGroups);
    }

    private ICompiledListReportGroup compileGroup(CompilerContext compilerContext, int groupId, ListReportGroup group) throws Exception {
        if (group.getFieldBased() == null)
            throw new UnableToCompileExpection("Only field based groups are supported");

        ListReportFieldBasedType sourceGroup = group.getFieldBased();

        return compileFieldBaseGroup(compilerContext, groupId, sourceGroup);
    }

    private FieldBasedGroup compileFieldBaseGroup(
            CompilerContext compilerContext,
            int groupId,
            ListReportFieldBasedType sourceFieldGroup) throws Exception {

        if (CollectionUtils.isEmpty(sourceFieldGroup.getFieldOutput()))
            throw new InvalidQueryDocumentException("No fields in field group");

        UUID dataSourceUuid = getDataSourceUuid(compilerContext, sourceFieldGroup);
        ICompiledDataSource dataSource = compilerContext.getCompiledLibrary().getCompiledDataSource(dataSourceUuid);

        List<ListReportOutputFieldContainer> fieldContainers = new ArrayList<>();

        for (FieldOutput fieldOutput: sourceFieldGroup.getFieldOutput()) {
            ListReportOutputFieldContainer container = compileField(fieldOutput, dataSource);
            fieldContainers.add(container);
        }

        return new FieldBasedGroup(dataSourceUuid, groupId, fieldContainers);
    }

    private ListReportOutputFieldContainer compileField(
            FieldOutput source,
            ICompiledDataSource dataSource) throws EntityMapException {

        int fieldIndex = dataSource.getFieldIndex(source.getField());
        Field field = dataSource.getField(fieldIndex);

        IListReportOutputField compiledField = fieldCompiler.compileField(source, field);
        return new ListReportOutputFieldContainer(compiledField, fieldIndex);
    }

    private UUID getDataSourceUuid(CompilerContext context, ListReportFieldBasedType source) throws Exception {
        UUID dataSourceUuid = UUID.randomUUID();

        DataSourceCompiler dataSourceCompiler = new DataSourceCompiler();
        ICompiledDataSource dataSource = dataSourceCompiler.compile(source.getDataSource(), context);
        context.getCompiledLibrary().addInlineDataSource(dataSourceUuid, dataSource);

        return dataSourceUuid;
    }
}
