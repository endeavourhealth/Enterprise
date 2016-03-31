package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

class ProcessorThreadPoolExecutor extends ThreadPoolExecutor {

    public interface IBatchComplete {
        void batchComplete();
    }

    private final int minimumBufferSize;
    private final IBatchComplete batchCompleteCallback;
    private final ExecutionContext executionContext;
    private final DataSourceRetriever dataSourceRetriever;

    private final Semaphore getMoreDataLock = new Semaphore(1);
    private final Semaphore hasRaisedBatchCompleteLock = new Semaphore(1);
    private boolean hasRaisedBatchComplete;
    private boolean addingItems;

    //This is a thread safe hashset.  I love Java :)
    private final Set<Runnable> jobsToRun = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ProcessorThreadPoolExecutor(
            int numberOfThreads,
            int minimumBufferSize,
            DataSourceRetriever dataSourceRetriever,
            IBatchComplete batchCompleteCallback,
            ExecutionContext executionContext){
        super(numberOfThreads, numberOfThreads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        this.minimumBufferSize = minimumBufferSize;
        this.dataSourceRetriever = dataSourceRetriever;
        this.batchCompleteCallback = batchCompleteCallback;
        this.executionContext = executionContext;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        try {
            jobsToRun.remove(r);

            if (super.getQueue().size() < minimumBufferSize) {
                tryGetMoreItemsToProcess();

                if (dataSourceRetriever.isBatchComplete() && !addingItems) {
                    if (jobsToRun.isEmpty()) {

                        if (hasRaisedBatchCompleteLock.tryAcquire()) {
                            if (!hasRaisedBatchComplete) {
                                hasRaisedBatchComplete = true;
                                batchCompleteCallback.batchComplete();
                            }

                            hasRaisedBatchCompleteLock.release();
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setNextBatch(long minimumId, long maximumId) throws Exception {
        if (!super.getQueue().isEmpty())
            throw new Exception("Queue is not empty");

        hasRaisedBatchComplete = false;
        dataSourceRetriever.setBatch(minimumId, maximumId);
        tryGetMoreItemsToProcess();
    }

    private void tryGetMoreItemsToProcess() throws Exception {
        boolean acquired = getMoreDataLock.tryAcquire();

        if (!acquired)
            return;

        addingItems = true;

        Collection<DataContainer> dataContainers = dataSourceRetriever.getRecords();

        if (dataContainers != null) {

            for (DataContainer dataContainer : dataContainers) {
                RunnableItem runnableItem = new RunnableItem(dataContainer, executionContext);
                jobsToRun.add(runnableItem);
                this.execute(runnableItem);  //only adds it to the queue
            }
        }

        addingItems = false;

        getMoreDataLock.release();
    }
}
