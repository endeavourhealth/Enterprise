USE [master]
GO
/****** Object:  Database [Endeavour_Enterprise_Data]    Script Date: 29/03/2016 09:31:29 ******/
CREATE DATABASE [Endeavour_Enterprise_Data]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'Endeavour_Enterprise_Data', FILENAME = N'D:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\Endeavour_Enterprise_Data.mdf' , SIZE = 51840KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'Endeavour_Enterprise_Data_log', FILENAME = N'D:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\Endeavour_Enterprise_Data_log.ldf' , SIZE = 2560KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET COMPATIBILITY_LEVEL = 120
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [Endeavour_Enterprise_Data].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET ARITHABORT OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET  DISABLE_BROKER 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET  MULTI_USER 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET DB_CHAINING OFF 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET DELAYED_DURABILITY = DISABLED 
GO
USE [Endeavour_Enterprise_Data]
GO
/****** Object:  Schema [07T]    Script Date: 29/03/2016 09:31:29 ******/
CREATE SCHEMA [07T]
GO
/****** Object:  Schema [GP]    Script Date: 29/03/2016 09:31:29 ******/
CREATE SCHEMA [GP]
GO
/****** Object:  Schema [shared]    Script Date: 29/03/2016 09:31:29 ******/
CREATE SCHEMA [shared]
GO
/****** Object:  Table [07T].[GPAppointment]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [07T].[GPAppointment](
	[SK_ServiceProviderID] [int] NOT NULL,
	[SK_PatientID] [int] NOT NULL,
	[AppointmentDate] [date] NOT NULL,
	[AppointmentStartTime] [time](0) NOT NULL,
	[AppointmentEndTime] [time](0) NOT NULL,
	[IsCancelled] [bit] NOT NULL,
	[CurrentStatus] [varchar](20) NOT NULL,
	[ArrivalTime] [time](0) NULL,
	[SeenTime] [time](0) NULL,
	[DateAppointmentBooked] [date] NULL,
	[TimeAppointmentBooked] [time](0) NULL,
	[SessionHolder] [varchar](100) NULL,
	[SessionType] [varchar](20) NULL,
	[SessionLocation] [varchar](50) NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Index [SK_PatientID]    Script Date: 29/03/2016 09:31:29 ******/
CREATE CLUSTERED INDEX [SK_PatientID] ON [07T].[GPAppointment]
(
	[SK_PatientID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Table [07T].[GPEncounter]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [07T].[GPEncounter](
	[SK_ServiceProviderID] [int] NOT NULL,
	[SK_PatientID] [int] NOT NULL,
	[EventDate] [date] NOT NULL,
	[EventTime] [time](0) NOT NULL,
	[NativeClinicalCode] [varchar](12) NOT NULL,
	[Value] [real] NOT NULL,
	[Units] [varchar](30) NOT NULL,
	[AgeAtEvent] [tinyint] NOT NULL,
	[IsDiaryEvent] [bit] NOT NULL,
	[IsReferralEvent] [bit] NOT NULL,
	[SK_StaffID] [int] NULL,
	[ConsultationType] [varchar](100) NULL,
	[ConsultationDuration] [smallint] NULL,
	[SK_ProblemID] [tinyint] NULL,
	[SnomedClinicalCode] [varchar](255) NOT NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Index [SK_PatientID]    Script Date: 29/03/2016 09:31:29 ******/
CREATE CLUSTERED INDEX [SK_PatientID] ON [07T].[GPEncounter]
(
	[SK_PatientID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Table [07T].[GPMedication]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [07T].[GPMedication](
	[SK_ServiceProviderID] [int] NOT NULL,
	[SK_PatientID] [int] NOT NULL,
	[IssueDate] [date] NOT NULL,
	[IssueTime] [time](0) NOT NULL,
	[DMDCode] [bigint] NOT NULL,
	[MedicationTerm] [varchar](130) NOT NULL,
	[BNFChapter] [varchar](20) NOT NULL,
	[IssueCount] [tinyint] NOT NULL,
	[Quantity] [real] NOT NULL,
	[Unit] [varchar](60) NOT NULL,
	[Cost] [smallmoney] NOT NULL,
	[SK_StaffID] [int] NOT NULL,
	[SK_MedicationIssueID] [tinyint] NOT NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Index [SK_PatientID]    Script Date: 29/03/2016 09:31:29 ******/
CREATE CLUSTERED INDEX [SK_PatientID] ON [07T].[GPMedication]
(
	[SK_PatientID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Table [07T].[PatientDemographics]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [07T].[PatientDemographics](
	[SK_ServiceProviderID] [int] NOT NULL,
	[SK_PatientID] [int] NOT NULL,
	[DateRegistered] [date] NOT NULL,
	[DateRegisteredEnd] [date] NULL,
	[PatientStatus] [varchar](50) NOT NULL,
	[PatientStatusCode] [tinyint] NOT NULL,
	[Gender] [char](1) NOT NULL,
	[LSOACode] [char](9) NULL,
	[EthnicityCode] [varchar](12) NULL,
	[YearOfDeath] [smallint] NULL,
	[UsualGPName] [varchar](100) NOT NULL,
 CONSTRAINT [PK_PatientDemographics] PRIMARY KEY NONCLUSTERED 
(
	[SK_ServiceProviderID] ASC,
	[SK_PatientID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Index [SK_PatientID]    Script Date: 29/03/2016 09:31:29 ******/
CREATE CLUSTERED INDEX [SK_PatientID] ON [07T].[PatientDemographics]
(
	[SK_PatientID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Table [07T].[Patients]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [07T].[Patients](
	[SK_ServiceProviderID] [int] NOT NULL,
	[SK_ServiceProviderID_Pseudo] [int] NULL,
	[SK_PatientID] [int] NOT NULL,
	[SK_PatientID_Pseudo] [int] NULL,
	[DateOfBirth] [date] NOT NULL,
	[YearOfDeath] [smallint] NULL,
	[Gender] [char](1) NOT NULL,
	[LowerSuperOutputArea] [char](9) NULL,
	[DateRegistered] [date] NOT NULL,
	[DateRegisteredEnd] [date] NULL,
	[ConsentBitmask] [smallint] NULL,
 CONSTRAINT [PK_Patients] PRIMARY KEY NONCLUSTERED 
(
	[SK_PatientID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Index [SK_PatientID]    Script Date: 29/03/2016 09:31:29 ******/
CREATE CLUSTERED INDEX [SK_PatientID] ON [07T].[Patients]
(
	[SK_PatientID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 80) ON [PRIMARY]
GO
/****** Object:  Table [GP].[ClinicalProblem]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [GP].[ClinicalProblem](
	[SK_ProblemID] [tinyint] NOT NULL,
	[ProblemStatus] [varchar](25) NOT NULL,
	[Significance] [varchar](25) NOT NULL,
	[Episode] [varchar](25) NOT NULL,
 CONSTRAINT [PK_ClinicalProblem] PRIMARY KEY CLUSTERED 
(
	[SK_ProblemID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [GP].[MedicationIssue]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [GP].[MedicationIssue](
	[SK_MedicationIssueID] [tinyint] NOT NULL,
	[PrescriptionType] [varchar](20) NOT NULL,
	[IssueMethod] [varchar](30) NOT NULL,
 CONSTRAINT [PK_MedicationIssue] PRIMARY KEY CLUSTERED 
(
	[SK_MedicationIssueID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [GP].[Staff]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [GP].[Staff](
	[SK_StaffID] [int] NOT NULL,
	[AuthorisingUser] [varchar](200) NOT NULL,
	[AuthorisingUserRole] [varchar](200) NOT NULL,
 CONSTRAINT [PK_Staff] PRIMARY KEY CLUSTERED 
(
	[SK_StaffID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [shared].[Organisation]    Script Date: 29/03/2016 09:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [shared].[Organisation](
	[SK_ServiceProviderID] [int] NOT NULL,
	[ServiceProviderName] [varchar](100) NOT NULL,
	[ServiceProviderCode] [varchar](9) NOT NULL,
	[ProviderGroup] [varchar](100) NOT NULL,
	[CommissionerID] [smallint] NOT NULL,
	[Commissioner] [varchar](100) NOT NULL,
	[CommissionerCode] [char](3) NOT NULL,
	[POD] [varchar](20) NULL,
	[CommissioningRegionCode] [char](3) NULL,
	[CommissioningRegion] [varchar](100) NULL,
	[LocalAreaTeamCode] [char](3) NULL,
	[LocalAreaTeam] [varchar](100) NULL,
	[CommissioningCounty] [varchar](50) NOT NULL,
	[CommissioningCountry] [varchar](50) NOT NULL,
 CONSTRAINT [PK_Organisation] PRIMARY KEY CLUSTERED 
(
	[SK_ServiceProviderID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
USE [master]
GO
ALTER DATABASE [Endeavour_Enterprise_Data] SET  READ_WRITE 
GO
