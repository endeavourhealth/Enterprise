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
	import Report = app.models.Report;
	'use strict';

	class ReportController extends LibraryController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;
		reportData : Report;
		itemAction : string;
		itemUuid : string;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$stateParams', 'uuid'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $scope : any,
			protected $stateParams : any,
			protected uuid : any) {
			super(libraryService, logger, $scope);
			this.itemAction = $stateParams.itemAction;
			this.itemUuid = $stateParams.itemUuid;

			this.reportData = {
					uuid: uuid.v4(),
					name: 'Report',
					nodes: []
			};
		}

		onDrop(evt : JQueryEventObject, ui : any) {
			var source = JSON.parse(ui.draggable.attr('id'));
			var targetId = evt.target.getAttribute('id');

			var newReportNode : ReportNode = {
				uuid : this.uuid.v4(),
				itemUuid : source.uuid,
				name : source.name,
				type : source.type,
				nodes: []
			};

			var target = this.searchTree(this.reportData, targetId);

			target.nodes.push(newReportNode);
		}

		deleteNode(tree: any, node: any) {
			for (var i = 0; i < tree.nodes.length; i++) {
				if (tree.nodes[i].uuid === node.uuid) {
					tree.nodes.splice(i, 1);
					return;
				} else {
					this.deleteNode(tree.nodes[i], node);
				}
			}
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
