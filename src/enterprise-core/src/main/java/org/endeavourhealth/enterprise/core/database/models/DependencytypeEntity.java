package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.*;

/**
 * Created by darren on 14/02/17.
 */
@Entity
@Table(name = "DependencyType", schema = "enterprise_admin", catalog = "")
public class DependencyTypeEntity {
    private short dependencyTypeId;
    private String description;

    @Id
    @Column(name = "DependencyTypeId", nullable = false)
    public short getDependencyTypeId() {
        return dependencyTypeId;
    }

    public void setDependencyTypeId(short dependencyTypeId) {
        this.dependencyTypeId = dependencyTypeId;
    }

    @Basic
    @Column(name = "Description", nullable = false, length = 100)
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

        DependencyTypeEntity that = (DependencyTypeEntity) o;

        if (dependencyTypeId != that.dependencyTypeId) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) dependencyTypeId;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
