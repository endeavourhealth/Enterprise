package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.controller.configuration.models.DatabaseConnection;
import org.endeavourhealth.enterprise.controller.configuration.models.MessageQueuing;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class PreviousJobCleanup {
    private final static Logger logger = LoggerFactory.getLogger(PreviousJobCleanup.class);

    public static void clearPreviousJobs(DatabaseConnection coreDatabase, MessageQueuing messageQueuingConfig) throws Exception {

        markExistingJobsAsFailed();
        removeItemsFromJobProcessorResult();
//        removePreviousWorkerQueues(messageQueuingConfig);

    }

    private static void removeItemsFromJobProcessorResult() {

    }

//    private static void removePreviousWorkerQueues(MessageQueuing messageQueuingConfig) throws Exception {
//        QueueConnectionProperties queueConnectionProperties = ConfigurationAPI.convertConnection(messageQueuingConfig);
//
//        try (ChannelFacade channelFacade = new ChannelFacade(queueConnectionProperties)) {
//            channelFacade.getQueues();
//        }
//    }

    private static void markExistingJobsAsFailed() throws Exception {

        //retrieve Jobs where status is Executing (should only be ONE in reality, if jobs are always completed before another created)
        List<JobEntity> jobs = JobEntity.retrieveForStatus((short)ExecutionStatus.Executing.getValue());

        for (JobEntity job: jobs) {
            job.markAsFinished((short)ExecutionStatus.Failed.getValue());
            //job.writeToDb(); TODO
            logger.error("Marking previous job as failed: " + job.getJobuuid().toString());
        }
    }
}
