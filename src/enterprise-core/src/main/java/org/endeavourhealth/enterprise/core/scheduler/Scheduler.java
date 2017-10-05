package org.endeavourhealth.enterprise.core.scheduler;

import org.endeavourhealth.coreui.framework.ContextShutdownHook;
import org.endeavourhealth.coreui.framework.StartupConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Scheduler implements ContextShutdownHook {
    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);
    public static final Scheduler INSTANCE = new Scheduler();

	Map<String, ScheduledTask> tasks = new HashMap<>();

	private Scheduler() {
        StartupConfig.registerShutdownHook("Scheduler", this);
    }

	public void add(ScheduledTask task) {
		if (this.tasks.containsKey(task.getName()))
			throw new IllegalStateException("A task with that name already exists.");

		tasks.put(task.getName(), task);
		task.start();
	}

	public ScheduledTask get(String name) {
		return this.tasks.get(name);
	}

	public void delete(String name) {
		ScheduledTask task = this.tasks.get(name);
		if (task==null)
			return;
		tasks.remove(name);
		task.stop();
	}

    @Override
    protected void finalize() throws Throwable {
        System.out.println("In finalize block");
        super.finalize();
    }

    @Override
    public void contextShutdown() {
        for (Map.Entry<String, ScheduledTask> entry : tasks.entrySet()) {
            LOG.trace("\tStopping task [" + entry.getKey() + "]");
            entry.getValue().stop();
        }
    }
}
