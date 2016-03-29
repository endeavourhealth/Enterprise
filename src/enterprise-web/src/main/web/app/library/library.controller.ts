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
	import CodeSetValueWithTerm = app.models.CodeSetValueWithTerm;
	'use strict';

	export class LibraryController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$uibModal', '$state'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger:ILoggerService,
			protected $scope : IScope,
			protected $modal : IModalService,
			protected $state : IStateService) {
			this.getLibraryRootFolders();
		}

		getLibraryRootFolders() {
			var vm = this;
			vm.libraryService.getFolders(1, null)
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

			vm.libraryService.getFolderContents(node.uuid)
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
				this.libraryService.getFolders(1, folderId)
					.then(function (data) {
						node.nodes = data.folders;
						// Set parent folder (not retrieved by API)
						node.nodes.forEach((item) => { item.parentFolderUuid = node.uuid; } );
						node.loading = false;
					});
			}
		}

		showCodePicker() {
			var selection : CodeSetValueWithTerm[] = [
				{
					code: '195967001',
					term: 'asthma',
					includeChildren: true,
					exclusion: []
				},
				{
					code: '194828000',
					term: 'angina',
					includeChildren: true,
					exclusion: [
						{code:'315025001', term:'refractory angina', includeChildren : null, exclusion : null },
						{code:'4557003', term:'preinfarcation syndrome', includeChildren : null, exclusion : null }
					]
				},
				{
					code: '73211009',
					term: 'diabetes',
					includeChildren: false,
					exclusion: []
				}
			];

			CodePickerController.open(this.$modal, selection)
				.result.then(function(resultData : CodeSetValueWithTerm[]){
					console.log('Dialog closed');
					console.log(resultData);
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
					vm.libraryService.saveFolder(folderNode)
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
					vm.libraryService.deleteFolder(folderNode)
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
					this.actionCodeSet(item.uuid, action);
					break;
			}
		}

		actionCodeSet(codeSetUuid : string, action : string) {
		}
	}

	angular
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
