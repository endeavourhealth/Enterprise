/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.library {
	import TreeNode = app.models.TreeNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	'use strict';

	class LibraryController {
		treeData : TreeNode[];
		selectedNode : TreeNode = null;
		itemSummaryList : ItemSummaryList;

		static $inject = ['LibraryService', '$scope'];

		constructor(private libraryService:app.core.ILibraryService, private $scope : any) {
			this.getLibraryRootFolders();
		}

		getLibraryRootFolders() {
			var vm = this;
			vm.libraryService.getFolders(1, null)
				.then(function (data) {
					vm.treeData = data;
				});
		}

		selectNode(node : TreeNode) {
			if (node === this.selectedNode) { return; }
			var vm = this;

			if (vm.selectedNode !== null) {
				vm.selectedNode.isSelected = false;
			}
			node.isSelected = true;
			vm.selectedNode = node;

			vm.libraryService.getFolderContents(node.uuid)
				.then(function(data) {
					vm.itemSummaryList = data;
				});
		}

		toggleExpansion(node : TreeNode) {
			if (!node.hasChildren) { return; }

			node.isExpanded = !node.isExpanded;

			if (node.isExpanded && (node.nodes == null || node.nodes.length === 0)) {
				var vm = this;
				var folderId = node.uuid;
				this.libraryService.getFolders(1, folderId)
					.then(function (data) {
						node.nodes = data;
					});
			}
		}
	}

	angular
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
