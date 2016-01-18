

insert into ReadV2.Code
	(Code, Term, ParentCodeId, Discontinued)
select
	replace(c.[Column 0], '.', ''),
	iif ( c.[Column 3] != '', c.[Column 3], 
		iif ( c.[Column 2] != '', c.[Column 2], c.[Column 1])
		),
	null,
	cast([Column 11] as bit)
from Scratch.dbo.Corev2 as c


update c
set c.ParentCodeId = p.CodeId
from ReadV2.Code as c
inner join ReadV2.Code as p on p.Code = left(c.Code, len(c.Code) - 1)
where len(c.Code) > 1


create table #keyv2temp
(
	Id int identity not null,
	RootCodeId int not null,
	SynonymousTermCode varchar(10) collate Latin1_General_CS_AS not null,
	Term varchar(max) not null,
	Discontinued bit not null
)


insert into #keyv2temp
(RootCodeId, SynonymousTermCode, Term, Discontinued)
select
	c.CodeId,
	iif ( k.[Column 5] like '1%', SUBSTRING(k.[Column 5], 2, 8000), k.[Column 5]),
	iif ( k.[Column 4] != '', k.[Column 4], 
		iif ( k.[Column 3] != '', k.[Column 3], k.[Column 2])
		),
	cast(k.[Column 8] as bit)
from Scratch.dbo.Keyv2 as k
inner join ReadV2.Code as c on c.Code = replace(k.[Column 7] collate Latin1_General_CS_AS, '.', '') 
where k.[Column 5] != '00'


insert into [ReadV2].[SynonymCode]
(CompleteCode, RootCodeId, SynonymousTermCode, Term, Discontinued)
select
	c.Code + '-' + k.SynonymousTermCode,
	c.CodeId,
	k.SynonymousTermCode,
	k.Term,
	k.Discontinued
from #keyv2temp as k
inner join ReadV2.Code as c on c.CodeId = k.RootCodeId
group by c.Code, c.CodeId, k.SynonymousTermCode, k.Term, k.Discontinued
