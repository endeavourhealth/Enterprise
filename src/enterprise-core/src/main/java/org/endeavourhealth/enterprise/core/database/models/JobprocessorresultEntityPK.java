package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
public class JobprocessorresultEntityPK implements Serializable {
    private UUID jobuuid;
    private UUID processoruuid;

    @Column(name = "jobuuid")
    @Id
    public UUID getJobuuid() {
        return jobuuid;
    }

    public void setJobuuid(UUID jobuuid) {
        this.jobuuid = jobuuid;
    }

    @Column(name = "processoruuid")
    @Id
    public UUID getProcessoruuid() {
        return processoruuid;
    }

    public void setProcessoruuid(UUID processoruuid) {
        this.processoruuid = processoruuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobprocessorresultEntityPK that = (JobprocessorresultEntityPK) o;

        if (jobuuid != null ? !jobuuid.equals(that.jobuuid) : that.jobuuid != null) return false;
        if (processoruuid != null ? !processoruuid.equals(that.processoruuid) : that.processoruuid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobuuid != null ? jobuuid.hashCode() : 0;
        result = 31 * result + (processoruuid != null ? processoruuid.hashCode() : 0);
        return result;
    }
}
