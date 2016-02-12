/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.library {
	import TreeNode = app.models.TreeNode;
	'use strict';

	class LibraryController {
		treeData : TreeNode[];
		itemSummaryList : any[]; // DefinitionItemSummary[];

		static $inject = ['LibraryService', '$scope'];

		constructor(private libraryService:app.core.ILibraryService, private $scope : any) {
			this.getLibraryRootFolders();
		}

		getLibraryRootFolders() {
			var vm = this;
			this.libraryService.getRootFolders('04CF1D8D-B6E6-4E20-9A74-D6F197A9FE78', 0)
				.then(function (data) {
					vm.treeData = data;
				});
		}

		toggleExpansion(node : TreeNode) {
			if (!node.hasChildren) { return; }

			node.isExpanded = !node.isExpanded;

			if (node.isExpanded && (node.nodes == null || node.nodes.length === 0)) {
				var vm = this;
				var folderId = node.itemUuid;
				this.libraryService.getChildFolders(folderId)
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
