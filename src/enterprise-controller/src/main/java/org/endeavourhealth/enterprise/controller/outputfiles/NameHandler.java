package org.endeavourhealth.enterprise.controller.outputfiles;

import org.endeavourhealth.enterprise.controller.jobinventory.JobInventory;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportInfo;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportItemInfo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

class NameHandler {

    private static String formatInstantForName(Instant instant) {
        return DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")
                .withZone(ZoneOffset.UTC)
                .format(instant);
    }

    public static String calculatePreferredName(JobReportInfo jobReportInfo) {
        return jobReportInfo.getReportName() + " - Scheduled " + formatInstantForName(jobReportInfo.getRequest().getTimestamp().toInstant());
    }

    public static String calculatePreferredName(
            JobReportItemInfo jobReportItemInfo,
            JobInventory jobInventory) throws Exception {

        return jobInventory.getItemName(jobReportItemInfo.getLibraryItemUuid());
    }

    public static String calculateJobName(Instant startDateTime) {
        return "Execution " + formatInstantForName(startDateTime);
    }
}
