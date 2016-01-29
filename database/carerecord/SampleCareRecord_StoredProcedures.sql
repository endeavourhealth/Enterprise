use SampleCareRecord;

go

----------DROP OBJECTS----------

if (object_id('EndeavourEnterprise.GetStatistics') is not null)
	drop procedure EndeavourEnterprise.GetStatistics;
	
if (object_id('EndeavourEnterprise.GetRecords') is not null)
	drop procedure EndeavourEnterprise.GetRecords;

IF EXISTS (SELECT * FROM sys.schemas WHERE name = 'EndeavourEnterprise')
	drop schema EndeavourEnterprise;

go

---------CREATE OBJECTS------

create schema EndeavourEnterprise;

go

create procedure EndeavourEnterprise.GetStatistics
as
begin

	set transaction isolation level read committed;

	select count(*), min(p.PatientId), max(p.PatientId)
	from dbo.Patient as p;

end;

go

create procedure EndeavourEnterprise.GetRecords
(
	@MinimumId int,
	@MaximumId int,
	@RegisteredOnly bit,
	@OrganisationUuidFilter uniqueidentifier  --optional
)
as
begin

	set transaction isolation level read committed;

	declare @OrganisationId int = null;
	declare @RegisteredPatientStatus tinyint = 1;

	if (@OrganisationUuidFilter is not null)
	begin
		select @OrganisationId = o.OrganisationId
		from dbo.Organisation as o
		where o.GUID = @OrganisationUuidFilter;
	end

	declare @patients table 
	(
		PatientId int not null primary key
	);

	insert into @patients
	select p.PatientId
	from dbo.Patient as p
	inner join dbo.PatientStatus as s on s.PatientStatusId = p.PatientStatusId
	where p.PatientId >= @MinimumId and p.PatientId <= @MaximumId
	and (@OrganisationId is null or p.RegistrationOrganisationId = @OrganisationId)
	and (@RegisteredOnly = 0 or s.CaseloadPatientStatusId = @RegisteredPatientStatus);

	
	select
		p.PatientId,
		p.DateOfBirth,
		p.Sex,
		p.CallingName
	from dbo.Patient as p
	inner join @Patients as j on j.PatientId = p.PatientId;

	select
		o.PatientId,
		o.LegacyCode,
		o.OriginalTerm,
		o.AvailabilityTimeStamp,
		o.EffectiveDate,
		o.NumericValue
	from CareRecord.Observation as o
	inner join @patients as p on p.PatientId = o.PatientId;
	
	select
		i.PatientId,
		i.LegacyCode,
		i.OriginalTerm,
		i.AvailabilityTimeStamp,
		i.EffectiveDate
	from Prescribing.IssueRecord as i
	inner join @patients as p on p.PatientId = i.PatientId;

	
end;

