package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;

/**
 * Created by darren on 30/07/2018.
 */
@Entity
@Table(name = "sct2_Concept", schema = "rf2", catalog = "")
public class Sct2ConceptEntity {
    private String id;
    private String effectiveTime;
    private String active;
    private String moduleId;
    private String definitionStatusId;

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
    @Column(name = "definitionStatusId", nullable = true, length = 45)
    public String getDefinitionStatusId() {
        return definitionStatusId;
    }

    public void setDefinitionStatusId(String definitionStatusId) {
        this.definitionStatusId = definitionStatusId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sct2ConceptEntity that = (Sct2ConceptEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (effectiveTime != null ? !effectiveTime.equals(that.effectiveTime) : that.effectiveTime != null)
            return false;
        if (active != null ? !active.equals(that.active) : that.active != null) return false;
        if (moduleId != null ? !moduleId.equals(that.moduleId) : that.moduleId != null) return false;
        if (definitionStatusId != null ? !definitionStatusId.equals(that.definitionStatusId) : that.definitionStatusId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (effectiveTime != null ? effectiveTime.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (moduleId != null ? moduleId.hashCode() : 0);
        result = 31 * result + (definitionStatusId != null ? definitionStatusId.hashCode() : 0);
        return result;
    }
}
