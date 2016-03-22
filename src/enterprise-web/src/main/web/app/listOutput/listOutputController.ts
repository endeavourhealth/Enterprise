/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.listOuput {
	import ILoggerService = app.blocks.ILoggerService;
	import IScope = angular.IScope;
	import ILibraryService = app.core.ILibraryService;
	import ListReport = app.models.ListReport;
	import ListReportGroup = app.models.ListReportGroup;
	import MessageBoxController = app.dialogs.MessageBoxController;
	import IModalProvider = angular.ui.bootstrap.IModalProvider;
	import InputBoxController = app.dialogs.InputBoxController;
	import IModalService = angular.ui.bootstrap.IModalService;
	import FieldOutput = app.models.FieldOutput;
	'use strict';

	export class ListOuputController {
		name : string;
		description : string;
		listReport : ListReport;
		selectedListReportGroup : ListReportGroup;
		selectedFieldOutput : FieldOutput;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$uibModal', 'AdminService'];

		constructor(
			protected libraryService:ILibraryService,
			protected logger : ILoggerService,
			protected $scope : IScope,
			protected $modal : IModalService,
			protected adminService : IAdminService) {
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

		selectDataSource(datasourceContainer : { dataSource : any }) {
			var vm = this;
			InputBoxController.open(this.$modal, 'Data source', 'Please select a data source', datasourceContainer.dataSource)
				.result.then(function(dataSource:any) {
					datasourceContainer.dataSource = dataSource;
					vm.adminService.setPendingChanges();
			});
		}
	}

	angular
		.module('app.listOutput')
		.controller('ListOutputController', ListOuputController);
}
