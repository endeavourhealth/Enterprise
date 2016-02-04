use master

go

if exists
(
  select * 
  from sys.databases 
  where name = 'Enterprise_Core'
)
begin
  drop database Enterprise_Core
end

create database Enterprise_Core on
primary
(
  name = Enterprise_Core,
  filename = 'x:\databases\Enterprise_Core\Enterprise_Core.mdf',
  size = 10,
  maxsize = unlimited,
  filegrowth = 10
)
log on
( 
  name = Enterprise_Core_Log,
  filename = 'x:\databases\Enterprise_Core\Enterprise_Core_Log.ldf',
  size = 10,
  maxsize = unlimited,
  filegrowth = 10
)
collate SQL_Latin1_General_CP1_CI_AS

alter database Enterprise_Core set recovery simple with no_wait

go
