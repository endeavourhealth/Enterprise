package org.endeavourhealth.enterprise.core.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;

public enum PersistenceManager {
    INSTANCE;

    private EntityManagerFactory emFactory;
    private EntityManagerFactory emFactory2;

    private PersistenceManager() {
        emFactory = Persistence.createEntityManagerFactory("NewPersistenceUnit", new HashMap());
        emFactory2 = Persistence.createEntityManagerFactory("NewPersistenceUnit2", new HashMap());
    }

    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public EntityManager getEntityManager2() {
        return emFactory2.createEntityManager();
    }

    public void close() {
        emFactory.close();
        emFactory2.close();

    }
}
