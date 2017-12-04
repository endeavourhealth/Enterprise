package org.endeavourhealth.enterprise.core.terminology;

import org.endeavourhealth.enterprise.core.querydocument.models.CodeSet;
import org.endeavourhealth.enterprise.core.querydocument.models.CodeSetValue;
import org.endeavourhealth.enterprise.core.terminology.termlex.Termlex;
import org.endeavourhealth.enterprise.core.terminology.termlex.TermlexWeb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Test script at https://gitlab.com/noesisinformatica/termlex-tutorial/blob/master/src/etc/termlex-test.sh
 */
abstract class Snomed {

    private static final Logger LOG = LoggerFactory.getLogger(Snomed.class);
    private static HashMap<String, List<String>> cachedDescendants = new HashMap<>();

    static Termlex termlex = new TermlexWeb();

    public static HashSet<String> enumerateConcepts(CodeSet codeSet) {

        HashSet<String> includedConcepts = new HashSet<>();
        HashSet<String> excludedConcepts = new HashSet<>();

        List<CodeSetValue> values = codeSet.getCodeSetValue();
        for (CodeSetValue value: values) {
            enumerateCodeSetValue(value, includedConcepts, excludedConcepts);
        }

        includedConcepts.removeAll(excludedConcepts);
        return includedConcepts;
    }

    private static void enumerateCodeSetValue(CodeSetValue value, HashSet<String> included, HashSet<String> excluded) {
        String concept = value.getCode();
        included.add(concept);
        if (value.isIncludeChildren()) {
            List<String> descendants = getDescendantsUsingCache(concept);
            included.addAll(descendants);
        }

        HashSet<String> exclusionsOfExclusions = new HashSet<>();

        List<CodeSetValue> exclusions = value.getExclusion();
        for (CodeSetValue exclusion: exclusions) {
            enumerateCodeSetValue(exclusion, excluded, exclusionsOfExclusions);
        }

        if (exclusionsOfExclusions.size() > 0) {
            throw new RuntimeException("CodeSet exclusions of exclusions not supports");
        }
    }

    /**
     * returns descendant codes, using cache if possible
     */
    public static List<String> getDescendantsUsingCache(String conceptCode) {
        List<String> ret = null;

        synchronized (cachedDescendants) {
            ret = cachedDescendants.get(conceptCode);
        }

        if (ret == null) {

            ret = termlex.getDescendants(conceptCode);

            synchronized (cachedDescendants) {
                cachedDescendants.put(conceptCode, ret);
            }
        }

        return ret;
    }

    /**
     * for quick testing
     */
    /*public static void main(String[] args) {

        String concept = null;
        if (args.length > 0) {
            concept = args[0];
        } else {
            concept = javax.swing.JOptionPane.showInputDialog(null, "Concept ID", "195967001"); //asthma
            if (concept == null) {
                return;
            }
        }

        //test basic Termlex functions
        LOG.debug("Preferred term of " + concept + ": " + getPreferredTerm(concept));

        LOG.debug("Children of " + concept);
        List<String> v = getChildren(concept);
        dumpWithDesc(v);

        LOG.debug("Descendants of " + concept);
        v = getDescendants(concept);
        dumpWithDesc(v);

        //test processing of QueryDocument objects
        CodeSet cs = new CodeSet();

        CodeSetValue value = new CodeSetValue();
        value.setCode(concept);
        value.setIncludeChildren(true);
        cs.getCodeSetValue().add(value);

        CodeSetValue exclusion = new CodeSetValue();
        exclusion.setCode("57607007"); //Occupational asthma
        exclusion.setIncludeChildren(true);
        value.getExclusion().add(exclusion);

        exclusion = new CodeSetValue();
        exclusion.setCode("41553006"); //Detergent asthma
        exclusion.setIncludeChildren(false);
        value.getExclusion().add(exclusion);

        value = new CodeSetValue();
        value.setCode("22298006"); //MI
        value.setIncludeChildren(false);
        cs.getCodeSetValue().add(value);

        HashSet<String> hs = enumerateConcepts(cs);

        LOG.debug("Enumeration of " + concept);
        dumpWithDesc(hs);
    }
    private static void dumpWithDesc(Iterable<? extends CharSequence> elements) {

        if (elements == null) {
            LOG.debug("\tNULL");
            return;
        }

        List<String> v = new ArrayList<>();
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            String concept = (String)it.next();
            String term = getPreferredTerm(concept);
            v.add("\t" + term + ": " + concept);
        }

        LOG.debug("\t==" + v.size() + " results");

        String[] arr = v.toArray(new String[0]);
        Arrays.sort(arr);

        for (String s: arr) {
            LOG.debug(s);
        }
    }*/

}
