package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.enterprise.core.database.models.*;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by darren on 21/07/16.
 */
public class DataManager {

    public static void saveItems(AuditEntity audit, ItemEntity item, ActiveitemEntity activeItem,
                                 List<ItemdependencyEntity> itemdependencyEntities) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(audit);
        entityManager.merge(item);
        entityManager.merge(activeItem);
        for (ItemdependencyEntity dependentItem: itemdependencyEntities) {
            entityManager.merge(dependentItem);
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveDeletedItems(AuditEntity audit, List<ItemEntity> items, List<ActiveitemEntity> activeItems) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(audit);
        for (ItemEntity it: items) {
            entityManager.merge(it);
        }
        for (ActiveitemEntity ai: activeItems) {
            entityManager.merge(ai);
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveMovedItems(AuditEntity audit, List<ItemEntity> items, List<ActiveitemEntity> activeItems,
                                 List<ItemdependencyEntity> itemdependencyEntities) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(audit);
        for (ItemEntity it: items) {
            entityManager.merge(it);
        }
        for (ActiveitemEntity ai: activeItems) {
            entityManager.merge(ai);
        }
        for (ItemdependencyEntity id: itemdependencyEntities) {
            entityManager.merge(id);
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveOrganisation(OrganisationEntity org) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(org);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveUser(EnduserEntity user) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(user);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteUser(OrganisationenduserlinkEntity link) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(link);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveUserEntities(EnduserpwdEntity enduserpwdEntity, EnduseremailinviteEntity enduseremailinviteEntity,
                                        OrganisationenduserlinkEntity link) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(enduserpwdEntity);
        entityManager.merge(enduseremailinviteEntity);
        entityManager.merge(link);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveUserInvites(List<EnduseremailinviteEntity> invitesToSave, EnduseremailinviteEntity invite) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        for (EnduseremailinviteEntity inv: invitesToSave) {
            entityManager.merge(inv);
        }
        entityManager.merge(invite);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveUserInvite(EnduseremailinviteEntity invite) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(invite);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveUserPassword(EnduserpwdEntity p, EnduserpwdEntity op) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(p);
        entityManager.merge(op);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveFolders(AuditEntity audit, ItemEntity item, ActiveitemEntity activeItem) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(audit);
        entityManager.merge(item);
        entityManager.merge(activeItem);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveOrganisationSet(SourceorganisationsetEntity set) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(set);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void deleteOrganisationSet(SourceorganisationsetEntity set) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        SourceorganisationsetEntity entToDelete = entityManager.find(SourceorganisationsetEntity.class, set.getSourceorganisationsetuuid());

        entityManager.getTransaction().begin();

        entityManager.remove(entToDelete);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveSchedule(RequestEntity request) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(request);

        entityManager.getTransaction().commit();

        entityManager.close();
    }

}
