-- noinspection SqlNoDataSourceInspectionForFile
USE [master]
GO
/****** Object:  Database [Endeavour_Enterprise]    Script Date: 20/03/2016 12:57:55 ******/
CREATE DATABASE [Endeavour_Enterprise]
 CONTAINMENT = NONE
 ON  PRIMARY
( NAME = N'Enterprise', FILENAME = N'D:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\Enterprise.mdf' , SIZE = 5120KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON
( NAME = N'Enterprise_log', FILENAME = N'D:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\Enterprise_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [Endeavour_Enterprise] SET COMPATIBILITY_LEVEL = 120
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [Endeavour_Enterprise].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [Endeavour_Enterprise] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET ANSI_NULLS OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET ANSI_PADDING OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET ARITHABORT OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [Endeavour_Enterprise] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET CURSOR_DEFAULT  GLOBAL
GO
ALTER DATABASE [Endeavour_Enterprise] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET  DISABLE_BROKER
GO
ALTER DATABASE [Endeavour_Enterprise] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [Endeavour_Enterprise] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET RECOVERY SIMPLE
GO
ALTER DATABASE [Endeavour_Enterprise] SET  MULTI_USER
GO
ALTER DATABASE [Endeavour_Enterprise] SET PAGE_VERIFY CHECKSUM
GO
ALTER DATABASE [Endeavour_Enterprise] SET DB_CHAINING OFF
GO
ALTER DATABASE [Endeavour_Enterprise] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF )
GO
ALTER DATABASE [Endeavour_Enterprise] SET TARGET_RECOVERY_TIME = 0 SECONDS
GO
ALTER DATABASE [Endeavour_Enterprise] SET DELAYED_DURABILITY = DISABLED
GO
USE [Endeavour_Enterprise]
GO
/****** Object:  User [test]    Script Date: 20/03/2016 12:57:55 ******/
CREATE USER [test] FOR LOGIN [test] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  User [Endeavour_Enterprise_ApplicationUser]    Script Date: 20/03/2016 12:57:55 ******/
CREATE USER [Endeavour_Enterprise_ApplicationUser] FOR LOGIN [Endeavour_Enterprise_ApplicationUser] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_datareader] ADD MEMBER [Endeavour_Enterprise_ApplicationUser]
GO
ALTER ROLE [db_datawriter] ADD MEMBER [Endeavour_Enterprise_ApplicationUser]
GO
/****** Object:  Schema [Administration]    Script Date: 20/03/2016 12:57:55 ******/
CREATE SCHEMA [Administration]
GO
/****** Object:  Schema [Definition]    Script Date: 20/03/2016 12:57:55 ******/
CREATE SCHEMA [Definition]
GO
/****** Object:  Schema [Execution]    Script Date: 20/03/2016 12:57:55 ******/
CREATE SCHEMA [Execution]
GO
/****** Object:  Schema [Logging]    Script Date: 20/03/2016 12:57:56 ******/
CREATE SCHEMA [Logging]
GO
/****** Object:  Schema [ReadV2]    Script Date: 20/03/2016 12:57:56 ******/
CREATE SCHEMA [ReadV2]
GO
/****** Object:  UserDefinedFunction [Logging].[ConvertTimestamp]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE FUNCTION [Logging].[ConvertTimestamp] (@TimeStamp Decimal(20,0))
RETURNS DateTime
AS

BEGIN

	DECLARE @UTC BIGINT
	SET @UTC = @TimeStamp
	RETURN DATEADD(MILLISECOND, @UTC % 1000, DATEADD(SECOND, @UTC / 1000, '19700101'))

END;


GO
/****** Object:  Table [Administration].[EndUser]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Administration].[EndUser](
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[Title] [varchar](255) NOT NULL,
	[Forename] [varchar](255) NOT NULL,
	[Surname] [varchar](255) NOT NULL,
	[Email] [varchar](255) NOT NULL,
	[IsSuperUser] [bit] NOT NULL,
 CONSTRAINT [PK_Person] PRIMARY KEY CLUSTERED
(
	[EndUserUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Administration].[EndUserEmailInvite]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Administration].[EndUserEmailInvite](
	[EndUserEmailInviteUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[UniqueToken] [varchar](255) NOT NULL,
	[DtCompleted] [datetime] NOT NULL,
 CONSTRAINT [PK_PersonEmailInvite] PRIMARY KEY CLUSTERED
(
	[EndUserEmailInviteUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Administration].[EndUserPwd]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Administration].[EndUserPwd](
	[EndUserPwdUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[PwdHash] [varchar](500) NOT NULL,
	[DtExpired] [datetime] NOT NULL,
 CONSTRAINT [PK_PersonPwd] PRIMARY KEY CLUSTERED
(
	[EndUserPwdUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Administration].[Organisation]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Administration].[Organisation](
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[Name] [varchar](255) NOT NULL,
	[NationalId] [varchar](255) NOT NULL,
 CONSTRAINT [PK_Organisation] PRIMARY KEY CLUSTERED
(
	[OrganisationUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Administration].[OrganisationEndUserLink]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Administration].[OrganisationEndUserLink](
	[OrganisationEndUserLinkUuid] [uniqueidentifier] NOT NULL,
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[Permissions] [int] NOT NULL,
	[DtExpired] [datetime] NOT NULL,
 CONSTRAINT [PK_OrganisationPersonLink] PRIMARY KEY CLUSTERED
(
	[OrganisationEndUserLinkUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [Definition].[ActiveItem]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Definition].[ActiveItem](
	[ActiveItemUuid] [uniqueidentifier] NOT NULL,
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[ItemUuid] [uniqueidentifier] NOT NULL,
	[Version] [int] NOT NULL,
	[ItemTypeId] [int] NOT NULL,
 CONSTRAINT [PK_ActiveItem] PRIMARY KEY CLUSTERED
(
	[ActiveItemUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [Definition].[ActiveItemDependency]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Definition].[ActiveItemDependency](
	[ActiveItemDependencyUuid] [uniqueidentifier] NOT NULL,
	[ItemUuid] [uniqueidentifier] NOT NULL,
	[DependentItemUuid] [uniqueidentifier] NOT NULL,
	[DependencyTypeId] [int] NOT NULL,
 CONSTRAINT [PK_ActiveItemDependency] PRIMARY KEY CLUSTERED
(
	[ActiveItemDependencyUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [Definition].[DependencyType]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Definition].[DependencyType](
	[DependencyTypeId] [tinyint] NOT NULL,
	[Description] [varchar](100) NOT NULL,
 CONSTRAINT [PK_Definition_DependencyType] PRIMARY KEY CLUSTERED
(
	[DependencyTypeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_Definition_DependencyType_Description] UNIQUE NONCLUSTERED
(
	[Description] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Definition].[Item]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Definition].[Item](
	[ItemUuid] [uniqueidentifier] NOT NULL,
	[Version] [int] NOT NULL,
	[XmlContent] [varchar](max) NOT NULL,
	[Title] [varchar](255) NOT NULL,
	[Description] [varchar](max) NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[TimeStamp] [datetime] NOT NULL,
	[IsDeleted] [bit] NOT NULL,
 CONSTRAINT [PK_Item] PRIMARY KEY CLUSTERED
(
	[ItemUuid] ASC,
	[Version] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Definition].[ItemType]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Definition].[ItemType](
	[ItemTypeId] [tinyint] NOT NULL,
	[Description] [varchar](100) NOT NULL,
 CONSTRAINT [PK_Definition_ItemType] PRIMARY KEY CLUSTERED
(
	[ItemTypeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_Definition_ItemType_Description] UNIQUE NONCLUSTERED
(
	[Description] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Execution].[Job]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Execution].[Job](
	[JobUuid] [uniqueidentifier] NOT NULL,
	[StatusId] [tinyint] NOT NULL,
	[StartDateTime] [datetime2](7) NOT NULL,
	[EndDateTime] [datetime2](7) NULL,
	[PatientsInDatabase] [int] NULL,
 CONSTRAINT [PK_Execution_Job] PRIMARY KEY CLUSTERED
(
	[JobUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [Execution].[JobReport]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Execution].[JobReport](
	[JobReportUuid] [uniqueidentifier] NOT NULL,
	[JobUuid] [uniqueidentifier] NOT NULL,
	[ReportUuid] [uniqueidentifier] NOT NULL,
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[TimeStamp] [datetime] NOT NULL,
	[Parameters] [varchar](max) NOT NULL,
	[StatusId] [tinyint] NOT NULL,
 CONSTRAINT [PK_JobReport] PRIMARY KEY CLUSTERED
(
	[JobReportUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Execution].[JobReportItem]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Execution].[JobReportItem](
	[JobReportItemUuid] [uniqueidentifier] NOT NULL,
	[JobReportUuid] [uniqueidentifier] NOT NULL,
	[ItemUuid] [uniqueidentifier] NOT NULL,
	[ResultCount] [int] NULL,
 CONSTRAINT [PK_JobReportItem] PRIMARY KEY CLUSTERED
(
	[JobReportItemUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [Execution].[Request]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Execution].[Request](
	[RequestUuid] [uniqueidentifier] NOT NULL,
	[ReportUuid] [uniqueidentifier] NOT NULL,
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[TimeStamp] [datetime] NOT NULL,
	[Parameters] [varchar](max) NOT NULL,
	[JobUuid] [uniqueidentifier] NULL,
 CONSTRAINT [PK_Request] PRIMARY KEY CLUSTERED
(
	[RequestUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Execution].[Status]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Execution].[Status](
	[StatusId] [tinyint] NOT NULL,
	[Description] [varchar](50) NOT NULL,
 CONSTRAINT [PK_Execution_Status_StatusId] PRIMARY KEY CLUSTERED
(
	[StatusId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_Execution_Status_Description] UNIQUE NONCLUSTERED
(
	[Description] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Logging].[logging_event]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Logging].[logging_event](
	[timestmp] [decimal](20, 0) NOT NULL,
	[formatted_message] [varchar](4000) NOT NULL,
	[logger_name] [varchar](254) NOT NULL,
	[level_string] [varchar](254) NOT NULL,
	[thread_name] [varchar](254) NULL,
	[reference_flag] [smallint] NULL,
	[arg0] [varchar](254) NULL,
	[arg1] [varchar](254) NULL,
	[arg2] [varchar](254) NULL,
	[arg3] [varchar](254) NULL,
	[caller_filename] [varchar](254) NOT NULL,
	[caller_class] [varchar](254) NOT NULL,
	[caller_method] [varchar](254) NOT NULL,
	[caller_line] [char](4) NOT NULL,
	[event_id] [decimal](38, 0) IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_Logging_logging_event_event_id] PRIMARY KEY CLUSTERED
(
	[event_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Logging].[logging_event_exception]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Logging].[logging_event_exception](
	[event_id] [decimal](38, 0) NOT NULL,
	[i] [smallint] NOT NULL,
	[trace_line] [varchar](254) NOT NULL,
 CONSTRAINT [PK_Logging_logging_event_exception_event_id_i] PRIMARY KEY CLUSTERED
(
	[event_id] ASC,
	[i] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [Logging].[logging_event_property]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [Logging].[logging_event_property](
	[event_id] [decimal](38, 0) NOT NULL,
	[mapped_key] [varchar](254) NOT NULL,
	[mapped_value] [varchar](1024) NULL,
 CONSTRAINT [PK_Logging_logging_event_property_event_id_mapped_key] PRIMARY KEY CLUSTERED
(
	[event_id] ASC,
	[mapped_key] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [ReadV2].[Code]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [ReadV2].[Code](
	[CodeId] [int] IDENTITY(1,1) NOT NULL,
	[Code] [varchar](10) NOT NULL,
	[Term] [varchar](max) NOT NULL,
	[ParentCodeId] [int] NULL,
	[Discontinued] [bit] NOT NULL,
 CONSTRAINT [PK_ReadV2_Code] PRIMARY KEY CLUSTERED
(
	[CodeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_ReadV2_Code_Code] UNIQUE NONCLUSTERED
(
	[Code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [ReadV2].[SynonymCode]    Script Date: 20/03/2016 12:57:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [ReadV2].[SynonymCode](
	[CompleteCode] [varchar](10) NOT NULL,
	[RootCodeId] [int] NOT NULL,
	[SynonymousTermCode] [varchar](10) NOT NULL,
	[Term] [varchar](max) NOT NULL,
	[Discontinued] [bit] NOT NULL,
 CONSTRAINT [PK_ReadV2_SynonymCode] PRIMARY KEY CLUSTERED
(
	[CompleteCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_ReadV2_SynonymCode_RootCodeId_SynonymousTermId] UNIQUE NONCLUSTERED
(
	[RootCodeId] ASC,
	[SynonymousTermCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [Email]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [Email] ON [Administration].[EndUser]
(
	[Email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [EndUserUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [EndUserUuid] ON [Administration].[EndUser]
(
	[EndUserUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [EndUserEmailInviteUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [EndUserEmailInviteUuid] ON [Administration].[EndUserEmailInvite]
(
	[EndUserEmailInviteUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [EndUserPwdUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [EndUserPwdUuid] ON [Administration].[EndUserPwd]
(
	[EndUserPwdUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [EndUserUuid_DtExpired]    Script Date: 20/03/2016 12:57:56 ******/
CREATE NONCLUSTERED INDEX [EndUserUuid_DtExpired] ON [Administration].[EndUserPwd]
(
	[EndUserUuid] ASC,
	[DtExpired] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [OrganisationUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [OrganisationUuid] ON [Administration].[Organisation]
(
	[OrganisationUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [OrganisationEndUserLinkUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [OrganisationEndUserLinkUuid] ON [Administration].[OrganisationEndUserLink]
(
	[OrganisationEndUserLinkUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [ActiveItemUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [ActiveItemUuid] ON [Definition].[ActiveItem]
(
	[ActiveItemUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [ItemUuid_Version]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [ItemUuid_Version] ON [Definition].[ActiveItem]
(
	[ItemUuid] ASC,
	[Version] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [ActiveItemDependencyUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [ActiveItemDependencyUuid] ON [Definition].[ActiveItemDependency]
(
	[ActiveItemDependencyUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [DependentItemUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [DependentItemUuid] ON [Definition].[ActiveItemDependency]
(
	[DependentItemUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [ItemUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE NONCLUSTERED INDEX [ItemUuid] ON [Definition].[ActiveItemDependency]
(
	[ItemUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [ItemUuid_Version]    Script Date: 20/03/2016 12:57:56 ******/
CREATE UNIQUE NONCLUSTERED INDEX [ItemUuid_Version] ON [Definition].[Item]
(
	[ItemUuid] ASC,
	[Version] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [StatusId]    Script Date: 20/03/2016 12:57:56 ******/
CREATE NONCLUSTERED INDEX [StatusId] ON [Execution].[Job]
(
	[StatusId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [JobUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE NONCLUSTERED INDEX [JobUuid] ON [Execution].[JobReport]
(
	[JobUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [JobReportUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE NONCLUSTERED INDEX [JobReportUuid] ON [Execution].[JobReportItem]
(
	[JobReportUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Index [ReportUuid]    Script Date: 20/03/2016 12:57:56 ******/
CREATE NONCLUSTERED INDEX [ReportUuid] ON [Execution].[Request]
(
	[ReportUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [Logging].[logging_event_exception]  WITH CHECK ADD  CONSTRAINT [FK_Logging_logging_event_exception_event_id] FOREIGN KEY([event_id])
REFERENCES [Logging].[logging_event] ([event_id])
GO
ALTER TABLE [Logging].[logging_event_exception] CHECK CONSTRAINT [FK_Logging_logging_event_exception_event_id]
GO
ALTER TABLE [Logging].[logging_event_property]  WITH CHECK ADD  CONSTRAINT [FK_Logging_logging_event_property_event_id] FOREIGN KEY([event_id])
REFERENCES [Logging].[logging_event] ([event_id])
GO
ALTER TABLE [Logging].[logging_event_property] CHECK CONSTRAINT [FK_Logging_logging_event_property_event_id]
GO
ALTER TABLE [ReadV2].[Code]  WITH CHECK ADD  CONSTRAINT [FK_ReadV2_Code_ParentCodeId] FOREIGN KEY([ParentCodeId])
REFERENCES [ReadV2].[Code] ([CodeId])
GO
ALTER TABLE [ReadV2].[Code] CHECK CONSTRAINT [FK_ReadV2_Code_ParentCodeId]
GO
ALTER TABLE [ReadV2].[SynonymCode]  WITH CHECK ADD  CONSTRAINT [FK_ReadV2_SynonymCode_RootCodeId] FOREIGN KEY([RootCodeId])
REFERENCES [ReadV2].[Code] ([CodeId])
GO
ALTER TABLE [ReadV2].[SynonymCode] CHECK CONSTRAINT [FK_ReadV2_SynonymCode_RootCodeId]
GO
USE [master]
GO
ALTER DATABASE [Endeavour_Enterprise] SET  READ_WRITE
GO
