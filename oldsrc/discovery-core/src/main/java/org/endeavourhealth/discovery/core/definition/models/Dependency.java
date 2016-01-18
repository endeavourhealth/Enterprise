package org.endeavourhealth.discovery.core.definition.models;

import java.util.UUID;

public class Dependency {
    private final UUID dependsOnUuid;
    private final boolean drivesUi;

    public Dependency(UUID dependsOnUuid, boolean drivesUi) {
        this.dependsOnUuid = dependsOnUuid;
        this.drivesUi = drivesUi;
    }

    public UUID getDependsOnUuid() {
        return dependsOnUuid;
    }

    public boolean isDrivesUi() {
        return drivesUi;
    }
}
