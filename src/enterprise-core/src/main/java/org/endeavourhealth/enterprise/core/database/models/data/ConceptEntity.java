package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darren on 18/03/17.
 */
@Entity
@Table(name = "Concept", schema = "enterprise_data_pseudonymised", catalog = "")
public class ConceptEntity {
    private int id;
    private String conceptId;
    private String definition;
    private String parentTypeConceptId;
    private String baseTypeConceptId;
    private byte status;
    private byte dataTypeId;
    private byte conceptTypeId;
    private byte present;

    @Id
    @Column(name = "Id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ConceptId", nullable = false, length = 45)
    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    @Basic
    @Column(name = "Definition", nullable = false, length = 500)
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Basic
    @Column(name = "ParentTypeConceptId", nullable = false, length = 45)
    public String getParentTypeConceptId() {
        return parentTypeConceptId;
    }

    public void setParentTypeConceptId(String parentTypeConceptId) {
        this.parentTypeConceptId = parentTypeConceptId;
    }

    @Basic
    @Column(name = "BaseTypeConceptId", nullable = false, length = 45)
    public String getBaseTypeConceptId() {
        return baseTypeConceptId;
    }

    public void setBaseTypeConceptId(String baseTypeConceptId) {
        this.baseTypeConceptId = baseTypeConceptId;
    }

    @Basic
    @Column(name = "Status", nullable = false)
    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Basic
    @Column(name = "DataTypeId", nullable = false)
    public byte getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(byte dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    @Basic
    @Column(name = "ConceptTypeId", nullable = false)
    public byte getConceptTypeId() {
        return conceptTypeId;
    }

    public void setConceptTypeId(byte conceptTypeId) {
        this.conceptTypeId = conceptTypeId;
    }

    @Basic
    @Column(name = "Present", nullable = false)
    public byte getPresent() {
        return present;
    }

    public void setPresent(byte present) {
        this.present = present;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConceptEntity that = (ConceptEntity) o;

        if (id != that.id) return false;
        if (status != that.status) return false;
        if (dataTypeId != that.dataTypeId) return false;
        if (conceptTypeId != that.conceptTypeId) return false;
        if (present != that.present) return false;
        if (conceptId != null ? !conceptId.equals(that.conceptId) : that.conceptId != null) return false;
        if (definition != null ? !definition.equals(that.definition) : that.definition != null) return false;
        if (parentTypeConceptId != null ? !parentTypeConceptId.equals(that.parentTypeConceptId) : that.parentTypeConceptId != null)
            return false;
        if (baseTypeConceptId != null ? !baseTypeConceptId.equals(that.baseTypeConceptId) : that.baseTypeConceptId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (conceptId != null ? conceptId.hashCode() : 0);
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        result = 31 * result + (parentTypeConceptId != null ? parentTypeConceptId.hashCode() : 0);
        result = 31 * result + (baseTypeConceptId != null ? baseTypeConceptId.hashCode() : 0);
        result = 31 * result + (int) status;
        result = 31 * result + (int) dataTypeId;
        result = 31 * result + (int) conceptTypeId;
        result = 31 * result + (int) present;
        return result;
    }

    public static List<Object[]> findConcept(String term) throws Exception {
        if (term.isEmpty()) {
            return new ArrayList<Object[]>();
        }

        /*String where = "select c.conceptId,c.definition, "+
                "c2.definition as parentType,c2.conceptId as parentTypeId, "+
                "c3.definition as baseType,c3.conceptId as baseTypeId, "+
                "c.dataTypeId,c.conceptTypeId,c.present,o.units "+
                "from ConceptEntity c "+
                "left join ConceptEntity c2 on c2.conceptId = c.parentTypeConceptId "+
                "left join ConceptEntity c3 on c3.conceptId = c.baseTypeConceptId "+
                "left join ObservationEntity o on o.snomedConceptId = c.conceptId "+
                "WHERE c.definition like :term "+
                "group by c.conceptId,c.definition," +
                "c2.definition,c2.conceptId," +
                "c3.definition,c3.conceptId," +
                "c.dataTypeId,c.conceptTypeId,c.present,o.units "+
                "order by c.present desc, c.definition";*/

        // removed units lookup against observations for now as too slow on live - needs alternative approach
        String where = "select c.conceptId,c.definition, "+
                "c2.definition as parentType,c2.conceptId as parentTypeId, "+
                "c3.definition as baseType,c3.conceptId as baseTypeId, "+
                "c.dataTypeId,c.conceptTypeId,c.present,' ' as units "+
                "from ConceptEntity c "+
                "left join ConceptEntity c2 on c2.conceptId = c.parentTypeConceptId "+
                "left join ConceptEntity c3 on c3.conceptId = c.baseTypeConceptId "+
                "WHERE c.definition like :term "+
                "group by c.conceptId,c.definition," +
                "c2.definition,c2.conceptId," +
                "c3.definition,c3.conceptId," +
                "c.dataTypeId,c.conceptTypeId,c.present "+
                "order by c.present desc, c.definition";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("term", term+"%").getResultList();

        entityManager.close();

        return ent;

    }

    public static List<Object[]> findConceptParents(String id) throws Exception {
        if (id.isEmpty()) {
            return new ArrayList<Object[]>();
        }

        String where = "select c.conceptId,c.definition, "+
                "c2.definition as parentType,c2.conceptId as parentTypeId, "+
                "c3.definition as baseType,c3.conceptId as baseTypeId, "+
                "c.dataTypeId,c.conceptTypeId,c2.conceptTypeId as parentConceptTypeId,c2.present,o.units "+
                "from ConceptEntity c "+
                "left join ConceptEntity c2 on c2.conceptId = c.parentTypeConceptId "+
                "left join ConceptEntity c3 on c3.conceptId = c.baseTypeConceptId "+
                "left join ObservationEntity o on o.snomedConceptId = c2.conceptId "+
                "WHERE c.conceptId = :id "+
                "group by c.conceptId,c.definition," +
                "c2.definition,c2.conceptId," +
                "c3.definition,c3.conceptId," +
                "c.dataTypeId,c.conceptTypeId,c2.conceptTypeId,c2.present,o.units "+
                "order by c2.present desc, c2.definition";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("id", id).getResultList();


        entityManager.close();

        return ent;

    }

    public static List<Object[]> findConceptChildren(String id) throws Exception {
        if (id.isEmpty()) {
            return new ArrayList<Object[]>();
        }

        String where = "select c.conceptId,c.definition, "+
                "c2.definition as parentType,c2.conceptId as parentTypeId, "+
                "c3.definition as baseType,c3.conceptId as baseTypeId, "+
                "c.dataTypeId,c.conceptTypeId,c.present,o.units "+
                "from ConceptEntity c "+
                "left join ConceptEntity c2 on c2.conceptId = c.parentTypeConceptId "+
                "left join ConceptEntity c3 on c3.conceptId = c.baseTypeConceptId "+
                "left join ObservationEntity o on o.snomedConceptId = c.conceptId "+
                "WHERE c.parentTypeConceptId = :id "+
                "group by c.conceptId,c.definition," +
                "c2.definition,c2.conceptId," +
                "c3.definition,c3.conceptId," +
                "c.dataTypeId,c.conceptTypeId,c.present,o.units "+
                "order by c.present desc, c.definition";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("id", id).getResultList();


        entityManager.close();

        return ent;

    }
}
