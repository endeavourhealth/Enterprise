/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.listOuput {
	import ILoggerService = app.blocks.ILoggerService;
	import IScope = angular.IScope;
	import ILibraryService = app.core.ILibraryService;
	import ListReport = app.models.ListReport;
	import ListReportGroup = app.models.ListReportGroup;
	'use strict';

	export class ListOuputController {
		name : string;
		description : string;
		listReport : ListReport;
		selectedListReportGroup : ListReportGroup;

		static $inject = ['LibraryService', 'LoggerService', '$scope'];

		constructor(
			protected libraryService:ILibraryService,
			protected logger : ILoggerService,
			protected $scope : IScope) {
			this.name = 'Asthmatic meds';
			this.description = 'List of medication (and latest issue date) for all asthmatic patients';
			this.listReport = {
				groups : [
					{
						heading : 'Patient',
						summary : null,
						fieldBased : {
							dataSource : 'Patient',
							fieldOutput : [
								{heading : 'DOB', field : 'DateOfBirth'},
								{heading : 'Forename', field : 'FirstName'},
								{heading : 'Surname', field : 'LastName'}
							]
						}
					},
					{
						heading : 'Issues',
						summary : null,
						fieldBased : {
							dataSource : 'MedicationIssue',
							fieldOutput : [
								{heading : 'Medication', field : 'DrugName'},
								{heading : 'Dose', field : 'Doseage'},
								{heading : 'Last Issue', field : 'LastIssued'}
							]
						}
					}
				]
			};
		}

	}

	angular
		.module('app.listOutput')
		.controller('ListOutputController', ListOuputController);
}
