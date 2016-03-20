package org.endeavourhealth.enterprise.core.terminology;

import org.endeavourhealth.enterprise.core.querydocument.models.CodeSet;
import org.endeavourhealth.enterprise.core.querydocument.models.CodingSystem;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Drew on 19/03/2016.
 */
public final class TerminologyService {

    public static HashSet<String> enumerateConcepts(CodeSet codeSet) {

        CodingSystem codingSystem = codeSet.getCodingSystem();
        if (codingSystem == CodingSystem.EMIS_READ_V_2) {
            throw new RuntimeException("CodingSystem " + codingSystem + " not supported");
        } else if (codingSystem == CodingSystem.DMD) {
            throw new RuntimeException("CodingSystem " + codingSystem + " not supported");
        } else if (codingSystem == CodingSystem.SNOMED_CT) {
            return Snomed.enumerateConcepts(codeSet);
        } else if (codingSystem == CodingSystem.CTV_3) {
            throw new RuntimeException("CodingSystem " + codingSystem + " not supported");
        } else {
            throw new RuntimeException("Unknown codingSystem " + codingSystem);
        }
    }
}
