package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;

/**
 * Created by darren on 08/09/2017.
 */
@Entity
@Table(name = "region", schema = "data_sharing_manager", catalog = "")
public class RegionEntity {
    private String uuid;
    private String name;
    private String description;

    @Id
    @Column(name = "uuid", nullable = false, length = 36)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 10000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegionEntity that = (RegionEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public static List<Object[]> getRegions() throws Exception {
        String where = "select r.uuid, r.name " +
                "from RegionEntity r order by name";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmDataSharingManagerData();

        List<Object[]> ent = entityManager.createQuery(where)
                .getResultList();

        entityManager.close();

        return ent;

    }

    public static List<Object[]> getOrgsForRegion(String uuid) throws Exception {
        String where = "select o2.id,o1.name,o1.odsCode from MasterMappingEntity m " +
                "JOIN OrganisationEntity o1 on o1.uuid = m.childUuid " +
                "JOIN OrganizationViewEntity o2 on o2.odsCode = o1.odsCode " +
                "where m.childMapTypeId = 1 " +
                "and m.parentMapTypeId = 2 " +
                "and m.parentUuid = :uuid";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmDataSharingManagerData();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("uuid", uuid)
                .getResultList();

        entityManager.close();

        return ent;

    }

    public static List<Object[]> getOrgsForParentOdsCode(String odsCode) throws Exception {
        String where = "select ov.id,o1.name,o1.odsCode from OrganisationEntity o " +
                "JOIN MasterMappingEntity m on m.parentUuid = o.uuid " +
                "JOIN OrganisationEntity o1 on o1.uuid = m.childUuid " +
                "JOIN OrganizationViewEntity ov on ov.odsCode = o1.odsCode " +
                "where o.odsCode = :odsCode";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmDataSharingManagerData();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("odsCode", odsCode)
                .getResultList();

        entityManager.close();

        return ent;

    }


}
