use Endeavour_Enterprise_Data;

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

	select count(*), min(p.SK_PatientID), max(p.SK_PatientID)
	from [07T].Patients as p;

end;

go

create procedure EndeavourEnterprise.GetRecords
(
	@MinimumId int,
	@MaximumId int
)
as
begin

	set transaction isolation level read committed;
		
	declare @Patients table 
	(
		PatientId int not null primary key
	);

	insert into @Patients
	select p.SK_PatientID
	from [07T].[Patients] as p
	where p.SK_PatientID >= @MinimumId and p.SK_PatientID <= @MaximumId;
		
	select
		p.SK_PatientID,
		o.ServiceProviderCode,
		p.DateOfBirth,
		floor( (cast(getdate() as integer) - cast( cast(p.DateOfBirth as datetime) as integer)) / 365.25) as Age,
		p.Gender,
		p.DateRegistered
	from [07T].Patients as p
	inner join shared.Organisation as o on o.SK_ServiceProviderID = p.SK_ServiceProviderID
	inner join @Patients as j on j.PatientId = p.SK_PatientID;

	select
		o.SK_PatientID,
		o.[SnomedConceptCode],
		--o.LegacyCode,
		--o.OriginalTerm,
		o.EventDate,
		o.Value
	from [07T].[GPEncounter] as o  --Observation
	inner join @Patients as p on p.PatientId = o.SK_PatientID;
	
	select
		i.SK_PatientID,
		i.DMDCode,
		i.MedicationTerm,
		i.IssueDate
	from [07T].[GPMedication] as i
	inner join @Patients as p on p.PatientId = i.SK_PatientID;

	
end;

