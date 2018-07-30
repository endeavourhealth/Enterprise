package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;

/**
 * Created by darren on 30/07/2018.
 */
@Entity
@Table(name = "sct2_transitiveclosure", schema = "rf2", catalog = "")
public class Sct2TransitiveclosureEntity {
    private int id;
    private long subtypeId;
    private long supertypeId;
    private Byte active;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "subtypeId", nullable = false)
    public long getSubtypeId() {
        return subtypeId;
    }

    public void setSubtypeId(long subtypeId) {
        this.subtypeId = subtypeId;
    }

    @Basic
    @Column(name = "supertypeId", nullable = false)
    public long getSupertypeId() {
        return supertypeId;
    }

    public void setSupertypeId(long supertypeId) {
        this.supertypeId = supertypeId;
    }

    @Basic
    @Column(name = "active", nullable = true)
    public Byte getActive() {
        return active;
    }

    public void setActive(Byte active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sct2TransitiveclosureEntity that = (Sct2TransitiveclosureEntity) o;

        if (id != that.id) return false;
        if (subtypeId != that.subtypeId) return false;
        if (supertypeId != that.supertypeId) return false;
        if (active != null ? !active.equals(that.active) : that.active != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (subtypeId ^ (subtypeId >>> 32));
        result = 31 * result + (int) (supertypeId ^ (supertypeId >>> 32));
        result = 31 * result + (active != null ? active.hashCode() : 0);
        return result;
    }
}
