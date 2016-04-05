package org.endeavourhealth.enterprise.engine.testhelpers;

import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainerPool;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataEntity;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataField;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

public class DataContainerBuilder {
    private final DataContainer dataContainer;
    private int currentDataEntityId = 0;

    public DataContainerBuilder(EntityMapWrapper.EntityMap entityMap) {
        dataContainer = DataContainerPool.createDataContainer(entityMap.getEntities());
    }

    public DataContainerBuilder nextEntity() {
        currentDataEntityId++;
        return this;
    }

    public DataContainerBuilder addRow(Object... values) {
        DataEntity dataEntity = dataContainer.getDataEntities().get(currentDataEntityId);
        int index = 0;

        for (Object value: values) {
            dataEntity.getFields().get(index).add(value);
            index++;
        }

        return this;
    }

    public DataContainer build() {
        levelFields();
        return dataContainer;
    }

    private void levelFields() {

        for (DataEntity dataEntity : dataContainer.getDataEntities()) {

            int max = 0;

            for (DataField dataField : dataEntity.getFields()) {
                if (dataField.size() > max)
                    max = dataField.size();
            }

            while (dataEntity.getFields().get(0).size() < max)
                dataEntity.getFields().get(0).add(null);
        }
    }
}
