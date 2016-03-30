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
	'use strict';

	export class LibraryController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;

		static $inject = ['LibraryService', 'FolderService', 'LoggerService', '$scope', '$uibModal', '$state'];

		constructor(
			protected libraryService:ILibraryService,
			protected folderService:IFolderService,
			protected logger:ILoggerService,
			protected $scope : IScope,
			protected $modal : IModalService,
			protected $state : IStateService) {
			this.getLibraryRootFolders();
		}

		getLibraryRootFolders() {
			var vm = this;
			vm.folderService.getFolders(1, null)
				.then(function (data) {
					vm.treeData = data.folders;

					if (vm.treeData && vm.treeData.length > 0) {
						// Set folder type (not retrieved by API)
						vm.treeData.forEach((item) => { item.folderType = FolderType.Library; } );
						// Expand top level by default
						vm.toggleExpansion(vm.treeData[0]);
					}
				});
		}

		selectNode(node : FolderNode) {
			if (node === this.selectedNode) { return; }
			var vm = this;

			vm.selectedNode = node;
			node.loading = true;

			vm.folderService.getFolderContents(node.uuid)
				.then(function(data) {
					vm.itemSummaryList = data;
					node.loading = false;
				});
		}

		toggleExpansion(node : FolderNode) {
			if (!node.hasChildren) { return; }

			node.isExpanded = !node.isExpanded;

			if (node.isExpanded && (node.nodes == null || node.nodes.length === 0)) {
				var vm = this;
				var folderId = node.uuid;
				node.loading = true;
				this.folderService.getFolders(1, folderId)
					.then(function (data) {
						node.nodes = data.folders;
						// Set parent folder (not retrieved by API)
						node.nodes.forEach((item) => { item.parentFolderUuid = node.uuid; } );
						node.loading = false;
					});
			}
		}

		addChildFolder(node : FolderNode) {
			var vm = this;
			InputBoxController.open(vm.$modal, 'New Folder', 'Enter new folder name', 'New folder')
				.result.then(function(result : string) {
				var folder : Folder = {
					uuid : null,
					folderName : result,
					folderType : FolderType.Library,
					parentFolderUuid : node.uuid,
					contentCount : 0,
					hasChildren : false
				};
				vm.folderService.saveFolder(folder)
					.then(function(response) {
						vm.logger.success('Folder created', response, 'New folder');
						node.isExpanded = false;
						node.hasChildren = true;
						node.nodes = null;
						vm.toggleExpansion(node);
					})
					.catch(function(error){
						vm.logger.error('Error creating folder', error, 'New folder');
					});
			});
		}

		renameFolder(scope : any) {
			var vm = this;
			var folderNode : FolderNode = scope.$modelValue;
			InputBoxController.open(vm.$modal,
				'Rename folder', 'Enter new name for ' + folderNode.folderName, folderNode.folderName)
				.result.then(function(newName : string) {
				var oldName = folderNode.folderName;
				folderNode.folderName = newName;
					vm.folderService.saveFolder(folderNode)
						.then(function (response) {
							vm.logger.success('Folder renamed to ' + newName, response, 'Rename folder');
						})
						.catch(function (error) {
							folderNode.folderName = oldName;
							vm.logger.error('Error renaming folder', error, 'Rename folder');
						});
				});
		}

		deleteFolder(scope : any) {
			var vm = this;
			var folderNode : FolderNode = scope.$modelValue;
			MessageBoxController.open(vm.$modal,
				'Delete folder', 'Are you sure you want to delete folder ' + folderNode.folderName + '?', 'Yes', 'No')
				.result.then(function() {
					vm.folderService.deleteFolder(folderNode)
					.then(function (response) {
						scope.remove();
						vm.logger.success('Folder deleted', response, 'Delete folder');
					})
					.catch(function (error) {
						vm.logger.error('Error deleting folder', error, 'Delete folder');
					});
			});
		}

		actionItem(item : FolderItem, action : string) {
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
	}

	angular
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
