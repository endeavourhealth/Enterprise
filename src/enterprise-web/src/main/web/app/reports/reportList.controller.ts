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
	import Folder = app.models.Folder;
	import QueueReportController = app.dialogs.QueueReportController;
	import RequestParameters = app.models.RequestParameters;
	import LoggerService = app.blocks.LoggerService;
	import IReportService = app.core.IReportService;
	import IFolderService = app.core.IFolderService;
	'use strict';

	export class ReportListController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;
		selectedReport : FolderItem;
		selectedReportDetails : any;

		static $inject = ['ReportService', 'FolderService', 'LoggerService', '$scope', '$uibModal'];

		constructor(
			protected reportService:IReportService,
			protected folderService : IFolderService,
			protected logger : ILoggerService,
			protected $scope : IScope,
			protected $modal : IModalService) {
			this.getReportsRootFolders();
		}

		run(item : FolderItem) {
			var vm = this;
			QueueReportController.open(vm.$modal, item.uuid, item.name)
				.result.then(function(result : RequestParameters) {
					vm.scheduleReport(result);
			});
		}

		scheduleReport(requestParameters : RequestParameters) {
			var vm = this;
			vm.reportService.scheduleReport(requestParameters)
				.then(function(result) {
					vm.logger.success('Report queued', result, 'Run report');
				})
				.catch(function(error) {
					vm.logger.error('Error queueing report', error, 'Run report');
				});
		}

		getReportsRootFolders() {
			var vm = this;
			vm.folderService.getFolders(2, null)
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
				this.folderService.getFolders(2, folderId)
					.then(function (data) {
						node.nodes = data.folders;
						// Set parent folder (not retrieved by API)
						node.nodes.forEach((item) => { item.parentFolderUuid = node.uuid; } );
						node.loading = false;
					});
			}
		}

		selectFolderItem(item : FolderItem) {
			this.selectedReport = item;
			// Load report details
			this.selectedReportDetails = {};
		}

		addChildFolder(node : FolderNode) {
			var vm = this;
			InputBoxController.open(vm.$modal, 'New Folder', 'Enter new folder name', 'New folder')
				.result.then(function(result : string) {
				var folder : Folder = {
					uuid : null,
					folderName : result,
					folderType : FolderType.Report,
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

		deleteItem(scope : any) {
			var vm = this;
			vm.reportService.deleteReport(scope.$modelValue)
				.then(function(result) {
					scope.remove();
				});
		}
	}

	angular
		.module('app.reports')
		.controller('ReportListController', ReportListController);
}
