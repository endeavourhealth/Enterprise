package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;

/**
 * Created by darren on 09/09/2017.
 */
@Entity
@Table(name = "organization_view", schema = "data_sharing_manager", catalog = "")
public class OrganizationViewEntity {
    private long id;
    private String odsCode;
    private String name;
    private String typeCode;
    private String typeDesc;
    private String postcode;
    private Integer parentOrganizationId;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ods_code", nullable = true, length = 50)
    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    @Basic
    @Column(name = "name", nullable = true, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "type_code", nullable = true, length = 50)
    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Basic
    @Column(name = "type_desc", nullable = true, length = 255)
    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    @Basic
    @Column(name = "postcode", nullable = true, length = 10)
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Basic
    @Column(name = "parent_organization_id", nullable = true)
    public Integer getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(Integer parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganizationViewEntity that = (OrganizationViewEntity) o;

        if (id != that.id) return false;
        if (odsCode != null ? !odsCode.equals(that.odsCode) : that.odsCode != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (typeCode != null ? !typeCode.equals(that.typeCode) : that.typeCode != null) return false;
        if (typeDesc != null ? !typeDesc.equals(that.typeDesc) : that.typeDesc != null) return false;
        if (postcode != null ? !postcode.equals(that.postcode) : that.postcode != null) return false;
        if (parentOrganizationId != null ? !parentOrganizationId.equals(that.parentOrganizationId) : that.parentOrganizationId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (odsCode != null ? odsCode.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (typeCode != null ? typeCode.hashCode() : 0);
        result = 31 * result + (typeDesc != null ? typeDesc.hashCode() : 0);
        result = 31 * result + (postcode != null ? postcode.hashCode() : 0);
        result = 31 * result + (parentOrganizationId != null ? parentOrganizationId.hashCode() : 0);
        return result;
    }
}
