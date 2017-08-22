package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CodeSet", schema = "enterprise_admin", catalog = "")
public class CodeSetEntity implements Serializable {

    private String itemUuid;
    private Long snomedConceptId;

    @Id
    @Column(name = "ItemUuid", nullable = true)
    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }

    @Id
    @Column(name = "SnomedConceptId", nullable = true)
    public Long getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(Long snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }
}
