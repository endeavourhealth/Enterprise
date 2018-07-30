package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;

/**
 * Created by darren on 30/07/2018.
 */
@Entity
@Table(name = "sct2_Relationship", schema = "rf2", catalog = "")
public class Sct2RelationshipEntity {
    private String id;
    private String effectiveTime;
    private String active;
    private String moduleId;
    private String sourceId;
    private String destinationId;
    private String relationshipGroup;
    private String typeId;
    private String characteristicTypeId;
    private String modifierId;

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
    @Column(name = "sourceId", nullable = true, length = 45)
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Basic
    @Column(name = "destinationId", nullable = true, length = 45)
    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    @Basic
    @Column(name = "relationshipGroup", nullable = true, length = 45)
    public String getRelationshipGroup() {
        return relationshipGroup;
    }

    public void setRelationshipGroup(String relationshipGroup) {
        this.relationshipGroup = relationshipGroup;
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
    @Column(name = "characteristicTypeId", nullable = true, length = 45)
    public String getCharacteristicTypeId() {
        return characteristicTypeId;
    }

    public void setCharacteristicTypeId(String characteristicTypeId) {
        this.characteristicTypeId = characteristicTypeId;
    }

    @Basic
    @Column(name = "modifierId", nullable = true, length = 45)
    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sct2RelationshipEntity that = (Sct2RelationshipEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (effectiveTime != null ? !effectiveTime.equals(that.effectiveTime) : that.effectiveTime != null)
            return false;
        if (active != null ? !active.equals(that.active) : that.active != null) return false;
        if (moduleId != null ? !moduleId.equals(that.moduleId) : that.moduleId != null) return false;
        if (sourceId != null ? !sourceId.equals(that.sourceId) : that.sourceId != null) return false;
        if (destinationId != null ? !destinationId.equals(that.destinationId) : that.destinationId != null)
            return false;
        if (relationshipGroup != null ? !relationshipGroup.equals(that.relationshipGroup) : that.relationshipGroup != null)
            return false;
        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;
        if (characteristicTypeId != null ? !characteristicTypeId.equals(that.characteristicTypeId) : that.characteristicTypeId != null)
            return false;
        if (modifierId != null ? !modifierId.equals(that.modifierId) : that.modifierId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (effectiveTime != null ? effectiveTime.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (moduleId != null ? moduleId.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        result = 31 * result + (destinationId != null ? destinationId.hashCode() : 0);
        result = 31 * result + (relationshipGroup != null ? relationshipGroup.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (characteristicTypeId != null ? characteristicTypeId.hashCode() : 0);
        result = 31 * result + (modifierId != null ? modifierId.hashCode() : 0);
        return result;
    }
}
