package org.endeavourhealth.enterprise.engine;

import org.endeavourhealth.enterprise.core.database.execution.DbJobReport;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EngineApi {

    private final EntityMapWrapper.EntityMap entityMap;
    private final HashMap<UUID, LibraryItem> requiredLibraryItems;
    private final List<DbJobReport> jobReports;

    public EngineApi(
            EntityMapWrapper.EntityMap entityMap,
            HashMap<UUID, LibraryItem> requiredLibraryItems,
            List<DbJobReport> jobReports) {

        this.entityMap = entityMap;
        this.requiredLibraryItems = requiredLibraryItems;
        this.jobReports = jobReports;
    }

    public Processor createProcessor() {
        return new Processor();
    }
}
