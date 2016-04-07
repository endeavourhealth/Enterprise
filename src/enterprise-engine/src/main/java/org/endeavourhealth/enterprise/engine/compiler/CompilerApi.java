package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.*;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.util.List;

public class CompilerApi {
    private final ReportCompiler reportCompiler = new ReportCompiler();
    private final QueryCompiler queryCompiler = new QueryCompiler();
    private final DataSourceCompiler dataSourceCompiler = new DataSourceCompiler();
    private final TestCompiler testCompiler = new TestCompiler();

    private final CompilerContext compilerContext;
    private final CompiledLibrary compiledLibrary = new CompiledLibrary();
    private List<LibraryItem> requiredLibraryItems;

    public CompilerApi(
            EntityMapWrapper.EntityMap entityMapWrapper) {

        compilerContext = new CompilerContext(entityMapWrapper, compiledLibrary);
    }

    public void compiledAllLibraryItems(List<LibraryItem> requiredLibraryItems) throws Exception {
        this.requiredLibraryItems = requiredLibraryItems;

        //compileCodesets();
        compileDataSources();
        compileTests();
        compileQueries();
        //compileListReports();
    }

    private void compileDataSources() throws Exception {
        for (LibraryItem libraryItem : requiredLibraryItems) {

            if (libraryItem.getDataSource() != null) {
                ICompiledDataSource compiledDataSource = dataSourceCompiler.compile(libraryItem.getDataSource(), compilerContext);
                compiledLibrary.add(libraryItem, compiledDataSource);
            }
        }
    }

    private void compileTests() throws Exception {
        for (LibraryItem libraryItem : requiredLibraryItems) {

            if (libraryItem.getTest() != null) {
                ICompiledTest compiledTest = testCompiler.compile(libraryItem.getTest(), compilerContext);
                compiledLibrary.add(libraryItem, compiledTest);
            }
        }
    }

    private void compileQueries() throws InvalidQueryDocumentException {

        for (LibraryItem libraryItem : requiredLibraryItems) {

            if (libraryItem.getQuery() != null) {
                CompiledQuery compiledQuery = queryCompiler.compile(compilerContext, libraryItem.getQuery());
                compiledLibrary.add(libraryItem, compiledQuery);
            }
        }
    }

    public CompiledReport compile(DbJobReport jobReport, RequestParameters parameters) throws UnableToCompileExpection {
        return reportCompiler.compile(jobReport, parameters, compilerContext);
    }

    public CompiledLibrary getCompiledLibrary() {
        return compiledLibrary;
    }
}
