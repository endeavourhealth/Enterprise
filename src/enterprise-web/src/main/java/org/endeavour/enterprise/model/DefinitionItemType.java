package org.endeavour.enterprise.model;

public enum DefinitionItemType {
    ReportFolder(0),
    Report(1),
    Query(2),
    Test(3),
    Datasource(4),
    CodeSet(5),
    ListOutput(6),
    LibraryFolder(7);

    private int value;

    DefinitionItemType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DefinitionItemType get(int value) {
        for (DefinitionItemType e : DefinitionItemType.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null; // not found
    }
}
