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
	import CodeSearchResult = app.models.CodeSearchResult;
	import InputBoxController = app.dialogs.InputBoxController;
	import CodePickerController = app.dialogs.CodePickerController;
	'use strict';

	export class LibraryController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$uibModal'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger:ILoggerService,
			protected $scope : any,
			protected $modal : IModalService) {
			this.getLibraryRootFolders();
		}

		getLibraryRootFolders() {
			var vm = this;
			vm.libraryService.getFolders(1, null)
				.then(function (data) {
					vm.treeData = data;
				});
		}

		selectNode(node : FolderNode) {
			if (node === this.selectedNode) { return; }
			var vm = this;

			if (vm.selectedNode !== null) {
				vm.selectedNode.isSelected = false;
			}
			node.isSelected = true;
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
						node.nodes = data;
						node.loading = false;
					});
			}
		}

		showCodePicker() {
			var selection : CodeSearchResult[] = [
				{
					term: 'asthma',
					matches: [
						{
							term: 'asthma',
							code: '195967001'
						}
					]
				},
				{
					term: 'angina',
					matches: [
						{
							term: 'angina',
							code: '194828000'
						}
					]
				},
				{
					term: 'diabetes',
					matches: [
						{
							term: 'diabetes mellitus',
							code: '73211009'
						}
					]
				}
			];

			CodePickerController.open(this.$modal, selection)
				.result.then(function(selectedItems : CodeSearchResult[]){
					console.log('Dialog closed');
					console.log(selectedItems);
				});
		}

		renameFolder(folder : FolderNode) {
			InputBoxController.open(this.$modal, 'Rename folder', 'Enter new name for ' + folder.folderName, folder.folderName)
				.result.then(function(resultData : any) {
					console.log('Dialog closed');
					console.log(resultData);
				});
		}
	}

	angular
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
