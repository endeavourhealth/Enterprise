use Endeavour_Enterprise

go

execute sp_executesql N'create schema Administration;'
execute sp_executesql N'create schema [Definition];'
execute sp_executesql N'create schema Execution;'
execute sp_executesql N'create schema ReadV2;'
execute sp_executesql N'create schema Logging;'

go

create table Administration.Organisation
(
	OrganisationUuid uniqueidentifier not null,
	Name varchar(100) not null,

	constraint PK_Administration_Organisation primary key clustered (OrganisationUuid)
);

create table Administration.[User]
(
	UserUuid uniqueidentifier not null,
	EmailAddress varchar(200) not null,
	Title varchar(35) null,
    GivenName varchar(35) null,
    FamilyName varchar(35) null

	constraint PK_Administration_User_UserUuid primary key clustered (UserUuid),
	constraint UQ_Administration_User_EmailAddress unique nonclustered (EmailAddress),
	constraint CK_Administration_User_EmailAddress check (len(ltrim(rtrim(EmailAddress))) > 0)
);

create table Administration.UserPassword
(
	UserUuid uniqueidentifier not null,
	PasswordHash varchar(500) not null,
	PasswordCreated datetime2 not null
              
	constraint PK_Administration_UserPassword_UserUuid primary key clustered (UserUuid),
	constraint FK_Administration_UserPassword_UserUuid foreign key (UserUuid) references Administration.[User] (UserUuid)
);

create table Administration.UserPreviousPassword
(
	UserUuid uniqueidentifier not null,
	PasswordCreated datetime2 not null,
	PasswordHash varchar(500) not null

	constraint PK_Administration_UserPreviousPassword_UserUuid_PasswordCreated primary key clustered (UserUuid, PasswordCreated),
	constraint FK_Administration_UserPreviousPassword_UserUuid foreign key (UserUuid) references Administration.[User] (UserUuid)
);

create table Administration.UserAtOrganisation
(
	UserUuid uniqueidentifier not null,
	OrganisationUuid uniqueidentifier not null,

	constraint PK_Administration_UserAtOrganisation primary key clustered (UserUuid, OrganisationUuid)
);

go

create table [Definition].ItemType
(
	ItemTypeId tinyint not null,
	[Description] varchar(100) not null,

	constraint PK_Definition_ItemType primary key clustered (ItemTypeId),
	constraint UQ_Definition_ItemType_Description unique ([Description])
);

insert into [Definition].ItemType
	(ItemTypeId, [Description])
values
	(0, 'Folder'),
	(1, 'Report'),
	(2, 'Query'),
	(3, 'Test'),
	(4, 'Datasource'),
	(5, 'CodeSet');

create table [Definition].Module
(
	ModuleId tinyint not null,
	[Description] varchar(100) not null,

	constraint PK_Definition_Module primary key clustered (ModuleId),
	constraint UQ_Definition_Module_Description unique ([Description])
);

insert into [Definition].Module
	(ModuleId, [Description])
values
	(0, 'Library'),
	(1, 'Searches'),
	(2, 'Reports');

create table [Definition].[Audit]
(
	AuditId int identity not null,
	UserUuid uniqueidentifier not null,
	[DateTime] datetime2 not null,

	constraint PK_Definition_Audit primary key clustered (AuditId),

	constraint FK_Definition_Audit_UserUuid foreign key (UserUuid)
		references Administration.[User] (UserUuid) on delete no action on update cascade
);

create table [Definition].Items
(
	ItemUuid uniqueidentifier not null,
	AuditId int not null,
	Content varchar(max) null,
	IsDeleted bit not null,
	OwnerOrganisationUuid uniqueidentifier not null,
	ModuleId tinyint not null,
	ItemTypeId tinyint not null,
	Title varchar(100) not null,
	[Description] varchar(max) null,
	
	constraint PK_Definition_Items_ItemUuid_AuditId primary key clustered (ItemUuid, AuditId),

	constraint FK_Definition_Items_OwnerOrganisationUuid foreign key (OwnerOrganisationUuid)
		references Administration.Organisation (OrganisationUuid) on delete no action on update cascade,
	constraint FK_Definition_Items_ModuleId foreign key (ModuleId)
		references [Definition].Module (ModuleId) on delete cascade on update cascade,
	constraint FK_Definition_Items_ItemTypeId foreign key (ItemTypeId)
		references [Definition].ItemType (ItemTypeId) on delete no action on update cascade,
);

create table [Definition].ActiveItems
(
	ItemUuid uniqueidentifier not null,
	CurrentAuditId int not null,

	--denormalised for speed
	OwnerOrganisationUuid uniqueidentifier not null,
	ModuleId tinyint not null,
	ItemTypeId tinyint not null,
	Title varchar(100) not null,

	constraint PK_Definition_ActiveItems_ItemUuid primary key clustered (ItemUuid),

	constraint FK_Definition_ActiveItems_ItemUuid_CurrentAuditId foreign key (ItemUuid, CurrentAuditId)
		references [Definition].Items (ItemUuid, AuditId) on delete cascade on update cascade,
	constraint FK_Definition_ActiveItems_OwnerOrganisationUuid foreign key (OwnerOrganisationUuid)
		references Administration.Organisation (OrganisationUuid) on delete no action on update no action,
	constraint FK_Definition_ActiveItems_ModuleId foreign key (ModuleId)
		references [Definition].Module (ModuleId) on delete no action on update no action,
	constraint FK_Definition_ActiveItems_ItemTypeId foreign key (ItemTypeId)
		references [Definition].ItemType (ItemTypeId) on delete no action on update no action
);

create nonclustered index IDX_Definition_ActiveItems_OwnerOrganisationUuid_ModuleId_ItemTypeId on [Definition].ActiveItems (OwnerOrganisationUuid, ModuleId, ItemTypeId);


create table [Definition].DependencyType
(
	DependencyTypeId tinyint not null,
	[Description] varchar(100) not null,

	constraint PK_Definition_DependencyType primary key clustered (DependencyTypeId),
	constraint UQ_Definition_DependencyType_Description unique ([Description])
);

insert into [Definition].DependencyType
	(DependencyTypeId, [Description])
values
	(0, 'IsChildOf'),
	(1, 'IsContainedWithin'),	
	(2, 'Uses');


create table [Definition].ActiveItemDependency
(
	ItemUuid uniqueidentifier not null,
	DependsOnItemUuid uniqueidentifier not null,
	DependencyTypeId tinyint not null,

	constraint PK_Definition_ActiveItemDependency_ItemUuid_DependsOnItemUuid primary key clustered (ItemUuid, DependsOnItemUuid),

	constraint FK_Definition_ActiveItemDependency_ItemUuid foreign key (ItemUuid)
		references [Definition].ActiveItems (ItemUuid) on delete cascade on update cascade,

	constraint FK_Definition_ActiveItemDependency_DependsOnItemUuid foreign key (DependsOnItemUuid)
		references [Definition].ActiveItems (ItemUuid) on delete no action on update no action,

	constraint FK_Definition_ActiveItemDependency_DependencyTypeId foreign key (DependencyTypeId)
		references [Definition].DependencyType (DependencyTypeId) on delete no action on update no action
);

create nonclustered index IDX_Definition_ActiveItemDependency_DependsOnItemUuid on [Definition].ActiveItemDependency (DependsOnItemUuid);

go

create table Execution.[Status]
(
	StatusId tinyint not null,
	[Description] varchar(50) not null,
		
	constraint PK_Execution_Status_StatusId primary key clustered (StatusId),
	constraint UQ_Execution_Status_Description unique ([Description])
);

insert into Execution.[Status]
	(StatusId, [Description])
values
	(0, 'Executing'),
	(1, 'Succeeded'),
	(2, 'Failed');


create table Execution.Job
(
	JobUuid uniqueidentifier not null,
	StatusId tinyint not null,
	StartDateTime datetime2 not null,
	EndDateTime datetime2 null,
	BaselineAuditId int null,
	PatientsInDatabase int null,
	
	constraint PK_Execution_Job primary key clustered (JobUuid),
	constraint FK_Execution_Job_StatusId foreign key (StatusId)
		references Execution.[Status] (StatusId) on delete no action on update cascade,
);

create unique index UQ_Execution_Job_CurrentStatus on Execution.Job (StatusId)
	where StatusId = 0;


create table Execution.JobReport
(
	JobReportId int identity not null,
	JobUuid uniqueidentifier not null,
	ReportUuid uniqueidentifier not null,
	AuditId int not null,	
	UserUuid uniqueidentifier not null,
	OrganisationUuid uniqueidentifier not null,
	[Parameters] varchar(max) not null,

	constraint PK_Execution_JobReport_JobReportId primary key clustered (JobReportId),

	constraint FK_Execution_JobReport_JobUuid foreign key (JobUuid)
		references Execution.Job (JobUuid) on delete cascade on update cascade,
	constraint FK_Execution_JobReport_ReportUuid_AuditId foreign key (ReportUuid, AuditId)
		references [Definition].Items (ItemUuid, AuditId) on delete no action on update cascade,
	constraint FK_Execution_JobReport_UserUuid foreign key (UserUuid)
		references Administration.[User] (UserUuid) on delete no action on update cascade,
	constraint FK_Execution_JobReport_OrganisationUuid foreign key (OrganisationUuid)
		references Administration.Organisation (OrganisationUuid) on delete no action on update no action,
);

create table Execution.JobReportItem
(
	JobReportId int not null,
	ItemUuid uniqueidentifier not null,
	AuditId int not null,
	ResultCount int null,

	constraint PK_Execution_JobReportItem primary key clustered (JobReportId, ItemUuid, AuditId),

	constraint FK_Execution_JobReportItem_ItemUuid_AuditId foreign key (ItemUuid, AuditId)
		references [Definition].Items (ItemUuid, AuditId) on delete no action on update cascade
);

create table Execution.Request
(
	RequestId int identity not null,
	ReportItemUuid uniqueidentifier not null,
	[DateTime] datetime2 not null,
	UserUuid uniqueidentifier not null,
	OrganisationUuid uniqueidentifier not null,
	[Parameters] varchar(max) not null,
	JobUuid uniqueidentifier null,

	constraint PK_Execution_Request primary key clustered (RequestId),

	constraint FK_Execution_Request_ItemUuid foreign key (ReportItemUuid)
		references [Definition].ActiveItems (ItemUuid) on delete cascade on update cascade,
	constraint FK_Execution_Request_UserUuid foreign key (UserUuid)
		references Administration.[User] (UserUuid) on delete no action on update cascade,
	constraint FK_Execution_Request_OrganisationUuid foreign key (OrganisationUuid)
		references Administration.Organisation (OrganisationUuid) on delete no action on update no action,
	constraint FK_Execution_Request_JobUuid foreign key (JobUuid)
		references Execution.Job (JobUuid) on delete no action on update no action,
);

go

create table ReadV2.Code
(
	CodeId int identity not null,
	Code varchar(10) collate Latin1_General_CS_AS not null,
	Term varchar(max) not null,
	ParentCodeId int null,
	Discontinued bit not null,

	constraint PK_ReadV2_Code primary key clustered (CodeId),
	constraint UQ_ReadV2_Code_Code unique (Code),
	constraint FK_ReadV2_Code_ParentCodeId foreign key (ParentCodeId)
		references ReadV2.Code (CodeId) on delete no action on update no action

);

create table ReadV2.SynonymCode
(
	CompleteCode varchar(10) collate Latin1_General_CS_AS not null,
	RootCodeId int not null,
	SynonymousTermCode varchar(10) collate Latin1_General_CS_AS not null,
	Term varchar(max) not null,
	Discontinued bit not null,
	
	constraint PK_ReadV2_SynonymCode primary key clustered (CompleteCode),
	constraint UQ_ReadV2_SynonymCode_RootCodeId_SynonymousTermId unique (RootCodeId, SynonymousTermCode),

	constraint FK_ReadV2_SynonymCode_RootCodeId foreign key (RootCodeId)
		references ReadV2.Code (CodeId) on delete no action on update no action
);

go

create table Logging.logging_event
( 
	timestmp decimal(20) not null,
	formatted_message varchar(4000) not null,
	logger_name varchar(254) not null,
	level_string varchar(254) not null,
	thread_name varchar(254),
	reference_flag smallint,
	arg0 varchar(254),
	arg1 varchar(254),
	arg2 varchar(254),
	arg3 varchar(254),
	caller_filename varchar(254) not null,
	caller_class varchar(254) not null,
	caller_method varchar(254) not null,
	caller_line char(4) not null,
	event_id decimal(38) not null identity(1, 1)

	constraint PK_Logging_logging_event_event_id primary key clustered (event_id) 
)

create table Logging.logging_event_property 
( 
	event_id decimal(38) not null, 
	mapped_key varchar(254) not null, 
	mapped_value varchar(1024)

	constraint PK_Logging_logging_event_property_event_id_mapped_key primary key clustered (event_id, mapped_key), 
	constraint FK_Logging_logging_event_property_event_id foreign key (event_id) references Logging.logging_event (event_id)
)

create table Logging.logging_event_exception 
( 
	event_id decimal(38) not null, 
	i smallint not null, 
	trace_line varchar(254) not null

	constraint PK_Logging_logging_event_exception_event_id_i primary key clustered (event_id, i), 
	constraint FK_Logging_logging_event_exception_event_id foreign key (event_id) references Logging.logging_event (event_id)
)

go
