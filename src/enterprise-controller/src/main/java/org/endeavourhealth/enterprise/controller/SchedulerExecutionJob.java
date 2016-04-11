package org.endeavourhealth.enterprise.controller;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class SchedulerExecutionJob implements Job {

    private final static Logger logger = LoggerFactory.getLogger(SchedulerExecutionJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            ControllerMainSingleton.requestStartNewJob();
        }
        catch (Exception e) {

            logger.error("Error running scheduler job", e);

            JobExecutionException e2 = new JobExecutionException(e);
            //e2.setUnscheduleAllTriggers(true);
            throw e2;
        }
    }
}
