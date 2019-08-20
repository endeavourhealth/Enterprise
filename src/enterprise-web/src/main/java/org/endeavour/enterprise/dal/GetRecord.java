package org.endeavour.enterprise.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;

public class GetRecord {

    private static final Logger LOG = LoggerFactory.getLogger(GetRecord.class);

    public static List<Object[]> getPatient(String id) {

        List<Object[]> patients = null;

        try {
            EntityManager entityManager = PersistenceManager.getEntityManager();

            String sql = "select p.first_names,p.last_name,p.date_of_birth,a.address_line_1,a.city,a.postcode,p.title " +
                    "from subscriber_pi.patient p "+
                    "join subscriber_pi.patient_address a on a.id = p.current_address_id "+
                    "where p.id = :id";

            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("id", id);
            patients = query.getResultList();

        } catch (Exception ex) {
            System.out.println(ex);
            LOG.error("getPatient DAL error: ", ex);
        }

        return patients;
    }
}
