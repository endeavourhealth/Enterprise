package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.*;
import org.endeavourhealth.enterprise.engine.compiled.listreports.CompiledListReport;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.util.List;

public class CompilerApi {
    private final ReportCompiler reportCompiler = new ReportCompiler();
    private final QueryCompiler queryCompiler = new QueryCompiler();
    private final DataSourceCompiler dataSourceCompiler = new DataSourceCompiler();
    private final TestCompiler testCompiler = new TestCompiler();
    private final ListReportCompiler listReportCompiler = new ListReportCompiler();

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
        compileListReports();
    }

    private void compileDataSources() throws Exception {
        for (LibraryItem libraryItem : requiredLibraryItems) {

            if (libraryItem.getDataSource() != null) {
                try {
                    ICompiledDataSource compiledDataSource = dataSourceCompiler.compile(libraryItem.getDataSource(), compilerContext);
                    compiledLibrary.add(libraryItem, compiledDataSource);
                } catch (Exception e) {
                    throw new UnableToCompileExpection("Could not compile DataSource: " + libraryItem.getUuid(), e);
                }
            }
        }
    }

    private void compileTests() throws Exception {
        for (LibraryItem libraryItem : requiredLibraryItems) {

            if (libraryItem.getTest() != null) {
                try {
                    ICompiledTest compiledTest = testCompiler.compile(libraryItem.getTest(), compilerContext);
                    compiledLibrary.add(libraryItem, compiledTest);
                } catch (Exception e) {
                    throw new UnableToCompileExpection("Could not compile Test: " + libraryItem.getUuid(), e);
                }
            }
        }
    }

    private void compileQueries() throws Exception {

        for (LibraryItem libraryItem : requiredLibraryItems) {

            if (libraryItem.getQuery() != null) {
                try {
                    CompiledQuery compiledQuery = queryCompiler.compile(compilerContext, libraryItem.getQuery());
                    compiledLibrary.add(libraryItem, compiledQuery);
                } catch (Exception e) {
                    throw new UnableToCompileExpection("Could not compile Query: " + libraryItem.getUuid(), e);
                }
            }
        }
    }

    private void compileListReports() throws UnableToCompileExpection {

        for (LibraryItem libraryItem : requiredLibraryItems) {

            if (libraryItem.getListReport() != null) {
                try {
                    CompiledListReport compiled = listReportCompiler.compile(compilerContext, libraryItem.getListReport());
                    compiledLibrary.add(libraryItem, compiled);
                } catch (Exception e) {
                    throw new UnableToCompileExpection("Could not compile ListReport: " + libraryItem.getUuid(), e);
                }
            }
        }
    }


    public CompiledReport compile(JobreportEntity jobReport, RequestParameters parameters) throws UnableToCompileExpection {
        return reportCompiler.compile(jobReport, parameters, compilerContext);
    }

    public CompiledLibrary getCompiledLibrary() {
        return compiledLibrary;
    }
}
