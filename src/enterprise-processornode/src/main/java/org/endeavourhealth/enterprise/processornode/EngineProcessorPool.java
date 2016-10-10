package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.engine.EngineApi;
import org.endeavourhealth.enterprise.engine.Processor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

class EngineProcessorPool {

    //Apparently the fastest queue
    private final ArrayDeque<Processor> queue = new ArrayDeque<>();
    private final EngineApi engineApi;
    private final int maximumProcessorsAllowed;
    private final List<Processor> allItemsCreated = new ArrayList<>();

    public EngineProcessorPool(EngineApi engineApi, int maximumProcessorsAllowed) {

        this.engineApi = engineApi;
        this.maximumProcessorsAllowed = maximumProcessorsAllowed;
    }

    public synchronized Processor acquire() throws Exception {

        if (queue.isEmpty())
            queue.add(createProcessor());

        return queue.pop();
    }

    private Processor createProcessor() throws Exception {

        Processor processor = engineApi.createProcessor();

        allItemsCreated.add(processor);

        if (allItemsCreated.size() > maximumProcessorsAllowed)
            throw new Exception("Exceeded maximum number of processor objects created");  //If it hits this then the items are not being recycled

        return processor;
    }

    public void recycle(Processor resource) {

        cleanProcessor(resource);
        pushItem(resource);
    }

    private void cleanProcessor(Processor resource) {
    }

    private synchronized void pushItem(Processor resource) {
        queue.push(resource);
    }

    public List<Processor> getAllProcessors() {
        return allItemsCreated;
    }
}
