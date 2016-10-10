declare @JobUuid uniqueidentifier;

select top 1 @JobUuid = j.JobUuid
from [Execution].[Job] as j
order by j.StartDateTime desc

--------------------------------------------------

select *
from [Execution].[Job] as j
left join Execution.Status as s on s.StatusId = j.StatusId
where j.JobUuid = @JobUuid

select *
from Execution.JobReport as r
where r.JobUuid = @JobUuid

select o.*
from Execution.JobReportOrganisation as o
inner join Execution.JobReport as r on r.JobReportUuid = o.JobReportUuid
where r.JobUuid = @JobUuid

select *
from Execution.JobContent as r
where r.JobUuid = @JobUuid

select i.*
from Execution.JobReportItem as i
inner join Execution.JobReport as r on r.JobReportUuid = i.JobReportUuid
where r.JobUuid = @JobUuid

select o.*
from Execution.JobReportItemOrganisation as o
inner join Execution.JobReportItem as i on i.JobReportItemUuid = o.JobReportItemUuid
inner join Execution.JobReport as r on r.JobReportUuid = i.JobReportUuid
where r.JobUuid = @JobUuid
