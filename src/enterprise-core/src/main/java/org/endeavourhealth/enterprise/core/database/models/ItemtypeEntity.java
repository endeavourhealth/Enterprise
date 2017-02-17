package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.*;

/**
 * Created by darren on 14/02/17.
 */
@Entity
@Table(name = "ItemType", schema = "enterprise_admin", catalog = "")
public class ItemTypeEntity {
    private short itemTypeId;
    private String description;

    @Id
    @Column(name = "ItemTypeId", nullable = false)
    public short getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(short itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    @Basic
    @Column(name = "Description", nullable = false, length = 100)
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

        ItemTypeEntity that = (ItemTypeEntity) o;

        if (itemTypeId != that.itemTypeId) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) itemTypeId;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
