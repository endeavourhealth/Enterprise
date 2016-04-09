package org.endeavourhealth.enterprise.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class JobProgressTracker {

    private final Set<Long> allWorkerItemStartIds = new HashSet<>();
    private final Set<UUID> allProcessorNodes = new HashSet<>();

    private final Set<Long> remainingWorkerItemStartIds = new HashSet<>();
    private final Set<UUID> activeProcessorNodes = new HashSet<>();

    public synchronized void registerWorkerItemStartId(long minimumId) {
        allWorkerItemStartIds.add(minimumId);
        remainingWorkerItemStartIds.add(minimumId);
    }

    public int getTotalNumberOfBatches() {
        return allWorkerItemStartIds.size();
    }

    public synchronized void receivedWorkItemComplete(long startId) throws Exception {

        if (!allWorkerItemStartIds.contains(startId))
            throw new Exception("Received WorkItemComplete for invalid StartId " + startId);

        if (!remainingWorkerItemStartIds.contains(startId))
            throw new Exception("Received WorkItemComplete for already processed StartId " + startId);

        remainingWorkerItemStartIds.remove(startId);
    }

    public synchronized void receivedProcessorNodeStartedMessage(UUID processorUuid) throws Exception {
        if (allProcessorNodes.contains(processorUuid))
            throw new Exception("Received start message for the same processor: " + processorUuid);

        allProcessorNodes.add(processorUuid);
        activeProcessorNodes.add(processorUuid);
    }

    public synchronized void receivedProcessorNodeCompleteMessage(UUID processorUuid) throws Exception {

        if (!allProcessorNodes.contains(processorUuid))
            throw new Exception("Received ProcessorNodeComplete for invalid processor " + processorUuid);

        if (!activeProcessorNodes.contains(processorUuid))
            throw new Exception("Received ProcessorNodeComplete for non-active processor: " + processorUuid);

        activeProcessorNodes.remove(processorUuid);

        if (activeProcessorNodes.isEmpty() && !remainingWorkerItemStartIds.isEmpty())
            throw new Exception("Received last ProcessorNodeComplete message but there are still " + allWorkerItemStartIds + " start IDs remaining.");
    }

    public synchronized boolean isComplete() {
        return activeProcessorNodes.isEmpty() && remainingWorkerItemStartIds.isEmpty();
    }
}
