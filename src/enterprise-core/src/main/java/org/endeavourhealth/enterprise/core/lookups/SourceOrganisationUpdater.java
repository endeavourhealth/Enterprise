package org.endeavourhealth.enterprise.core.lookups;

import org.endeavourhealth.enterprise.core.database.models.SourceorganisationEntity;

import java.util.*;

public abstract class SourceOrganisationUpdater {

    public static void updateSourceOrganisations(List<SourceOrganisation> sourceOrganisations) throws Exception {

        HashMap<String, SourceOrganisation> hm = new HashMap<>();
        for (SourceOrganisation org: sourceOrganisations) {
            String odsCode = org.getOdsCode();
            if (hm.containsKey(odsCode)) {
                throw new RuntimeException("Duplicate ODS code " + odsCode);
            }
            hm.put(odsCode, org);
        }

        //update records already on the DB
        List<SourceorganisationEntity> dbSourcesOrgs = SourceorganisationEntity.retrieveAll(true);
        List<SourceorganisationEntity> toSave = new ArrayList<>();

        for (SourceorganisationEntity dbSourceOrg: dbSourcesOrgs) {
            String odsCode = dbSourceOrg.getOdscode();
            SourceOrganisation sourceOrganisation = hm.remove(odsCode);

            if (applyNewData(dbSourceOrg, sourceOrganisation)) {
                toSave.add(dbSourceOrg);
            }
        }

        //create new records
        Iterator it = hm.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, SourceOrganisation> entry = (Map.Entry)it.next();
            String odsCode = entry.getKey();
            SourceOrganisation sourceOrganisation = entry.getValue();

            SourceorganisationEntity dbSourceOrg = new SourceorganisationEntity();
            dbSourceOrg.setOdscode(odsCode);
            applyNewData(dbSourceOrg, sourceOrganisation);
            toSave.add(dbSourceOrg);
        }

        //DatabaseManager.db().writeEntities(toSave);
    }

    private static boolean applyNewData(SourceorganisationEntity db, SourceOrganisation sourceOrganisation) {
        boolean changed = false;

        if (sourceOrganisation == null) {

            if (db.getIsreferencedbydata()) {
                db.setIsreferencedbydata(false);
                changed = true;
            }

        } else {

            if (!db.getIsreferencedbydata()) {
                db.setIsreferencedbydata(true);
                changed = true;
            }

            if (db.getName() == null || !db.getName().equals(sourceOrganisation.getName())) {
                db.setName(sourceOrganisation.getName());
                changed = true;
            }
        }

        return changed;
    }
}
