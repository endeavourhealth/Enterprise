package org.endeavourhealth.enterprise.core.scheduler;

import java.util.Timer;
import java.util.TimerTask;

public class ScheduledTask {
	private String name;
	private Timer timer;
	private TimerTask task;
	private long intevalInSeconds;

	public ScheduledTask(String name, TimerTask task, long intervalInSeconds) {
		this.name = name;
		this.task = task;
		this.timer = new Timer();
		this.intevalInSeconds = intervalInSeconds;
	}

	public void start() {
		this.timer.schedule(this.task, this.intevalInSeconds * 1000, this.intevalInSeconds * 1000);
		}

	public void stop() {
		this.timer.cancel();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
