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
	'use strict';

	export class LibraryController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$uibModal'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger:ILoggerService,
			protected $scope : IScope,
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
			var selection : TermlexCodeSelection[] = [
				{
					id: '195967001',
					label: 'asthma',
					includeChildren: true,
					exclusions: []
				},
				{
					id: '194828000',
					label: 'angina',
					includeChildren: true,
					exclusions: [
						{id:'315025001', label:'refractory angina' },
						{id:'4557003', label:'preinfarcation syndrome' }
					]
				},
				{
					id: '73211009',
					label: 'diabetes',
					includeChildren: false,
					exclusions: []
				}
			];

			CodePickerController.open(this.$modal, selection)
				.result.then(function(resultData : TermlexCodeSelection[]){
					console.log('Dialog closed');
					console.log(resultData);
				});
		}

		renameFolder(folder : FolderNode) {
			var vm = this;
			InputBoxController.open(vm.$modal, 'Rename folder', 'Enter new name for ' + folder.folderName, folder.folderName)
				.result.then(function(newName : string) {
				var oldName = folder.folderName;
				folder.folderName = newName;
					vm.libraryService.saveFolder(folder)
						.then(function (response) {
							vm.logger.success('Folder renamed to ' + newName, response, 'Rename folder');
						})
						.catch(function (error) {
							vm.logger.error('Error renaming folder', error, 'Rename folder');
						});
				});
		}
	}

	angular
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
