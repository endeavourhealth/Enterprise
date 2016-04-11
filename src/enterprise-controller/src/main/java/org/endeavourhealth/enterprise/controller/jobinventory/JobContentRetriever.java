package org.endeavourhealth.enterprise.controller.jobinventory;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbItemDependency;
import org.endeavourhealth.enterprise.core.database.execution.DbRequest;

import java.util.*;

class JobContentRetriever {

    private final Map<UUID, UUID> libraryItemToAuditMap = new HashMap<>();
    private final Set<UUID> listReportItems = new HashSet<>();

    public JobContentRetriever(List<DbRequest> requests) throws Exception {

        for (DbRequest request: requests) {
            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(request.getReportUuid());

            recursivelyAddItems(activeItem, request.getOrganisationUuid());
        }
    }

    public Set<UUID> getAllItemsUsed() {
        return libraryItemToAuditMap.keySet();
    }

    public boolean isListReport(UUID libraryItemUuid) {
        return listReportItems.contains(libraryItemUuid);
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

        if (activeItem.getItemTypeId() == DefinitionItemType.ListOutput)
            listReportItems.add(activeItem.getItemUuid());

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
