use master

-- Note 1 - Don't run this on Azure - you will need to create the database 
--          through the Azure portal.

go

if exists
(
  select * 
  from sys.databases 
  where name = 'Endeavour_Enterprise'
)
begin
  drop database Endeavour_Enterprise
end

create database Endeavour_Enterprise on
primary
(
  name = Endeavour_Enterprise,
  filename = 'x:\databases\Endeavour_Enterprise\Enterprise_Core.mdf',
  size = 10,
  maxsize = unlimited,
  filegrowth = 10
)
log on
( 
  name = Endeavour_Enterprise_Log,
  filename = 'x:\databases\Endeavour_Enterprise\Endeavour_Enterprise_Log.ldf',
  size = 10,
  maxsize = unlimited,
  filegrowth = 10
)
collate SQL_Latin1_General_CP1_CI_AS

alter database Enterprise_Core set recovery simple with no_wait

go
