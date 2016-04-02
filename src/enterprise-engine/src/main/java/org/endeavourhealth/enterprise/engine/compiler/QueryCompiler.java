package org.endeavourhealth.enterprise.engine.compiler;

import com.sun.istack.internal.Nullable;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentHelper;
import org.endeavourhealth.enterprise.core.querydocument.models.Query;
import org.endeavourhealth.enterprise.engine.compiled.CompiledQuery;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;

import java.util.UUID;

public class QueryCompiler {
    private NodeCompiler nodeCompiler = new NodeCompiler();

    public CompiledQuery compile(CompilerContext context, Query query) throws InvalidQueryDocumentException {

        nodeCompiler.compile(query);

        UUID parentQueryUuid = QueryDocumentHelper.parseOptionalUuid(query.getParentQueryUuid());

        CompiledQuery compiledQuery = new CompiledQuery(
                nodeCompiler.getNodeTraversal(),
                null,
                parentQueryUuid);

        return compiledQuery;

    }
}
