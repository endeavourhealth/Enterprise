package org.endeavourhealth.enterprise.controller.jobinventory;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.database.models.*;

import java.util.*;

class JobContentRetriever {

    private final Map<UUID, UUID> libraryItemToAuditMap = new HashMap<>();
    private final Set<UUID> listReportItems = new HashSet<>();

    public JobContentRetriever(List<RequestEntity> requests) throws Exception {

        for (RequestEntity request: requests) {
            ActiveitemEntity activeItem = ActiveitemEntity.retrieveForItemUuid(request.getReportuuid());

            recursivelyAddItems(activeItem, request.getOrganisationuuid());
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

    private void recursivelyAddItems(ActiveitemEntity activeItem, UUID organisationUuid) throws Exception {

        addItem(activeItem, organisationUuid);

        List<ItemdependencyEntity> itemDependencies = ItemdependencyEntity.retrieveForActiveItem(activeItem);

        for (ItemdependencyEntity itemDependency: itemDependencies) {

            if (itemDependency.getDependencytypeid() == DependencyType.Uses.getValue() || itemDependency.getDependencytypeid() == DependencyType.IsChildOf.getValue()) {
                ActiveitemEntity childActiveItem = ActiveitemEntity.retrieveForItemUuid(itemDependency.getDependentitemuuid());
                recursivelyAddItems(childActiveItem, organisationUuid);
            }
        }
    }

    private void addItem(ActiveitemEntity activeItem, UUID requestOrganisationUuid) throws Exception {

        if (!requestOrganisationUuid.equals(activeItem.getOrganisationuuid()))
            throw new Exception("Execution request for item not owned by organisation.  Org: " + requestOrganisationUuid.toString() + " ItemUuid: " + activeItem.getOrganisationuuid().toString());

        if (libraryItemToAuditMap.containsKey(activeItem.getItemuuid()))
            return;

        if (activeItem.getItemtypeid() == DefinitionItemType.ListOutput.getValue())
            listReportItems.add(activeItem.getItemuuid());

        libraryItemToAuditMap.put(activeItem.getItemuuid(), activeItem.getAudituuid());
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
