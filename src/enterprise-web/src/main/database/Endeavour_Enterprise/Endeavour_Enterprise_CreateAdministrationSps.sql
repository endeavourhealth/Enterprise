USE [Endeavour_Enterprise]
GO

/****** Object:  StoredProcedure [Administration].[EndUser_SelectForEmail]    Script Date: 22/02/2016 12:05:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [Administration].[EndUser_SelectForEmail]
@Email varchar(256)
 AS BEGIN
SELECT EndUserUuid, Title, Forename, Surname, Email, IsSuperUser FROM [Administration].[EndUser] WHERE Email = @Email
END


GO

/****** Object:  StoredProcedure [Administration].[EndUserPwd_SelectForEndUserNotExpired]    Script Date: 22/02/2016 12:05:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [Administration].[EndUserPwd_SelectForEndUserNotExpired]
@EndUserUuid uniqueidentifier
 AS BEGIN
	SELECT EndUserPwdUuid, EndUserUuid, PwdHash, DtExpired 
	FROM [Administration].[EndUserPwd] 
	WHERE 
		EndUserUuid = @EndUserUuid
		AND DtExpired > GETDATE()
END


GO

/****** Object:  StoredProcedure [Administration].[Folder_SelectForOrganisationTitleParentOrganisation]    Script Date: 22/02/2016 12:05:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [Administration].[Folder_SelectForOrganisationTitleParentOrganisation]
@OrganisationUuid uniqueIdentifier, @Title varchar(256), @ParentFolderUuid uniqueIdentifier
 AS BEGIN
SELECT FolderUuid, OrganisationUuid, ParentFolderUuid, Title FROM [Administration].[Folder] 
	WHERE OrganisationUuid = @OrganisationUuid
	AND Title = @Title
	AND ParentFolderUuid = @ParentFolderUuid
END

GO

/****** Object:  StoredProcedure [Administration].[Organisation_SelectForAll]    Script Date: 22/02/2016 12:05:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [Administration].[Organisation_SelectForAll]
 AS BEGIN
	SELECT OrganisationUuid, Name, NationalId 
	FROM [Administration].[Organisation] 
END

GO

/****** Object:  StoredProcedure [Administration].[OrganisationPersonLink_SelectForPersonNotExpired]    Script Date: 22/02/2016 12:05:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [Administration].[OrganisationPersonLink_SelectForPersonNotExpired]
@PersonUuid uniqueidentifier
 AS BEGIN
	SELECT OrganisationPersonLinkUuid, OrganisationUuid, PersonUuid, Persmissions, DtExpired 
	FROM [Administration].[OrganisationPersonLink] 
	WHERE 
		PersonUuid = @PersonUuid
		AND DtExpired > GETDATE()
END

GO

/****** Object:  StoredProcedure [dbo].[GenerateAllStandardStoredProcedures]    Script Date: 22/02/2016 12:05:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[GenerateAllStandardStoredProcedures]
AS
BEGIN

	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	EXEC GenerateStandardStoredProcedures 'Administration.Folder'
	EXEC GenerateStandardStoredProcedures 'Administration.FolderItemLink'
	EXEC GenerateStandardStoredProcedures 'Administration.Organisation'
	EXEC GenerateStandardStoredProcedures 'Administration.OrganisationEndUserLink'
	EXEC GenerateStandardStoredProcedures 'Administration.EndUser'
	EXEC GenerateStandardStoredProcedures 'Administration.EndUserPwd'
	EXEC GenerateStandardStoredProcedures 'Administration.EndUserEmailInvite'
END


GO

/****** Object:  StoredProcedure [dbo].[GenerateStandardStoredProcedures]    Script Date: 22/02/2016 12:05:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



-- =============================================
-- Author:		DL
-- Create date: 2016-02-17
-- Description:	Stored Procedure to generate the standard insert/update/retrieve/delete SPs for a given table
-- =============================================
CREATE PROCEDURE [dbo].[GenerateStandardStoredProcedures]
	@tableAndSchema varchar(MAX) 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;


	--split the parameter into schema and table name
	DECLARE @schemaName varchar(255)
	DECLARE @tableName varchar(255)

	--see if a schema name was given. If not assume it's dbo
	DECLARE @dotIndex int = CHARINDEX('.', @tableAndSchema)
	IF (@dotIndex = -1)
	BEGIN
		SET @schemaName = 'dbo'
		SET @tableName = @tableAndSchema
	END
	ELSE
	BEGIN
		SET @schemaName = SUBSTRING(@tableAndSchema, 0, @dotIndex)
		SET @tableName = SUBSTRING(@tableAndSchema, @dotIndex+1, LEN(@tableAndSchema)-@dotIndex)
	END

	PRINT 'Schema = ' + @schemaName
	PRINT 'Table = ' + @tableName

	--work out the schema ID so we don't have to keep joining back to sys.schemas
	DECLARE @schemaId int = (SELECT schema_Id FROM Sys.schemas WHERE name = @schemaName)
	If (@schemaId IS NULL)
	BEGIN
		PRINT 'No schema found for name ' + @schemaName
		RETURN
	END

	PRINT 'SchemaId = ' + CONVERT(Varchar, @schemaId)

	--to create the four SPs, we need the column names formatted in four different ways
	DECLARE @spParametersPrimaryKeyOnly nvarchar(Max) = ''
	DECLARE @spParametersAllColumns nvarchar(Max) = ''
	DECLARE @spWherePrimaryKeyMatches nvarchar(Max) = ''
	DECLARE @spSelectAllColumns nvarchar(Max) = ''
	DECLARE @spInsertAllColumns nvarchar(Max) = ''
	DECLARE @spUpdateAllColumns nvarchar(Max) = ''

	--SQL to get all the column names for the table
	DECLARE columnCursor CURSOR
	FOR
		SELECT c.Name, c.column_id, y.name, c.max_length
		FROM
			Sys.Columns c, 
			Sys.Tables t,
			Sys.Types y
		WHERE
			t.Name = @tableName
			AND t.schema_id = @schemaId
			AND c.object_id = t.object_id
			AND c.system_type_id = y.system_type_id
		ORDER BY 
			c.column_id

	DECLARE @ColumnName nvarchar(255)
	DECLARE @ColumnId nvarchar(255)
	DECLARE @ColumnType nvarchar(255)
	DECLARE @ColumnLen nvarchar(255)

	--run the SQL with a cursor, so we can iterate through each row in the result set
	OPEN columnCursor

	FETCH NEXT FROM columnCursor
	INTO @ColumnName, @ColumnId, @ColumnType, @ColumnLen

	WHILE @@FETCH_STATUS = 0
	BEGIN

		print '' + @ColumnName + '  ' + @ColumnId + '  ' + @ColumnType

		IF (CONVERT(int, @ColumnId) = 1)
		BEGIN
			SET @spParametersPrimaryKeyOnly += '@' + @ColumnName + ' ' + @ColumnType
			SET @spParametersAllColumns += '@' + @ColumnName + ' ' + @ColumnType
			SET @spWherePrimaryKeyMatches += @ColumnName + ' = @' + @ColumnName
		
			SET @spSelectAllColumns += @ColumnName
			SET @spInsertAllColumns += '@' + @ColumnName
			SET @spUpdateAllColumns += @ColumnName + ' = @' + @ColumnName
		END
		ELSE
		BEGIN
			--SET @spParametersPrimaryKeyOnly --only uses first column name
			SET @spParametersAllColumns += ', @' + @ColumnName + ' ' + @ColumnType
			--SET @spWherePrimaryKeyMatches 
			SET @spSelectAllColumns += ', ' + @ColumnName
			SET @spInsertAllColumns += ', @' + @ColumnName
			SET @spUpdateAllColumns += ', ' + @ColumnName + ' = @' + @ColumnName
		END

		--if it's a varchar column we need to specify the column length in the SP parameters
		if (@ColumnType = 'varchar' OR @ColumnType = 'nvarchar'
			OR @ColumnType = 'char' OR @ColumnType = 'nchar')
		BEGIN
			--set the parameter length to be ONE MORE than the table actually supports. This
			--means that if we try to insert a String that's too long, it'll raise an error
			--rather than silently truncating it
			DECLARE @ColumnLenPlusOne varchar(255) = (CONVERT(int, @ColumnLen)+1) 
			SET @spParametersAllColumns += '(' + @columnLenPlusOne + ')'
		END

		--select the next row from the result set
		FETCH NEXT FROM columnCursor
		INTO @ColumnName, @ColumnId, @ColumnType, @ColumnLen
	END


	CLOSE columnCursor;
	DEALLOCATE columnCursor;


	--work out the names for the SPs we're going to create
	DECLARE @spInsert nvarchar(255) = '_' + @TableName + '_Insert'
	DECLARE @spUpdate nvarchar(255) = '_' + @TableName + '_Update'
	DECLARE @spDelete nvarchar(255) = '_' + @TableName + '_Delete'
	DECLARE @spSelect nvarchar(255) = '_' + @TableName + '_SelectForUuid'

	DECLARE @NewLine char(2) = CHAR(13)+CHAR(10)

	--start defining the four SPs
	DECLARE @spInsertSql nvarchar(Max) = 'CREATE PROCEDURE [' + @schemaName + '].[' + @spInsert + ']' + @NewLine
									   + @spParametersAllColumns + @NewLine
									   + ' AS BEGIN' + @NewLine
									   + 'INSERT INTO [' + @schemaName + '].[' + @TableName + '] VALUES ('
									   + @spInsertAllColumns
									   + ')' + @NewLine
									   + 'END'

	DECLARE @spUpdateSql nvarchar(Max) = 'CREATE PROCEDURE [' + @schemaName + '].[' + @spUpdate + ']' + @NewLine
									   + @spParametersAllColumns + @NewLine
									   + ' AS BEGIN' + @NewLine
									   + 'UPDATE [' + @schemaName + '].[' + @TableName + '] SET '
									   + @spUpdateAllColumns
									   + ' WHERE '
									   + @spWherePrimaryKeyMatches
									   + @NewLine
									   + 'END'

	DECLARE @spDeleteSql nvarchar(Max) = 'CREATE PROCEDURE [' + @schemaName + '].[' + @spDelete + ']' + @NewLine
									   + @spParametersPrimaryKeyOnly + @NewLine
									   + ' AS BEGIN' + @NewLine
									   + 'DELETE FROM [' + @schemaName + '].[' + @TableName + '] WHERE '
									   + @spWherePrimaryKeyMatches
									   + @NewLine
									   + 'END'

	DECLARE @spSelectSql nvarchar(Max) = 'CREATE PROCEDURE [' + @schemaName + '].[' + @spSelect + ']' + @NewLine
									   + @spParametersPrimaryKeyOnly + @NewLine
									   + ' AS BEGIN' + @NewLine
									   + 'SELECT '
									   + @spSelectAllColumns
									   + ' FROM [' + @schemaName + '].[' + @TableName + '] WHERE '
									   + @spWherePrimaryKeyMatches
									   + @NewLine
									   + 'END'

	--drop all the existing SPs
	DECLARE @sql nvarchar(MAX)

	SET @sql = N'IF EXISTS (SELECT 1 FROM Sys.Procedures '
			 + 'WHERE schema_id = ' + CONVERT(varchar, @schemaId) + ' AND name = ''' + @SpInsert + ''') '
			 + 'DROP PROCEDURE [' + @schemaName + '].[' + @SpInsert + ']'
	PRINT @sql
	EXEC sp_executesql @sql

	SET @sql = N'IF EXISTS (SELECT 1 FROM Sys.Procedures '
			 + 'WHERE schema_id = ' + CONVERT(varchar, @schemaId) + ' AND name = ''' + @SpUpdate + ''') '
			 + 'DROP PROCEDURE [' + @schemaName + '].[' + @SpUpdate + ']'
	PRINT @sql
	EXEC sp_executesql @sql

	SET @sql = N'IF EXISTS (SELECT 1 FROM Sys.Procedures '
			 + 'WHERE schema_id = ' + CONVERT(varchar, @schemaId) + ' AND name = ''' + @SpDelete + ''') '
			 + 'DROP PROCEDURE [' + @schemaName + '].[' + @SpDelete + ']'
	PRINT @sql
	EXEC sp_executesql @sql

	SET @sql = N'IF EXISTS (SELECT 1 FROM Sys.Procedures '
			 + 'WHERE schema_id = ' + CONVERT(varchar, @schemaId) + ' AND name = ''' + @SpSelect + ''') '
			 + 'DROP PROCEDURE [' + @schemaName + '].[' + @SpSelect + ']'
	PRINT @sql
	EXEC sp_executesql @sql

	--now create the new SPs
	PRINT @spInsertSql
	EXEC sp_executesql @spInsertSql

	PRINT @spUpdateSql
	EXEC sp_executesql @spUpdateSql

	PRINT @spDeleteSql
	EXEC sp_executesql @spDeleteSql

	PRINT @spSelectSql
	EXEC sp_executesql @spSelectSql


END



GO

