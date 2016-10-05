package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
public class JobreportorganisationEntityPK implements Serializable {
    private UUID jobreportuuid;
    private String organisationodscode;

    @Column(name = "jobreportuuid")
    @Id
    public UUID getJobreportuuid() {
        return jobreportuuid;
    }

    public void setJobreportuuid(UUID jobreportuuid) {
        this.jobreportuuid = jobreportuuid;
    }

    @Column(name = "organisationodscode")
    @Id
    public String getOrganisationodscode() {
        return organisationodscode;
    }

    public void setOrganisationodscode(String organisationodscode) {
        this.organisationodscode = organisationodscode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobreportorganisationEntityPK that = (JobreportorganisationEntityPK) o;

        if (jobreportuuid != null ? !jobreportuuid.equals(that.jobreportuuid) : that.jobreportuuid != null)
            return false;
        if (organisationodscode != null ? !organisationodscode.equals(that.organisationodscode) : that.organisationodscode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobreportuuid != null ? jobreportuuid.hashCode() : 0;
        result = 31 * result + (organisationodscode != null ? organisationodscode.hashCode() : 0);
        return result;
    }
}
