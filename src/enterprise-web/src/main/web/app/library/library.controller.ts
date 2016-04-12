/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.library {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import FolderContent = app.models.FolderItem;
	import ILoggerService = app.blocks.ILoggerService;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import IModalService = angular.ui.bootstrap.IModalService;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import InputBoxController = app.dialogs.InputBoxController;
	import CodePickerController = app.dialogs.CodePickerController;
	import IScope = angular.IScope;
	import TermlexCode = app.models.Code;
	import TermlexCodeSelection = app.models.CodeSelection;
	import Folder = app.models.Folder;
	import FolderType = app.models.FolderType;
	import MessageBoxController = app.dialogs.MessageBoxController;
	import FolderItem = app.models.FolderItem;
	import ItemType = app.models.ItemType;
	import LibraryItem = app.models.LibraryItem;
	import CodeSetValue = app.models.CodeSetValue;
	import IFolderService = app.core.IFolderService;
	import ILibraryService = app.core.ILibraryService;
	import LibraryItemFolderModuleBase = app.blocks.LibraryItemFolderModuleBase;
	import IModuleStateService = app.core.IModuleStateService;
	'use strict';

	export class LibraryController extends LibraryItemFolderModuleBase {
		static $inject = ['LibraryService', 'FolderService', 'LoggerService', 'ModuleStateService', '$scope', '$uibModal',
			'$state'];

		constructor(
			protected libraryService:ILibraryService,
			protected folderService:IFolderService,
			protected logger:ILoggerService,
			protected moduleStateService : IModuleStateService,
			protected $scope : IScope,
			protected $modal : IModalService,
			protected $state : IStateService) {
			super(logger, $modal, folderService, FolderType.Library);

			var state = moduleStateService.getState('library');
			if (state) {
				this.treeData = state.treeData;
				this.selectNode(state.selectedNode);
			}
		}

		actionItem(item : FolderItem, action : string) {
			this.saveState();
			switch (item.type) {
				case ItemType.Query:
					this.$state.go('app.queryAction', {itemUuid: item.uuid, itemAction: action});
					break;
				case ItemType.ListOutput:
					this.$state.go('app.listOutputAction', {itemUuid: item.uuid, itemAction: action});
					break;
				case ItemType.CodeSet:
					this.$state.go('app.codeSetAction', {itemUuid: item.uuid, itemAction: action});
					break;
			}
		}

		deleteItem(item : FolderItem) {
			var vm = this;
			vm.libraryService.deleteLibraryItem(item.uuid)
				.then(function(result) {
					var i = vm.itemSummaryList.contents.indexOf(item);
					vm.itemSummaryList.contents.splice(i, 1);
					vm.logger.success('Library item deleted', result, 'Delete item');
				})
				.catch(function(error) {
					vm.logger.error('Error deleting library item', error, 'Delete item');
				});
		}

		saveState() {
			var state = {
				selectedNode : this.selectedNode,
				treeData : this.treeData,
				itemSummaryList : this.itemSummaryList
			};
			this.moduleStateService.setState('library', state);
		}
	}

	angular
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
