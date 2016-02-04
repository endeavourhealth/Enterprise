use master

go

if exists
(
  select * 
  from sys.databases 
  where name = 'Enterprise_Logging'
)
begin
  drop database Enterprise_Logging
end

create database Enterprise_Logging on
primary
(
  name = Enterprise_Logging,
  filename = 'x:\databases\Enterprise_Logging\Enterprise_Logging.mdf',
  size = 10,
  maxsize = unlimited,
  filegrowth = 10
)
log on
( 
  name = Enterprise_Logging_Log,
  filename = 'x:\databases\Enterprise_Logging\Enterprise_Logging_Log.ldf',
  size = 10,
  maxsize = unlimited,
  filegrowth = 10
)
collate SQL_Latin1_General_CP1_CI_AS

alter database Enterprise_Core set recovery simple with no_wait

go
