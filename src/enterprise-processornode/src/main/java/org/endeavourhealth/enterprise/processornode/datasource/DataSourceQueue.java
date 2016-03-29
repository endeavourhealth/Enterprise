package org.endeavourhealth.enterprise.processornode.datasource;

import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;

import java.util.LinkedList;
import java.util.Queue;

class DataSourceQueue {

    private Queue<DataContainer> queue = new LinkedList<DataContainer>();

    public synchronized void add(DataContainer container) {
        queue.add(container);
    }

    public synchronized DataContainer poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}
