package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by darren on 14/02/17.
 */
public class ItemEntityPK implements Serializable {
    private String itemUuid;
    private String auditUuid;

    @Column(name = "ItemUuid", nullable = false, length = 36)
    @Id
    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }

    @Column(name = "AuditUuid", nullable = false, length = 36)
    @Id
    public String getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(String auditUuid) {
        this.auditUuid = auditUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemEntityPK that = (ItemEntityPK) o;

        if (itemUuid != null ? !itemUuid.equals(that.itemUuid) : that.itemUuid != null) return false;
        if (auditUuid != null ? !auditUuid.equals(that.auditUuid) : that.auditUuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemUuid != null ? itemUuid.hashCode() : 0;
        result = 31 * result + (auditUuid != null ? auditUuid.hashCode() : 0);
        return result;
    }
}
