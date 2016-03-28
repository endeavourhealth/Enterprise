package org.endeavourhealth.enterprise.controller;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class SchedulerExecutionJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            ControllerMainSingleton.requestStartNewJob();
        }
        catch (Exception e) {
            JobExecutionException e2 = new JobExecutionException(e);
            //e2.setUnscheduleAllTriggers(true);
            throw e2;
        }
    }
}
