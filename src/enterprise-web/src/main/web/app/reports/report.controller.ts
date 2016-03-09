/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderItem;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import LibraryController = app.library.LibraryController;
	import Report = app.models.Report;
	import ICallbacks = AngularUITree.ICallbacks;
	import IEventInfo = AngularUITree.IEventInfo;
	import FolderItem = app.models.FolderItem;
	import ReportNode = app.models.ReportNode;
	import ItemType = app.models.ItemType;
	'use strict';

	class ReportController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;
		reportData : Report;
		itemAction : string;
		itemUuid : string;
		reportTreeCallbackOptions : any;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$stateParams'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $scope : any,
			protected $stateParams : any) {
			this.itemAction = $stateParams.itemAction;
			this.itemUuid = $stateParams.itemUuid;
			this.reportTreeCallbackOptions = {dropped : this.reportTreeDroppedCallback };

			//this.reportData = {
			//		uuid: uuid.v4(),
			//		name: 'Report',
			//		nodes: []
			//};

			this.reportData = {"uuid":"2fe4448b-d6b9-48f7-a684-6feba9b004ec","name":"Report","children":[{"uuid":"2d0ad623-69db-42dc-a191-73ef5abe7f58","itemUuid":"ecb4497a-16a2-44c3-8b51-15cfc4bea9f5","name":"Asthmatics","type":2,"children":[{"uuid":"cbe78a5f-ce19-4ba0-86c3-19bd4bc9e693","itemUuid":"098be27b-1dd3-432f-9edd-1049dad4f7ac","name":"Sub Asthmatics","type":2,"children":[]}]},{"uuid":"b7911567-9a78-4dc2-935a-4551e46e261e","itemUuid":"55086fcb-d24f-4601-afd7-b7cae55426e4","name":"renamed query","type":2,"children":[]},{"uuid":"ec525c9f-d3c7-41a0-8301-ab4bdb683cf9","itemUuid":"d7219ff4-339f-4a54-9ddd-818a0e00ace9","name":"Diabetics","type":2,"children":[{"uuid":"fc6ad839-9dea-4a00-bcb1-18fc590b6321","type":6,"typeDesc":"ListOutput","name":"List Ouput 1","lastModified":"2016-03-09","children":[]}]}]};
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
					// filter content by those allowed in reports
					vm.itemSummaryList.contents = vm.itemSummaryList.contents.filter(vm.validReportItemType);
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

		remove(scope:any) {
			scope.remove();
		}

		validReportItemType(input:FolderContent):boolean {
			switch (input.type) {
				case ItemType.Query:
				case ItemType.ListOutput:
					return true;
				default:
					return false;
			}
		};

		reportTreeDroppedCallback(eventInfo: IEventInfo) {
			eventInfo.source.cloneModel.children = [];
		}

	}

	angular
		.module('app.reports')
		.controller('ReportController', ReportController);
}
