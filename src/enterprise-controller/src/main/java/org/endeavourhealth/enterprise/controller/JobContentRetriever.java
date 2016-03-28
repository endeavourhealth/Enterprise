package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbItemDependency;
import org.endeavourhealth.enterprise.core.database.execution.DbRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class JobContentRetriever {

    private final Map<UUID, UUID> libraryItemToAuditMap = new HashMap<>();

    public JobContentRetriever(List<DbRequest> requests) throws Exception {

        for (DbRequest request: requests) {
            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(request.getReportUuid());

            recursivelyAddItems(activeItem, request.getOrganisationUuid());
        }
    }

    public Map<UUID, UUID> getLibraryItemToAuditMap() {
        return libraryItemToAuditMap;
    }

    public UUID getAuditUuid(UUID libraryItemUuid) {
        return libraryItemToAuditMap.get(libraryItemUuid);
    }

    private void recursivelyAddItems(DbActiveItem activeItem, UUID organisationUuid) throws Exception {

        addItem(activeItem, organisationUuid);

        List<DbItemDependency> itemDependencies = DbItemDependency.retrieveForActiveItem(activeItem);

        for (DbItemDependency itemDependency: itemDependencies) {

            if (itemDependency.getDependencyTypeId() == DependencyType.Uses || itemDependency.getDependencyTypeId() == DependencyType.IsChildOf) {
                DbActiveItem childActiveItem = DbActiveItem.retrieveForItemUuid(itemDependency.getDependentItemUuid());
                recursivelyAddItems(childActiveItem, organisationUuid);
            }
        }
    }

    private void addItem(DbActiveItem activeItem, UUID requestOrganisationUuid) throws Exception {

        if (!requestOrganisationUuid.equals(activeItem.getOrganisationUuid()))
            throw new Exception("Execution request for item not owned by organisation.  Org: " + requestOrganisationUuid.toString() + " ItemUuid: " + activeItem.getOrganisationUuid().toString());

        if (libraryItemToAuditMap.containsKey(activeItem.getItemUuid()))
            return;

        if (invalidItemType(activeItem.getItemTypeId()))
            throw new Exception("Item type not supported.  Item UUID: " + activeItem.getItemUuid() + " Type: " + activeItem.getItemTypeId().name());

        libraryItemToAuditMap.put(activeItem.getItemUuid(), activeItem.getAuditUuid());
    }

    private boolean invalidItemType(DefinitionItemType itemType) {
        switch (itemType) {
            case CodeSet:
            case DataSource:
            case ListOutput:
            case Query:
            case Report:
                return false;
            default:
                return true;
        }
    }
}
