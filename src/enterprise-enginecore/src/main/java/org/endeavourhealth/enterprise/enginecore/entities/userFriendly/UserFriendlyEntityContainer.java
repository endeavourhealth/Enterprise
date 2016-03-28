package org.endeavourhealth.enterprise.enginecore.entities.userFriendly;

import java.util.ArrayList;
import java.util.List;

public class UserFriendlyEntityContainer {
    private List<UserFriendlyEntity> entities = new ArrayList<UserFriendlyEntity>();

    public List<UserFriendlyEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<UserFriendlyEntity> entities) {
        this.entities = entities;
    }
}
