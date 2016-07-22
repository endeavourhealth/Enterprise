package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
public class ItemdependencyEntityPK implements Serializable {
    private UUID itemuuid;
    private UUID audituuid;
    private UUID dependentitemuuid;

    @Column(name = "itemuuid")
    @Id
    public UUID getItemuuid() {
        return itemuuid;
    }

    public void setItemuuid(UUID itemuuid) {
        this.itemuuid = itemuuid;
    }

    @Column(name = "audituuid")
    @Id
    public UUID getAudituuid() {
        return audituuid;
    }

    public void setAudituuid(UUID audituuid) {
        this.audituuid = audituuid;
    }

    @Column(name = "dependentitemuuid")
    @Id
    public UUID getDependentitemuuid() {
        return dependentitemuuid;
    }

    public void setDependentitemuuid(UUID dependentitemuuid) {
        this.dependentitemuuid = dependentitemuuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemdependencyEntityPK that = (ItemdependencyEntityPK) o;

        if (itemuuid != null ? !itemuuid.equals(that.itemuuid) : that.itemuuid != null) return false;
        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;
        if (dependentitemuuid != null ? !dependentitemuuid.equals(that.dependentitemuuid) : that.dependentitemuuid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemuuid != null ? itemuuid.hashCode() : 0;
        result = 31 * result + (audituuid != null ? audituuid.hashCode() : 0);
        result = 31 * result + (dependentitemuuid != null ? dependentitemuuid.hashCode() : 0);
        return result;
    }
}
