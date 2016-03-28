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
	import EntityMap = app.models.EntityMap;
	import Entity = app.models.Entity;
	import Field = app.models.Field;
	'use strict';

	export class ListOuputController {
		libraryItem : LibraryItem;
		selectedListReportGroup : ListReportGroup;
		selectedFieldOutput : FieldOutput;
		dataSourceAvailableFields : Field[];
		entityMap : EntityMap;

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

			this.loadEntityMap();
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

		loadEntityMap() {
			var vm = this;
			vm.libraryService.getEntityMap().then(function (result : EntityMap) {
				vm.entityMap = result;
			})
			.catch(function(data) {
				vm.logger.error('Error loading entity map', data, 'Error');
			});
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
			// Find entity in entitymap
			this.dataSourceAvailableFields = [];
			var entityName : string = this.selectedListReportGroup.fieldBased.dataSource.entity;

			var matchingEntities : Entity[] = $.grep(this.entityMap.entity, (e) => e.logicalName === entityName);
			if (matchingEntities.length === 1) {
				this.dataSourceAvailableFields = $.grep(matchingEntities[0].field, (e) => e.availability.indexOf('output') > -1);
			}
		}

		getFieldDisplayName(logicalName : string) : string {
			var matchingFields : Field[] = $.grep(this.dataSourceAvailableFields, (e) => e.logicalName === logicalName);
			if (matchingFields.length === 1) {
				return matchingFields[0].displayName;
			}
			return '<Unknown>';
		}

		getDatasourceDisplayName() : string {
			if (this.selectedListReportGroup
				&& this.selectedListReportGroup.fieldBased
				&& this.selectedListReportGroup.fieldBased.dataSource) {
				var logicalName = this.selectedListReportGroup.fieldBased.dataSource.entity;
				var matchingEntities:Entity[] = $.grep(this.entityMap.entity, (e) => e.logicalName === logicalName);
				if (matchingEntities.length === 1) {
					return matchingEntities[0].displayName;
				}
			}
			return '<Unknown>';
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
									{heading: 'Birth Date', field: 'DOB'},
									{heading: 'Gender', field: 'SEX'},
									{heading: 'Surname', field: 'SURNAME'}
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
									{heading: 'Medication', field: 'TERM'},
									{heading: 'Date', field: 'EFFECTIVEDATE'}
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
				})
				.catch(function(data) {
					vm.logger.error('Error loading list output', data, 'Error');
				});
		}

		save(close : boolean) {
			var vm = this;
			vm.libraryService.saveLibraryItem(vm.libraryItem)
				.then(function(libraryItem : LibraryItem) {
					vm.libraryItem.uuid = libraryItem.uuid;
					vm.adminService.clearPendingChanges();
					vm.logger.success('List output saved', vm.libraryItem, 'Saved');
					if (close) { vm.$window.history.back(); }
				})
				.catch(function(data) {
					vm.logger.error('Error saving list output', data, 'Error');
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
