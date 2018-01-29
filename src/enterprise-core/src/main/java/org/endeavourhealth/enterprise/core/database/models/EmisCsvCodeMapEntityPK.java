package org.endeavourhealth.enterprise.core.database.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class EmisCsvCodeMapEntityPK implements Serializable {
    private byte medication;
    private long codeId;

    @Column(name = "medication")
    @Id
    public byte getMedication() {
        return medication;
    }

    public void setMedication(byte medication) {
        this.medication = medication;
    }

    @Column(name = "code_id")
    @Id
    public long getCodeId() {
        return codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmisCsvCodeMapEntityPK that = (EmisCsvCodeMapEntityPK) o;
        return medication == that.medication &&
                codeId == that.codeId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(medication, codeId);
    }
}
