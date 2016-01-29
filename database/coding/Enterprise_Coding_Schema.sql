use Enterprise_Coding;

go

----------DROP ALL OBJECTS----------

declare @n char(1)
set @n = char(10)

declare @stmt nvarchar(max)

-- procedures
select @stmt = isnull( @stmt + @n, '' ) +
    'drop procedure [' + schema_name(schema_id) + '].[' + name + ']'
from sys.procedures

-- check constraints
select @stmt = isnull( @stmt + @n, '' ) +
'alter table [' + schema_name(schema_id) + '].[' + object_name( parent_object_id ) + ']    drop constraint [' + name + ']'
from sys.check_constraints

-- functions
select @stmt = isnull( @stmt + @n, '' ) +
    'drop function [' + schema_name(schema_id) + '].[' + name + ']'
from sys.objects
where type in ( 'FN', 'IF', 'TF' )

-- views
select @stmt = isnull( @stmt + @n, '' ) +
    'drop view [' + schema_name(schema_id) + '].[' + name + ']'
from sys.views

-- foreign keys
select @stmt = isnull( @stmt + @n, '' ) +
    'alter table [' + schema_name(schema_id) + '].[' + object_name( parent_object_id ) + '] drop constraint [' + name + ']'
from sys.foreign_keys

-- tables
select @stmt = isnull( @stmt + @n, '' ) +
    'drop table [' + schema_name(schema_id) + '].[' + name + ']'
from sys.tables

-- user defined types
select @stmt = isnull( @stmt + @n, '' ) +
    'drop type [' + schema_name(schema_id) + '].[' + name + ']'
from sys.types
where is_user_defined = 1

-- schemas
select @stmt = isnull( @stmt + @n, '' ) +
    'drop schema [' + name + ']'
from sys.schemas
where principal_id = 1 and name != 'dbo'


exec sp_executesql @stmt

go


-----------------------------ADMINISTRATION-----------------------------
------------------------------------------------------------------------

create schema ReadV2;

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