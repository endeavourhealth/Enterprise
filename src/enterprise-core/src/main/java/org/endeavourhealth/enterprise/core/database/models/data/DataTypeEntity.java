package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;

/**
 * Created by darren on 18/03/17.
 */
@Entity
@Table(name = "DataType", schema = "enterprise_data_pseudonymised", catalog = "")
public class DataTypeEntity {
    private byte dataTypeId;
    private String type;

    @Id
    @Column(name = "DataTypeId", nullable = false)
    public byte getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(byte dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    @Basic
    @Column(name = "Type", nullable = true, length = 45)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataTypeEntity that = (DataTypeEntity) o;

        if (dataTypeId != that.dataTypeId) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) dataTypeId;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
