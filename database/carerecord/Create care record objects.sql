use SampleCareRecord;

go

----------DROP OBJECTS----------

if (object_id('EndeavourDiscovery.GetStatistics') is not null)
	drop procedure EndeavourDiscovery.GetStatistics;
	
if (object_id('EndeavourDiscovery.GetRecords') is not null)
	drop procedure EndeavourDiscovery.GetRecords;

IF EXISTS (SELECT * FROM sys.schemas WHERE name = 'EndeavourDiscovery')
	drop schema EndeavourDiscovery;

go

---------CREATE OBJECTS------

create schema EndeavourDiscovery;

go

create procedure EndeavourDiscovery.GetStatistics
as
begin

	set transaction isolation level read committed;

	select count(*), min(p.PatientId), max(p.PatientId)
	from dbo.Patient as p;

end;

go

create procedure EndeavourDiscovery.GetRecords
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

