-- Table: admin.organisation

-- DROP TABLE admin.organisation;

CREATE TABLE admin.organisation
(
  id uuid NOT NULL,
  ods_code character varying(50) NOT NULL,
  name character varying(255) NOT NULL,
  type_code character varying(50) NOT NULL,
  type_desc character varying(255) NOT NULL,
  postcode character varying(10),
  parent_organisation_id uuid,
  CONSTRAINT pk_organisation_id PRIMARY KEY (id),
  CONSTRAINT fk_organisation_parent_organisation_id FOREIGN KEY (parent_organisation_id)
      REFERENCES admin.organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE admin.organisation
  OWNER TO postgres;

-- Index: admin.fki_organisation_parent_organisation_id

-- DROP INDEX admin.fki_organisation_parent_organisation_id;

CREATE INDEX fki_organisation_parent_organisation_id
  ON admin.organisation
  USING btree
  (parent_organisation_id);

-- Index: admin.organisation_id

-- DROP INDEX admin.organisation_id;

CREATE UNIQUE INDEX organisation_id
  ON admin.organisation
  USING btree
  (id);

-- Table: admin.practitioner

-- DROP TABLE admin.practitioner;

CREATE TABLE admin.practitioner
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  name character varying(1024) NOT NULL,
  role_code character varying(50) NOT NULL,
  role_desc character varying(255) NOT NULL,
  CONSTRAINT pk_practitioner_id PRIMARY KEY (id),
  CONSTRAINT fk_practitioner_organisation_id FOREIGN KEY (id)
      REFERENCES admin.organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE admin.practitioner
  OWNER TO postgres;

-- Index: admin.practitioner_id

-- DROP INDEX admin.practitioner_id;

CREATE UNIQUE INDEX practitioner_id
  ON admin.practitioner
  USING btree
  (id);

-- Table: admin.schedule

-- DROP TABLE admin.schedule;

CREATE TABLE admin.schedule
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  type character varying(255) NOT NULL,
  location character varying(255) NOT NULL,
  CONSTRAINT pk_schedule_id PRIMARY KEY (id),
  CONSTRAINT fk_schedule_organisation_id FOREIGN KEY (organisation_id)
      REFERENCES admin.organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE admin.schedule
  OWNER TO postgres;

-- Index: admin.schedule_id

-- DROP INDEX admin.schedule_id;

CREATE UNIQUE INDEX schedule_id
  ON admin.schedule
  USING btree
  (id);

