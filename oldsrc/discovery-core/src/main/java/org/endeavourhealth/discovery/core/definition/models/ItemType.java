package org.endeavourhealth.discovery.core.definition.models;

import java.util.HashMap;
import java.util.Map;

public enum ItemType {

    FOLDER(0),
    REPORT(1),
    QUERY(2),
    TEST(3),
    DATASOURCE(4),
    CODESET(5);

    private int value;

    private ItemType(int value){
        this.value = value;
    }

    private static final Map<Integer, ItemType> lookup = new HashMap<>();

    static {
        for (ItemType d : ItemType.values())
            lookup.put(d.getValue(), d);
    }

    public static ItemType get(int value) {
        return lookup.get(value);
    }

    public int getValue() {
        return value;
    }
};

