/*
select newid()
*/


begin transaction

declare @EndUserUuid uniqueidentifier;
declare @OrganisationUuid uniqueidentifier;

select @EndUserUuid = e.EndUserUuid
from Administration.EndUser as e
where e.Email = 'regular@email'

select @OrganisationUuid = o.OrganisationUuid
from Administration.Organisation as o
where o.NationalId = '12345';


insert into [Definition].[Audit]
	(AuditUuid, EndUserUuid, [TimeStamp])
values
	('7E0108C9-2B28-48B8-B572-6FB821DD2017', @EndUserUuid, getdate());

declare @Query varchar(max) = '<?xml version="1.0" encoding="UTF-8"?>
<libraryItem xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="H:\Code\Enterprise\src\enterprise-core\src\main\resources\QueryDocument.xsd">
	<uuid>7CFFD13B-9BA5-40F0-9A3D-8E6F0F33F9FF</uuid>
	<name>Test query</name>
	<folderUuid>FD9309C2-9D7E-4152-84CD-D934B7D81579</folderUuid>
	<query>
		<startingRules>
			<ruleId>1</ruleId>
		</startingRules>
		<rule>
			<description>Date filter</description>
			<id>1</id>
			<test>
				<dataSource>
					<entity>OBSERVATION</entity>
					<filter>
						<field>EFFECTIVE_DATE</field>
						<valueFrom>
							<constant>2005-01-01</constant>
							<absoluteUnit>date</absoluteUnit>
							<operator>greaterThanOrEqualTo</operator>
						</valueFrom>
						<negate>false</negate>
					</filter>
				</dataSource>
				<isAny></isAny>
			</test>
			<onPass>
				<action>include</action>
			</onPass>
			<onFail>
				<action>exclude</action>
			</onFail>
			<layout>
				<x>10</x>
				<y>10</y>
			</layout>
		</rule>
	</query>
</libraryItem>';

declare @Report varchar(max) = '<?xml version="1.0" encoding="UTF-8"?>
<report xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="H:\Code\Enterprise\src\enterprise-core\src\main\resources\QueryDocument.xsd">
	<uuid>A42495AD-E047-4E9D-A7B1-E0CDD4BB907D</uuid>
	<name>Test report</name>
	<folderUuid>FD9309C2-9D7E-4152-84CD-D934B7D81579</folderUuid>
	<reportItem>
		<queryLibraryItemUuid>7CFFD13B-9BA5-40F0-9A3D-8E6F0F33F9FF</queryLibraryItemUuid>
	</reportItem>
</report>';

insert into [Definition].Item
	(ItemUuid, AuditUuid, XmlContent, Title, [Description], IsDeleted)
values
	('7CFFD13B-9BA5-40F0-9A3D-8E6F0F33F9FF', '7E0108C9-2B28-48B8-B572-6FB821DD2017', @Query, 'Test query', '', 0),
	('A42495AD-E047-4E9D-A7B1-E0CDD4BB907D', '7E0108C9-2B28-48B8-B572-6FB821DD2017', @Report, 'Test report', '', 0);

insert into [Definition].ActiveItem
	(ActiveItemUuid, OrganisationUuid, ItemUuid, AuditUuid, ItemTypeId, IsDeleted)
values
	('D777B3F7-CC54-4281-8013-1865788AE1C0', @OrganisationUuid, '7CFFD13B-9BA5-40F0-9A3D-8E6F0F33F9FF', '7E0108C9-2B28-48B8-B572-6FB821DD2017', 2, 0),
	('897FB90B-021A-4F72-B94F-47A929DF39A4', @OrganisationUuid, 'A42495AD-E047-4E9D-A7B1-E0CDD4BB907D', '7E0108C9-2B28-48B8-B572-6FB821DD2017', 1, 0);

insert into [Definition].ItemDependency
	(ItemUuid, AuditUuid, DependentItemUuid, DependencyTypeId)
values
	('A42495AD-E047-4E9D-A7B1-E0CDD4BB907D', '7E0108C9-2B28-48B8-B572-6FB821DD2017', '7CFFD13B-9BA5-40F0-9A3D-8E6F0F33F9FF', 2)


insert into Execution.Request
	(RequestUuid, ReportUuid, OrganisationUuid, EndUserUuid, [TimeStamp], Parameters, JobUuid)
values
	('51EFDA87-A82A-4E5C-B90C-D05416A19010', 'A42495AD-E047-4E9D-A7B1-E0CDD4BB907D', @OrganisationUuid, @EndUserUuid, getdate(), '', null)

rollback transaction