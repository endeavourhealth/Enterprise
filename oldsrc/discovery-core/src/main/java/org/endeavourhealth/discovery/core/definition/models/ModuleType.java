package org.endeavourhealth.discovery.core.definition.models;

import java.util.HashMap;
import java.util.Map;

public enum ModuleType {

    LIBRARY(0),
    SEARCHES(1),
    REPORTS(2);

    private int value;

    private ModuleType(int value){
        this.value = value;
    }

    private static final Map<Integer, ModuleType> lookup = new HashMap<>();

    static {
        for (ModuleType d : ModuleType.values())
            lookup.put(d.getValue(), d);
    }

    public static ModuleType get(int value) {
        return lookup.get(value);
    }

    public int getValue() {
        return value;
    }
};

