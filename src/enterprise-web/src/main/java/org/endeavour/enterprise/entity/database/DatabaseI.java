package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavour.enterprise.model.DependencyType;

import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 29/02/2016.
 */
public interface DatabaseI
{
    //generic read/write functions
    public void writeDpdate(DbAbstractTable entity) throws Exception;
    public void writeInsert(DbAbstractTable entity) throws Exception;
    public void writeDelete(DbAbstractTable entity) throws Exception;
    public DbAbstractTable retrieveForPrimaryKeys(TableAdapter a, Object... keys) throws Exception;

    //specific functions
    public DbEndUser retrieveEndUserForEmail(String email) throws Exception;
    public List<DbEndUser> retrieveSuperUsers() throws Exception;
    public List<DbOrganisation> retrieveAllOrganisations() throws Exception;
    public List<DbEndUserEmailInvite> retrieveEndUserEmailInviteForUserNotCompleted(UUID userUuid) throws Exception;
    public DbEndUserEmailInvite retrieveEndUserEmailInviteForToken(String token) throws Exception;
    public DbActiveItem retrieveActiveItemForItemUuid(UUID itemUuid) throws Exception;
    public DbEndUserPwd retrieveEndUserPwdForUserNotExpired(UUID endUserUuid) throws Exception;
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForOrganisationNotExpired(UUID organisationUuid) throws Exception;
    public List<DbOrganisationEndUserLink> retrieveOrganisationEndUserLinksForUserNotExpired(UUID endUserUuid) throws Exception;
    public DbOrganisationEndUserLink retrieveOrganisationEndUserLinksForOrganisationEndUserNotExpired(UUID organisationUuid, UUID endUserUuid) throws Exception;
    public DbOrganisation retrieveOrganisationForNameNationalId(String name, String nationalId) throws Exception;

    public List<DbItem> retrieveDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception;
    public List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception;
    public int retrieveCountDependencies(UUID itemUuid, DependencyType dependencyType) throws Exception;
    public DbItem retrieveForUuidLatestVersion(UUID organisationUuid, UUID itemUuid) throws Exception;

    public List<DbActiveItem> retrieveActiveItemDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception;

    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForItem(UUID itemUuid) throws Exception;
    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForItemType(UUID itemUuid, DependencyType dependencyType) throws Exception;
    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForDependentItem(UUID dependentItemUuid) throws Exception;
    public List<DbActiveItemDependency> retrieveActiveItemDependenciesForDependentItemType(UUID dependentItemUuid, DependencyType dependencyType) throws Exception;


}
