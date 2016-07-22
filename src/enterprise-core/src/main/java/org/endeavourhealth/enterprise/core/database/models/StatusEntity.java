package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.*;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "status", schema = "\"Execution\"", catalog = "Endeavour_Enterprise")
public class StatusEntity {
    private short statusid;
    private String description;

    @Id
    @Column(name = "statusid")
    public short getStatusid() {
        return statusid;
    }

    public void setStatusid(short statusid) {
        this.statusid = statusid;
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

        StatusEntity that = (StatusEntity) o;

        if (statusid != that.statusid) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) statusid;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
