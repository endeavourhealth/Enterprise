package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;

/**
 * Created by darren on 18/03/17.
 */
@Entity
@Table(name = "ConceptType", schema = "enterprise_data_pseudonymised", catalog = "")
public class ConceptTypeEntity {
    private byte conceptTypeId;
    private String type;

    @Id
    @Column(name = "ConceptTypeId", nullable = false)
    public byte getConceptTypeId() {
        return conceptTypeId;
    }

    public void setConceptTypeId(byte conceptTypeId) {
        this.conceptTypeId = conceptTypeId;
    }

    @Basic
    @Column(name = "Type", nullable = true, length = 45)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConceptTypeEntity that = (ConceptTypeEntity) o;

        if (conceptTypeId != that.conceptTypeId) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) conceptTypeId;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
