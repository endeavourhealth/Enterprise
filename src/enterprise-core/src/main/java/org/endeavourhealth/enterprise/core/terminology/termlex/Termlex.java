package org.endeavourhealth.enterprise.core.terminology.termlex;

import java.util.List;

public interface Termlex {
    List<String> getDescendants(String conceptCode);
    List<String> getChildren(String conceptCode);
    String getPreferredTerm(String conceptCode);
}
