package org.endeavourhealth.enterprise.enginecore.entities.model;

import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;
import org.endeavourhealth.enterprise.core.entitymap.models.Field;

import java.util.ArrayDeque;

/*
The processor will load up many patients.  That means loading up many datacontainers.  Each container contains quite a few lists which are
quite expensive on memory.  This pool class means we can reuse datacontainers.
 */
public class DataContainerPool {

    //Apparently the fastest queue
    private final ArrayDeque<DataContainer> queue = new ArrayDeque<>();
    private final int maximumItemsToCreate;
    private final EntityMapWrapper.EntityMap entityMapWrapper;
    private int itemsCreated;

    public DataContainerPool(int dataItemBufferSize, EntityMapWrapper.EntityMap entityMapWrapper) {
        this.entityMapWrapper = entityMapWrapper;
        maximumItemsToCreate = 3 * dataItemBufferSize;
    }

    public synchronized DataContainer acquire(long populationId) throws Exception {

        if (queue.isEmpty())
            queue.add(createDataContainer(populationId));

        return queue.pop();
    }

    private DataContainer createDataContainer(long populationId) throws Exception {

        itemsCreated++;

        if (itemsCreated > maximumItemsToCreate)
            throw new Exception("Maximum created items exceeded");  //If it hits this then the items are not being recycled

        DataContainer dataContainer = new DataContainer();
        dataContainer.setId(populationId);

        for (EntityMapWrapper.Entity entity : entityMapWrapper.getEntities()) {

            DataEntity dataEntity = new DataEntity();

            for (Field field : entity.getSource().getField()) {
                dataEntity.getFields().add(new DataField());
            }

            dataContainer.getDataEntities().add(dataEntity);
        }

        return dataContainer;
    }

    public void recycle(DataContainer resource) {

        cleanDataContainer(resource);
        pushItem(resource);
    }

    private void cleanDataContainer(DataContainer resource) {
        resource.setId(0);

        for (DataEntity entity : resource.getDataEntities()) {
            for (DataField field : entity.getFields()) {
                field.clear();
            }
        }
    }

    private synchronized void pushItem(DataContainer resource) {
        queue.push(resource);
    }
}
