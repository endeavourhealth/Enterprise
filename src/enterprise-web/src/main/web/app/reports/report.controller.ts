/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderContent;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import LibraryController = app.library.LibraryController;
	import ReportNode = app.models.ReportNode;
	'use strict';

	class ReportController extends LibraryController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;
		reportData : ReportNode[];
		itemAction : string;
		itemUuid : string;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$stateParams'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $scope : any,
			protected $stateParams : any) {
			super(libraryService, logger, $scope);
			this.itemAction = $stateParams.itemAction;
			this.itemUuid = $stateParams.itemUuid;

			this.reportData = [
				{
					uuid: 'de0a0055-f6a7-4c29-82b7-49b2a28f5777',
					name: 'Report',
					type: 1,
					nodes: [
						{
							uuid: '2455900e-2151-4617-b659-043be07d441c',
							name: 'Diabetics',
							type: 2,
							nodes: [
								{uuid: '6f5143d1-9b40-462a-bccd-18de6ea5ed8e', name: 'Over 50', type: 2, nodes: []},
								{uuid: '1ab9a8ff-6c35-4e3a-90cc-1eeb31e676d0', name: 'Amputation', type: 2, nodes: []},
								{uuid: '650f5976-ecbc-4b59-9a36-1d53eaa83e71', name: 'Decreasing BP', type: 2, nodes: []},
							]
						},
						{
							uuid: '4a4f1026-39f1-4e52-964e-79bca480d401',
							name: 'Recent Diabetics',
							type: 2,
							nodes: [
								{uuid: '1a6458de-27fd-49f5-be19-317874b1f110', name: 'All medication', type: 6, nodes: []}
							]
						}
					]
				}
			];
		}

		onDrop(evt : JQueryEventObject, ui : any) {
			var source = JSON.parse(ui.draggable.attr('id'));
			var targetId = evt.target.getAttribute('id');

			var newReportNode : ReportNode = {
				uuid : source.uuid,
				name : source.name,
				type : source.type,
				nodes: []
			}; // = new ReportNode();

			var target = this.searchTree(this.reportData[0], targetId);

			target.nodes.push(newReportNode);
		}

		searchTree(node: any, uuid: string) {
		if (node.uuid === uuid) {
			return node;
		}else if (node.nodes != null) {
			var result : any = null;
			for (var i = 0; result == null && i < node.nodes.length; i++) {
				result = this.searchTree(node.nodes[i], uuid);
			}
			return result;
		}
		return null;
	}
	}

	angular
		.module('app.reports')
		.controller('ReportController', ReportController);
}
