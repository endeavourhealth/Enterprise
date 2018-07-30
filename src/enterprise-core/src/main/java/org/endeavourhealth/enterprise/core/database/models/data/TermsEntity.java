package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darren on 05/07/2018.
 */
@Entity
@Table(name = "terms", schema = "enterprise_data_pseudonymised", catalog = "")
public class TermsEntity {
    private int id;
    private String originalTerm;
    private String snomedTerm;
    private Long snomedConceptId;
    private String originalCode;
    private Byte recordType;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "original_term", nullable = true, length = 1000)
    public String getOriginalTerm() {
        return originalTerm;
    }

    public void setOriginalTerm(String originalTerm) {
        this.originalTerm = originalTerm;
    }

    @Basic
    @Column(name = "snomed_term", nullable = true, length = 1000)
    public String getSnomedTerm() {
        return snomedTerm;
    }

    public void setSnomedTerm(String snomedTerm) {
        this.snomedTerm = snomedTerm;
    }

    @Basic
    @Column(name = "snomed_concept_id", nullable = true)
    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    @Basic
    @Column(name = "original_code", nullable = true, length = 45)
    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    @Basic
    @Column(name = "record_type", nullable = true)
    public Byte getRecordType() {
        return recordType;
    }

    public void setRecordType(Byte recordType) {
        this.recordType = recordType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermsEntity that = (TermsEntity) o;

        if (id != that.id) return false;
        if (originalTerm != null ? !originalTerm.equals(that.originalTerm) : that.originalTerm != null) return false;
        if (snomedTerm != null ? !snomedTerm.equals(that.snomedTerm) : that.snomedTerm != null) return false;
        if (snomedConceptId != null ? !snomedConceptId.equals(that.snomedConceptId) : that.snomedConceptId != null)
            return false;
        if (originalCode != null ? !originalCode.equals(that.originalCode) : that.originalCode != null) return false;
        if (recordType != null ? !recordType.equals(that.recordType) : that.recordType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (originalTerm != null ? originalTerm.hashCode() : 0);
        result = 31 * result + (snomedTerm != null ? snomedTerm.hashCode() : 0);
        result = 31 * result + (snomedConceptId != null ? snomedConceptId.hashCode() : 0);
        result = 31 * result + (originalCode != null ? originalCode.hashCode() : 0);
        result = 31 * result + (recordType != null ? recordType.hashCode() : 0);
        return result;
    }

    public static List<Object[]> findTerms(String term) throws Exception {
        if (term.isEmpty()) {
            return new ArrayList<Object[]>();
        }

        String[] parts = term.split(" ");
        String part1 = parts[0];
        String part2 = "";

        if (parts.length>1)
            part2 = parts[1];
        else
            part2 = parts[0];

        String where = "select t.originalTerm,t.snomedTerm,t.snomedConceptId,t.originalCode, "+
                "case when t.recordType=0 then 'Observation' "+
                "when t.recordType=1 then 'Medication' "+
                "when t.recordType=2 then 'Allergy' "+
                "when t.recordType=3 then 'Referral' "+
                "when t.recordType=4 then 'Encounter' END as recordType "+
                "from TermsEntity t "+
                "WHERE (t.originalTerm like :term1 or t.originalTerm like :term2 or t.snomedTerm like :term1 or t.snomedTerm like :term2 or t.originalCode like :term1 or t.snomedConceptId like :term1)"+
                "order by t.originalTerm";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("term1", term)
                .setParameter("term2", part2+" "+part1)
                .getResultList();

        entityManager.close();

        return ent;

    }

    public static List<Object[]> getTermChildren(String code) throws Exception {
        if (code.isEmpty()) {
            return new ArrayList<Object[]>();
        }

        String where = "select sd.term,sd.term,sd.conceptId,'','Observation' "+
                "from Sct2RelationshipEntity sr " +
                "JOIN Sct2DescriptionEntity sd " +
                "on sd.conceptId = sr.sourceId " +
                "JOIN Sct2ConceptEntity sc on sc.id = sd.conceptId " +
                "where sr.destinationId = :code " +
                "and sr.typeId = 116680003 " +
                "and sr.active = 1 " +
                "and sd.typeId = 900000000000003001 "+
                "order by sd.term";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmRf2();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("code", code)
                .getResultList();

        entityManager.close();

        return ent;

    }

    public static List<Object[]> getTermParents(String code) throws Exception {
        if (code.isEmpty()) {
            return new ArrayList<Object[]>();
        }

        String where = "select sd.term,sd.term,sd.conceptId,'','Observation' "+
                "from Sct2RelationshipEntity sr " +
                "JOIN Sct2DescriptionEntity sd " +
                "on sd.conceptId = sr.destinationId " +
                "JOIN Sct2ConceptEntity sc on sc.id = sd.conceptId " +
                "where sr.sourceId = :code " +
                "and sr.typeId = 116680003 " +
                "and sr.active = 1 " +
                "and sd.typeId = 900000000000003001 "+
                "order by sd.term";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmRf2();

        List<Object[]> ent = entityManager.createQuery(where)
                .setParameter("code", code)
                .getResultList();

        entityManager.close();

        return ent;

    }
}
