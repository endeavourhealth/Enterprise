package org.endeavourhealth.enterprise.processornode;

import java.util.concurrent.Semaphore;

///Because the locking and start method is called outside of this class, then using synchronised will
///easily end up with deadlocks.
public class ShortLivedThreadWrapper <T extends Runnable> {

    private final T item;
    private Thread thread;
    private final Semaphore lock = new Semaphore(1);
    private boolean shutdown = false;

    public ShortLivedThreadWrapper(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void lock() throws InterruptedException {
        lock.acquire();
    }

    public boolean tryLock() {

        if (shutdown)
            return false;

        boolean acquired = lock.tryAcquire();

        if (!acquired)
            return false;

        if (shutdown) {
            lock.release();
            return false;
        } else {
            return true;
        }
    }

    public void unlock() {
        thread = null;
        lock.release();
    }

    public void shutdown() throws InterruptedException {
        shutdown = true;

        lock.acquire();  //you can only acquire it while the thread is not running

        if (thread != null)
            thread.join();

        lock.release();
    }

    public void start() {
        thread = new Thread(getItem());
        thread.start();
    }
}
