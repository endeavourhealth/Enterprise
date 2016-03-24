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
	import IWindowService = angular.IWindowService;
	import LibraryItem = app.models.LibraryItem;
	import UuidNameKVP = app.models.UuidNameKVP;
	'use strict';

	export class ListOuputController {
		libraryItem : LibraryItem;
		dataSourceMap : any;
		selectedListReportGroup : ListReportGroup;
		selectedFieldOutput : FieldOutput;

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

		selectDataSource(datasourceContainer : { dataSource : any }) {
			var vm = this;
			InputBoxController.open(this.$modal, 'Data source', 'Please select a data source', datasourceContainer.dataSource)
				.result.then(function(dataSource:any) {
					datasourceContainer.dataSource = dataSource;
					vm.adminService.setPendingChanges();
			});
		}

		create(folderUuid : string) {
			this.libraryItem = {
				uuid : null,
				name : 'New item',
				description : '',
				folderUuid : folderUuid,
				codeSet : null,
				listReport : {
					group: [
						{
							heading: 'Patient',
							summary: null,
							fieldBased: {
								dataSourceUuid: '2ee82b03-7b40-425c-a687-2fd96d46c59b',
								fieldOutput: [
									{heading: 'DOB', field: 'DateOfBirth'},
									{heading: 'Forename', field: 'FirstName'},
									{heading: 'Surname', field: 'LastName'}
								]
							}
						},
						{
							heading: 'Issues',
							summary: null,
							fieldBased: {
								dataSourceUuid: 'a8f952d4-1055-4689-8589-7e9fbf80c69b',
								fieldOutput: [
									{heading: 'Medication', field: 'DrugName'},
									{heading: 'Dose', field: 'Doseage'},
									{heading: 'Last Issue', field: 'LastIssued'}
								]
							}
						}
					]
				}
			};
			this.dataSourceMap = {};
			this.dataSourceMap['2ee82b03-7b40-425c-a687-2fd96d46c59b'] = 'Patient DS';
			this.dataSourceMap['a8f952d4-1055-4689-8589-7e9fbf80c69b'] = 'Medication DS';
		}

		load(uuid : string) {
			var vm = this;
			vm.libraryService.getLibraryItem(uuid)
				.then(function(libraryItem : LibraryItem) {
					vm.libraryService.getContentNamesForReportLibraryItem(uuid)
						.then(function (data) {
							vm.libraryItem = libraryItem;
							vm.dataSourceMap = UuidNameKVP.toAssociativeArray(data.contents);
						});
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
