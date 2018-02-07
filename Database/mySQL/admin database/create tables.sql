
DROP TABLE IF EXISTS enterprise_admin.ActiveItem;
DROP TABLE IF EXISTS enterprise_admin.Audit;
DROP TABLE IF EXISTS enterprise_admin.DependencyType;
DROP TABLE IF EXISTS enterprise_admin.Item;
DROP TABLE IF EXISTS enterprise_admin.ItemDependency;
DROP TABLE IF EXISTS enterprise_admin.ItemType;
DROP TABLE IF EXISTS enterprise_admin.CodeSet;
DROP TABLE IF EXISTS enterprise_data_pseudonymised.CohortPatients;
DROP TABLE IF EXISTS enterprise_data_pseudonymised.CohortResult;
DROP TABLE IF EXISTS enterprise_data_pseudonymised.ReportSchedule;
DROP TABLE IF EXISTS enterprise_data_pseudonymised.ReportResult;
DROP TABLE IF EXISTS enterprise_data_pseudonymised.ReportResultQuery;
DROP TABLE IF EXISTS enterprise_data_pseudonymised.ReportResultOrganisation;
DROP TABLE IF EXISTS enterprise_data_pseudonymised.reportrow;
DROP TABLE IF EXISTS enterprise_admin.incidence_prevalence_result;
DROP TABLE IF EXISTS enterprise_admin.incidence_prevalence_organisation_group_lookup;
DROP TABLE IF EXISTS enterprise_admin.incidence_prevalence_organisation_group;

CREATE TABLE enterprise_admin.ActiveItem (
	ActiveItemUuid char(36) NOT NULL,
	OrganisationUuid char(36) NOT NULL,
	ItemUuid char(36) NOT NULL,
	AuditUuid char(36) NOT NULL,
	ItemTypeId smallint NOT NULL,
	IsDeleted TINYINT(1) NOT NULL,
	PRIMARY KEY (ActiveItemUuid)
);
CREATE TABLE enterprise_admin.Audit (
	AuditUuid char(36) NOT NULL,
	EndUserUuid char(36) NOT NULL,
	TimeStamp timestamp NOT NULL,
	OrganisationUuid char(36) NOT NULL,
	PRIMARY KEY (AuditUuid)
);
CREATE TABLE enterprise_admin.DependencyType (
	DependencyTypeId smallint NOT NULL,
	Description varchar(100) NOT NULL,
	PRIMARY KEY (DependencyTypeId)
);
CREATE TABLE enterprise_admin.Item (
	ItemUuid char(36) NOT NULL,
	AuditUuid char(36) NOT NULL,
	XmlContent text NOT NULL,
	Title varchar(255) NOT NULL,
	Description text NOT NULL,
	IsDeleted TINYINT(1) NOT NULL,
	PRIMARY KEY (AuditUuid,ItemUuid)
);
CREATE TABLE enterprise_admin.ItemDependency (
	ItemUuid char(36) NOT NULL,
	AuditUuid char(36) NOT NULL,
	DependentItemUuid char(36) NOT NULL,
	DependencyTypeId smallint NOT NULL,
	PRIMARY KEY (AuditUuid,DependentItemUuid,ItemUuid)
);
CREATE TABLE enterprise_admin.ItemType (
	ItemTypeId smallint NOT NULL,
	Description varchar(100) NOT NULL,
	PRIMARY KEY (ItemTypeId)
);

CREATE TABLE enterprise_admin.CodeSet (
	ItemUuid char(36) NOT NULL,
	SnomedConceptId bigint NOT NULL,
	PRIMARY KEY (ItemUuid, SnomedConceptId)
);

CREATE TABLE enterprise_data_pseudonymised.CohortPatients (
  CohortPatientId int(11) NOT NULL AUTO_INCREMENT,
  RunDate timestamp(3) NULL DEFAULT NULL,
  QueryItemUuid char(36) NOT NULL,
  OrganisationId BIGINT(20) NOT NULL,
  PatientId BIGINT(20) NOT NULL,
  PRIMARY KEY (CohortPatientId),
  KEY RunDate (RunDate),
  KEY QueryItemUuid (QueryItemUuid),
  KEY OrganisationId (OrganisationId)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE enterprise_data_pseudonymised.CohortResult (
  CohortResultId int(11) NOT NULL AUTO_INCREMENT,
  EndUserUuid char(36) NOT NULL,
  BaselineDate timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  RunDate timestamp(3) NULL DEFAULT NULL,
  OrganisationId BIGINT(20) NOT NULL,
  QueryItemUuid char(36) NOT NULL,
  PopulationTypeId tinyint(1) NOT NULL,
  DenominatorCount int(11) DEFAULT NULL,
  EnumeratorCount int(11) DEFAULT NULL,
  PRIMARY KEY (CohortResultId),
  KEY RunDate (RunDate),
  KEY QueryItemUuid (QueryItemUuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

INSERT INTO enterprise_admin.ItemType(ItemTypeId, Description) VALUES (5, 'CodeSet');
INSERT INTO enterprise_admin.ItemType(ItemTypeId, Description) VALUES (4, 'DataSource');
INSERT INTO enterprise_admin.ItemType(ItemTypeId, Description) VALUES (7, 'LibraryFolder');
INSERT INTO enterprise_admin.ItemType(ItemTypeId, Description) VALUES (2, 'Query');
INSERT INTO enterprise_admin.ItemType(ItemTypeId, Description) VALUES (3, 'Test');

INSERT INTO enterprise_admin.DependencyType(DependencyTypeId, Description) VALUES (0, 'IsChildOf');
INSERT INTO enterprise_admin.DependencyType(DependencyTypeId, Description) VALUES (1, 'IsContainedWithin');
INSERT INTO enterprise_admin.DependencyType(DependencyTypeId, Description) VALUES (2, 'Uses');


CREATE TABLE enterprise_data_pseudonymised.ReportResult (
  ReportResultId int(11) NOT NULL AUTO_INCREMENT,
  EndUserUuid char(36) NOT NULL,
  ReportItemUuid char(36) NOT NULL,
  RunDate timestamp NULL DEFAULT NULL,
  ReportRunParams text NOT NULL,
  PRIMARY KEY (ReportResultId),
  KEY RunDate (RunDate),
  KEY ReportItemUuid (ReportItemUuid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE enterprise_data_pseudonymised.ReportResultQuery (
	ReportResultId int(11) NOT NULL,
    QueryItemUuid char(36) NOT NULL
);

CREATE TABLE enterprise_data_pseudonymised.ReportResultOrganisation (
	ReportResultId int(11) NOT NULL,
    OrganisationId bigint(20) NOT NULL
);

CREATE TABLE enterprise_data_pseudonymised.ReportSchedule (
    ReportScheduleId int(11) NOT NULL AUTO_INCREMENT,
    ScheduledAt timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    EndUserUuid char(36) NOT NULL,
    ReportItemUuid char(36) NOT NULL,
    ReportRunParams text NOT NULL,
    ReportResultId int (11) NULL DEFAULT NULL,
    PRIMARY KEY (ReportScheduleId)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE reportrow (
  ReportRowId bigint(20) NOT NULL AUTO_INCREMENT,
  ReportResultId int(11) NOT NULL,
  PatientId bigint(20) NOT NULL,
  OrganisationId bigint(20) NOT NULL,
  Label varchar(250) NULL,
  ClinicalEffectiveDate DATE NULL,
  OriginalTerm varchar(1000) NULL,
  OriginalCode varchar(20) NULL,
  SnomedConceptId bigint(20) NULL,
  Value double NULL,
  Units varchar(50) NULL,
  PRIMARY KEY (ReportRowId),
  KEY ReportResultId (ReportResultId),
  KEY PatientId (PatientId),
  KEY OrganisationId (OrganisationId)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


CREATE TABLE enterprise_admin.incidence_prevalence_result (
	query_id char(36) not null,
    query_title varchar(100) null,
	min_date date not null, 
	max_date date not null, 
	incidence_male int null default 0, 
	incidence_female int null default 0, 
	incidence_other int null default 0,  
	population_male int null default 0, 
	population_female int null default 0,
	population_other int null default 0,  
	prevalence_male int null default 0, 
	prevalence_female int null default 0, 
	prevalence_other int null default 0
);

create table enterprise_admin.incidence_prevalence_organisation_group (
	group_id int not null auto_increment primary key,
	group_name varchar(100) not null,

	index ix_incidence_prevalence_organisation_group_lookup_group_name (group_name)
);

create table enterprise_admin.incidence_prevalence_organisation_group_lookup (
	group_id int not null,
	ods_code varchar(50) not null,

	primary key pk_organisation_group_lookup_group_id_organisation_id (group_id, ods_code)
);
