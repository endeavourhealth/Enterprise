package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darren on 16/07/16.
 */
@Entity
@Table(name = "sourceorganisation", schema = "\"Lookups\"", catalog = "Endeavour_Enterprise")
public class SourceorganisationEntity {
    private String odscode;
    private String name;
    private Boolean isreferencedbydata;

    @Id
    @Column(name = "odscode")
    public String getOdscode() {
        return odscode;
    }

    public void setOdscode(String odscode) {
        this.odscode = odscode;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "isreferencedbydata")
    public Boolean getIsreferencedbydata() {
        return isreferencedbydata;
    }

    public void setIsreferencedbydata(Boolean isreferencedbydata) {
        this.isreferencedbydata = isreferencedbydata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceorganisationEntity that = (SourceorganisationEntity) o;

        if (odscode != null ? !odscode.equals(that.odscode) : that.odscode != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (isreferencedbydata != null ? !isreferencedbydata.equals(that.isreferencedbydata) : that.isreferencedbydata != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = odscode != null ? odscode.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isreferencedbydata != null ? isreferencedbydata.hashCode() : 0);
        return result;
    }

    public static List<SourceorganisationEntity> retrieveAll(boolean includeUnreferencedOnes) throws Exception {
        String where = "FROM SourceorganisationEntity WHERE 1=1";
        if (!includeUnreferencedOnes) {
            where += " AND isreferencedbydata = 'true'";
        }

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<SourceorganisationEntity> ret = entityManager.createQuery(where, SourceorganisationEntity.class)
                .getResultList();

        entityManager.close();

        return ret;
    }

    public static List<SourceorganisationEntity> retrieveForSearch(String searchTerm) throws Exception {
        String where = "FROM SourceorganisationEntity WHERE (name LIKE :searchTerm1 OR odscode LIKE :searchTerm2)"
                + " AND isreferencedbydata = 1";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<SourceorganisationEntity> ret = entityManager.createQuery(where, SourceorganisationEntity.class)
                .setParameter("searchTerm1", searchTerm)
                .setParameter("searchTerm2", searchTerm)
                .getResultList();

        entityManager.close();

        return ret;

    }

    public static List<SourceorganisationEntity> retrieveForOdsCodes(List<String> odscodes) throws Exception {
        if (odscodes.isEmpty()) {
            return new ArrayList<>();
        }
        String where = "FROM SourceorganisationEntity WHERE odscode IN :odscodes";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<SourceorganisationEntity> ret = entityManager.createQuery(where, SourceorganisationEntity.class)
                .setParameter("odscodes", odscodes)
                .getResultList();

        entityManager.close();

        return ret;

    }

}
