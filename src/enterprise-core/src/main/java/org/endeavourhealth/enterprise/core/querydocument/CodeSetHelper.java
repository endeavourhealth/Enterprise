package org.endeavourhealth.enterprise.core.querydocument;

import com.sun.org.apache.bcel.internal.classfile.Code;
import org.endeavourhealth.enterprise.core.database.PersistenceManager;
import org.endeavourhealth.enterprise.core.database.models.CodeSetEntity;
import org.endeavourhealth.enterprise.core.database.models.data.ConceptEntity;
import org.endeavourhealth.enterprise.core.querydocument.models.CodeSet;
import org.endeavourhealth.enterprise.core.querydocument.models.CodeSetValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeSetHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CodeSetHelper.class);

    public static void populateCodeSet(CodeSet codeSet, String itemUuid) throws Exception {

        Set<Long> snomedConceptIds = findAllCodes(codeSet);
        updateCodeSetTable(snomedConceptIds, itemUuid);
    }

    private static void updateCodeSetTable(Set<Long> snomedConceptIds, String itemUuid) {

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseAdmin();

        entityManager.getTransaction().begin();

        //retrieve the existing code sets from the DB for our item
        String sql = "select c"
                + " from "
                + " CodeSetEntity c"
                + " where c.itemUuid = :itemUuid";

        Query query = entityManager.createQuery(sql, CodeSetEntity.class)
                .setParameter("itemUuid", itemUuid);

        List<CodeSetEntity> list = query.getResultList();

        //go through the existing code sets records and see which can stay and which should be deleted
        for (CodeSetEntity entity: list) {
            Long snomedConceptId = entity.getSnomedConceptId();

            if (snomedConceptIds.contains(snomedConceptId)) {
                snomedConceptIds.remove(snomedConceptId);

            } else {
                entityManager.remove(entity);
            }
        }

        //any snomed concepts remaining need new code set records creating
        for (Long snomedConceptId: snomedConceptIds) {
            CodeSetEntity entity = new CodeSetEntity();
            entity.setItemUuid(itemUuid);
            entity.setSnomedConceptId(snomedConceptId);
            entityManager.persist(entity);
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }



    private static Set<Long> findAllCodes(CodeSet codeSet) throws Exception {
        Set<Long> ret = new HashSet<>();

        EntityManager entityManager = PersistenceManager.INSTANCE.getEmEnterpriseData();

        for (CodeSetValue item: codeSet.getCodeSetValue()) {
            findAllCodes(item, ret, entityManager);
        }

        entityManager.close();

        return ret;
    }


    private static void findAllCodes(CodeSetValue item, Set<Long> codes, EntityManager entityManager) {
        String code = item.getCode();
        long snomedConcept = Long.parseLong(code);
        boolean includeChildren = item.isIncludeChildren();

        codes.add(new Long(snomedConcept));

        if (includeChildren) {
            Set<Long> children = new HashSet<>();
            populateAllChildCodes(snomedConcept, children, entityManager);

            Set<Long> exclusions = new HashSet<>();
            for (CodeSetValue exclusion: item.getExclusion()) {
                findAllCodes(exclusion, exclusions, entityManager);
            }

            children.removeAll(exclusions);

            codes.addAll(children);
        }
    }

    private static void populateAllChildCodes(long snomedConceptId, Set<Long> codes, EntityManager entityManager) {

        String sql = "SELECT DISTINCT c.conceptId"
                   + " FROM ConceptEntity c"
                   + " WHERE c.parentTypeConceptId = :conceptId";

        Query query = entityManager.createQuery(sql)
                .setParameter("conceptId", "" + snomedConceptId);

        List<String> list = query.getResultList();

        for (String s: list) {
            Long childSnomedConceptId = Long.valueOf(s);

            //only add and recurse if it's not already in the set, so cyclic references (if there are any)
            //shouldn't cause us any problems
            if (!codes.contains(childSnomedConceptId)) {
                codes.add(childSnomedConceptId);
                populateAllChildCodes(childSnomedConceptId, codes, entityManager);
            }
        }
    }
}
