

DROP TABLE IF EXISTS medication_order;
DROP TABLE IF EXISTS medication_statement;
DROP TABLE IF EXISTS allergy_intolerance;
DROP TABLE IF EXISTS condition;
DROP TABLE IF EXISTS specimen;
DROP TABLE IF EXISTS diagnostic_order;
DROP TABLE IF EXISTS diagnostic_report;
DROP TABLE IF EXISTS family_member_history;
DROP TABLE IF EXISTS immunization;
DROP TABLE IF EXISTS observation;
DROP TABLE IF EXISTS procedure;
DROP TABLE IF EXISTS procedure_request;
DROP TABLE IF EXISTS referral_request;
DROP TABLE IF EXISTS encounter;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS episode_of_care;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS practitioner;
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


-- Table: date_precision

CREATE TABLE date_precision
(
  id smallint NOT NULL,
  value character varying(11) NOT NULL,
  CONSTRAINT pk_date_precision_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE date_precision
  OWNER TO postgres;
  
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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.appointment_status
  OWNER TO postgres;

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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.procedure_request_status
  OWNER TO postgres;

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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.referral_request_priority
  OWNER TO postgres;

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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.referral_request_type
  OWNER TO postgres;

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

-- Table: public.medication_statement_authorisation_type

CREATE TABLE medication_statement_authorisation_type
(
  id smallint NOT NULL,
  value character varying(50) NOT NULL,
  CONSTRAINT pk_medication_statement_authorisation_type_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.medication_statement_authorisation_type
  OWNER TO postgres;

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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.patient_gender
  OWNER TO postgres;

INSERT INTO patient_gender (id, value) VALUES (0, 'Male');
INSERT INTO patient_gender (id, value) VALUES (1, 'Female');

-- Table: registration_type

CREATE TABLE registration_type
(
  id smallint NOT NULL,
  code character varying(10) NOT NULL,
  description character varying(30) NOT NULL,
  CONSTRAINT pk_registration_type_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.registration_type
  OWNER TO postgres;

INSERT INTO registration_type (id, code, description) VALUES (0, 'E', 'Emergency');
INSERT INTO registration_type (id, code, description) VALUES (1, 'IN', 'Immediately Necessary');
INSERT INTO registration_type (id, code, description) VALUES (2, 'R', 'Regular/GMS');
INSERT INTO registration_type (id, code, description) VALUES (3, 'T', 'Temporary');
INSERT INTO registration_type (id, code, description) VALUES (4, 'P', 'Private');
INSERT INTO registration_type (id, code, description) VALUES (5, 'O', 'Other');
INSERT INTO registration_type (id, code, description) VALUES (6, 'D', 'Dummy/Synthetic');

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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.organization
  OWNER TO postgres;

-- Index: fki_organization_parent_organization_id

-- DROP INDEX fki_organization_parent_organization_id;

CREATE INDEX fki_organization_parent_organization_id
  ON organization
  USING btree
  (parent_organization_id);

-- Index: organization_id

-- DROP INDEX organization_id;

CREATE UNIQUE INDEX organization_id
  ON organization
  USING btree
  (id);

-- Table: public.practitioner

-- DROP TABLE public.practitioner;

CREATE TABLE public.practitioner
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  name character varying(1024) NOT NULL,
  role_code character varying(50),
  role_desc character varying(255),
  CONSTRAINT pk_practitioner_id PRIMARY KEY (id),
  CONSTRAINT fk_practitioner_organisation_id FOREIGN KEY (organization_id)
      REFERENCES public.organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.practitioner
  OWNER TO postgres;

-- Index: public.practitioner_id

-- DROP INDEX public.practitioner_id;

CREATE UNIQUE INDEX practitioner_id
  ON public.practitioner
  USING btree
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
  CONSTRAINT pk_schedule_id PRIMARY KEY (id),
  CONSTRAINT fk_schedule_organization_id FOREIGN KEY (organization_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE schedule
  OWNER TO postgres;

-- Index: schedule_id

-- DROP INDEX schedule_id;

CREATE UNIQUE INDEX schedule_id
  ON schedule
  USING btree
  (id);
  
-- Table: public.person

-- DROP TABLE public.person;

CREATE TABLE person
(
  id bigint NOT NULL,
  patient_gender_id smallint NOT NULL,
  pseudo_id character varying(255),
  age_years integer,
  age_months integer,
  age_weeks integer,
  date_of_death date,
  postcode_prefix character varying(20),
  household_id bigint,
  lsoa_code character varying(50),
  msoa_code character varying(50),
  ethnic_code character(1),
  CONSTRAINT pk_person_id PRIMARY KEY (id),
  CONSTRAINT fk_person_person_gender_id FOREIGN KEY (patient_gender_id)
      REFERENCES public.patient_gender (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.person
  OWNER TO postgres;

-- Index: public.person_id

-- DROP INDEX public.person_id;

CREATE UNIQUE INDEX person_id
  ON public.person
  USING btree
  (id);
ALTER TABLE public.person CLUSTER ON person_id;

  

-- Table: public.patient

-- DROP TABLE public.patient;

CREATE TABLE patient
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  person_id bigint NOT NULL,
  patient_gender_id smallint NOT NULL,
  pseudo_id character varying(255),
  age_years integer,
  age_months integer,
  age_weeks integer,
  date_of_death date,
  postcode_prefix character varying(20),
  household_id bigint,
  lsoa_code character varying(50),
  msoa_code character varying(50),
  ethnic_code character(1),
  CONSTRAINT pk_patient_id_organization_id PRIMARY KEY (id, organization_id),
  CONSTRAINT fk_patient_organization_id FOREIGN KEY (organization_id)
      REFERENCES public.organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_patient_patient_gender_id FOREIGN KEY (patient_gender_id)
      REFERENCES public.patient_gender (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.patient
  OWNER TO postgres;

-- Index: public.patient_id

-- DROP INDEX public.patient_id;

CREATE UNIQUE INDEX patient_id
  ON public.patient
  USING btree
  (id);
ALTER TABLE public.patient CLUSTER ON patient_id;  

-- Table: public.episode_of_care

-- DROP TABLE public.episode_of_care;

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
  CONSTRAINT pk_episode_of_care_id PRIMARY KEY (id),
  CONSTRAINT fk_episode_of_care_patient_id_organisation_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES public.patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_episode_of_care_practitioner_id FOREIGN KEY (usual_gp_practitioner_id)
      REFERENCES public.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_episode_of_care_registration_type_id FOREIGN KEY (registration_type_id)
      REFERENCES public.registration_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.episode_of_care
  OWNER TO postgres;

-- Index: episode_of_care_id

-- DROP INDEX episode_of_care_id;

CREATE UNIQUE INDEX episode_of_care_id
  ON episode_of_care
  USING btree
  (id);

-- Table: appointment

CREATE TABLE public.appointment
(
  id bigint NOT NULL,
  organization_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  person_id bigint NOT NULL,
  practitioner_id bigint,
  schedule_id bigint NOT NULL,
  start_date date,
  planned_duration integer NOT NULL,
  actual_duration integer,
  appointment_status_id smallint NOT NULL,
  patient_wait integer,
  patient_delay integer,
  sent_in date,
  "left" date,
  CONSTRAINT pk_appointment_id PRIMARY KEY (id),
  CONSTRAINT fk_appointment_appointment_status_id FOREIGN KEY (appointment_status_id)
      REFERENCES public.appointment_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appointment_organization_id FOREIGN KEY (organization_id)
      REFERENCES public.organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appointment_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES public.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.appointment
  OWNER TO postgres;

-- Index: appointment_id

-- DROP INDEX appointment_id;

CREATE UNIQUE INDEX appointment_id
  ON appointment
  USING btree
  (id);

-- Index: appointment_patient_id

-- DROP INDEX appointment_patient_id;

CREATE INDEX appointment_patient_id
  ON appointment
  USING btree
  (patient_id);
ALTER TABLE appointment CLUSTER ON appointment_patient_id;


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
  CONSTRAINT pk_encounter_id PRIMARY KEY (id),
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
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_episode_of_care_id FOREIGN KEY (episode_of_care_id)
      REFERENCES public.episode_of_care (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_service_provider_organization_id FOREIGN KEY (service_provider_organization_id)
      REFERENCES public.organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION		  
)
WITH (
  OIDS=FALSE
);
ALTER TABLE encounter
  OWNER TO postgres;

-- Index: encounter_id

-- DROP INDEX encounter_id;

CREATE UNIQUE INDEX encounter_id
  ON encounter
  USING btree
  (id);

-- Index: encounter_patient_id

-- DROP INDEX encounter_patient_id;

CREATE INDEX encounter_patient_id
  ON encounter
  USING btree
  (patient_id);
ALTER TABLE encounter CLUSTER ON encounter_patient_id;

-- Index: fki_encounter_appointment_id

-- DROP INDEX fki_encounter_appointment_id;

CREATE INDEX fki_encounter_appointment_id
  ON encounter
  USING btree
  (appointment_id);

-- Index: fki_encounter_patient_id_organization_id

-- DROP INDEX fki_encounter_patient_id_organization_id;

CREATE INDEX fki_encounter_patient_id_organization_id
  ON encounter
  USING btree
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
  CONSTRAINT pk_allergy_intolerance_id PRIMARY KEY (id),
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
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE allergy_intolerance
  OWNER TO postgres;

-- Index: allergy_intolerance_id

-- DROP INDEX allergy_intolerance_id;

CREATE UNIQUE INDEX allergy_intolerance_id
  ON allergy_intolerance
  USING btree
  (id);

-- Index: allergy_intolerance_patient_id

-- DROP INDEX allergy_intolerance_patient_id;

CREATE INDEX allergy_intolerance_patient_id
  ON allergy_intolerance
  USING btree
  (patient_id);
ALTER TABLE allergy_intolerance CLUSTER ON allergy_intolerance_patient_id;


-- Table: condition
/*
CREATE TABLE condition
(
  id integer NOT NULL,
  organization_id integer NOT NULL,
  patient_id integer NOT NULL,
  encounter_id integer,
  practitioner_id integer,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  is_review boolean NOT NULL,
  original_code character varying(20),
  CONSTRAINT pk_condition_id PRIMARY KEY (id),
  CONSTRAINT fk_condition_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_condition_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_condition_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_condition_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION            
      
)
WITH (
  OIDS=FALSE
);
ALTER TABLE condition
  OWNER TO postgres;

-- Index: condition_id

-- DROP INDEX condition_id;

CREATE UNIQUE INDEX condition_id
  ON condition
  USING btree
  (id);

-- Index: condition_patient_id

-- DROP INDEX condition_patient_id;

CREATE INDEX condition_patient_id
  ON condition
  USING btree
  (patient_id);
ALTER TABLE condition CLUSTER ON condition_patient_id;
*/

-- Table: specimen
/*
CREATE TABLE specimen
(
  id integer NOT NULL,
  organization_id integer NOT NULL,
  patient_id integer NOT NULL,
  encounter_id integer,
  practitioner_id integer,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  original_code character varying(20),
  CONSTRAINT pk_specimen_id PRIMARY KEY (id),
  CONSTRAINT fk_specimen_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_specimen_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_specimen_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_specimen_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION            
      
)
WITH (
  OIDS=FALSE
);
ALTER TABLE specimen
  OWNER TO postgres;

-- Index: specimen_id

-- DROP INDEX specimen_id;

CREATE UNIQUE INDEX specimen_id
  ON specimen
  USING btree
  (id);

-- Index: specimen_patient_id

-- DROP INDEX specimen_patient_id;

CREATE INDEX specimen_patient_id
  ON specimen
  USING btree
  (patient_id);
ALTER TABLE specimen CLUSTER ON specimen_patient_id;
*/

-- Table: diagnostic_order
/*
CREATE TABLE diagnostic_order
(
  id integer NOT NULL,
  organization_id integer NOT NULL,
  patient_id integer NOT NULL,
  encounter_id integer,
  practitioner_id integer,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  original_code character varying(20),
  CONSTRAINT pk_diagnostic_order_id PRIMARY KEY (id),
  CONSTRAINT fk_diagnostic_order_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_order_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_order_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_order_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION            
     
)
WITH (
  OIDS=FALSE
);
ALTER TABLE diagnostic_order
  OWNER TO postgres;

-- Index: diagnostic_order_id

-- DROP INDEX diagnostic_order_id;

CREATE UNIQUE INDEX diagnostic_order_id
  ON diagnostic_order
  USING btree
  (id);

-- Index: diagnostic_order_patient_id

-- DROP INDEX diagnostic_order_patient_id;

CREATE INDEX diagnostic_order_patient_id
  ON diagnostic_order
  USING btree
  (patient_id);
ALTER TABLE diagnostic_order CLUSTER ON diagnostic_order_patient_id;
*/

-- Table: diagnostic_report
/*
CREATE TABLE diagnostic_report
(
  id integer NOT NULL,
  organization_id integer NOT NULL,
  patient_id integer NOT NULL,
  encounter_id integer,
  practitioner_id integer,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  original_code character varying(20),
  CONSTRAINT pk_diagnostic_report_id PRIMARY KEY (id),
  CONSTRAINT fk_diagnostic_report_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_report_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_report_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_report_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION            
           
)
WITH (
  OIDS=FALSE
);
ALTER TABLE diagnostic_report
  OWNER TO postgres;

-- Index: diagnostic_report_id

-- DROP INDEX diagnostic_report_id;

CREATE UNIQUE INDEX diagnostic_report_id
  ON diagnostic_report
  USING btree
  (id);

-- Index: diagnostic_report_patient_id

-- DROP INDEX diagnostic_report_patient_id;

CREATE INDEX diagnostic_report_patient_id
  ON diagnostic_report
  USING btree
  (patient_id);
ALTER TABLE diagnostic_report CLUSTER ON diagnostic_report_patient_id;
*/


-- Table: family_member_history
/*
CREATE TABLE family_member_history
(
  id integer NOT NULL,
  organization_id integer NOT NULL,
  patient_id integer NOT NULL,
  encounter_id integer,
  practitioner_id integer,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  original_code character varying(20),
  CONSTRAINT pk_family_member_history_id PRIMARY KEY (id),
  CONSTRAINT fk_family_member_history_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_family_member_history_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_family_member_history_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_family_member_history_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION            
      
)
WITH (
  OIDS=FALSE
);
ALTER TABLE family_member_history
  OWNER TO postgres;

-- Index: family_member_history_id

-- DROP INDEX family_member_history_id;

CREATE UNIQUE INDEX family_member_history_id
  ON family_member_history
  USING btree
  (id);

-- Index: family_member_history_patient_id

-- DROP INDEX family_member_history_patient_id;

CREATE INDEX family_member_history_patient_id
  ON family_member_history
  USING btree
  (patient_id);
ALTER TABLE family_member_history CLUSTER ON family_member_history_patient_id;
*/


-- Table: immunization
/*
CREATE TABLE immunization
(
  id integer NOT NULL,
  organization_id integer NOT NULL,
  patient_id integer NOT NULL,
  encounter_id integer,
  practitioner_id integer,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  original_code character varying(20),
  CONSTRAINT pk_immunization_id PRIMARY KEY (id),
  CONSTRAINT fk_immunization_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_immunization_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_immunization_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_immunization_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                  
)
WITH (
  OIDS=FALSE
);
ALTER TABLE immunization
  OWNER TO postgres;

-- Index: immunization_id

-- DROP INDEX immunization_id;

CREATE UNIQUE INDEX immunization_id
  ON immunization
  USING btree
  (id);

-- Index: immunization_patient_id

-- DROP INDEX immunization_patient_id;

CREATE INDEX immunization_patient_id
  ON immunization
  USING btree
  (patient_id);
ALTER TABLE immunization CLUSTER ON immunization_patient_id;
*/


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
  CONSTRAINT pk_medication_statement_id PRIMARY KEY (id),
  CONSTRAINT fk_medication_statement_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES public.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_medication_statement_authorisation_type FOREIGN KEY (medication_statement_authorisation_type_id)
      REFERENCES public.medication_statement_authorisation_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES public.patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES public.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                
)
WITH (
  OIDS=FALSE
);
ALTER TABLE medication_statement
  OWNER TO postgres;

-- Index: medication_statement_id

-- DROP INDEX medication_statement_id;

CREATE UNIQUE INDEX medication_statement_id
  ON medication_statement
  USING btree
  (id);

-- Index: medication_statement_patient_id

-- DROP INDEX medication_statement_patient_id;

CREATE INDEX medication_statement_patient_id
  ON medication_statement
  USING btree
  (patient_id);
ALTER TABLE medication_statement CLUSTER ON medication_statement_patient_id;

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
  CONSTRAINT pk_medication_order_id PRIMARY KEY (id),
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
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                  
)
WITH (
  OIDS=FALSE
);
ALTER TABLE medication_order
  OWNER TO postgres;

-- Index: medication_order_id

-- DROP INDEX medication_order_id;

CREATE UNIQUE INDEX medication_order_id
  ON medication_order
  USING btree
  (id);

-- Index: medication_order_patient_id

-- DROP INDEX medication_order_patient_id;

CREATE INDEX medication_order_patient_id
  ON medication_order
  USING btree
  (patient_id);
ALTER TABLE medication_order CLUSTER ON medication_order_patient_id;

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
  value real,
  units character varying(50),
  original_code character varying(20),
  is_problem boolean NOT NULL,
  original_term character varying(1000),
  is_review boolean NOT NULL,
  problem_end_date date,
  CONSTRAINT pk_observation_id PRIMARY KEY (id),
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
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                  
)
WITH (
  OIDS=FALSE
);
ALTER TABLE observation
  OWNER TO postgres;

-- Index: observation_id

-- DROP INDEX observation_id;

CREATE UNIQUE INDEX observation_id
  ON observation
  USING btree
  (id);

-- Index: observation_patient_id

-- DROP INDEX observation_patient_id;

CREATE INDEX observation_patient_id
  ON observation
  USING btree
  (patient_id);
ALTER TABLE observation CLUSTER ON observation_patient_id;


-- Table: procedure
/*
CREATE TABLE procedure
(
  id integer NOT NULL,
  organization_id integer NOT NULL,
  patient_id integer NOT NULL,
  encounter_id integer,
  practitioner_id integer,
  clinical_effective_date date,
  date_precision_id smallint,
  snomed_concept_id bigint,
  original_code character varying(20),
  CONSTRAINT pk_procedure_id PRIMARY KEY (id),
  CONSTRAINT fk_procedure_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                  
)
WITH (
  OIDS=FALSE
);
ALTER TABLE procedure
  OWNER TO postgres;

-- Index: procedure_id

-- DROP INDEX procedure_id;

CREATE UNIQUE INDEX procedure_id
  ON procedure
  USING btree
  (id);

-- Index: procedure_patient_id

-- DROP INDEX procedure_patient_id;

CREATE INDEX procedure_patient_id
  ON procedure
  USING btree
  (patient_id);
ALTER TABLE procedure CLUSTER ON procedure_patient_id;
*/


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
  CONSTRAINT pk_procedure_request_id PRIMARY KEY (id),
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
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_procedure_request_status_id FOREIGN KEY (procedure_request_status_id)
      REFERENCES public.procedure_request_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION                  
)
WITH (
  OIDS=FALSE
);
ALTER TABLE procedure_request
  OWNER TO postgres;

-- Index: procedure_request_id

-- DROP INDEX procedure_request_id;

CREATE UNIQUE INDEX procedure_request_id
  ON procedure_request
  USING btree
  (id);

-- Index: procedure_request_patient_id

-- DROP INDEX procedure_request_patient_id;

CREATE INDEX procedure_request_patient_id
  ON procedure_request
  USING btree
  (patient_id);
ALTER TABLE procedure_request CLUSTER ON procedure_request_patient_id;


-- Table: public.referral_request

-- DROP TABLE public.referral_request;

CREATE TABLE public.referral_request
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
  CONSTRAINT pk_referral_request_id PRIMARY KEY (id),
  CONSTRAINT fk_referral_request_date_precision FOREIGN KEY (date_precision_id)
      REFERENCES public.date_precision (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES public.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_patient_id_organization_id FOREIGN KEY (patient_id, organization_id)
      REFERENCES public.patient (id, organization_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES public.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_recipient_organization_id FOREIGN KEY (recipient_organization_id)
      REFERENCES public.organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_requester_organization_id FOREIGN KEY (requester_organization_id)
      REFERENCES public.organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_type_id FOREIGN KEY (type_id)
      REFERENCES public.referral_request_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_priority_id FOREIGN KEY (priority_id)
      REFERENCES public.referral_request_priority (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.referral_request
  OWNER TO postgres;

-- Index: public.referral_request_id

-- DROP INDEX public.referral_request_id;

CREATE UNIQUE INDEX referral_request_id
  ON public.referral_request
  USING btree
  (id);

-- Index: public.referral_request_patient_id

-- DROP INDEX public.referral_request_patient_id;

CREATE INDEX referral_request_patient_id
  ON public.referral_request
  USING btree
  (patient_id);
ALTER TABLE public.referral_request CLUSTER ON referral_request_patient_id;




