package org.endeavourhealth.enterprise.engine;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultCounter {
    //Key = org ODS code
    private final Map<String, AtomicInteger> organisationCounter;

    public ResultCounter(Set<String> organisationIdentifiers) {
        organisationCounter = new ConcurrentHashMap<>(organisationIdentifiers.size());

        for (String orgId: organisationIdentifiers) {
            organisationCounter.put(orgId, new AtomicInteger(0));
        }
    }

    public void recordResult(String organisationIdentifier) {
        organisationCounter.get(organisationIdentifier).incrementAndGet();
    }

    public Map<String, AtomicInteger> getResults() {
        return organisationCounter;
    }
}
