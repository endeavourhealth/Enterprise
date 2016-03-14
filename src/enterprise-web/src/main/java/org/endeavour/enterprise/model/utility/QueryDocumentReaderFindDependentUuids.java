package org.endeavour.enterprise.model.utility;

import org.endeavourhealth.enterprise.core.querydocument.AbstractQueryDocumentReader;
import org.endeavourhealth.enterprise.core.querydocument.models.*;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 11/03/2016.
 */
public final class QueryDocumentReaderFindDependentUuids extends AbstractQueryDocumentReader {

    private HashSet<UUID> uuids = new HashSet<UUID>();

    public QueryDocumentReaderFindDependentUuids(QueryDocument doc) {
        super(doc);
    }

    public HashSet<UUID> findUuids()
    {
        super.process();

        return uuids;
    }

    private void addUuid(String uuid) {
        if (uuid != null && !uuid.isEmpty()) {
            uuids.add(UUID.fromString(uuid));
        }
    }

    @Override
    protected void processQuery(Query query) {
        addUuid(query.getParentQueryUuid());

        super.processQuery(query);
    }

    @Override
    protected void processReportQuery(Report.Query query) {
        addUuid(query.getUuid());
        addUuid(query.getParentUuid());

        super.processReportQuery(query);
    }

    @Override
    protected void processReportListOutput(Report.ListOutput listOutput) {
        addUuid(listOutput.getUuid());
        addUuid(listOutput.getParentUuid());

        super.processReportListOutput(listOutput);
    }

    @Override
    protected void processRule(Rule rule) {
        addUuid(rule.getQueryLibraryItemUUID());
        addUuid(rule.getTestLibraryItemUUID());

        super.processRule(rule);
    }

    @Override
    protected void processListGroupSummary(ListGroup.Summary listGroupSummary) {
        addUuid(listGroupSummary.getDataSourceUuid());

        super.processListGroupSummary(listGroupSummary);
    }

    @Override
    protected void processListGroupField(ListGroup.Field listGroupField) {
        addUuid(listGroupField.getDataSourceUuid());

        super.processListGroupField(listGroupField);
    }

    @Override
    protected void processDataSource(DataSource dataSource) {
        List<String> ids = dataSource.getDataSourceUuid();
        for (String id: ids) {
            addUuid(id);
        }

        super.processDataSource(dataSource);
    }

    @Override
    protected void processCalculationParameter(CalculationParameter calculationParameter) {
        addUuid(calculationParameter.getDataSourceUuid());

        super.processCalculationParameter(calculationParameter);
    }

    @Override
    protected void processTest(Test test) {
        addUuid(test.getDataSourceUuid());

        super.processTest(test);
    }

    @Override
    protected void processLinkedTestType(LinkedTestType linkedTestType) {
        addUuid(linkedTestType.getDataSourceUuid());

        super.processLinkedTestType(linkedTestType);
    }

    @Override
    protected void processFieldTest(FieldTest fieldTest) {
        List<String> ids = fieldTest.getCodeSetLibraryItemUuid();
        for (String id: ids) {
            addUuid(id);
        }

        super.processFieldTest(fieldTest);
    }
}
