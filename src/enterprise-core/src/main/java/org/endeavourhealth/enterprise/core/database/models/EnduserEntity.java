package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "enduser", schema = "\"Administration\"", catalog = "Endeavour_Enterprise")
public class EnduserEntity {
    private UUID enduseruuid;
    private String title;
    private String forename;
    private String surname;
    private String email;
    private boolean issuperuser;

    @Id
    @Column(name = "enduseruuid")
    public UUID getEnduseruuid() {
        return enduseruuid;
    }

    public void setEnduseruuid(UUID enduseruuid) {
        this.enduseruuid = enduseruuid;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "forename")
    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    @Basic
    @Column(name = "surname")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIssuperuser() {
        return issuperuser;
    }

    public void setIssuperuser(boolean issuperuser) {
        this.issuperuser = issuperuser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnduserEntity that = (EnduserEntity) o;

        if (enduseruuid != null ? !enduseruuid.equals(that.enduseruuid) : that.enduseruuid != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (forename != null ? !forename.equals(that.forename) : that.forename != null) return false;
        if (surname != null ? !surname.equals(that.surname) : that.surname != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = enduseruuid != null ? enduseruuid.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (forename != null ? forename.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    public static final String SELECT_QUERY_EMAIL =
            "from EnduserEntity where email = :email";

    public static final String SELECT_QUERY_UUID =
            "from EnduserEntity where enduseruuid = :enduseruuid";

    public static final String SELECT_QUERY_SUPERS =
            "from EnduserEntity where issuperuser = 'true'";

    public static final String SELECT_QUERY_UUID_LIST =
            "from EnduserEntity where enduseruuid IN :enduseruuid";

    public static EnduserEntity retrieveForEmail(String email) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        EnduserEntity ent = entityManager.createQuery(SELECT_QUERY_EMAIL, EnduserEntity.class).setParameter("email", email).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static EnduserEntity retrieveForUuid(UUID enduseruuid) {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        EnduserEntity ent = entityManager.createQuery(SELECT_QUERY_UUID, EnduserEntity.class).setParameter("enduseruuid", enduseruuid).getSingleResult();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<EnduserEntity> retrieveSuperUsers() {
        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<EnduserEntity> ent = entityManager.createQuery(SELECT_QUERY_SUPERS, EnduserEntity.class).getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

    public static List<EnduserEntity> retrieveForRequests(List<RequestEntity> requests) throws Exception {
        List<UUID> uuids = new ArrayList<>();
        for (RequestEntity request: requests) {
            uuids.add(request.getEnduseruuid());
        }
        return retrieveEndUsersForUuids(uuids);
    }


    public static List<EnduserEntity> retrieveEndUsersForUuids(List<UUID> uuids) throws Exception {
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        List<EnduserEntity> ent = entityManager.createQuery(SELECT_QUERY_UUID_LIST, EnduserEntity.class).setParameter("enduseruuid", uuids).getResultList();

        entityManager.close();
        //PersistenceManager.INSTANCE.close();

        return ent;
    }

}
