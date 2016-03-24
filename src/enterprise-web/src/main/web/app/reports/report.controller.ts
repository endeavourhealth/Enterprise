/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderItem;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import LibraryController = app.library.LibraryController;
	import ICallbacks = AngularUITree.ICallbacks;
	import IEventInfo = AngularUITree.IEventInfo;
	import FolderItem = app.models.FolderItem;
	import ReportNode = app.models.ReportNode;
	import ItemType = app.models.ItemType;
	import Report = app.models.Report;
	import ReportItem = app.models.ReportItem;
	import UuidNameKVP = app.models.UuidNameKVP;
	import IScope = angular.IScope;
	'use strict';

	class ReportController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;
		report : Report;
		reportContent : ReportNode[];
		contentTreeCallbackOptions : ICallbacks;
		dataSourceMap : any;

		static $inject = ['LibraryService', 'LoggerService', '$stateParams'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $stateParams : {itemAction : string, itemUuid : string}) {
			this.contentTreeCallbackOptions = {dropped: this.contentTreeDroppedCallback, accept: null, dragStart: null};

			this.getLibraryRootFolders();
			this.performAction($stateParams.itemAction, $stateParams.itemUuid);
		}

		// General report methods
		performAction(action:string, itemUuid:string) {
			switch (action) {
				case 'add':
					this.createReport(itemUuid);
					break;
				case 'view':
					this.getReport(itemUuid);
					break;
			}
		}

		createReport(folderUuid:string) {
			// Initialize blank report
			this.report = {
				uuid: '',
				name: 'New report',
				description: '',
				folderUuid: folderUuid,
				reportItem: []
			};
			this.reportContent = [];
		}

		getReport(reportUuid:string) {
			var vm = this;
			vm.libraryService.getReport(reportUuid)
				.then(function (data) {
					vm.report = data;
					vm.reportContent = [];
					vm.libraryService.getContentNamesForReportLibraryItem(reportUuid)
						.then(function(data) {
							vm.dataSourceMap = UuidNameKVP.toAssociativeArray(data.contents);
							vm.populateTreeFromReportLists(vm.report, vm.reportContent, '');
					})
					.catch(function(data) {
						vm.logger.error('Error loading report item names', data, 'Error');
					});
				})
				.catch(function(data) {
					vm.logger.error('Error loading report', data, 'Error');
				});
		}

		populateTreeFromReportLists(report : Report,
																nodeList : ReportNode[],
																parentUuid : string) {
			var vm = this;
			if (report.reportItem == null) { report.reportItem = []; }

			for (var i = 0; i < report.reportItem.length; i++) {
				var reportItem:ReportItem = report.reportItem[i];
				if (reportItem.parentUuid === parentUuid) {
					var uuid:string = null;
					var type:ItemType = null;

					if (reportItem.queryLibraryItemUuid && reportItem.queryLibraryItemUuid !== '') {
						uuid = reportItem.queryLibraryItemUuid;
						type = ItemType.Query;
					} else if (reportItem.listReportLibraryItemUuid && reportItem.listReportLibraryItemUuid !== '') {
						uuid = reportItem.listReportLibraryItemUuid;
						type = ItemType.ListOutput;
					}

					if (uuid != null) {
						var reportNode:ReportNode = {
							uuid : uuid,
							name : vm.dataSourceMap[uuid],
							type : type,
							children : []
						};

						nodeList.push(reportNode);
						vm.populateTreeFromReportLists(report, reportNode.children, reportNode.uuid);
					}
				}
			}
		}

		save() {
			var vm = this;
			vm.report.reportItem = [];
			vm.populateReportListsFromTree(vm.report, '', vm.reportContent);

			vm.libraryService.saveReport(vm.report)
				.then(function (data:Report) {
					vm.report.uuid = data.uuid;
					vm.logger.success('Report saved', vm.report, 'Saved');
				})
				.catch(function(data) {
					vm.logger.error('Error saving report', data, 'Error');
				});
		}

		populateReportListsFromTree(report : Report, parentUuid : string, nodes : ReportNode[]) {
			for (var i = 0; i < nodes.length; i++) {
				var reportItem : ReportItem = {
					queryLibraryItemUuid : null,
					listReportLibraryItemUuid : null,
					parentUuid : parentUuid
				};

				switch (nodes[i].type) {
					case ItemType.Query:
						reportItem.queryLibraryItemUuid = nodes[i].uuid;
						break;
					case ItemType.ListOutput:
						reportItem.listReportLibraryItemUuid = nodes[i].uuid;
						break;
				}
				report.reportItem.push(reportItem);
				if (nodes[i].children && nodes[i].children.length > 0) {
					this.populateReportListsFromTree(report, nodes[i].uuid, nodes[i].children);
				}
			}
		}

		// Library tree methods
		getLibraryRootFolders() {
			var vm = this;
			vm.libraryService.getFolders(1, null)
				.then(function (data) {
					vm.treeData = data.folders;
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
					// filter content by those allowed in reports
					if (vm.itemSummaryList.contents) {
						vm.itemSummaryList.contents = vm.itemSummaryList.contents.filter(vm.validReportItemType);
					}
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
						node.nodes = data.folders;
						node.loading = false;
					});
			}
		}

		// Report structure methods
		remove(scope:any) {
			scope.remove();
		}

		// Library folder content methods
		validReportItemType(input:FolderContent):boolean {
			switch (input.type) {
				case ItemType.Query:
				case ItemType.ListOutput:
					return true;
				default:
					return false;
			}
		};

		contentTreeDroppedCallback(eventInfo: IEventInfo) {
			// Convert clone model to report node
			eventInfo.source.cloneModel.children = [];
		}

	}

	angular
		.module('app.reports')
		.controller('ReportController', ReportController);
}
