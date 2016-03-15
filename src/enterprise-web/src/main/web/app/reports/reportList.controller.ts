/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderItem;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import IScope = angular.IScope;
	'use strict';

	export class ReportListController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;

		static $inject = ['LibraryService', 'LoggerService', '$scope'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $scope : IScope) {
			this.getReportsRootFolders();
		}

		getReportsRootFolders() {
			var vm = this;
			vm.libraryService.getFolders(2, null)
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
				this.libraryService.getFolders(2, folderId)
					.then(function (data) {
						node.nodes = data;
						node.loading = false;
					});
			}
		}
	}

	angular
		.module('app.reports')
		.controller('ReportListController', ReportListController);
}
