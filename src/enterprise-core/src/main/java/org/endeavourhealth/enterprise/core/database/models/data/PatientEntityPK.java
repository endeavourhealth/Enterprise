package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by darren on 23/03/17.
 */
public class PatientEntityPK implements Serializable {
    private long id;
    private long organizationId;

    @Column(name = "id", nullable = false)
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "organization_id", nullable = false)
    @Id
    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientEntityPK that = (PatientEntityPK) o;

        if (id != that.id) return false;
        if (organizationId != that.organizationId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (organizationId ^ (organizationId >>> 32));
        return result;
    }
}
