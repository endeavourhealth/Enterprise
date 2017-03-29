package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.enterprise.core.database.models.*;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by darren on 21/07/16.
 */
public class DataManager {

    public static void saveItems(AuditEntity audit, ItemEntity item, ActiveItemEntity activeItem,
                                 List<ItemDependencyEntity> itemdependencyEntities) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(audit);
        entityManager.merge(item);
        entityManager.merge(activeItem);
        for (ItemDependencyEntity dependentItem: itemdependencyEntities) {
            entityManager.merge(dependentItem);
        }

        entityManager.getTransaction().commit();

        entityManager.close();


    }

    public static void saveDeletedItems(AuditEntity audit, List<ItemEntity> items, List<ActiveItemEntity> activeItems) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(audit);
        for (ItemEntity it: items) {
            entityManager.merge(it);
        }
        for (ActiveItemEntity ai: activeItems) {
            entityManager.merge(ai);
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveMovedItems(AuditEntity audit, List<ItemEntity> items, List<ActiveItemEntity> activeItems,
                                 List<ItemDependencyEntity> itemdependencyEntities) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(audit);
        for (ItemEntity it: items) {
            entityManager.merge(it);
        }
        for (ActiveItemEntity ai: activeItems) {
            entityManager.merge(ai);
        }
        for (ItemDependencyEntity id: itemdependencyEntities) {
            entityManager.merge(id);
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public static void saveFolders(AuditEntity audit, ItemEntity item, ActiveItemEntity activeItem) throws Exception {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.getTransaction().begin();

        entityManager.merge(audit);
        entityManager.merge(item);
        entityManager.merge(activeItem);

        entityManager.getTransaction().commit();

        entityManager.close();
    }


}
