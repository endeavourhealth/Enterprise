use master

go

if exists
(
  select * 
  from sys.databases 
  where name = 'Enterprise_Coding'
)
begin
  drop database Enterprise_Coding
end

create database Enterprise_Coding on
primary
(
  name = Enterprise_Coding,
  filename = 'x:\databases\Enterprise_Coding\Enterprise_Coding.mdf',
  size = 10,
  maxsize = unlimited,
  filegrowth = 10
)
log on
( 
  name = Enterprise_Coding_Log,
  filename = 'x:\databases\Enterprise_Coding\Enterprise_Coding_Log.ldf',
  size = 10,
  maxsize = unlimited,
  filegrowth = 10
)
collate SQL_Latin1_General_CP1_CI_AS

alter database Enterprise_Core set recovery simple with no_wait

go
