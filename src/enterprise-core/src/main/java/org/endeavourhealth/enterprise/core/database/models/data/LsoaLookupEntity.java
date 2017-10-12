package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by darren on 04/09/2017.
 */
@Entity
@javax.persistence.Table(name = "lsoa_lookup", schema = "enterprise_data_pseudonymised", catalog = "")
public class LsoaLookupEntity {
    private String lsoaCode;

    @Id
    @javax.persistence.Column(name = "lsoa_code", nullable = false, length = 9)
    public String getLsoaCode() {
        return lsoaCode;
    }

    public void setLsoaCode(String lsoaCode) {
        this.lsoaCode = lsoaCode;
    }

    private String lsoaName;

    @Basic
    @javax.persistence.Column(name = "lsoa_name", nullable = true, length = 255)
    public String getLsoaName() {
        return lsoaName;
    }

    public void setLsoaName(String lsoaName) {
        this.lsoaName = lsoaName;
    }

    private Integer imdRank;

    @Basic
    @javax.persistence.Column(name = "imd_rank", nullable = true)
    public Integer getImdRank() {
        return imdRank;
    }

    public void setImdRank(Integer imdRank) {
        this.imdRank = imdRank;
    }

    private Integer imdDecile;

    @Basic
    @javax.persistence.Column(name = "imd_decile", nullable = true)
    public Integer getImdDecile() {
        return imdDecile;
    }

    public void setImdDecile(Integer imdDecile) {
        this.imdDecile = imdDecile;
    }

    private Integer incomeRank;

    @Basic
    @javax.persistence.Column(name = "income_rank", nullable = true)
    public Integer getIncomeRank() {
        return incomeRank;
    }

    public void setIncomeRank(Integer incomeRank) {
        this.incomeRank = incomeRank;
    }

    private Integer incomeDecile;

    @Basic
    @javax.persistence.Column(name = "income_decile", nullable = true)
    public Integer getIncomeDecile() {
        return incomeDecile;
    }

    public void setIncomeDecile(Integer incomeDecile) {
        this.incomeDecile = incomeDecile;
    }

    private Integer employmentRank;

    @Basic
    @javax.persistence.Column(name = "employment_rank", nullable = true)
    public Integer getEmploymentRank() {
        return employmentRank;
    }

    public void setEmploymentRank(Integer employmentRank) {
        this.employmentRank = employmentRank;
    }

    private Integer employmentDecile;

    @Basic
    @javax.persistence.Column(name = "employment_decile", nullable = true)
    public Integer getEmploymentDecile() {
        return employmentDecile;
    }

    public void setEmploymentDecile(Integer employmentDecile) {
        this.employmentDecile = employmentDecile;
    }

    private Integer educationRank;

    @Basic
    @javax.persistence.Column(name = "education_rank", nullable = true)
    public Integer getEducationRank() {
        return educationRank;
    }

    public void setEducationRank(Integer educationRank) {
        this.educationRank = educationRank;
    }

    private Integer educationDecile;

    @Basic
    @javax.persistence.Column(name = "education_decile", nullable = true)
    public Integer getEducationDecile() {
        return educationDecile;
    }

    public void setEducationDecile(Integer educationDecile) {
        this.educationDecile = educationDecile;
    }

    private Integer healthRank;

    @Basic
    @javax.persistence.Column(name = "health_rank", nullable = true)
    public Integer getHealthRank() {
        return healthRank;
    }

    public void setHealthRank(Integer healthRank) {
        this.healthRank = healthRank;
    }

    private Integer healthDecile;

    @Basic
    @javax.persistence.Column(name = "health_decile", nullable = true)
    public Integer getHealthDecile() {
        return healthDecile;
    }

    public void setHealthDecile(Integer healthDecile) {
        this.healthDecile = healthDecile;
    }

    private Integer crimeRank;

    @Basic
    @javax.persistence.Column(name = "crime_rank", nullable = true)
    public Integer getCrimeRank() {
        return crimeRank;
    }

    public void setCrimeRank(Integer crimeRank) {
        this.crimeRank = crimeRank;
    }

    private Integer crimeDecile;

    @Basic
    @javax.persistence.Column(name = "crime_decile", nullable = true)
    public Integer getCrimeDecile() {
        return crimeDecile;
    }

    public void setCrimeDecile(Integer crimeDecile) {
        this.crimeDecile = crimeDecile;
    }

    private Integer housingAndServicesBarriersRank;

    @Basic
    @javax.persistence.Column(name = "housing_and_services_barriers_rank", nullable = true)
    public Integer getHousingAndServicesBarriersRank() {
        return housingAndServicesBarriersRank;
    }

    public void setHousingAndServicesBarriersRank(Integer housingAndServicesBarriersRank) {
        this.housingAndServicesBarriersRank = housingAndServicesBarriersRank;
    }

    private Integer housingAndServicesBarriersDecile;

    @Basic
    @javax.persistence.Column(name = "housing_and_services_barriers_decile", nullable = true)
    public Integer getHousingAndServicesBarriersDecile() {
        return housingAndServicesBarriersDecile;
    }

    public void setHousingAndServicesBarriersDecile(Integer housingAndServicesBarriersDecile) {
        this.housingAndServicesBarriersDecile = housingAndServicesBarriersDecile;
    }

    private Integer livingEnvironmentRank;

    @Basic
    @javax.persistence.Column(name = "living_environment_rank", nullable = true)
    public Integer getLivingEnvironmentRank() {
        return livingEnvironmentRank;
    }

    public void setLivingEnvironmentRank(Integer livingEnvironmentRank) {
        this.livingEnvironmentRank = livingEnvironmentRank;
    }

    private Integer livingEnvironmentDecile;

    @Basic
    @javax.persistence.Column(name = "living_environment_decile", nullable = true)
    public Integer getLivingEnvironmentDecile() {
        return livingEnvironmentDecile;
    }

    public void setLivingEnvironmentDecile(Integer livingEnvironmentDecile) {
        this.livingEnvironmentDecile = livingEnvironmentDecile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LsoaLookupEntity that = (LsoaLookupEntity) o;

        if (lsoaCode != null ? !lsoaCode.equals(that.lsoaCode) : that.lsoaCode != null) return false;
        if (lsoaName != null ? !lsoaName.equals(that.lsoaName) : that.lsoaName != null) return false;
        if (imdRank != null ? !imdRank.equals(that.imdRank) : that.imdRank != null) return false;
        if (imdDecile != null ? !imdDecile.equals(that.imdDecile) : that.imdDecile != null) return false;
        if (incomeRank != null ? !incomeRank.equals(that.incomeRank) : that.incomeRank != null) return false;
        if (incomeDecile != null ? !incomeDecile.equals(that.incomeDecile) : that.incomeDecile != null) return false;
        if (employmentRank != null ? !employmentRank.equals(that.employmentRank) : that.employmentRank != null)
            return false;
        if (employmentDecile != null ? !employmentDecile.equals(that.employmentDecile) : that.employmentDecile != null)
            return false;
        if (educationRank != null ? !educationRank.equals(that.educationRank) : that.educationRank != null)
            return false;
        if (educationDecile != null ? !educationDecile.equals(that.educationDecile) : that.educationDecile != null)
            return false;
        if (healthRank != null ? !healthRank.equals(that.healthRank) : that.healthRank != null) return false;
        if (healthDecile != null ? !healthDecile.equals(that.healthDecile) : that.healthDecile != null) return false;
        if (crimeRank != null ? !crimeRank.equals(that.crimeRank) : that.crimeRank != null) return false;
        if (crimeDecile != null ? !crimeDecile.equals(that.crimeDecile) : that.crimeDecile != null) return false;
        if (housingAndServicesBarriersRank != null ? !housingAndServicesBarriersRank.equals(that.housingAndServicesBarriersRank) : that.housingAndServicesBarriersRank != null)
            return false;
        if (housingAndServicesBarriersDecile != null ? !housingAndServicesBarriersDecile.equals(that.housingAndServicesBarriersDecile) : that.housingAndServicesBarriersDecile != null)
            return false;
        if (livingEnvironmentRank != null ? !livingEnvironmentRank.equals(that.livingEnvironmentRank) : that.livingEnvironmentRank != null)
            return false;
        if (livingEnvironmentDecile != null ? !livingEnvironmentDecile.equals(that.livingEnvironmentDecile) : that.livingEnvironmentDecile != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lsoaCode != null ? lsoaCode.hashCode() : 0;
        result = 31 * result + (lsoaName != null ? lsoaName.hashCode() : 0);
        result = 31 * result + (imdRank != null ? imdRank.hashCode() : 0);
        result = 31 * result + (imdDecile != null ? imdDecile.hashCode() : 0);
        result = 31 * result + (incomeRank != null ? incomeRank.hashCode() : 0);
        result = 31 * result + (incomeDecile != null ? incomeDecile.hashCode() : 0);
        result = 31 * result + (employmentRank != null ? employmentRank.hashCode() : 0);
        result = 31 * result + (employmentDecile != null ? employmentDecile.hashCode() : 0);
        result = 31 * result + (educationRank != null ? educationRank.hashCode() : 0);
        result = 31 * result + (educationDecile != null ? educationDecile.hashCode() : 0);
        result = 31 * result + (healthRank != null ? healthRank.hashCode() : 0);
        result = 31 * result + (healthDecile != null ? healthDecile.hashCode() : 0);
        result = 31 * result + (crimeRank != null ? crimeRank.hashCode() : 0);
        result = 31 * result + (crimeDecile != null ? crimeDecile.hashCode() : 0);
        result = 31 * result + (housingAndServicesBarriersRank != null ? housingAndServicesBarriersRank.hashCode() : 0);
        result = 31 * result + (housingAndServicesBarriersDecile != null ? housingAndServicesBarriersDecile.hashCode() : 0);
        result = 31 * result + (livingEnvironmentRank != null ? livingEnvironmentRank.hashCode() : 0);
        result = 31 * result + (livingEnvironmentDecile != null ? livingEnvironmentDecile.hashCode() : 0);
        return result;
    }

    public static List<Object[]> getLsoaCodes() throws Exception {
        String where = "select lsoaCode, lsoaName " +
                "from LsoaLookupEntity";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        List<Object[]> ent = entityManager.createQuery(where)
                .setMaxResults(100)
                .getResultList();

        entityManager.close();

        return ent;

    }
}
