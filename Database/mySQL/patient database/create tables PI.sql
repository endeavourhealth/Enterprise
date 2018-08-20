-- Data Checking (PI) database WITH foreign keys

-- create database enterprise_pi;

use enterprise_pi;

DROP TABLE IF EXISTS patient_uprn;
DROP TABLE IF EXISTS medication_order;
DROP TABLE IF EXISTS medication_statement;
DROP TABLE IF EXISTS flag;
DROP TABLE IF EXISTS allergy_intolerance;
DROP TABLE IF EXISTS `condition`;
DROP TABLE IF EXISTS specimen;
DROP TABLE IF EXISTS diagnostic_order;
DROP TABLE IF EXISTS diagnostic_report;
DROP TABLE IF EXISTS family_member_history;
DROP TABLE IF EXISTS immunization;
DROP TABLE IF EXISTS observation;
DROP TABLE IF EXISTS `procedure`;
DROP TABLE IF EXISTS procedure_request;
DROP TABLE IF EXISTS referral_request;
DROP TABLE IF EXISTS encounter_raw;
DROP TABLE IF EXISTS encounter_detail;
DROP TABLE IF EXISTS encounter;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS episode_of_care;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS `schedule`;
DROP TABLE IF EXISTS practitioner;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS organization;
DROP TABLE IF EXISTS date_precision;
DROP TABLE IF EXISTS appointment_status;
DROP TABLE IF EXISTS procedure_request_status;
DROP TABLE IF EXISTS referral_request_priority;
DROP TABLE IF EXISTS referral_request_type;
DROP TABLE IF EXISTS medication_statement_authorisation_type;
DROP TABLE IF EXISTS patient_gender;
DROP TABLE IF EXISTS registration_type;
DROP TABLE IF EXISTS lsoa_lookup;
DROP TABLE IF EXISTS msoa_lookup;
DROP TABLE IF EXISTS ward_lookup;
DROP TABLE IF EXISTS local_authority_lookup;
DROP TABLE IF EXISTS ethnicity_lookup;


CREATE TABLE ethnicity_lookup
(
  ethnic_code character(1) NOT NULL,
  ethnic_name character varying(100),
  CONSTRAINT pk_ethnicity_lookup PRIMARY KEY (ethnic_code)
);

INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('A', 'British');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('B', 'Irish');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('C', 'Any other White background');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('D', 'White and Black Caribbean');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('E', 'White and Black African');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('F', 'White and Asian');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('G', 'Any other mixed background');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('H', 'Indian');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('J', 'Pakistani');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('K', 'Bangladeshi');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('L', 'Any other Asian background');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('M', 'Caribbean');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('N', 'African');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('P', 'Any other Black background');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('R', 'Chinese');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('S', 'Any other ethnic group');
INSERT INTO ethnicity_lookup (ethnic_code, ethnic_name) VALUES ('Z', 'Not stated');

-- Table: lsoa_lookup

CREATE TABLE lsoa_lookup
(
  lsoa_code character(9) NOT NULL,
  lsoa_name character varying(255),
  imd_rank integer,
  imd_decile integer,
  income_rank integer,
  income_decile integer,
  employment_rank integer,
  employment_decile integer,
  education_rank integer,
  education_decile integer,
  health_rank integer,
  health_decile integer,
  crime_rank integer,
  crime_decile integer,
  housing_and_services_barriers_rank integer,
  housing_and_services_barriers_decile integer,
  living_environment_rank integer,
  living_environment_decile integer,  
  CONSTRAINT pk_lsoa_lookup PRIMARY KEY (lsoa_code)
);

-- Table: msoa_lookup

CREATE TABLE msoa_lookup
(
  msoa_code character(9) NOT NULL,
  msoa_name character varying(255),
  CONSTRAINT pk_msoa_lookup PRIMARY KEY (msoa_code)
);

CREATE TABLE local_authority_lookup
(
  local_authority_code varchar(9) NOT NULL,
  local_authority_name varchar(255),
  CONSTRAINT pk_local_authority_lookup PRIMARY KEY (local_authority_code)
);

CREATE TABLE ward_lookup
(
  ward_code varchar(9) NOT NULL,
  ward_name varchar(255),
  CONSTRAINT pk_ward_lookup PRIMARY KEY (ward_code)
);


-- Table: date_precision

CREATE TABLE date_precision
(
  id smallint NOT NULL,
  value character varying(11) NOT NULL,
  CONSTRAINT pk_date_precision_id PRIMARY KEY (id)
);
  
INSERT INTO date_precision (id, value) VALUES (1, 'year');
INSERT INTO date_precision (id, value) VALUES (2, 'month');
INSERT INTO date_precision (id, value) VALUES (5, 'day');
INSERT INTO date_precision (id, value) VALUES (12, 'minute');
INSERT INTO date_precision (id, value) VALUES (13, 'second');
INSERT INTO date_precision (id, value) VALUES (14, 'millisecond');

-- Table: appointment_status

CREATE TABLE appointment_status
(
  id smallint NOT NULL,
  value character varying(50) NOT NULL,
  CONSTRAINT pk_appointment_status_id PRIMARY KEY (id)
);

INSERT INTO appointment_status (id, value) VALUES (0, 'Proposed');
INSERT INTO appointment_status (id, value) VALUES (1, 'Pending');
INSERT INTO appointment_status (id, value) VALUES (2, 'Booked');
INSERT INTO appointment_status (id, value) VALUES (3, 'Arrived');
INSERT INTO appointment_status (id, value) VALUES (4, 'Fulfilled');
INSERT INTO appointment_status (id, value) VALUES (5, 'Cancelled');
INSERT INTO appointment_status (id, value) VALUES (6, 'No Show');

-- Table: procedure_request_status

CREATE TABLE procedure_request_status
(
  id smallint NOT NULL,
  value character varying(50) NOT NULL,
  CONSTRAINT pk_procedure_request_status_id PRIMARY KEY (id)
);

INSERT INTO procedure_request_status (id, value) VALUES (0, 'Proposed');
INSERT INTO procedure_request_status (id, value) VALUES (1, 'Draft');
INSERT INTO procedure_request_status (id, value) VALUES (2, 'Requested');
INSERT INTO procedure_request_status (id, value) VALUES (3, 'Received');
INSERT INTO procedure_request_status (id, value) VALUES (4, 'Accepted');
INSERT INTO procedure_request_status (id, value) VALUES (5, 'In Progress');
INSERT INTO procedure_request_status (id, value) VALUES (6, 'Completed');
INSERT INTO procedure_request_status (id, value) VALUES (7, 'Suspended');
INSERT INTO procedure_request_status (id, value) VALUES (8, 'Rejected');
INSERT INTO procedure_request_status (id, value) VALUES (9, 'Aborted');

-- Table: referral_priority

CREATE TABLE referral_request_priority
(
  id smallint NOT NULL,
  value character varying(50) NOT NULL,
  CONSTRAINT pk_referral_request_priority_id PRIMARY KEY (id)
);

INSERT INTO referral_request_priority (id, value) VALUES (0, 'Routine');
INSERT INTO referral_request_priority (id, value) VALUES (1, 'Urgent');
INSERT INTO referral_request_priority (id, value) VALUES (2, 'Two Week Wait');
INSERT INTO referral_request_priority (id, value) VALUES (3, 'Soon');

-- Table: referral_request_type

CREATE TABLE referral_request_type
(
  id smallint NOT NULL,
  value character varying(50) NOT NULL,
  CONSTRAINT pk_referral_request_type_id PRIMARY KEY (id)
);

INSERT INTO referral_request_type (id, value) VALUES (0, 'Unknown');
INSERT INTO referral_request_type (id, value) VALUES (1, 'Assessment');
INSERT INTO referral_request_type (id, value) VALUES (2, 'Investigation');
INSERT INTO referral_request_type (id, value) VALUES (3, 'Management advice');
INSERT INTO referral_request_type (id, value) VALUES (4, 'Patient reassurance');
INSERT INTO referral_request_type (id, value) VALUES (5, 'Self referral');
INSERT INTO referral_request_type (id, value) VALUES (6, 'Treatment');
INSERT INTO referral_request_type (id, value) VALUES (7, 'Outpatient');
INSERT INTO referral_request_type (id, value) VALUES (8, 'Performance of a procedure / operation');
INSERT INTO referral_request_type (id, value) VALUES (9, 'Community Care');
INSERT INTO referral_request_type (id, value) VALUES (10, 'Admission');
INSERT INTO referral_request_type (id, value) VALUES (11, 'Day Care');
INSERT INTO referral_request_type (id, value) VALUES (12, 'Assessment & Education');

-- Table: medication_statement_authorisation_type

CREATE TABLE medication_statement_authorisation_type
(
  id smallint NOT NULL,
  value character varying(50) NOT NULL,
  CONSTRAINT pk_medication_statement_authorisation_type_id PRIMARY KEY (id)
);

INSERT INTO medication_statement_authorisation_type (id, value) VALUES (0, 'Acute');
INSERT INTO medication_statement_authorisation_type (id, value) VALUES (1, 'Repeat');
INSERT INTO medication_statement_authorisation_type (id, value) VALUES (2, 'Repeat Dispensing');
INSERT INTO medication_statement_authorisation_type (id, value) VALUES (3, 'Automatic');

-- Table: patient_gender

CREATE TABLE patient_gender
(
  id smallint NOT NULL,
  value character varying(10) NOT NULL,
  CONSTRAINT pk_patient_gender_id PRIMARY KEY (id)
);

INSERT INTO patient_gender (id, value) VALUES (0, 'Male');
INSERT INTO patient_gender (id, value) VALUES (1, 'Female');
INSERT INTO patient_gender (id, value) VALUES (2, 'Other');
INSERT INTO patient_gender (id, value) VALUES (3, 'Unknown');

-- Table: registration_type

CREATE TABLE registration_type
(
  id smallint NOT NULL,
  code character varying(10) NOT NULL,
  description character varying(30) NOT NULL,
  CONSTRAINT pk_registration_type_id PRIMARY KEY (id)
);

INSERT INTO registration_type (id, code, description) VALUES (0, 'E', 'Emergency');
INSERT INTO registration_type (id, code, description) VALUES (1, 'IN', 'Immediately Necessary');
INSERT INTO registration_type (id, code, description) VALUES (2, 'R', 'Regular/GMS');
INSERT INTO registration_type (id, code, description) VALUES (3, 'T', 'Temporary');
INSERT INTO registration_type (id, code, description) VALUES (4, 'P', 'Private');
INSERT INTO registration_type (id, code, description) VALUES (5, 'O', 'Other');
INSERT INTO registration_type (id, code, description) VALUES (6, 'D', 'Dummy/Synthetic');
INSERT INTO registration_type (id, code, description) VALUES (7, 'C', 'Community');
INSERT INTO registration_type (id, code, description) VALUES (8, 'W', 'Walk-In');
INSERT INTO registration_type (id, code, description) VALUES (9, 'MS', 'Minor Surgery');
INSERT INTO registration_type (id, code, description) VALUES (10, 'CHS', 'Child Health Services');
INSERT INTO registration_type (id, code, description) VALUES (11, 'N', 'Contraceptive Services');
INSERT INTO registration_type (id, code, description) VALUES (12, 'Y', 'Yellow Fever');
INSERT INTO registration_type (id, code, description) VALUES (13, 'M', 'Maternity Services');
INSERT INTO registration_type (id, code, description) VALUES (14, 'PR', 'Pre-Registration');
INSERT INTO registration_type (id, code, description) VALUES (15, 'SH', 'Sexual Health');
INSERT INTO registration_type (id, code, description) VALUES (16, 'V', 'Vasectomy');
INSERT INTO registration_type (id, code, description) VALUES (17, 'OH', 'Out of Hours');


-- Table: organization

CREATE TABLE organization
(
  id bigint NOT NULL,
  ods_code character varying(50),
  name character varying(255),
  type_code character varying(50),
  type_desc character varying(255),
  postcode character varying(10),
  parent_organization_id bigint,
  CONSTRAINT pk_organization_id PRIMARY KEY (id)
);

CREATE UNIQUE INDEX organization_id
  ON organization
  (id);

CREATE INDEX fki_organization_parent_organization_id
  ON organization
  (parent_organization_id);


-- Table: location

CREATE TABLE location (
  id bigint NOT NULL,
  name character varying(255),
  type_code character varying(50),
  type_desc character varying(255),
  postcode character varying(10),
  managing_organization_id bigint,
  CONSTRAINT pk_location_id PRIMARY KEY (id),
  CONSTRAINT fk_location_organisation_id FOREIGN KEY (managing_organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION    
);

CREATE UNIQUE INDEX location_id
  ON location
  (id);
  
CREATE INDEX fk_location_managing_organisation_id
  ON location
  (managing_organization_id);


-- Table: practitioner

-- DROP TABLE practitioner;

CREATE TABLE practitioner
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  name character varying(1024),
  role_code character varying(50),
  role_desc character varying(255),
  CONSTRAINT pk_practitioner_id PRIMARY KEY (id),
  CONSTRAINT fk_practitioner_organisation_id FOREIGN KEY (organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX practitioner_id
  ON practitioner
  (id);

-- Table: schedule

CREATE TABLE schedule
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  practitioner_id bigint,
  start_date date,
  type character varying(255),
  location character varying(255),
  CONSTRAINT pk_schedule_id PRIMARY KEY (organization_id, id),
  CONSTRAINT fk_schedule_organization_id FOREIGN KEY (organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Index: schedule_id

-- DROP INDEX schedule_id;

CREATE UNIQUE INDEX schedule_id
  ON schedule
  (id);
  

-- Table: person

-- DROP TABLE person;

CREATE TABLE person
(
  id bigint NOT NULL,
  patient_gender_id smallint NOT NULL,
  nhs_number character varying(255),
  date_of_birth date,
  date_of_death date,
  postcode character varying(20),
  household_id bigint,
  lsoa_code character varying(50),
  msoa_code character varying(50),
  ethnic_code character(1),
  ward_code varchar(50),
  local_authority_code varchar(50),
  registered_practice_organization_id bigint,
  CONSTRAINT pk_person_id PRIMARY KEY (id),
  CONSTRAINT fk_person_patient_gender_id FOREIGN KEY (patient_gender_id)
      REFERENCES patient_gender (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX person_id
  ON person
  (id);  


  
-- Table: patient

-- DROP TABLE patient;

CREATE TABLE patient
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  person_id bigint NOT NULL,
  patient_gender_id smallint NOT NULL,
  nhs_number character varying(255),
  date_of_birth date,
  date_of_death date,
  postcode character varying(20),
  household_id bigint,
  lsoa_code character varying(50),
  msoa_code character varying(50),
  ethnic_code character(1),
  ward_code varchar(50),
  local_authority_code varchar(50),
  registered_practice_organization_id bigint,
  CONSTRAINT pk_patient_id_organization_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_patient_organization_id FOREIGN KEY (organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_patient_patient_gender_id FOREIGN KEY (patient_gender_id)
      REFERENCES patient_gender (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX patient_id
  ON patient
  (id);
  
CREATE INDEX patient_person_id
  ON patient
  (person_id);


-- Table: episode_of_care

-- DROP TABLE episode_of_care;

CREATE TABLE episode_of_care
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  registration_type_id smallint,
  date_registered date,
  date_registered_end date,
  usual_gp_practitioner_id bigint,
  CONSTRAINT pk_episode_of_care_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_episode_of_care_patient_id_organisation_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_episode_of_care_practitioner_id FOREIGN KEY (usual_gp_practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_episode_of_care_registration_type_id FOREIGN KEY (registration_type_id)
      REFERENCES registration_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX episode_of_care_id
  ON episode_of_care
  (id);
  
CREATE INDEX episode_of_care_patient_id
  ON episode_of_care
  (patient_id);
  
CREATE INDEX episode_of_care_registration_type_id
  ON episode_of_care
  (registration_type_id);

CREATE INDEX episode_of_care_date_registered
  ON episode_of_care
  (date_registered);
  
CREATE INDEX episode_of_care_date_registered_end
  ON episode_of_care
  (date_registered_end);
  
CREATE INDEX episode_of_care_person_id
  ON episode_of_care
  (person_id);
  
CREATE INDEX episode_of_care_organization_id
  ON episode_of_care
  (organization_id);

-- Table: appointment

CREATE TABLE appointment
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  practitioner_id bigint,
  schedule_id bigint,
  start_date date,
  planned_duration integer NOT NULL,
  actual_duration integer,
  appointment_status_id smallint NOT NULL,
  patient_wait integer,
  patient_delay integer,
  sent_in date,
  `left` date,
  CONSTRAINT pk_appointment_id PRIMARY KEY (organization_id,person_id,id),
  CONSTRAINT fk_appointment_appointment_status_id FOREIGN KEY (appointment_status_id)
      REFERENCES appointment_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appointment_organization_id FOREIGN KEY (organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appointment_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX appointment_id
  ON appointment
  (id);

CREATE INDEX appointment_patient_id
  ON appointment
  (patient_id);

-- Table: encounter

CREATE TABLE encounter
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  practitioner_id bigint,
  appointment_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  original_code character varying(20),
  original_term character varying(1000),
  episode_of_care_id bigint,
  service_provider_organization_id bigint,
  CONSTRAINT pk_encounter_id PRIMARY KEY (organization_id,person_id,id),
  CONSTRAINT fk_encounter_appointment_id FOREIGN KEY (appointment_id)
      REFERENCES appointment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_episode_of_care_id FOREIGN KEY (episode_of_care_id)
      REFERENCES episode_of_care (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_service_provider_organization_id FOREIGN KEY (service_provider_organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION		  
);

CREATE UNIQUE INDEX encounter_id
  ON encounter
  (id);

CREATE INDEX encounter_patient_id
  ON encounter
  (patient_id);

CREATE INDEX fki_encounter_appointment_id
  ON encounter
  (appointment_id);
  
CREATE INDEX fki_encounter_patient_id_organization_id
  ON encounter
  (patient_id, organization_id);
  
CREATE INDEX encounter_snomed_concept_id_clinical_effective_date
  ON encounter
  (snomed_concept_id, clinical_effective_date);


-- Table: encounter_detail

CREATE TABLE encounter_detail (
  id bigint NOT NULL COMMENT 'same as the id column on the encounter table',
  organization_id bigint NOT NULL COMMENT 'owning organisation (i.e. publisher)',
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  practitioner_id bigint COMMENT 'performing practitioner',
  episode_of_care_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  recording_practitioner_id bigint COMMENT 'who recorded the encounter',
  recording_date date,
  appointment_id bigint,
  service_provider_organization_id bigint COMMENT 'organisation that performed the encounter', 
  location_id bigint COMMENT 'where the encounter took place',
  end_date date,
  duration_minutes int COMMENT 'duration always in minutes',
  completion_status_concept_id bigint,
  healthcare_service_type_concept_id bigint,
  interaction_mode_concept_id bigint,
  administrative_action_concept_id bigint,
  purpose_concept_id bigint,
  disposition_concept_id bigint,
  site_of_care_type_concept_id bigint,
  patient_status_concept_id bigint,
  CONSTRAINT pk_encounter_detail_id PRIMARY KEY (organization_id, person_id, id),
  CONSTRAINT fk_encounter_detail_appointment_id FOREIGN KEY (appointment_id)
      REFERENCES appointment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_detail_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_detail_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_detail_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_detail_episode_of_care_id FOREIGN KEY (episode_of_care_id)
      REFERENCES episode_of_care (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_detail_service_provider_organization_id FOREIGN KEY (service_provider_organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION	
);

CREATE UNIQUE INDEX ix_encounter_detail_id
  ON encounter_detail
  (id);

CREATE INDEX ix_encounter_detail_patient_id
  ON encounter_detail
  (patient_id);

CREATE INDEX ix_encounter_detail_appointment_id
  ON encounter_detail
  (appointment_id);
  
CREATE INDEX ix_encounter_detail_patient_id_organization_id
  ON encounter_detail
  (patient_id, organization_id);
  

-- need location table too

-- Table: encounter_raw

CREATE TABLE encounter_raw (
  id bigint NOT NULL COMMENT 'same as the id column on the encounter table',
  organization_id bigint NOT NULL COMMENT 'owning organisation (i.e. publisher)',
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  practitioner_id bigint COMMENT 'performing practitioner',
  episode_of_care_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  recording_practitioner_id bigint COMMENT 'who recorded the encounter',
  recording_date date,
  appointment_id bigint,
  service_provider_organization_id bigint COMMENT 'organisation that performed the encounter', 
  location_id bigint COMMENT 'where the encounter took place',
  end_date date,
  duration_minutes int COMMENT 'encounter duration always in terms of minutes',
  fhir_adt_message_code varchar(50) COMMENT 'ADT message type e.g. A01',
  fhir_class varchar(50) COMMENT 'class from FHIR Encounter',
  fhir_type varchar(50) COMMENT 'type from FHIR Encounter',
  fhir_status varchar(50) COMMENT 'status from FHIR Encounter',
  fhir_snomed_concept_id bigint,
  fhir_original_code character varying(20),
  fhir_original_term character varying(1000),
  CONSTRAINT pk_encounter_raw_id PRIMARY KEY (organization_id, person_id, id),
  CONSTRAINT fk_encounter_raw_appointment_id FOREIGN KEY (appointment_id)
      REFERENCES appointment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_raw_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_raw_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_raw_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_raw_episode_of_care_id FOREIGN KEY (episode_of_care_id)
      REFERENCES episode_of_care (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_raw_service_provider_organization_id FOREIGN KEY (service_provider_organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION	
) COMMENT 'table of raw encounter data to allow working out mappings for encounter_detail concepts';

CREATE UNIQUE INDEX ix_raw_detail_id
  ON encounter_raw
  (id);

CREATE INDEX ix_encounter_raw_patient_id
  ON encounter_raw
  (patient_id);

CREATE INDEX ix_encounter_raw_appointment_id
  ON encounter_raw
  (appointment_id);
  
CREATE INDEX ix_encounter_raw_patient_id_organization_id
  ON encounter_raw
  (patient_id, organization_id);
  

-- Table: allergy_intolerance

CREATE TABLE allergy_intolerance
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  encounter_id bigint,
  practitioner_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  original_code character varying(20),
  original_term character varying(1000),
  is_review boolean NOT NULL,
  CONSTRAINT pk_allergy_intolerance_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_allergy_intolerance_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_allergy_intolerance_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_allergy_intolerance_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_allergy_intolerance_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX allergy_intolerance_id
  ON allergy_intolerance
  (id);

CREATE INDEX allergy_intolerance_patient_id
  ON allergy_intolerance
  (patient_id);

CREATE INDEX allergy_intolerance_snomed_concept_id
  ON allergy_intolerance
  (snomed_concept_id);  

-- Table: medication_statement

CREATE TABLE medication_statement
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  encounter_id bigint,
  practitioner_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  dmd_id bigint,
  is_active boolean NOT NULL,
  cancellation_date date,
  dose character varying(1000),
  quantity_value real,
  quantity_unit character varying(255),
  medication_statement_authorisation_type_id smallint NOT NULL,
  original_term character varying(1000),
  CONSTRAINT pk_medication_statement_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_medication_statement_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_medication_statement_authorisation_type FOREIGN KEY (medication_statement_authorisation_type_id)
      REFERENCES medication_statement_authorisation_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                
);

CREATE UNIQUE INDEX medication_statement_id
  ON medication_statement
  (id);

CREATE INDEX medication_statement_patient_id
  ON medication_statement
  (patient_id);

CREATE INDEX medication_statement_dmd_id
  ON medication_statement
  (patient_id);
  
-- Table: medication_order

CREATE TABLE medication_order
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  encounter_id bigint,
  practitioner_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  dmd_id bigint,
  dose character varying(1000),
  quantity_value real,
  quantity_unit character varying(255),
  duration_days integer NOT NULL,
  estimated_cost real,
  medication_statement_id bigint,
  original_term character varying(1000),
  CONSTRAINT pk_medication_order_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_medication_order_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_medication_statement_id FOREIGN KEY (medication_statement_id)
      REFERENCES medication_statement (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                  
);

CREATE UNIQUE INDEX medication_order_id
  ON medication_order
  (id);

CREATE INDEX medication_order_patient_id
  ON medication_order
  (patient_id);

CREATE INDEX medication_order_dmd_id
  ON medication_order
  (dmd_id);

-- Table: flag

CREATE TABLE flag
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  effective_date date,
  date_precision_id smallint,
  is_active boolean NOT NULL,
  flag_text text,
  CONSTRAINT pk_flag_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_flag_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
  REFERENCES patient (id, organization_id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_flag_date_precision FOREIGN KEY (date_precision_id)
  REFERENCES date_precision (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX flag_id
  ON flag
  (id);

CREATE INDEX flag_patient_id
  ON flag
  (patient_id);

-- Table: observation

CREATE TABLE observation
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  encounter_id bigint,
  practitioner_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  result_value real,
  result_value_units character varying(50),
  result_date date,
  result_text text,
  result_concept_id bigint,
  original_code character varying(20),
  is_problem boolean NOT NULL,
  original_term character varying(1000),
  is_review boolean NOT NULL,
  problem_end_date date,
  parent_observation_id bigint,
  CONSTRAINT pk_observation_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_observation_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_observation_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_observation_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_observation_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                  
);

CREATE UNIQUE INDEX observation_id
  ON observation
  (id);

CREATE INDEX observation_patient_id
  ON observation
  (patient_id);

CREATE INDEX observation_snomed_concept_id
  ON observation
  (snomed_concept_id);

CREATE INDEX observation_snomed_concept_id_is_problem
  ON observation
  (`snomed_concept_id`,`is_problem`);

CREATE INDEX observation_snomed_concept_id_value
  ON observation
  (`snomed_concept_id`,`result_value`);

CREATE INDEX observation_original_code
  ON observation
  (original_code);
    
CREATE INDEX ix_observation_organization_id
  ON observation
  (organization_id);
  
CREATE INDEX ix_observation_clinical_effective_date
  ON observation
  (clinical_effective_date);
  
  
CREATE INDEX ix_observation_person_id
  ON observation
  (person_id);	
	
-- Table: procedure_request

CREATE TABLE procedure_request
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  encounter_id bigint,
  practitioner_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  procedure_request_status_id smallint,
  original_code character varying(20),
  original_term character varying(1000),
  CONSTRAINT pk_procedure_request_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_procedure_request_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_procedure_request_status_id FOREIGN KEY (procedure_request_status_id)
      REFERENCES procedure_request_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                  
);

CREATE UNIQUE INDEX procedure_request_id
  ON procedure_request
  (id);

CREATE INDEX procedure_request_patient_id
  ON procedure_request
  (patient_id);


-- Table: referral_request

-- DROP TABLE referral_request;

CREATE TABLE referral_request
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  encounter_id bigint,
  practitioner_id bigint,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  requester_organization_id bigint,
  recipient_organization_id bigint,
  priority_id smallint,
  type_id smallint,
  mode character varying(50),
  outgoing_referral boolean,
  original_code character varying(20),
  original_term character varying(1000),
  is_review boolean NOT NULL,
  CONSTRAINT pk_referral_request_id PRIMARY KEY (`organization_id`,`person_id`,`id`),
  CONSTRAINT fk_referral_request_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_recipient_organization_id FOREIGN KEY (recipient_organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_requester_organization_id FOREIGN KEY (requester_organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_type_id FOREIGN KEY (type_id)
      REFERENCES referral_request_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_priority_id FOREIGN KEY (priority_id)
      REFERENCES referral_request_priority (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX referral_request_id
  ON referral_request
  (id);

CREATE INDEX referral_request_patient_id
  ON referral_request
  (patient_id);

CREATE INDEX referral_request_snomed_concept_id
  ON referral_request
  (snomed_concept_id);
  
create table patient_uprn (
	patient_id bigint,
    organization_id bigint,
    person_id bigint,
    lsoa_code varchar(50),
    uprn bigint,
    qualifier varchar(50),
    `algorithm` varchar(255),
    `match` varchar(255),
    no_address boolean,
    invalid_address boolean,
    missing_postcode boolean,
    invalid_postcode boolean,
    CONSTRAINT pk_patient_id_organization_id PRIMARY KEY (`organization_id`,`person_id`,`patient_id`),
    CONSTRAINT fk_patient_uprn_patient_id_organisation_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE UNIQUE INDEX patient_uprn_id
  ON patient_uprn
  (patient_id);
