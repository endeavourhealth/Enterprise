package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.*;
import java.util.List;

/**
 * Created by darren on 04/09/2017.
 */
@Entity
@Table(name = "msoa_lookup", schema = "enterprise_data_pseudonymised", catalog = "")
public class MsoaLookupEntity {
    private String msoaCode;
    private String msoaName;

    @Id
    @Column(name = "msoa_code", nullable = false, length = 9)
    public String getMsoaCode() {
        return msoaCode;
    }

    public void setMsoaCode(String msoaCode) {
        this.msoaCode = msoaCode;
    }

    @Basic
    @Column(name = "msoa_name", nullable = true, length = 255)
    public String getMsoaName() {
        return msoaName;
    }

    public void setMsoaName(String msoaName) {
        this.msoaName = msoaName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MsoaLookupEntity that = (MsoaLookupEntity) o;

        if (msoaCode != null ? !msoaCode.equals(that.msoaCode) : that.msoaCode != null) return false;
        if (msoaName != null ? !msoaName.equals(that.msoaName) : that.msoaName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = msoaCode != null ? msoaCode.hashCode() : 0;
        result = 31 * result + (msoaName != null ? msoaName.hashCode() : 0);
        return result;
    }

    public static List<Object[]> getMsoaCodes() throws Exception {
        String where = "select msoaCode, msoaName " +
                "from MsoaLookupEntity";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<Object[]> ent = entityManager.createQuery(where)
                .getResultList();

        entityManager.close();

        return ent;

    }
}
