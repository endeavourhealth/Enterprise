DROP TABLE `enterprise_data_pseudonymised`.`Concept`;
DROP TABLE `enterprise_data_pseudonymised`.`DataType`;
DROP TABLE `enterprise_data_pseudonymised`.`ConceptType`;

CREATE TABLE `enterprise_data_pseudonymised`.`Concept` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ConceptId` varchar(45) NOT NULL,
  `Definition` varchar(500) NOT NULL,
  `ParentTypeConceptId` varchar(45) NOT NULL,
  `BaseTypeConceptId` varchar(45) NOT NULL,
  `Status` tinyint(1) NOT NULL,
  `DataTypeId` tinyint(1) NOT NULL,
  `ConceptTypeId` tinyint(1) NOT NULL,
  `Present` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`Id`),
  KEY `ConceptId` (`ConceptId`),
  KEY `Definition` (`Definition`),
  KEY `ParentTypeConceptId` (`ParentTypeConceptId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

  
CREATE TABLE `enterprise_data_pseudonymised`.`DataType` (
  `DataTypeId` TINYINT(1) NOT NULL,
  `Type` VARCHAR(45) NULL,
  PRIMARY KEY (`DataTypeId`));

CREATE TABLE `enterprise_data_pseudonymised`.`ConceptType` (
  `ConceptTypeId` TINYINT(1) NOT NULL,
  `Type` VARCHAR(45) NULL,
  PRIMARY KEY (`ConceptTypeId`));

INSERT INTO `enterprise_data_pseudonymised`.`ConceptType` (`ConceptTypeId`, `Type`) VALUES ('0', 'CodeableConcept');
INSERT INTO `enterprise_data_pseudonymised`.`ConceptType` (`ConceptTypeId`, `Type`) VALUES ('1', 'Resource');
INSERT INTO `enterprise_data_pseudonymised`.`ConceptType` (`ConceptTypeId`, `Type`) VALUES ('2', 'Field');
INSERT INTO `enterprise_data_pseudonymised`.`ConceptType` (`ConceptTypeId`, `Type`) VALUES ('3', 'Units');

INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('0', 'None');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('1', 'DateTime');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('2', 'Date');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('3', 'Time');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('4', 'Day');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('5', 'Month');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('6', 'Year');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('7', 'MonthYear');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('8', 'String');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('9', 'Integer');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('10', 'Double');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('11', 'CodeableConcept');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('12', 'List');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('13', 'StringRange');
INSERT INTO `enterprise_data_pseudonymised`.`DataType` (`DataTypeId`, `Type`) VALUES ('14', 'DateRange');

INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('1', 'Patient', '0', '1', '1','0','1');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('2', 'Observation', '0', '2', '1','0','1');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('3', 'Value Observation', '2', '2', '1','0','1');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('4', 'Medication Statement', '0', '4', '1','0','1');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('5', 'Medication Order', '0', '5', '1','0','1');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('6', 'Encounter', '0', '6', '1','0','1');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('7', 'Referral', '0', '7', '1','0','1');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('8', 'Allergy', '0', '8', '1','0','1');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('9', 'Sex', '1', '1', '1','8','2');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('10', 'Male', '9', '1', '1', '11','0');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('11', 'Female', '9', '1', '1', '11','0');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('12', 'Age Years', '1', '1', '1','13','2');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('13', 'Age Months', '1', '1', '1','13','2');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('14', 'Age Weeks', '1', '1', '1','13','2');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('15', 'Date of Death', '1', '1', '1','14','2');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('16', 'Post Code Prefix', '1', '1', '1','8','2');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('17', 'LSOA Code', '1', '1', '1','8','2');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('18', 'MSOA Code', '1', '1', '1','8','2');
INSERT INTO `enterprise_data_pseudonymised`.`Concept` (`ConceptId`, `Definition`, `ParentTypeConceptId`, `BaseTypeConceptId`, `Status`, `DataTypeId`, `ConceptTypeId`) VALUES ('19', 'Townsend Score', '1', '1', '1','13','2');

ALTER TABLE `enterprise_data_pseudonymised`.`allergy_intolerance` 
ADD INDEX `snomed_concept_id` (`snomed_concept_id` ASC);
ALTER TABLE `enterprise_data_pseudonymised`.`encounter` 
ADD INDEX `snomed_concept_id` (`snomed_concept_id` ASC);
ALTER TABLE `enterprise_data_pseudonymised`.`medication_order` 
ADD INDEX `snomed_concept_id` (`dmd_id` ASC);
ALTER TABLE `enterprise_data_pseudonymised`.`medication_statement` 
ADD INDEX `snomed_concept_id` (`dmd_id` ASC);
ALTER TABLE `enterprise_data_pseudonymised`.`observation` 
ADD INDEX `snomed_concept_id` (`snomed_concept_id` ASC);
ALTER TABLE `enterprise_data_pseudonymised`.`referral_request` 
ADD INDEX `snomed_concept_id` (`snomed_concept_id` ASC);

INSERT INTO `enterprise_data_pseudonymised`.`Concept`
(`ConceptId`,
`Definition`,
`ParentTypeConceptId`,
`BaseTypeConceptId`,
`Status`,
`DataTypeId`,
`ConceptTypeId`,
`Present`)
select s.concept_id,s.term,s3.code,'2',1,11,0,0 from enterprise_admin.snomed_lookup s
join enterprise_admin.snomed s2 on s2.code = s.concept_id 
join enterprise_admin.snomed_hier h on h.child_pid = s2.pid
join enterprise_admin.snomed s3 on s3.pid = h.parent_pid

update `enterprise_data_pseudonymised`.`Concept` c
join enterprise_data_pseudonymised.observation o on o.snomed_concept_id = c.ConceptId
set c.present = 1
update `enterprise_data_pseudonymised`.`Concept` c
join enterprise_data_pseudonymised.medication_statement o on o.dmd_id = c.ConceptId
set c.present = 1
update `enterprise_data_pseudonymised`.`Concept` c
join enterprise_data_pseudonymised.allergy_intolerance o on o.snomed_concept_id = c.ConceptId
set c.present = 1
update `enterprise_data_pseudonymised`.`Concept` c
join enterprise_data_pseudonymised.referral_request o on o.snomed_concept_id = c.ConceptId
set c.present = 1
update `enterprise_data_pseudonymised`.`Concept` c
join enterprise_data_pseudonymised.encounter o on o.snomed_concept_id = c.ConceptId
set c.present = 1


INSERT INTO `enterprise_data_pseudonymised`.`Concept`
(`ConceptId`,
`Definition`,
`ParentTypeConceptId`,
`BaseTypeConceptId`,
`Status`,
`DataTypeId`,
`ConceptTypeId`,
`Present`)
select distinct snomed_concept_id,original_term,'2','2',1,11,0,1 from enterprise_data_pseudonymised.observation
where snomed_concept_id IS NOT NULL

INSERT INTO `enterprise_data_pseudonymised`.`Concept`
(`ConceptId`,
`Definition`,
`ParentTypeConceptId`,
`BaseTypeConceptId`,
`Status`,
`DataTypeId`,
`ConceptTypeId`,
`Present`)
select distinct dmd_id,original_term,'4','4',1,11,0,1 from enterprise_data_pseudonymised.medication_statement
where dmd_id IS NOT NULL

INSERT INTO `enterprise_data_pseudonymised`.`Concept`
(`ConceptId`,
`Definition`,
`ParentTypeConceptId`,
`BaseTypeConceptId`,
`Status`,
`DataTypeId`,
`ConceptTypeId`,
`Present`)
select distinct snomed_concept_id,original_term,'8','8',1,11,0,1 from enterprise_data_pseudonymised.allergy_intolerance
where snomed_concept_id IS NOT NULL

INSERT INTO `enterprise_data_pseudonymised`.`Concept`
(`ConceptId`,
`Definition`,
`ParentTypeConceptId`,
`BaseTypeConceptId`,
`Status`,
`DataTypeId`,
`ConceptTypeId`,
`Present`)
select distinct snomed_concept_id,original_term,'7','7',1,11,0,1 from enterprise_data_pseudonymised.referral_request
where snomed_concept_id IS NOT NULL

INSERT INTO `enterprise_data_pseudonymised`.`Concept`
(`ConceptId`,
`Definition`,
`ParentTypeConceptId`,
`BaseTypeConceptId`,
`Status`,
`DataTypeId`,
`ConceptTypeId`,
`Present`)
select distinct snomed_concept_id,original_term,'6','6',1,11,0,1 from enterprise_data_pseudonymised.encounter
where snomed_concept_id IS NOT NULL






	
