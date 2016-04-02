package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentHelper;
import org.endeavourhealth.enterprise.core.querydocument.models.Query;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.CompiledQuery;
import org.endeavourhealth.enterprise.engine.compiled.CompiledReport;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;
import org.endeavourhealth.enterprise.enginecore.Library;
import org.endeavourhealth.enterprise.enginecore.LibraryItem;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompilerApi {
    private final ReportCompiler reportCompiler;
    private final QueryCompiler queryCompiler;
    private final CompilerContext compilerContext;

    public CompilerApi(
            EntityMapWrapper.EntityMap entityMapWrapper,
            Library requiredLibraryItems) {

        compilerContext = new CompilerContext(entityMapWrapper, requiredLibraryItems);
        this.reportCompiler = new ReportCompiler(compilerContext);
        this.queryCompiler = new QueryCompiler();
    }

    public Map<UUID, CompiledQuery> compileAllQueries(Library library) throws InvalidQueryDocumentException {

        Map<UUID, CompiledQuery> map = new HashMap<>();

        for (LibraryItem libraryItem: library.getAllLibraryItems()) {
            if (libraryItem.getItemType() == DefinitionItemType.Query) {

                Query query = null;

                CompiledQuery compiledQuery = queryCompiler.compile(compilerContext, query);
                map.put(libraryItem.getUuid(), compiledQuery);
            }
        }

        return map;
    }

    public CompiledReport compile(DbJobReport jobReport, RequestParameters parameters) throws UnableToCompileExpection {
        return reportCompiler.compile(jobReport, parameters);
    }
}
