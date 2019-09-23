package org.endeavourhealth.enterprise.core.database.models;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
//access to corexx servers removed, so moved this table to another DB
//@Table(name = "emis_csv_code_map", schema = "publisher_common")
@Table(name = "emis_csv_code_map", schema = "rf2")
@IdClass(EmisCsvCodeMapEntityPK.class)
public class EmisCsvCodeMapEntity {
    private byte medication;
    private long codeId;
    private String codeType;
    private String codeableConcept;
    private String readTerm;
    private String readCode;
    private Long snomedConceptId;
    private Long snomedDescriptionId;
    private String snomedTerm;
    private String nationalCode;
    private String nationalCodeCategory;
    private String nationalCodeDescription;
    private Long parentCodeId;

    @Id
    @Column(name = "medication")
    public byte getMedication() {
        return medication;
    }

    public void setMedication(byte medication) {
        this.medication = medication;
    }

    @Id
    @Column(name = "code_id")
    public long getCodeId() {
        return codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    @Basic
    @Column(name = "code_type")
    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    @Basic
    @Column(name = "codeable_concept")
    public String getCodeableConcept() {
        return codeableConcept;
    }

    public void setCodeableConcept(String codeableConcept) {
        this.codeableConcept = codeableConcept;
    }

    @Basic
    @Column(name = "read_term")
    public String getReadTerm() {
        return readTerm;
    }

    public void setReadTerm(String readTerm) {
        this.readTerm = readTerm;
    }

    @Basic
    @Column(name = "read_code")
    public String getReadCode() {
        return readCode;
    }

    public void setReadCode(String readCode) {
        this.readCode = readCode;
    }

    @Basic
    @Column(name = "snomed_concept_id")
    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    @Basic
    @Column(name = "snomed_description_id")
    public Long getSnomedDescriptionId() {
        return snomedDescriptionId;
    }

    public void setSnomedDescriptionId(Long snomedDescriptionId) {
        this.snomedDescriptionId = snomedDescriptionId;
    }

    @Basic
    @Column(name = "snomed_term")
    public String getSnomedTerm() {
        return snomedTerm;
    }

    public void setSnomedTerm(String snomedTerm) {
        this.snomedTerm = snomedTerm;
    }

    @Basic
    @Column(name = "national_code")
    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    @Basic
    @Column(name = "national_code_category")
    public String getNationalCodeCategory() {
        return nationalCodeCategory;
    }

    public void setNationalCodeCategory(String nationalCodeCategory) {
        this.nationalCodeCategory = nationalCodeCategory;
    }

    @Basic
    @Column(name = "national_code_description")
    public String getNationalCodeDescription() {
        return nationalCodeDescription;
    }

    public void setNationalCodeDescription(String nationalCodeDescription) {
        this.nationalCodeDescription = nationalCodeDescription;
    }

    @Basic
    @Column(name = "parent_code_id")
    public Long getParentCodeId() {
        return parentCodeId;
    }

    public void setParentCodeId(Long parentCodeId) {
        this.parentCodeId = parentCodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmisCsvCodeMapEntity that = (EmisCsvCodeMapEntity) o;
        return medication == that.medication &&
                codeId == that.codeId &&
                Objects.equals(codeType, that.codeType) &&
                Objects.equals(codeableConcept, that.codeableConcept) &&
                Objects.equals(readTerm, that.readTerm) &&
                Objects.equals(readCode, that.readCode) &&
                Objects.equals(snomedConceptId, that.snomedConceptId) &&
                Objects.equals(snomedDescriptionId, that.snomedDescriptionId) &&
                Objects.equals(snomedTerm, that.snomedTerm) &&
                Objects.equals(nationalCode, that.nationalCode) &&
                Objects.equals(nationalCodeCategory, that.nationalCodeCategory) &&
                Objects.equals(nationalCodeDescription, that.nationalCodeDescription) &&
                Objects.equals(parentCodeId, that.parentCodeId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(medication, codeId, codeType, codeableConcept, readTerm, readCode, snomedConceptId, snomedDescriptionId, snomedTerm, nationalCode, nationalCodeCategory, nationalCodeDescription, parentCodeId);
    }

    public static Long findCodeIdFromReadCode(String code) throws Exception {
        //EntityManager entityManager = PersistenceManager.INSTANCE.getEmPublisherCommonData();
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmRf2();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmisCsvCodeMapEntity> cq = cb.createQuery(EmisCsvCodeMapEntity.class);
        Root<EmisCsvCodeMapEntity> rootEntry = cq.from(EmisCsvCodeMapEntity.class);

        Predicate predicate = cb.equal(rootEntry.get("readCode"), code);
        cq.where(predicate);

        TypedQuery<EmisCsvCodeMapEntity> query = entityManager.createQuery(cq);
        List<EmisCsvCodeMapEntity> ret = query.getResultList();

        Long codeId = 0L;
        for (EmisCsvCodeMapEntity codeMap : ret) {
            codeId = codeMap.codeId;
        }
        entityManager.close();

        return codeId;
    }

    public static List<EmisCsvCodeMapEntity> findChildCodes(List<Long> parents) throws Exception {
        //EntityManager entityManager = PersistenceManager.INSTANCE.getEmPublisherCommonData();
        EntityManager entityManager = PersistenceManager.INSTANCE.getEmRf2();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmisCsvCodeMapEntity> cq = cb.createQuery(EmisCsvCodeMapEntity.class);
        Root<EmisCsvCodeMapEntity> rootEntry = cq.from(EmisCsvCodeMapEntity.class);

        Predicate predicate = rootEntry.get("parentCodeId").in(parents);
        cq.where(predicate);

        TypedQuery<EmisCsvCodeMapEntity> query = entityManager.createQuery(cq);
        List<EmisCsvCodeMapEntity> ret = query.getResultList();

        entityManager.close();
        return ret;
    }

}
