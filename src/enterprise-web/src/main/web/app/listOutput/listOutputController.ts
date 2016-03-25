/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.listOuput {
	import ILoggerService = app.blocks.ILoggerService;
	import IScope = angular.IScope;
	import ILibraryService = app.core.ILibraryService;
	import ListReport = app.models.ListReport;
	import ListReportGroup = app.models.ListReportGroup;
	import IModalService = angular.ui.bootstrap.IModalService;
	import FieldOutput = app.models.FieldOutput;
	import IWindowService = angular.IWindowService;
	import LibraryItem = app.models.LibraryItem;
	import DataSource = app.models.DataSource;
	import TestEditorController = app.dialogs.TestEditorController;
	import Test = app.models.Test;
	'use strict';

	export class ListOuputController {
		libraryItem : LibraryItem;
		selectedListReportGroup : ListReportGroup;
		selectedFieldOutput : FieldOutput;
		dataSourceAvailableFields : string[];

		static $inject = ['LibraryService', 'LoggerService', '$scope',
			'$uibModal', 'AdminService', '$window', '$stateParams'];

		constructor(
			protected libraryService:ILibraryService,
			protected logger : ILoggerService,
			protected $scope : IScope,
			protected $modal : IModalService,
			protected adminService : IAdminService,
			protected $window : IWindowService,
			protected $stateParams : {itemAction : string, itemUuid : string}) {

			this.performAction($stateParams.itemAction, $stateParams.itemUuid);
		}

		// General report methods
		performAction(action:string, itemUuid:string) {
			switch (action) {
				case 'add':
					this.create(itemUuid);
					break;
				case 'view':
					this.load(itemUuid);
					break;
			}
		}

		selectDataSource(datasourceContainer : { dataSource : DataSource }) {
			var vm = this;
			var test : Test = null;

			if (datasourceContainer.dataSource) {
				test = {dataSource: datasourceContainer.dataSource} as Test;
			}

			TestEditorController.open(this.$modal, test)
				.result.then(function(dataSourceContainer : { dataSource : DataSource }) {
					datasourceContainer.dataSource = dataSourceContainer.dataSource;
					vm.loadDataSourceAvailableFieldList();
					vm.adminService.setPendingChanges();
			});
		}

		loadDataSourceAvailableFieldList() {
			this.dataSourceAvailableFields = ['Forename', 'Surname', 'DateOfBirth'];
		}

		addListGroup() {
			this.selectedListReportGroup = {
				heading: 'New list group',
				fieldBased: {
					dataSource: null,
					fieldOutput: []
				}
			} as ListReportGroup;
			this.libraryItem.listReport.group.push(this.selectedListReportGroup);
		}

		removeListGroup(scope : any) {
			this.libraryItem.listReport.group.splice(scope.$index, 1);
			if (this.selectedListReportGroup === scope.item) {
				this.selectedListReportGroup = null;
			}
		}

		addFieldOutput() {
			this.selectedFieldOutput = {
				heading : '',
				field : ''
			} as FieldOutput;
			this.selectedListReportGroup.fieldBased.fieldOutput.push(this.selectedFieldOutput);
		}

		removeFieldOutput(scope : any) {
			this.selectedListReportGroup.fieldBased.fieldOutput.splice(scope.$index, 1);
			if (this.selectedFieldOutput === scope.item) {
				this.selectedFieldOutput = null;
			}
		}

		create(folderUuid : string) {
			this.libraryItem = {
				uuid : null,
				name : 'New item',
				description : '',
				folderUuid : folderUuid,
				listReport : {
					group: [
						{
							heading: 'Patient',
							fieldBased: {
								dataSource: {
									entity : 'PATIENT',
									filter : []
								},
								fieldOutput: [
									{heading: 'DOB', field: 'DateOfBirth'},
									{heading: 'Forename', field: 'FirstName'},
									{heading: 'Surname', field: 'LastName'}
								]
							}
						},
						{
							heading: 'Issues',
							fieldBased: {
								dataSource: {
									entity : 'MEDICATION_ISSUE',
									filter : []
								},
								fieldOutput: [
									{heading: 'Medication', field: 'DrugName'},
									{heading: 'Dose', field: 'Doseage'},
									{heading: 'Last Issue', field: 'LastIssued'}
								]
							}
						}
					]
				}
			} as LibraryItem;
		}

		load(uuid : string) {
			var vm = this;
			vm.libraryService.getLibraryItem(uuid)
				.then(function(libraryItem : LibraryItem) {
					vm.libraryItem = libraryItem;
				});
		}

		save() {
			var vm = this;
			vm.libraryService.saveLibraryItem(vm.libraryItem)
				.then(function(libraryItem : LibraryItem) {
					vm.libraryItem.uuid = libraryItem.uuid;
					vm.adminService.clearPendingChanges();
				});
		}

		saveAndClose() {
			var vm = this;
			vm.libraryService.saveLibraryItem(vm.libraryItem)
				.then(function(libraryItem : LibraryItem) {
					vm.libraryItem.uuid = libraryItem.uuid;
					vm.close();
				});
		}

		close() {
			this.adminService.clearPendingChanges();
			this.$window.history.back();
		}

	}

	angular
		.module('app.listOutput')
		.controller('ListOutputController', ListOuputController);
}
