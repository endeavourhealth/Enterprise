-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: enterprise_admin
-- ------------------------------------------------------
-- Server version	5.7.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `concepts`
--

DROP TABLE IF EXISTS `concepts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concepts` (
  `id` int(4) NOT NULL,
  `name` varchar(250) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `class` int(4) DEFAULT NULL,
  `short_name` varchar(125) DEFAULT NULL,
  `description` varchar(10000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concepts`
--

LOCK TABLES `concepts` WRITE;
/*!40000 ALTER TABLE `concepts` DISABLE KEYS */;
INSERT INTO `concepts` VALUES (1,'Class',1,1,'Class','All class concepts are classes e.g. a DateTime class is a type of class'),(2,'Concept',1,1,'Concept','has an identifier a name and a class'),(3,'Record type',1,1,'Record type','An entry that holds stored data. Variously referred to as a table or resource. Has fields'),(5,'Attribute group',1,1,'Attribute group','A grouping construct for attributes holding one or more attribute value pairs'),(6,'Quantity time',1,1,'Quantity time','An attribute group specialising in  units of time'),(7,'Range numeric',1,1,'Range numeric','structure that has a range type operator and upper and lower value'),(8,'Numeric',1,1,'Numeric','A type of field that holds a number (integer or float)'),(9,'DateTime',1,1,'DateTime','A type of field that holds a date and time'),(10,'Code',1,1,'Code','A field that holds a simple code'),(11,'Text',1,1,'Text','contains a simple text value'),(12,'Boolean',1,1,'Boolean','Ony a 1 or a zero (Y or N)'),(13,'Codeable concept',1,1,'Codeable concept','A field that holds a code that is a concept in the information model'),(14,'Folder',1,1,'Folder','A concept that contains other concepts as a place holder in a view'),(15,'Relationship',1,1,'Relationship','A concept only used in the relationship links between one concept and another, a specialised form of attribute'),(16,'Field',1,1,'Field','A concept that is a field linked to a record type'),(17,'Abstract field',1,1,'Abstract field','A concept that is a generic field which can be used to author real fields. For example an effective date is an abstract field but when used in an observation is an effective date (observation)'),(19,'Attribute',1,1,'Attribute','Type of concept that is used in an expression as the attribute name (e.g. laterality)'),(21,'Quantity medication',1,1,'Quantity medication','An attribute group specialising in medication quantities with a value and unit'),(22,'View',1,1,'View','Type of concept that is a view on a relationship type (for example the full IM navigatoin view  or the IM Field selector view)'),(100,'is a',1,19,'is a','Means that the concept Child is a type of parent concept. This is used in queries on concepts that subsume child concept. '),(101,'inherits fields',1,19,'inherits fields','Means that the concept C inherits the names of all fields from concept P and may have additional fields. For example:<P>Numeric observation -> inherits fields - > Observation<P>This relationship is used at authoring time but not at run time as the inheritance is often incomplete'),(104,'has reciprocal',1,19,'has reciprocal','Links one relationship as a reciprocal of another relationship so that target concepts are linked to parents by the reverse relationship'),(105,'has subtype',1,19,'has subtype','Reciprocal of is a'),(106,'has field',1,19,'has field','Fields of a concept that is a record type'),(107,'has value type',1,19,'has value type','Fields have values of a certain class e.g. date, text,  numeric codeable concept'),(108,'has preferred value set',1,19,'has preferred value set','Points to one or more value sets that a field should contain'),(109,'has linked record type',1,19,'has linked record type','When  field links to a different record type (e.g. an address) the record type concept it links to (equivalent to a foreign key linking to an ID of another table)'),(110,'has linked field',1,19,'has linked field','When a field links directly to a field in another record type the field it links to (used together with the preferred value set (e.g. patient ethinicity links to an observation field containing codes from the ethnicity value set) this enables query mapping using this as a guide'),(111,'derived from field',1,19,'derived from field','The abstract field that the field is copied from (e.g. observation effective date is derived from effective date)'),(112,'Has branch',1,19,'Has branch','A concept may branch into any other structural or non structural concepts. This relatoinship is used as the core model organiser when other relationships to not exist i.e. it has no real semantic meaning'),(113,'Provided in service setting',1,19,'Provided in service setting','The concept is provided by a healthcare setting such as General Practice or accicent and emergency'),(500,'Information model',1,14,'Information model','The Discovery information model is a knowledge base that describes all of the known concepts held within the Discovery data stores and incorporates a knowledge base to enable a user to understand and classify the concepts within the store. <p>It is a browsable and navigable resource that can be used by all users seeking to understand the underlying data or seeking to define cohort queries or reports operating on the data.'),(501,'Record structures',1,14,'Record structures','The banch of the information model that describes the logical structures that store records of various types such as tables and fields'),(502,'Attributes',1,14,'Attributes','Groups the relationship types when navigating the information model'),(503,'Patient Care Records',1,14,'Patient Care Records','Structures that hold information about patients/clients/ service users'),(504,'Care administration',1,14,'Care administration','Folder containing record types that hold information about care administration in general terms and not specific to a single patient e.g. appointment schedules'),(505,'Clinical entries',1,14,'Clinical entries','Folder containing Record types that store patient clinical or personal characteristics such as clinical observations or measurements&#44; social or personal events'),(506,'Care processes',1,14,'Care processes','Structures describing care process events in relation to the patient&#44; such as admissions&#44; encounters&#44; referrals.'),(507,'Health workers organisations and other entities',1,14,'Health workers organisations and other entities','Structures that describe staff professionals organisations departments and services.'),(508,'Clinical and administrative Concepts',1,14,'Clinical and administrative Concepts','Content concepts such as clinical terms and administration value sets'),(509,'Navigation Views',1,14,'Navigation Views','Folder containing names of views for display of subsets of information model'),(510,'IM Navigation view',1,22,'IM Navigation view','The default view for viewing the information model'),(511,'Model support structures',1,14,'Model support structures','Folder for things that support the information model such as relationships'),(512,'Encounter types (view)',1,14,'Encounter types','A view of encounter types when selecting for query'),(8000,'Encounters by setting',1,14,'Encounters by setting','Encounters categorised by service type'),(8001,'Encounters by interaction mode',1,14,'Encounters by interaction mode','Encounters categorised according to communication method such as face to face or email'),(8002,'Encounters by care process',1,14,'Encounters by care process','Encounters categorised according to the flow through the care process such as admission or discharge or attendance'),(8003,'General Practice consultation',1,13,'General Practice consultation','Consultations in the context of general practice'),(8005,'Acute care setting',1,13,'Acute care setting','Encounters such as 999 or NHS 111 or out of hours services or walk in centres'),(8006,'Community health setting',1,13,'Community health setting','Encounters associated with community care services'),(8007,'Social care settings',1,13,'Social care settings','Encounters associated with Socal care services'),(8008,'Face to face encounter',1,13,'Face to face encounter','An encounter that takes place with the physical presence of the patient'),(8009,'E-mail encounter',1,13,'E-mail encounter','A consultation or exchange of emails'),(8010,'Video encounter',1,13,'Video encounter','A consultation via Video'),(8011,'Encounter by place type',1,14,'Encounter by place type','Encounters categorised according to the nature of the place it takes place in such as a home visit'),(8012,'Third party encounter',1,13,'Third party encounter','A consultation that takes place via a third party e.g a relative without the presence of the patient'),(8013,'Administration entry',1,13,'Administration entry','Administrative entries into the records often recorded as consultations but are not truly consultations'),(8014,'Hospital admission',1,13,'Hospital admission','The process of admission into a hospital'),(8015,'Hospital Discharge',1,13,'Hospital Discharge','Discharge from a hospital'),(8016,'Accident and emergency attendance',1,13,'Accident and emergency attendance','Patient attended the casualty department');
/*!40000 ALTER TABLE `concepts` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-09-27  9:57:21
