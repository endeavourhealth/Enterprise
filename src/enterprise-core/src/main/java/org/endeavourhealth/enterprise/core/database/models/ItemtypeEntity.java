package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.*;

/**
 * Created by darren on 08/07/16.
 */
@Entity
@Table(name = "itemtype", schema = "\"Definition\"", catalog = "Endeavour_Enterprise")
public class ItemtypeEntity {
    private short itemtypeid;
    private String description;

    @Id
    @Column(name = "itemtypeid")
    public short getItemtypeid() {
        return itemtypeid;
    }

    public void setItemtypeid(short itemtypeid) {
        this.itemtypeid = itemtypeid;
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

        ItemtypeEntity that = (ItemtypeEntity) o;

        if (itemtypeid != that.itemtypeid) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) itemtypeid;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
