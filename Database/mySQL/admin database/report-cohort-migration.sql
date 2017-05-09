RENAME TABLE ReportPatients TO CohortPatients;
ALTER TABLE CohortPatients CHANGE COLUMN ReportPatientId CohortPatientId int(11) NOT NULL AUTO_INCREMENT;

RENAME TABLE ReportResult TO CohortResult;
ALTER TABLE CohortResult CHANGE COLUMN ReportResultId CohortResultId int(11) NOT NULL AUTO_INCREMENT;
