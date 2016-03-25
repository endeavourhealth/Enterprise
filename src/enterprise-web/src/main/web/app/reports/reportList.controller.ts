/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderItem;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import IScope = angular.IScope;
	import FolderItem = app.models.FolderItem;
	import FolderType = app.models.FolderType;
	import InputBoxController = app.dialogs.InputBoxController;
	import MessageBoxController = app.dialogs.MessageBoxController;
	import IModalService = angular.ui.bootstrap.IModalService;
	'use strict';

	export class ReportListController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$uibModal'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $scope : IScope,
			protected $modal : IModalService) {
			this.getReportsRootFolders();
		}

		getReportsRootFolders() {
			var vm = this;
			vm.libraryService.getFolders(2, null)
				.then(function (data) {
					vm.treeData = data.folders;

					if (vm.treeData && vm.treeData.length > 0) {
						// Set folder type (not retrieved by API)
						vm.treeData.forEach((item) => { item.folderType = FolderType.Report; });
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
				this.libraryService.getFolders(2, folderId)
					.then(function (data) {
						node.nodes = data.folders;
						// Set parent folder (not retrieved by API)
						node.nodes.forEach((item) => { item.parentFolderUuid = node.uuid; } );
						node.loading = false;
					});
			}
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

		deleteItem(scope : any) {
			var vm = this;
			vm.libraryService.deleteReport(scope.$modelValue)
				.then(function(result) {
					scope.remove();
				});
		}
	}

	angular
		.module('app.reports')
		.controller('ReportListController', ReportListController);
}
