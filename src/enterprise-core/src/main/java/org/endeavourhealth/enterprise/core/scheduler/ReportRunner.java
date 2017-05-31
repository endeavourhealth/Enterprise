package org.endeavourhealth.enterprise.core.scheduler;

import org.endeavourhealth.enterprise.core.database.ReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.TimerTask;

public class ReportRunner extends TimerTask implements ServletContextListener {
	private static final Logger LOG = LoggerFactory.getLogger(ReportRunner.class);
	private static final String SCHEDULED_TASK_NAME = "ReportRunner";
	private static final long INTERVAL_IN_SECONDS = 60;

	private ReportManager reportManager = new ReportManager();

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		// Get polling config
		ScheduledTask reportRunnerTask = new ScheduledTask(SCHEDULED_TASK_NAME, this, INTERVAL_IN_SECONDS);
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
		try {
			this.reportManager.runScheduledReports();
		} catch (Exception e) {
			LOG.error("Error running schedule report(s)", e);
		}
	}
}
