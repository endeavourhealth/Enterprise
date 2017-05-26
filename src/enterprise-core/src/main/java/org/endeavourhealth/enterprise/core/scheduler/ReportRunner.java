package org.endeavourhealth.enterprise.core.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.TimerTask;

public class ReportRunner extends TimerTask implements ServletContextListener {
	private static final Logger LOG = LoggerFactory.getLogger(ReportRunner.class);
	private static final String SCHEDULED_TASK_NAME = "ReportRunner";

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
			// Get polling config
		ScheduledTask reportRunnerTask = new ScheduledTask(SCHEDULED_TASK_NAME, 60, this);
		Scheduler.INSTANCE.add(reportRunnerTask);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		Scheduler.INSTANCE.delete(SCHEDULED_TASK_NAME);
	}

	@Override
	public void run() {
		LOG.debug("Report runner tick!");
		// Check DB for reports due to be run

		// Run them!

		// Mark as run
	}
}
