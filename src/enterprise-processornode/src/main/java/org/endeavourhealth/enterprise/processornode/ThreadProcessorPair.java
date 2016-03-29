package org.endeavourhealth.enterprise.processornode;

class ThreadProcessorPair {

    private final Thread thread;
    private final EngineProcessorWrapper processor;

    public ThreadProcessorPair(Thread thread, EngineProcessorWrapper processor) {
        this.thread = thread;
        this.processor = processor;
    }

    public Thread getThread() {
        return thread;
    }

    public EngineProcessorWrapper getProcessor() {
        return processor;
    }
}
