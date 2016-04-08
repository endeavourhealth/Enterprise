package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.controller.configuration.models.Configuration;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

class SchedulerWrapper {

    private static Scheduler scheduler;

    public SchedulerWrapper(Configuration configuration) throws SchedulerException {

        SetupScheduler();

        JobDetail job = JobBuilder.newJob(SchedulerExecutionJob.class).build();

        Trigger trigger = CreateMainJobTrigger(configuration);
        scheduler.scheduleJob(job, trigger);
    }

    public void shutdown() throws SchedulerException {
        scheduler.shutdown(false);
    }

    private static void SetupScheduler() throws SchedulerException {
        Properties props = new Properties();
        props.setProperty(StdSchedulerFactory.PROP_SCHED_SKIP_UPDATE_CHECK, "true");
        props.setProperty("org.quartz.threadPool.threadCount", "4");

        SchedulerFactory sf = new StdSchedulerFactory(props);
        scheduler = sf.getScheduler();
    }

    private static Trigger CreateMainJobTrigger(Configuration configuration) {

        Trigger trigger;

        if (configuration.getDebugging().isStartImmediately()) {
            trigger = TriggerBuilder.newTrigger()
                    .startNow()
                    .build();
        } else {

            //Eg, "0 0 12 * * ?" = 12 noon each day
            String cron = "0 0 " + configuration.getExecutionSchedule().getStartTimeInHours() + " * * ?";

            trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .startNow()
                    .build();
        }

        return trigger;
    }

    public void startDelayed(int timeInSeconds) throws SchedulerException {
        scheduler.startDelayed(timeInSeconds);
    }
}
