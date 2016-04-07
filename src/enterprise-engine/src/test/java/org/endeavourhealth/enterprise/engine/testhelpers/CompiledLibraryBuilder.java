package org.endeavourhealth.enterprise.engine.testhelpers;

import org.endeavourhealth.enterprise.core.querydocument.models.DataSource;
import org.endeavourhealth.enterprise.core.querydocument.models.LibraryItem;
import org.endeavourhealth.enterprise.core.querydocument.models.Test;
import org.endeavourhealth.enterprise.engine.compiled.CompiledLibrary;
import org.endeavourhealth.enterprise.engine.compiler.CompilerApi;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompiledLibraryBuilder {

    private final EntityMapWrapper.EntityMap entityMap;
    private List<LibraryItem> libraryItems = new ArrayList<>();

    public CompiledLibraryBuilder(EntityMapWrapper.EntityMap entityMap) {

        this.entityMap = entityMap;
    }

    public CompiledLibraryBuilder addTest(Test test, UUID testUuid) {

        LibraryItem libraryItem = new LibraryItem();
        libraryItem.setTest(test);
        libraryItem.setUuid(testUuid.toString());

        libraryItems.add(libraryItem);
        return this;
    }

    public CompiledLibraryBuilder addDataSource(DataSource dataSource, UUID dataSourceUuid) {

        LibraryItem libraryItem = new LibraryItem();
        libraryItem.setDataSource(dataSource);
        libraryItem.setUuid(dataSourceUuid.toString());

        libraryItems.add(libraryItem);
        return this;
    }

    public CompiledLibrary build() throws Exception {

        CompilerApi api = new CompilerApi(entityMap);
        api.compiledAllLibraryItems(libraryItems);

        return api.getCompiledLibrary();
    }
}
