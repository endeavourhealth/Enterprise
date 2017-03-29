package org.endeavour.enterprise.utility;

import org.endeavourhealth.enterprise.core.querydocument.AbstractQueryDocumentReader;
import org.endeavourhealth.enterprise.core.querydocument.models.*;

import java.util.HashSet;
import java.util.List;

public final class QueryDocumentReaderFindDependentUuids extends AbstractQueryDocumentReader {

    private HashSet<String> uuids = new HashSet<String>();

    public QueryDocumentReaderFindDependentUuids(QueryDocument doc) {
        super(doc);
    }

    public HashSet<String> findUuids()
    {
        super.processQueryDocument();

        return uuids;
    }

    private void addUuid(String uuid) {
        if (uuid != null && !uuid.isEmpty()) {
            uuids.add(uuid);
        }
    }

    @Override
    protected void processQuery(Query query) {
        addUuid(query.getParentQueryUuid());

        super.processQuery(query);
    }


    @Override
    protected void processRule(Rule rule) {
        addUuid(rule.getQueryLibraryItemUUID());
        addUuid(rule.getTestLibraryItemUUID());

        super.processRule(rule);
    }

    @Override
    protected void processFilter(Filter filter) {
        List<String> ids = filter.getCodeSetLibraryItemUuid();
        for (String id: ids) {
            addUuid(id);
        }

        super.processFilter(filter);
    }
}
