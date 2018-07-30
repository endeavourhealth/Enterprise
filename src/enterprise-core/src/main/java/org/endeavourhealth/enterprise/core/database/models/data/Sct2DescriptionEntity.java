package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;

/**
 * Created by darren on 30/07/2018.
 */
@Entity
@Table(name = "sct2_Description", schema = "rf2", catalog = "")
public class Sct2DescriptionEntity {
    private String id;
    private String effectiveTime;
    private String active;
    private String moduleId;
    private long conceptId;
    private String languageCode;
    private String typeId;
    private String term;
    private String caseSignificanceId;

    @Id
    @Column(name = "id", nullable = false, length = 45)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "effectiveTime", nullable = true, length = 45)
    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    @Basic
    @Column(name = "active", nullable = true, length = 45)
    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Basic
    @Column(name = "moduleId", nullable = true, length = 45)
    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Basic
    @Column(name = "conceptId", nullable = false)
    public long getConceptId() {
        return conceptId;
    }

    public void setConceptId(long conceptId) {
        this.conceptId = conceptId;
    }

    @Basic
    @Column(name = "languageCode", nullable = true, length = 45)
    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Basic
    @Column(name = "typeId", nullable = true, length = 45)
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Basic
    @Column(name = "term", nullable = true, length = 1000)
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Basic
    @Column(name = "caseSignificanceId", nullable = true, length = 45)
    public String getCaseSignificanceId() {
        return caseSignificanceId;
    }

    public void setCaseSignificanceId(String caseSignificanceId) {
        this.caseSignificanceId = caseSignificanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sct2DescriptionEntity that = (Sct2DescriptionEntity) o;

        if (conceptId != that.conceptId) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (effectiveTime != null ? !effectiveTime.equals(that.effectiveTime) : that.effectiveTime != null)
            return false;
        if (active != null ? !active.equals(that.active) : that.active != null) return false;
        if (moduleId != null ? !moduleId.equals(that.moduleId) : that.moduleId != null) return false;
        if (languageCode != null ? !languageCode.equals(that.languageCode) : that.languageCode != null) return false;
        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;
        if (term != null ? !term.equals(that.term) : that.term != null) return false;
        if (caseSignificanceId != null ? !caseSignificanceId.equals(that.caseSignificanceId) : that.caseSignificanceId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (effectiveTime != null ? effectiveTime.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (moduleId != null ? moduleId.hashCode() : 0);
        result = 31 * result + (int) (conceptId ^ (conceptId >>> 32));
        result = 31 * result + (languageCode != null ? languageCode.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (term != null ? term.hashCode() : 0);
        result = 31 * result + (caseSignificanceId != null ? caseSignificanceId.hashCode() : 0);
        return result;
    }
}
