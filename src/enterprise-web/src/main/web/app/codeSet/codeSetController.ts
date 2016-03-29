/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.codeSet {
	import LibraryItem = app.models.LibraryItem;
	import ILibraryService = app.core.ILibraryService;
	import IModalScope = angular.ui.bootstrap.IModalScope;
	import IWindowService = angular.IWindowService;
	import CodeSetValue = app.models.CodeSetValue;
	import ICodingService = app.core.ICodingService;
	import Concept = app.models.Concept;
	import CodePickerController = app.dialogs.CodePickerController;
	'use strict';

	export class CodeSetController {
		libraryItem : LibraryItem;
		termCache : any;
		readOnly : boolean;

		static $inject = ['LibraryService', 'LoggerService', '$scope',
			'$uibModal', 'AdminService', '$window', '$stateParams', 'CodingService'];

		constructor(
			protected libraryService:ILibraryService,
			protected logger : ILoggerService,
			protected $scope : IModalScope,
			protected $modal : IModalService,
			protected adminService : IAdminService,
			protected $window : IWindowService,
			protected $stateParams : {itemAction : string, itemUuid : string},
			protected codingService : ICodingService) {

			this.termCache = {};
			this.performAction($stateParams.itemAction, $stateParams.itemUuid);
		}

		// General report methods
		performAction(action:string, itemUuid:string) {
			this.readOnly = (action === 'view');
			switch (action) {
				case 'add':
					this.create(itemUuid);
					break;
				case 'view':
				case 'edit':
					this.load(itemUuid);
					break;
			}
		}

		create(folderUuid : string) {
			this.libraryItem = {
				uuid : null,
				name : 'New item',
				description : '',
				folderUuid : folderUuid,
				codeSet : {
					codingSystem: 'SNOMED_CT',
					codeSetValue: []
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
					vm.logger.error('Error loading library item', data, 'Error');
				});
		}

		save(close : boolean) {
			var vm = this;
			vm.libraryService.saveLibraryItem(vm.libraryItem)
				.then(function(libraryItem : LibraryItem) {
					vm.libraryItem.uuid = libraryItem.uuid;
					vm.adminService.clearPendingChanges();
					vm.logger.success('Library item saved', vm.libraryItem, 'Saved');
					if (close) { vm.$window.history.back(); }
				})
				.catch(function(data) {
					vm.logger.error('Error saving library item', data, 'Error');
				});
		}

		close() {
			this.adminService.clearPendingChanges();
			this.$window.history.back();
		}

		getTerm(code : string) : string {
			var vm = this;
			var term = vm.termCache[code];
			if (term) { return term; }
			vm.termCache[code] = 'Loading...';

			vm.codingService.getPreferredTerm(code)
				.then(function(concept:Concept) {
					vm.termCache[code] = concept.preferredTerm;
				});

			return vm.termCache[code];
		}

		editCodeSetValueList() {
			var vm = this;
			CodePickerController.open(vm.$modal, vm.libraryItem.codeSet.codeSetValue)
				.result.then(function(result) {
					vm.libraryItem.codeSet.codeSetValue = result;
			});
		}
	}

	angular
		.module('app.listOutput')
		.controller('CodeSetController', CodeSetController);
}
