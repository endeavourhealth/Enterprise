package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
public class JobcontentEntityPK implements Serializable {
    private UUID jobuuid;
    private UUID itemuuid;

    @Column(name = "jobuuid")
    @Id
    public UUID getJobuuid() {
        return jobuuid;
    }

    public void setJobuuid(UUID jobuuid) {
        this.jobuuid = jobuuid;
    }

    @Column(name = "itemuuid")
    @Id
    public UUID getItemuuid() {
        return itemuuid;
    }

    public void setItemuuid(UUID itemuuid) {
        this.itemuuid = itemuuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobcontentEntityPK that = (JobcontentEntityPK) o;

        if (jobuuid != null ? !jobuuid.equals(that.jobuuid) : that.jobuuid != null) return false;
        if (itemuuid != null ? !itemuuid.equals(that.itemuuid) : that.itemuuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobuuid != null ? jobuuid.hashCode() : 0;
        result = 31 * result + (itemuuid != null ? itemuuid.hashCode() : 0);
        return result;
    }
}
