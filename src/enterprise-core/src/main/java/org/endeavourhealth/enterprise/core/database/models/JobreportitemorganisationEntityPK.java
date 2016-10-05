package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
public class JobreportitemorganisationEntityPK implements Serializable {
    private UUID jobreportitemuuid;
    private String organisationodscode;

    @Column(name = "jobreportitemuuid")
    @Id
    public UUID getJobreportitemuuid() {
        return jobreportitemuuid;
    }

    public void setJobreportitemuuid(UUID jobreportitemuuid) {
        this.jobreportitemuuid = jobreportitemuuid;
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

        JobreportitemorganisationEntityPK that = (JobreportitemorganisationEntityPK) o;

        if (jobreportitemuuid != null ? !jobreportitemuuid.equals(that.jobreportitemuuid) : that.jobreportitemuuid != null)
            return false;
        if (organisationodscode != null ? !organisationodscode.equals(that.organisationodscode) : that.organisationodscode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobreportitemuuid != null ? jobreportitemuuid.hashCode() : 0;
        result = 31 * result + (organisationodscode != null ? organisationodscode.hashCode() : 0);
        return result;
    }
}
