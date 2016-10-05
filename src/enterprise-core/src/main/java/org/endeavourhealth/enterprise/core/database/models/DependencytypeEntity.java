package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.*;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "dependencytype", schema = "\"Definition\"", catalog = "Endeavour_Enterprise")
public class DependencytypeEntity {
    private short dependencytypeid;
    private String description;

    @Id
    @Column(name = "dependencytypeid")
    public short getDependencytypeid() {
        return dependencytypeid;
    }

    public void setDependencytypeid(short dependencytypeid) {
        this.dependencytypeid = dependencytypeid;
    }

    @Basic
    @Column(name = "description")
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

        DependencytypeEntity that = (DependencytypeEntity) o;

        if (dependencytypeid != that.dependencytypeid) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) dependencytypeid;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
