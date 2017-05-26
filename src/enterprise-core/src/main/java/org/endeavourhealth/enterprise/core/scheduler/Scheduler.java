package org.endeavourhealth.enterprise.core.scheduler;

import java.util.HashMap;
import java.util.Map;

public enum Scheduler {
	INSTANCE;

	Map<String, ScheduledTask> tasks = new HashMap<>();

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

}
