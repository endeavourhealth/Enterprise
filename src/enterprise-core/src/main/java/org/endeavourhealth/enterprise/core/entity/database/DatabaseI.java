package org.endeavourhealth.enterprise.core.entity.database;

import org.endeavourhealth.enterprise.core.entity.DefinitionItemType;
import org.endeavourhealth.enterprise.core.entity.DependencyType;

import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 29/02/2016.
 */
public interface DatabaseI {
    //generic read/write functions
    public void writeEntity(DbAbstractTable entity) throws Exception;

    public void writeEntities(List<DbAbstractTable> entities) throws Exception;

    public DbAbstractTable retrieveForPrimaryKeys(TableAdapter a, Object... keys) throws Exception;

    //specific functions
    public DbEndUser retrieveEndUserForEmail(String email) throws Exception;

    public List<DbEndUser> retrieveSuperUsers() throws Exception;

    public DbEndUserPwd retrieveEndUserPwdForUserNotExpired(UUID endUserUuid) throws Exception;

    public List<DbOrganisation> retrieveAllOrganisations() throws Exception;

    public DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception;

    public List<DbEndUserEmailInvite> retrieveEndUserEmailInviteForUserNotCompleted(UUID userUuid) throws Exception;

    public DbEndUserEmailInvite retrieveEndUserEmailInviteForToken(String token) throws Exception;

    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForOrganisationNotExpired(UUID organisationUuid) throws Exception;

    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForUserNotExpired(UUID endUserUuid) throws Exception;

    public DbOrganisationEndUserLink retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception;

    public List<DbItem> retrieveDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception;

    public List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception;

    public DbItem retrieveForUuidLatestVersion(UUID organisationUuid, UUID itemUuid) throws Exception;

    public DbActiveItem retrieveActiveItemForItemUuid(UUID itemUuid) throws Exception;

    public List<DbActiveItem> retrieveActiveItemDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception;

    public List<DbActiveItem> retrieveActiveItemRecentItems(UUID userUuid, int count) throws Exception;

    public int retrieveCountDependencies(UUID itemUuid, DependencyType dependencyType) throws Exception;

    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForItem(UUID itemUuid) throws Exception;

    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForItemType(UUID itemUuid, DependencyType dependencyType) throws Exception;

    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForDependentItem(UUID dependentItemUuid) throws Exception;

    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForDependentItemType(UUID dependentItemUuid, DependencyType dependencyType) throws Exception;

    public List<DbRequest> retrievePendingRequestsForItems(UUID organisationUuid, List<UUID> itemUuids) throws Exception;

    public List<DbRequest> retrievePendingRequests() throws Exception;

    public List<DbJob> retrieveRecentJobs(int count) throws Exception;

    public List<DbJobReport> retrieveJobReports(UUID organisationUuid, int count) throws Exception;
}
