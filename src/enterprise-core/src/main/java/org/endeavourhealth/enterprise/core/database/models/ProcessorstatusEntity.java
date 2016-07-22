package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "processorstatus", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
public class ProcessorstatusEntity {
    private short stateid;

    @Id
    @Column(name = "stateid")
    public short getStateid() {
        return stateid;
    }

    public void setStateid(short stateid) {
        this.stateid = stateid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessorstatusEntity that = (ProcessorstatusEntity) o;

        if (stateid != that.stateid) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) stateid;
        return result;
    }

    public static ProcessorstatusEntity retrieveCurrentStatus() throws Exception {
        String where = "FROM ProcessorstatusEntity";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ProcessorstatusEntity ret = entityManager.createQuery(where, ProcessorstatusEntity.class)
                .getSingleResult();

        entityManager.close();

        return ret;
    }

    public static void deleteCurrentProcessorStatus() throws Exception {
        String where = "DELETE FROM ProcessorstatusEntity";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        entityManager.createQuery(where)
                .executeUpdate();

        entityManager.close();
    }

    public static void setCurrentStatus(Short state) throws Exception {
        deleteCurrentProcessorStatus();

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        ProcessorstatusEntity ps = new ProcessorstatusEntity();
        ps.setStateid(state);

        entityManager.getTransaction().begin();
        entityManager.persist(ps);
        entityManager.getTransaction().commit();

    }
}
