ALTER TABLE `enterprise_admin`.`CodeSet` 
ADD INDEX `SnomedConceptId` (`SnomedConceptId` ASC);

ALTER TABLE `enterprise_data_pseudonymised`.`episode_of_care` 
ADD INDEX `person_id` (`person_id` ASC),
ADD INDEX `organisation_id` (`organization_id` ASC);

ALTER TABLE `enterprise_data_pseudonymised`.`patient` 
ADD INDEX `person_id` (`person_id` ASC);

ALTER TABLE `enterprise_data_pseudonymised`.`observation` 
ADD INDEX `person_id` (`person_id` ASC),
ADD INDEX `organisation_id` (`organization_id` ASC);

ALTER TABLE `enterprise_data_pseudonymised`.`observation` 
ADD INDEX `prev_inc` (`person_id` ASC, `organization_id` ASC, `snomed_concept_id` ASC, `clinical_effective_date` ASC, `id` ASC);
