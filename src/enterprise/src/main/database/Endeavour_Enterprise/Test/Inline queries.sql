select
  c.[name] as ColumnName,
  t.[name] as TypeName,
  c.is_nullable as IsNullable,
  c.collation_name as Collation,
  (
	select cast(1 as bit)
	from sys.index_columns as ic
	inner join sys.indexes as i on i.index_id = ic.index_id
		and i.[object_id] = ic.[object_id]
	where ic.[object_id] = c.[object_id]
		and ic.column_id = c.column_id
		and i.is_primary_key = 1
  ) as IsPrimaryKey
from sys.columns as c
inner join sys.objects as o on c.[object_id] = o.[object_id]
inner join sys.schemas as s on s.[schema_id] = o.[schema_id]
inner join sys.types as t on c.system_type_id = t.system_type_id
where s.name = 'dbo'
and o.name = 'patient'
and o.[type] in ('U', 'V', 'TF')
and t.system_type_id = t.user_type_id
order by c.column_id;


---Find root folders

select
	a.ItemUuid,
	a.CurrentAuditId,
	a.Title,
	case when exists (select null from [Definition].ActiveItemDependency as dc where dc.DependsOnItemUuid = a.ItemUuid and dc.DependencyTypeId = 0)
        then 1
        else 0
	end as HasChildren
from Definition.ActiveItems as a
left join Definition.ActiveItemDependency as d
	on d.ItemUuid = a.ItemUuid
	and d.DependencyTypeId = 0
where a.OwnerOrganisationUuid = '04CF1D8D-B6E6-4E20-9A74-D6F197A9FE78'
and a.ModuleId = 0
and a.ItemTypeId = 0
and d.ItemUuid is null
	

---Find child folders

select
	a.ItemUuid,
	a.CurrentAuditId,
	a.Title,
	case when exists (select null from [Definition].ActiveItemDependency as dc where dc.DependsOnItemUuid = a.ItemUuid and dc.DependencyTypeId = 0)
        then 1
        else 0
	end as HasChildren
from Definition.ActiveItems as a
inner join Definition.ActiveItemDependency as d
	on d.ItemUuid = a.ItemUuid
	and d.DependsOnItemUuid = 'C69970DA-CF6C-406B-BEEC-31762FC9A0A4'
	and d.DependencyTypeId = 0
where a.OwnerOrganisationUuid = '04CF1D8D-B6E6-4E20-9A74-D6F197A9FE78'
and a.ModuleId = 0
and a.ItemTypeId = 0
	

--Get folder contents

select
	a.ItemUuid,
	a.Title,
	a.ItemTypeId,
	aud.DateTime,
	(select dc.DependsOnItemUuid from [Definition].ActiveItemDependency as dc where dc.ItemUuid = a.ItemUuid and dc.DependencyTypeId = 0) as ParentUuid
from Definition.ActiveItems as a
inner join Definition.ActiveItemDependency as d
	on d.ItemUuid = a.ItemUuid
	and d.DependsOnItemUuid = '01BE7640-802F-416B-8FD4-CC58A133F40E'
	and d.DependencyTypeId = 1
inner join Definition.[Audit] as aud
	on aud.AuditId = a.CurrentAuditId
where a.OwnerOrganisationUuid = '04CF1D8D-B6E6-4E20-9A74-D6F197A9FE78'
and a.ModuleId = 0
and a.ItemTypeId != 0
order by a.Title

