package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by darren on 08/07/16.
 */
public class ItemEntityPK implements Serializable {
    private UUID itemuuid;
    private UUID audituuid;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemEntityPK that = (ItemEntityPK) o;

        if (itemuuid != null ? !itemuuid.equals(that.itemuuid) : that.itemuuid != null) return false;
        if (audituuid != null ? !audituuid.equals(that.audituuid) : that.audituuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemuuid != null ? itemuuid.hashCode() : 0;
        result = 31 * result + (audituuid != null ? audituuid.hashCode() : 0);
        return result;
    }
}
